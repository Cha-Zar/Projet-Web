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
})();

const ADMIN_TOKEN_KEY = "adminToken";
const DASHBOARD_URL = "DashboradAdmin.html";

const form = document.getElementById("adminLoginForm");
const errorEl = document.getElementById("adminLoginError");

function loginHeaders(extra = {}, hasBody = false) {
  const token = localStorage.getItem(ADMIN_TOKEN_KEY);
  return {
    Accept: "application/json",
    ...(hasBody ? { "Content-Type": "application/json" } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extra,
  };
}

async function redirectIfSessionExists() {
  try {
    const response = await fetch(window.apiUrl("/api/admin/auth/session"), {
      method: "GET",
      headers: loginHeaders(),
    });

    if (!response.ok) {
      return;
    }

    const session = await response.json().catch(() => null);

    if (session?.fullName) {
      localStorage.setItem("adminName", session.fullName);
    }

    if (session?.role) {
      localStorage.setItem("adminRole", session.role);
    }

    window.location.replace(DASHBOARD_URL);
  } catch (_) {
  }
}

if (form && errorEl) {
  redirectIfSessionExists();

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    errorEl.textContent = "";

    const email = document.getElementById("adminEmail").value.trim();
    const password = document.getElementById("adminPassword").value;

    try {
      const response = await fetch(window.apiUrl("/api/admin/auth/login"), {
        method: "POST",
        headers: loginHeaders({}, true),
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.message || "Email ou mot de passe invalide");
      }

      const data = await response.json();
      localStorage.setItem(ADMIN_TOKEN_KEY, data.token || "");
      localStorage.setItem("adminName", data.fullName || "Admin");
      localStorage.setItem("adminRole", data.role || "ADMIN");
      window.location.replace(DASHBOARD_URL);
    } catch (error) {
      errorEl.textContent = error.message;
    }
  });
}
