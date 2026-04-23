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

document.addEventListener("DOMContentLoaded", () => {
  // ---- NAVBAR SCROLL ----
  const navbar = document.getElementById("navbar");
  if (navbar) {
    window.addEventListener("scroll", () => {
      navbar.classList.toggle("scrolled", window.scrollY > 60);
    });
  }

  // ---- MOBILE MENU ----
  const hamburger = document.getElementById("hamburger");
  const navLinks = document.getElementById("navLinks");
  if (hamburger && navLinks) {
    hamburger.addEventListener("click", () => {
      navLinks.classList.toggle("open");
      const spans = hamburger.querySelectorAll("span");
      if (navLinks.classList.contains("open")) {
        spans[0].style.transform = "rotate(45deg) translate(5px, 6px)";
        spans[1].style.opacity = "0";
        spans[2].style.transform = "rotate(-45deg) translate(5px, -6px)";
      } else {
        spans.forEach((s) => {
          s.style.transform = "";
          s.style.opacity = "";
        });
      }
    });
  }

  // ---- MENU TABS ----
  const tabBtns = document.querySelectorAll(".tab-btn");
  if (tabBtns.length) {
    tabBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
        tabBtns.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        document
          .querySelectorAll(".menu-section")
          .forEach((s) => s.classList.remove("active"));
        const target = document.getElementById(btn.dataset.tab);
        if (target) target.classList.add("active");
      });
    });
  }

  // ---- SCROLL REVEAL ----
  const style = document.createElement("style");
  style.textContent = `
    .reveal { opacity: 0; transform: translateY(22px); transition: opacity 0.6s ease, transform 0.6s ease; }
    .reveal.in { opacity: 1; transform: none; }
    .reveal-d1 { transition-delay: 0.1s !important; }
    .reveal-d2 { transition-delay: 0.2s !important; }
    .reveal-d3 { transition-delay: 0.3s !important; }
  `;
  document.head.appendChild(style);

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) {
          e.target.classList.add("in");
          observer.unobserve(e.target);
        }
      });
    },
    { threshold: 0.1 },
  );

  [
    ".dish-card",
    ".ing-card",
    ".testi-card",
    ".menu-item",
    ".res-detail",
    ".contact-item",
    ".faq-item",
  ].forEach((sel) => {
    document.querySelectorAll(sel).forEach((el, i) => {
      el.classList.add("reveal");
      if (i === 1) el.classList.add("reveal-d1");
      if (i === 2) el.classList.add("reveal-d2");
      if (i === 3) el.classList.add("reveal-d3");
      observer.observe(el);
    });
  });

  // ---- COUNTER ANIMATION ----
  document.querySelectorAll(".stat-n").forEach((el) => {
    const match = el.textContent.match(/\d+/);
    if (!match) return;
    const num = parseInt(match[0]);
    const suffix = el.textContent.replace(/\d+/, "");
    el.textContent = "0" + suffix;
    const obs = new IntersectionObserver(
      ([entry]) => {
        if (!entry.isIntersecting) return;
        let start = null;
        const step = (ts) => {
          if (!start) start = ts;
          const prog = Math.min((ts - start) / 1400, 1);
          el.textContent = Math.floor(prog * num) + suffix;
          if (prog < 1) requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
        obs.unobserve(el);
      },
      { threshold: 0.5 },
    );
    obs.observe(el);
  });

  // ---- PAGE FADE IN/OUT ----
  document.body.style.transition = "opacity 0.6s ease";

  document.querySelectorAll("a[href]").forEach((link) => {
    const href = link.getAttribute("href");
    if (
      href &&
      !href.startsWith("#") &&
      !href.startsWith("http") &&
      !href.startsWith("mailto") &&
      !href.startsWith("tel")
    ) {
      link.addEventListener("click", (e) => {
        e.preventDefault();
        document.body.style.opacity = "0";
        setTimeout(() => {
          window.location.href = href;
        }, 400);
      });
    }
  });

  // ---- DATE MIN ----
  document.querySelectorAll('input[type="date"]').forEach((input) => {
    if (!input.min) input.min = new Date().toISOString().split("T")[0];
  });

  console.log(
    "%c✦ Mondélys",
    "color: #B8895A; font-family: serif; font-size: 1.5rem; font-weight: bold;",
  );
  console.log("%cGastronomie du Monde · Tunis, Tunisie", "color: #888;");
});
// Affichage du formulaire d’avis
const showReviewBtn = document.getElementById('showReviewBtn');
const reviewFormWrapper = document.getElementById('reviewFormWrapper');
const showReviewBtnWrapper = document.getElementById('showReviewBtnWrapper');
const cancelReviewBtn = document.getElementById('cancelReviewBtn');

