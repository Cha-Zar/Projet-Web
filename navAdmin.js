/* ===========================
  MONDÉLYS – nav.js
  Shared navigation logic
  (sidebar highlight + clock)
  =========================== */

// Highlight active nav link based on current page filename
(function highlightNav() {
  const currentPage = window.location.pathname.split("/").pop() || "index.html";
  document.querySelectorAll("[data-page]").forEach((link) => {
    if (link.dataset.page === currentPage) link.classList.add("active");
  });
})();

(function setupSiteLink() {
  document.querySelectorAll(".btn-sm.outline").forEach((button) => {
    if (button.textContent && button.textContent.includes("Voir le Site")) {
      button.setAttribute("href", "/");
      button.setAttribute("target", "_blank");
      button.setAttribute("rel", "noopener noreferrer");
    }
  });
})();

(function applyAdminIdentity() {
  const storedName = localStorage.getItem("adminName");
  const storedRole = localStorage.getItem("adminRole");

  if (storedName) {
    document.querySelectorAll(".sidebar-user strong").forEach((node) => {
      node.textContent = storedName;
    });
  }

  if (storedRole) {
    const roleLabel = storedRole === "ADMIN" ? "Administrateur" : storedRole;

    document.querySelectorAll(".sidebar-user span").forEach((node) => {
      node.textContent = roleLabel;
    });

    document.querySelectorAll(".sidebar-role").forEach((node) => {
      node.textContent = roleLabel;
    });
  }
})();

const ADMIN_SEEN_KEYS = {
  reservations: "adminSeenReservationsAt",
  reviews: "adminSeenReviewsAt",
  messages: "adminSeenMessagesAt",
};

const ADMIN_PAGE_TO_SECTION = {
  "resAdmin.html": "reservations",
  "reviwAdmin.html": "reviews",
  "contactAdmin.html": "messages",
};

let latestAdminNotificationState = {
  reservations: 0,
  reviews: 0,
  messages: 0,
  total: 0,
};

function currentAdminPage() {
  return window.location.pathname.split("/").pop() || "index.html";
}

