const ADMIN_TOKEN_KEY = "adminToken";
const ADMIN_LOGIN_URL = "/admin";
const ADMIN_LOGIN_PAGE = "/adminLogin.html";
const ADMIN_NAME_KEY = "adminName";
const ADMIN_ROLE_KEY = "adminRole";

(() => {
  if (typeof window.apiUrl === "function") {
    return;
  }

  const API_BASE_STORAGE_KEY = "mondelysApiBaseUrl";

  function normalizeBase(value) {
    if (typeof value !== "string") {
      return null;
    }
    const trimmed = value.trim();
    if (!trimmed) {
      return "";
    }
    return trimmed.replace(/\/+$/, "");
  }

  function defaultApiBase() {
    const { protocol, hostname, port } = window.location;
    if (protocol === "file:") {
      return "http://localhost:8080";
    }

    const isLocalhost = hostname === "localhost" || hostname === "127.0.0.1";
    const frontendPorts = new Set(["3000", "3001", "4173", "5173", "5500", "5501"]);

    if (isLocalhost && frontendPorts.has(port)) {
      return "http://localhost:8080";
    }

    return "";
  }

  function resolveApiBase() {
    const stored = normalizeBase(window.localStorage.getItem(API_BASE_STORAGE_KEY));
    return stored === null ? defaultApiBase() : stored;
  }

  let apiBase = resolveApiBase();

  window.apiUrl = function apiUrl(path) {
    if (!path) {
      return apiBase;
    }
    if (/^https?:\/\//i.test(path)) {
      return path;
    }
    const normalizedPath = path.startsWith("/") ? path : `/${path}`;
    return `${apiBase}${normalizedPath}`;
  };

  window.setApiBaseUrl = function setApiBaseUrl(nextBase) {
    const normalized = normalizeBase(nextBase);
    apiBase = normalized === null ? "" : normalized;

    if (apiBase) {
      window.localStorage.setItem(API_BASE_STORAGE_KEY, apiBase);
    } else {
      window.localStorage.removeItem(API_BASE_STORAGE_KEY);
    }

    return apiBase;
  };

  window.getApiBaseUrl = function getApiBaseUrl() {
    return apiBase;
  };
})();

let adminSessionPromise = null;

function clearAdminSessionState() {
  localStorage.removeItem(ADMIN_TOKEN_KEY);
  localStorage.removeItem(ADMIN_NAME_KEY);
  localStorage.removeItem(ADMIN_ROLE_KEY);
  adminSessionPromise = null;
}

function persistAdminSession(session) {
  if (!session) return;

  if (session.fullName) {
    localStorage.setItem(ADMIN_NAME_KEY, session.fullName);
  }

  if (session.role) {
    localStorage.setItem(ADMIN_ROLE_KEY, session.role);
  }
}

function redirectToAdminLogin() {
  const currentPath = window.location.pathname;
  if (currentPath === ADMIN_LOGIN_URL || currentPath.endsWith(ADMIN_LOGIN_PAGE)) {
    return;
  }
  window.location.replace(ADMIN_LOGIN_PAGE);
}

function getAdminToken() {
  return localStorage.getItem(ADMIN_TOKEN_KEY);
}

function adminHeaders(extra = {}, hasBody = false) {
  const token = getAdminToken();
  return {
    Accept: "application/json",
    ...(hasBody ? { "Content-Type": "application/json" } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extra,
  };
}

async function ensureAdminAuthenticated(forceRefresh = false) {
  if (forceRefresh || !adminSessionPromise) {
    adminSessionPromise = fetch(window.apiUrl("/api/admin/auth/session"), {
      method: "GET",
      headers: adminHeaders(),
    })
      .then(async (response) => {
        const contentType = response.headers.get("content-type") || "";
        const body = contentType.includes("application/json")
          ? await response.json().catch(() => null)
          : null;

        if (!response.ok) {
          throw new Error((body && body.message) || "Session expirée");
        }

        persistAdminSession(body);
        return body;
      })
      .catch((error) => {
        clearAdminSessionState();
        redirectToAdminLogin();
        throw error;
      });
  }

  return adminSessionPromise;
}

async function adminFetch(url, options = {}) {
  await ensureAdminAuthenticated();

  const response = await fetch(window.apiUrl(url), {
    ...options,
    headers: adminHeaders(options.headers || {}, options.body != null),
  });

  if (response.status === 401 || response.status === 403) {
    clearAdminSessionState();
    redirectToAdminLogin();
    throw new Error("Session expirée");
  }

  const contentType = response.headers.get("content-type") || "";
  const body = contentType.includes("application/json") ? await response.json() : null;

  if (!response.ok) {
    throw new Error((body && body.message) || "Erreur serveur");
  }

  return body;
}

window.clearAdminSessionState = clearAdminSessionState;
window.getAdminToken = getAdminToken;