if (showReviewBtn && reviewFormWrapper && showReviewBtnWrapper) {
  showReviewBtn.addEventListener('click', () => {
    reviewFormWrapper.style.display = 'block';
    showReviewBtnWrapper.style.display = 'none';
  });
}

if (cancelReviewBtn) {
  cancelReviewBtn.addEventListener('click', () => {
    reviewFormWrapper.style.display = 'none';
    showReviewBtnWrapper.style.display = 'block';
    // Optionnel : réinitialiser le formulaire
    document.getElementById('reviewForm')?.reset();
  });
}
// ===== MENU DU JOUR =====
(function loadDailyMenu() {
  const grid    = document.getElementById('dailyDishesGrid');
  const empty   = document.getElementById('dailyEmpty');
  const chefBox = document.getElementById('chefNote');
  const chefTxt = document.getElementById('chefNoteText');

  if (!grid) return; // page sans section menu du jour

  // Timer countdown jusqu'à minuit
  function startMidnightTimer() {
    const timerEl = document.getElementById('timerCountdown');
    if (!timerEl) return;

    function update() {
      const now       = new Date();
      const midnight  = new Date();
      midnight.setHours(23, 59, 59, 999);
      const diff      = midnight - now;
      const h = Math.floor(diff / 3600000);
      const m = Math.floor((diff % 3600000) / 60000);
      const s = Math.floor((diff % 60000) / 1000);
      timerEl.textContent =
        String(h).padStart(2,'0') + ':' +
        String(m).padStart(2,'0') + ':' +
        String(s).padStart(2,'0');
    }
    update();
    setInterval(update, 1000);
  }

  function renderDish(dish) {
    if (!dish.name) return '';
    return `
      <div class="daily-dish-card reveal">
        <div class="dish-origin-flag">${dish.origin || 'Mondélys'}</div>
        <div class="daily-dish-name">${dish.name}</div>
        <div class="daily-dish-desc">${dish.description || ''}</div>
        <div class="daily-dish-footer">
          <span class="daily-dish-price">${dish.price || ''} TND</span>
          <span class="daily-dish-badge">Aujourd'hui</span>
        </div>
      </div>
    `;
  }

  fetch(window.apiUrl('/api/daily-menu'))
    .then(r => {
      if (r.status === 204) return null; // Pas de menu aujourd'hui
      return r.json();
    })
    .then(menu => {
      if (!menu) {
        grid.innerHTML  = '';
        empty.style.display = 'flex';
        return;
      }

      // Chef note
      if (menu.chefNote) {
        chefTxt.textContent = menu.chefNote;
        chefBox.style.display = 'block';
      }

      // Plats
      const dishes = [
        { name: menu.dish1Name, description: menu.dish1Description, price: menu.dish1Price, origin: menu.dish1Origin },
        { name: menu.dish2Name, description: menu.dish2Description, price: menu.dish2Price, origin: menu.dish2Origin },
        { name: menu.dish3Name, description: menu.dish3Description, price: menu.dish3Price, origin: menu.dish3Origin },
      ].filter(d => d.name);

      grid.innerHTML = dishes.map(renderDish).join('');
      startMidnightTimer();
    })
    .catch(() => {
      grid.innerHTML = '';
      empty.style.display = 'flex';
    });
})();