function getSeenAt(section) {
  const key = ADMIN_SEEN_KEYS[section];
  if (!key) return null;
  const raw = localStorage.getItem(key);
  if (!raw) return null;
  const parsed = new Date(raw);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

function setSeenAt(section, date = new Date()) {
  const key = ADMIN_SEEN_KEYS[section];
  if (!key) return;
  localStorage.setItem(key, date.toISOString());
}

function markCurrentAdminSectionSeen() {
  const section = ADMIN_PAGE_TO_SECTION[currentAdminPage()];
  if (section) {
    setSeenAt(section);
  }
}

function countUnseen(items, seenAt) {
  if (!Array.isArray(items) || items.length === 0) return 0;
  if (!seenAt) return items.length;

  const seenAtMs = seenAt.getTime();
  return items.filter((item) => {
    const createdAt = item && item.createdAt ? new Date(item.createdAt) : null;
    if (!createdAt || Number.isNaN(createdAt.getTime())) {
      return false;
    }
    return createdAt.getTime() > seenAtMs;
  }).length;
}

function setSidebarBadge(page, count) {
  const link = document.querySelector(`.sidebar-nav a[data-page="${page}"]`);
  if (!link) return;

  let badge = link.querySelector(".badge");
  if (!badge) {
    badge = document.createElement("span");
    badge.className = "badge";
    link.appendChild(badge);
  }

  if (count > 0) {
    badge.textContent = String(count);
    badge.style.display = "inline-block";
  } else {
    badge.textContent = "";
    badge.style.display = "none";
  }
}

function updateTopbarNotification(state) {
  const notifButton = document.querySelector(".topbar-notif");
  if (!notifButton) return;

  const dot = notifButton.querySelector(".notif-dot");
  if (dot) {
    dot.style.display = state.total > 0 ? "block" : "none";
  }

  notifButton.title =
    state.total > 0
      ? `${state.total} notification(s) non ouvertes`
      : "Aucune nouvelle notification";
}

function showNotificationsToast() {
  if (typeof showToast !== "function") return;

  const state = latestAdminNotificationState;
  if (state.total <= 0) {
    showToast("Aucune nouvelle notification.", true);
    return;
  }

  showToast(
    `Nouvelles notifications : ${state.total}<br>` +
      `Réservations: ${state.reservations} · Avis: ${state.reviews} · Messages: ${state.messages}`,
    true,
  );
}

async function refreshAdminNotificationUI() {
  if (typeof adminFetch !== "function") return;

  try {
    const [reservations, reviews, messages] = await Promise.all([
      adminFetch("/api/admin/reservations"),
      adminFetch("/api/admin/reviews"),
      adminFetch("/api/admin/contact-messages"),
    ]);

    const reservationsCount = countUnseen(reservations, getSeenAt("reservations"));
    const reviewsCount = countUnseen(reviews, getSeenAt("reviews"));
    const messagesCount = countUnseen(messages, getSeenAt("messages"));
    const total = reservationsCount + reviewsCount + messagesCount;

    latestAdminNotificationState = {
      reservations: reservationsCount,
      reviews: reviewsCount,
      messages: messagesCount,
      total,
    };

    setSidebarBadge("resAdmin.html", reservationsCount);
    setSidebarBadge("reviwAdmin.html", reviewsCount);
    setSidebarBadge("contactAdmin.html", messagesCount);
    updateTopbarNotification(latestAdminNotificationState);
  } catch (_) {
    // Silent fail: keep navigation usable even if notifications fail.
  }
}

function setupNotificationButton() {
  const notifButton = document.querySelector(".topbar-notif");
  if (!notifButton) return;

  notifButton.addEventListener("click", showNotificationsToast);
}

markCurrentAdminSectionSeen();
setupNotificationButton();
refreshAdminNotificationUI();
setInterval(refreshAdminNotificationUI, 30000);

window.refreshAdminNotificationUI = refreshAdminNotificationUI;
window.markCurrentAdminSectionSeen = markCurrentAdminSectionSeen;

// Mobile hamburger
const hamburgerAdmin = document.getElementById("hamburgerAdmin");
if (hamburgerAdmin) {
  hamburgerAdmin.addEventListener("click", () => {
    const sidebar = document.getElementById("sidebar");
    if (sidebar) sidebar.classList.toggle("open");
  });
}

// Clock
function updateClock() {
  const now = new Date();
  const el = document.getElementById("clock");
  if (el)
    el.textContent =
      now.toLocaleDateString("fr-FR", {
        weekday: "short",
        day: "2-digit",
        month: "short",
      }) +
      " · " +
      now.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
}
updateClock();
setInterval(updateClock, 1000);

// Toast utility (shared across all pages)
function showToast(msg, success = false) {
  const el = document.createElement("div");
  el.className = "toast" + (success ? " success" : "");
  el.innerHTML = `<i class="fas ${success ? "fa-check-circle" : "fa-exclamation-circle"}"></i> ${msg}`;
  document.getElementById("toastContainer").appendChild(el);
  requestAnimationFrame(() =>
    requestAnimationFrame(() => el.classList.add("show")),
  );
  setTimeout(() => {
    el.classList.remove("show");
    setTimeout(() => el.remove(), 400);
  }, 3200);
}

// Modal utilities (shared)
function openModal(id) {
  document.getElementById(id).classList.add("open");
}
function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}
document.querySelectorAll(".modal-overlay").forEach((m) => {
  m.addEventListener("click", (e) => {
    if (e.target === m) m.classList.remove("open");
  });
});

document.querySelectorAll(".logout-btn").forEach((button) => {
  button.addEventListener("click", async () => {
    try {
      const token =
        typeof window.getAdminToken === "function" ? window.getAdminToken() : null;
      await fetch(window.apiUrl("/api/admin/auth/logout"), {
        method: "POST",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
    } catch (_) {
    }
    if (typeof window.clearAdminSessionState === "function") {
      window.clearAdminSessionState();
    } else {
      localStorage.removeItem("adminToken");
      localStorage.removeItem("adminName");
      localStorage.removeItem("adminRole");
    }
    window.location.replace("adminLogin.html");
  });
});
