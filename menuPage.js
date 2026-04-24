const MENU_SECTIONS = [
  {
    key: "JAPON",
    id: "japon",
    tabLabel: "Japon",
    sectionLabel: "Cuisine Japonaise",
    titleHtml: "L'Art de la <em>Precision</em>",
    description:
      "Saveurs du Japon a l'etat pur ~ sushi exquis, bouillons parfumes et techniques ancestrales perfectionnees.",
  },
  {
    key: "MEXIQUE",
    id: "mexique",
    tabLabel: "Mexique",
    sectionLabel: "Cuisine Mexicaine",
    titleHtml: "Sabores <em>Mexicanos</em>",
    description:
      "Voyage au coeur du Mexique ~ tortillas, sauces relevees, produits frais et couleurs vibrantes a chaque assiette.",
  },
  {
    key: "ITALIE",
    id: "italie",
    tabLabel: "Italie",
    sectionLabel: "Cuisine Italienne",
    titleHtml: "<em>Dolce Vita</em> Italiana",
    description:
      "Un voyage au coeur de l'Italie, entre pates artisanales, pizzas croustillantes et parfums mediterraneens.",
  },
  {
    key: "ORIENTALE",
    id: "orientale",
    tabLabel: '<span class="code">OR</span> Orientale',
    sectionLabel: '<span class="code">OR</span> Cuisine Orientale',
    titleHtml: "<em>Saveurs d'</em>Orient",
    description:
      "Voyage gourmand au coeur de l'Orient ~ des saveurs authentiques, parfumees et genereuses qui melangent tradition et partage.",
  },
  {
    key: "DESSERTS",
    id: "desserts",
    tabLabel: "Desserts",
    sectionLabel: "Desserts du Monde",
    titleHtml: "La <em>Douceur</em> des Continents",
    description: "Une finale sucree qui fait le tour du monde.",
  },
];

let menuIntersectionObserver = null;

document.addEventListener("DOMContentLoaded", () => {
  const tabsRoot = document.getElementById("menuTabs");
  const contentRoot = document.getElementById("menuContent");

  if (!tabsRoot || !contentRoot) {
    return;
  }

  renderTabs(tabsRoot);
  renderLoading(contentRoot);
  loadDynamicMenu();
});

async function loadDynamicMenu() {
  const contentRoot = document.getElementById("menuContent");
  if (!contentRoot) return;

  try {
    const response = await fetch(window.apiUrl ? window.apiUrl("/api/menu") : "/api/menu");
    if (!response.ok) {
      throw new Error("Impossible de charger le menu");
    }

    const items = await response.json();
    renderMenuSections(contentRoot, Array.isArray(items) ? items : []);
    bindMenuTabs();
    setupMenuReveal();
    syncTabMarker();
    window.addEventListener("resize", syncTabMarker);
  } catch (_) {
    renderLoadError(contentRoot);
    bindMenuTabs();
    syncTabMarker();
  }
}

function renderTabs(tabsRoot) {
  tabsRoot.innerHTML =
    MENU_SECTIONS.map(
      (section, index) =>
        `<button class="tab-btn${index === 0 ? " active" : ""}" data-tab="${section.id}">${section.tabLabel}</button>`,
    ).join("") + '<span class="tab-marker" id="tabMarker"></span>';
}

function renderLoading(contentRoot) {
  contentRoot.innerHTML = MENU_SECTIONS.map((section, index) => {
    return `
      <div class="menu-section${index === 0 ? " active" : ""}" id="${section.id}">
        ${renderSectionIntro(section)}
        <div class="menu-loading-grid">
          ${Array.from({ length: 4 }, () => renderLoadingCard()).join("")}
        </div>
      </div>
    `;
  }).join("");
}

function renderLoadError(contentRoot) {
  contentRoot.innerHTML = `
    <div class="menu-status-bar">
      <h3>Le menu n'a pas pu etre charge.</h3>
      <p>Verifiez la connexion avec l'API ou rechargez la page.</p>
    </div>
  `;
}

function renderMenuSections(contentRoot, items) {
  const groupedItems = groupMenuItems(items);

  contentRoot.innerHTML = MENU_SECTIONS.map((section, index) => {
    const sectionItems = groupedItems[section.key] || [];
    return `
      <div class="menu-section${index === 0 ? " active" : ""}" id="${section.id}">
        ${renderSectionIntro(section)}
        ${
          sectionItems.length
            ? `<div class="menu-grid">${sectionItems.map(renderMenuItemCard).join("")}</div>`
            : `
              <div class="menu-empty-state">
                <h3>Aucun article disponible</h3>
                <p>Cette categorie sera remplie des qu'un article sera publie depuis l'espace admin.</p>
              </div>
            `
        }
      </div>
    `;
  }).join("");
}

function renderSectionIntro(section) {
  return `
    <div class="menu-intro">
      <p class="section-label">${section.sectionLabel}</p>
      <h2 class="section-title">${section.titleHtml}</h2>
      <p>${escapeHtml(section.description)}</p>
    </div>
  `;
}

function renderLoadingCard() {
  return `
    <div class="menu-loading-card">
      <div class="menu-loading-photo"></div>
      <div class="menu-loading-line lg"></div>
      <div class="menu-loading-line sm"></div>
      <div class="menu-loading-line md"></div>
    </div>
  `;
}

function renderMenuItemCard(item) {
  const tags = Array.isArray(item.badges) ? item.badges : [];
  return `
    <div class="menu-item">
      <img src="${escapeAttribute(resolveImageUrl(item.imageUrl))}" alt="${escapeAttribute(item.name || "Menu item")}" loading="lazy" />
      <div class="menu-item-info">
        <div class="menu-item-header">
          <h3>${escapeHtml(item.name || "")}</h3>
          ${renderPriceLabels(item.price, item.pieces)}
        </div>
        <p class="menu-item-desc">${escapeHtml(item.description || "")}</p>
        ${
          item.location
            ? `<p class="menu-item-origin"><i class="fas fa-map-marker-alt"></i> ${escapeHtml(item.location)}</p>`
            : ""
        }
        ${
          tags.length
            ? `<div class="menu-item-tags">${tags
                .map((label) => `<span class="tag-badge ${resolveBadgeClass(label)}">${escapeHtml(label)}</span>`)
                .join("")}</div>`
            : ""
        }
      </div>
    </div>
  `;
}

function renderPriceLabels(price, pieces) {
  const priceParts = String(price || "")
    .split("|")
    .map((part) => part.trim())
    .filter(Boolean);

  if (!priceParts.length) {
    return "";
  }

  const cleanPieces = String(pieces || "").trim();
  return priceParts
    .map((part, index) => {
      const label = index === 0 && cleanPieces ? `${part} (${cleanPieces})` : part;
      return `<span class="menu-item-price">${escapeHtml(label)}</span>`;
    })
    .join("");
}

function bindMenuTabs() {
  const buttons = Array.from(document.querySelectorAll(".tab-btn"));
  const sections = Array.from(document.querySelectorAll(".menu-section"));

  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      const target = button.dataset.tab;
      buttons.forEach((entry) => entry.classList.remove("active"));
      sections.forEach((section) => section.classList.remove("active"));

      button.classList.add("active");
      const activeSection = document.getElementById(target);
      if (activeSection) {
        activeSection.classList.add("active");
      }

      syncTabMarker();
      setupMenuReveal();
    });
  });
}

function syncTabMarker() {
  const marker = document.getElementById("tabMarker");
  const activeButton = document.querySelector(".tab-btn.active");
  const tabsRoot = document.querySelector(".menu-tabs");

  if (!marker || !activeButton || !tabsRoot) {
    return;
  }

  const buttonRect = activeButton.getBoundingClientRect();
  const parentRect = tabsRoot.getBoundingClientRect();
  marker.style.left = `${buttonRect.left - parentRect.left}px`;
  marker.style.width = `${buttonRect.width}px`;
}

function setupMenuReveal() {
  if (menuIntersectionObserver) {
    menuIntersectionObserver.disconnect();
  }

  menuIntersectionObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.remove("scroll-hidden");
        entry.target.classList.add("scroll-visible");
        menuIntersectionObserver.unobserve(entry.target);
      });
    },
    { threshold: 0.12, rootMargin: "0px 0px -40px 0px" },
  );

  document.querySelectorAll(".menu-item").forEach((item) => {
    item.classList.add("scroll-hidden");
    item.classList.remove("scroll-visible");
    menuIntersectionObserver.observe(item);
  });
}

function groupMenuItems(items) {
  const groups = Object.fromEntries(MENU_SECTIONS.map((section) => [section.key, []]));

  items.forEach((item) => {
    if (!item || !groups[item.cuisine]) {
      return;
    }
    groups[item.cuisine].push(item);
  });

  return groups;
}

function resolveBadgeClass(label) {
  const value = String(label || "").toLowerCase();

  if (value.includes("nouveau") || value.includes("new")) return "tag-new";
  if (value.includes("releve") || value.includes("epice") || value.includes("spicy")) return "tag-epice";
  if (value.includes("veget") || value.includes("sucree")) return "tag-vege";
  if (value.includes("halal") || value.includes("sans gluten") || value.includes("poulpe") || value.includes("experience")) {
    return "tag-gf";
  }

  return "tag-chef";
}

function resolveImageUrl(imageUrl) {
  const fallback = "images/cafe.jpg";
  const value = String(imageUrl || "").trim();
  if (!value) return fallback;

  if (/^https?:\/\//i.test(value)) {
    return value;
  }

  const normalized = value.replace(/\\/g, "/");
  return normalized.startsWith("/") ? normalized : normalized.replace(/^\.?\//, "");
}

function escapeHtml(value) {
  return String(value || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function escapeAttribute(value) {
  return escapeHtml(value);
}
