const MENU_ADMIN_CUISINES = [
  { value: "JAPON", label: "Japon" },
  { value: "MEXIQUE", label: "Mexique" },
  { value: "ITALIE", label: "Italie" },
  { value: "ORIENTALE", label: "Orientale" },
  { value: "DESSERTS", label: "Desserts" },
];

let adminMenuItems = [];
let editingMenuItemId = null;
let localMenuPreviewUrl = null;

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("menuForm");
  if (!form) return;

  ensureAdminAuthenticated();
  hydrateCuisineOptions();
  bindMenuAdminEvents();
  resetMenuForm();
  loadAdminMenuItems();
});

function hydrateCuisineOptions() {
  const optionsHtml = MENU_ADMIN_CUISINES.map(
    (option) => `<option value="${option.value}">${option.label}</option>`,
  ).join("");

  const select = document.getElementById("menuCuisine");
  if (select) {
    select.innerHTML = optionsHtml;
  }

  const filter = document.getElementById("menuCuisineFilter");
  if (filter) {
    filter.innerHTML = '<option value="">Toutes les cuisines</option>' + optionsHtml;
  }
}

function bindMenuAdminEvents() {
  document.getElementById("menuForm").addEventListener("submit", saveMenuItem);
  document.getElementById("resetMenuFormBtn").addEventListener("click", resetMenuForm);
  document.getElementById("menuSearch").addEventListener("input", renderAdminMenuCards);
  document.getElementById("menuCuisineFilter").addEventListener("change", renderAdminMenuCards);
  document.getElementById("menuImageUrl").addEventListener("input", handleImageUrlInput);
  document.getElementById("menuBadges").addEventListener("input", renderBadgePreview);
  document.getElementById("newMenuItemBtn").addEventListener("click", openFreshMenuForm);
  document.getElementById("refreshMenuBtn").addEventListener("click", loadAdminMenuItems);
  document.getElementById("chooseMenuImageBtn").addEventListener("click", openLocalImagePicker);
  document.getElementById("menuImagePickerTrigger").addEventListener("click", openLocalImagePicker);
  document.getElementById("menuImageFile").addEventListener("change", handleLocalImageSelection);
  document.getElementById("closeMenuFormBtn").addEventListener("click", closeMenuForm);
  document.getElementById("menuFormBackdrop").addEventListener("click", closeMenuForm);
  document.addEventListener("keydown", handleMenuFormEscape);
}

async function loadAdminMenuItems() {
  const container = document.getElementById("menuAdminCards");
  container.innerHTML = renderCardSkeletons(6);

  try {
    adminMenuItems = await adminFetch("/api/admin/menu");
    renderAdminMenuCards();
    renderAdminStats();
  } catch (error) {
    container.innerHTML = `
      <div class="menu-admin-empty">
        <h3>Impossible de charger les articles</h3>
        <p>${escapeHtml(error.message || "Une erreur est survenue.")}</p>
      </div>
    `;
  }
}

function renderCardSkeletons(count) {
  return Array.from({ length: count }, () => `
    <article class="menu-admin-card loading">
      <div class="menu-admin-card-image skel"></div>
      <div class="menu-admin-card-body">
        <div class="menu-admin-line skel w-70"></div>
        <div class="menu-admin-line skel w-40"></div>
        <div class="menu-admin-line skel w-90"></div>
      </div>
    </article>
  `).join("");
}

function renderAdminMenuCards() {
  const container = document.getElementById("menuAdminCards");
  const search = document.getElementById("menuSearch").value.trim().toLowerCase();
  const cuisineFilter = document.getElementById("menuCuisineFilter").value;

  const filteredItems = adminMenuItems.filter((item) => {
    const matchesCuisine = !cuisineFilter || item.cuisine === cuisineFilter;
    const haystack = [
      item.name,
      item.description,
      item.location,
      item.price,
      ...(item.badges || []),
    ]
      .join(" ")
      .toLowerCase();

    const matchesSearch = !search || haystack.includes(search);
    return matchesCuisine && matchesSearch;
  });

  document.getElementById("menuResultsCount").textContent =
    `${filteredItems.length} article(s) affiche(s) sur ${adminMenuItems.length}`;

  if (!filteredItems.length) {
    container.innerHTML = `
      <div class="menu-admin-empty">
        <h3>Aucun article trouve</h3>
        <p>Essayez une autre recherche ou ajoutez un nouvel article.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = filteredItems
    .map((item) => {
      const activeClass = item.active ? "is-active" : "is-hidden";
      const activeLabel = item.active ? "Visible" : "Masque";
      const toggleLabel = item.active ? "Rendre non visible" : "Rendre visible";
      const toggleIcon = item.active ? "fa-eye-slash" : "fa-eye";
      return `
        <article class="menu-admin-card">
          <div class="menu-admin-card-image-wrap">
            <img class="menu-admin-card-image" src="${escapeAttribute(resolveAdminImageUrl(item.imageUrl))}" alt="${escapeAttribute(item.name)}" />
            <span class="menu-admin-visibility ${activeClass}">${activeLabel}</span>
          </div>
          <div class="menu-admin-card-body">
            <div class="menu-admin-card-top">
              <div>
                <p class="menu-admin-card-kicker">${escapeHtml(item.cuisineLabel || item.cuisine)}</p>
                <h3>${escapeHtml(item.name)}</h3>
              </div>
              <span class="menu-admin-order">#${item.displayOrder ?? 0}</span>
            </div>
            <p class="menu-admin-price">${escapeHtml(renderAdminPrice(item))}</p>
            <p class="menu-admin-desc">${escapeHtml(item.description || "")}</p>
            ${
              item.location
                ? `<p class="menu-admin-location"><i class="fas fa-map-marker-alt"></i> ${escapeHtml(item.location)}</p>`
                : ""
            }
            ${
              item.badges && item.badges.length
                ? `<div class="menu-admin-tags">${item.badges
                    .map((badge) => `<span>${escapeHtml(badge)}</span>`)
                    .join("")}</div>`
                : ""
            }
            <div class="menu-admin-actions">
              <button class="btn-sm visibility-toggle ${activeClass}" type="button" onclick="toggleMenuVisibility(${item.id})">
                <i class="fas ${toggleIcon}" style="margin-right:5px;"></i>${toggleLabel}
              </button>
              <button class="btn-sm outline" type="button" onclick="editMenuItem(${item.id})">
                <i class="fas fa-pen" style="margin-right:5px;"></i>Modifier
              </button>
              <button class="btn-sm danger-soft" type="button" onclick="removeMenuItem(${item.id})">
                <i class="fas fa-trash" style="margin-right:5px;"></i>Supprimer
              </button>
            </div>
          </div>
        </article>
      `;
    })
    .join("");
}

function renderAdminStats() {
  const visibleItems = adminMenuItems.filter((item) => item.active).length;
  const hiddenItems = adminMenuItems.length - visibleItems;
  const activeCuisines = new Set(adminMenuItems.map((item) => item.cuisine)).size;

  document.getElementById("menuStatTotal").textContent = String(adminMenuItems.length);
  document.getElementById("menuStatVisible").textContent = String(visibleItems);
  document.getElementById("menuStatHidden").textContent = String(hiddenItems);
  document.getElementById("menuStatCuisines").textContent = String(activeCuisines);
}

async function saveMenuItem(event) {
  event.preventDefault();

  const payload = collectMenuFormPayload();
  const isEditing = editingMenuItemId !== null;
  const url = isEditing ? `/api/admin/menu/${editingMenuItemId}` : "/api/admin/menu";
  const method = isEditing ? "PUT" : "POST";

  try {
    await adminFetch(url, {
      method,
      body: JSON.stringify(payload),
    });

    showToast(isEditing ? "Article mis a jour." : "Article ajoute au menu.", true);
    await loadAdminMenuItems();
    resetMenuForm();
    closeMenuForm();
  } catch (error) {
    showToast(error.message || "Impossible d'enregistrer l'article.", false);
  }
}

function editMenuItem(id) {
  const item = adminMenuItems.find((entry) => entry.id === id);
  if (!item) return;

  editingMenuItemId = item.id;
  clearLocalPreview();
  document.getElementById("menuFormTitle").textContent = "Modifier un article";
  document.getElementById("menuSubmitLabel").textContent = "Mettre a jour";
  document.getElementById("menuName").value = item.name || "";
  document.getElementById("menuCuisine").value = item.cuisine || "JAPON";
  document.getElementById("menuLocation").value = item.location || "";
  document.getElementById("menuPrice").value = item.price || "";
  document.getElementById("menuPieces").value = item.pieces || "";
  document.getElementById("menuDescription").value = item.description || "";
  document.getElementById("menuImageUrl").value = item.imageUrl || "";
  document.getElementById("menuBadges").value = (item.badges || []).join(", ");
  document.getElementById("menuDisplayOrder").value = item.displayOrder ?? "";
  document.getElementById("menuActive").checked = Boolean(item.active);

  renderBadgePreview();
  updateImagePreview();
  openMenuForm();
}

async function removeMenuItem(id) {
  const item = adminMenuItems.find((entry) => entry.id === id);
  if (!item) return;

  const confirmed = window.confirm(`Supprimer "${item.name}" du menu ?`);
  if (!confirmed) return;

  try {
    await adminFetch(`/api/admin/menu/${id}`, { method: "DELETE" });
    showToast("Article supprime.", true);
    if (editingMenuItemId === id) {
      resetMenuForm();
    }
    await loadAdminMenuItems();
  } catch (error) {
    showToast(error.message || "Suppression impossible.", false);
  }
}

async function toggleMenuVisibility(id) {
  const item = adminMenuItems.find((entry) => entry.id === id);
  if (!item) return;

  const nextActive = !item.active;

  try {
    const updatedItem = await adminFetch(`/api/admin/menu/${id}`, {
      method: "PUT",
      body: JSON.stringify(buildMenuPayload(item, nextActive)),
    });

    adminMenuItems = adminMenuItems.map((entry) => (entry.id === id ? updatedItem : entry));
    if (editingMenuItemId === id) {
      document.getElementById("menuActive").checked = Boolean(updatedItem.active);
    }

    renderAdminMenuCards();
    renderAdminStats();
    showToast(
      nextActive ? "L'article est maintenant visible sur le site." : "L'article est maintenant masque cote client.",
      true,
    );
  } catch (error) {
    showToast(error.message || "Impossible de changer la visibilite.", false);
  }
}

function collectMenuFormPayload() {
  const displayOrderRaw = document.getElementById("menuDisplayOrder").value.trim();
  return {
    name: document.getElementById("menuName").value.trim(),
    cuisine: document.getElementById("menuCuisine").value,
    location: document.getElementById("menuLocation").value.trim(),
    price: document.getElementById("menuPrice").value.trim(),
    pieces: document.getElementById("menuPieces").value.trim(),
    description: document.getElementById("menuDescription").value.trim(),
    imageUrl: document.getElementById("menuImageUrl").value.trim(),
    badges: parseBadgeInput(document.getElementById("menuBadges").value),
    displayOrder: displayOrderRaw ? Number(displayOrderRaw) : null,
    active: document.getElementById("menuActive").checked,
  };
}

function buildMenuPayload(item, activeOverride = item.active) {
  return {
    name: item.name || "",
    cuisine: item.cuisine || "JAPON",
    location: item.location || "",
    price: item.price || "",
    pieces: item.pieces || "",
    description: item.description || "",
    imageUrl: item.imageUrl || "",
    badges: Array.isArray(item.badges) ? item.badges : [],
    displayOrder: item.displayOrder ?? null,
    active: Boolean(activeOverride),
  };
}

function resetMenuForm() {
  editingMenuItemId = null;
  clearLocalPreview();
  document.getElementById("menuForm").reset();
  document.getElementById("menuFormTitle").textContent = "Ajouter un article";
  document.getElementById("menuSubmitLabel").textContent = "Publier";
  document.getElementById("menuCuisine").value = "JAPON";
  document.getElementById("menuActive").checked = true;
  document.getElementById("menuImageFile").value = "";
  renderBadgePreview();
  updateImagePreview();
}

function openFreshMenuForm() {
  resetMenuForm();
  openMenuForm();
}

function openMenuForm() {
  document.body.classList.add("menu-form-open");
  document.getElementById("menuFormCard").classList.add("open");
  document.getElementById("menuFormBackdrop").classList.add("open");
  document.getElementById("menuFormCard").scrollTop = 0;
  document.getElementById("menuName").focus();
}

function closeMenuForm() {
  document.body.classList.remove("menu-form-open");
  document.getElementById("menuFormCard").classList.remove("open");
  document.getElementById("menuFormBackdrop").classList.remove("open");
}

function handleMenuFormEscape(event) {
  if (event.key === "Escape") {
    closeMenuForm();
  }
}

function renderBadgePreview() {
  const container = document.getElementById("badgePreview");
  const badges = parseBadgeInput(document.getElementById("menuBadges").value);

  if (!badges.length) {
    container.innerHTML = '<span class="badge-preview-empty">Les badges apparaitront ici.</span>';
    return;
  }

  container.innerHTML = badges.map((badge) => `<span>${escapeHtml(badge)}</span>`).join("");
}

function updateImagePreview() {
  const image = document.getElementById("menuImagePreview");
  const empty = document.getElementById("menuImagePreviewEmpty");
  const value = document.getElementById("menuImageUrl").value.trim();

  if (localMenuPreviewUrl) {
    image.src = localMenuPreviewUrl;
    image.style.display = "block";
    empty.style.display = "none";
    return;
  }

  if (!value) {
    image.style.display = "none";
    empty.style.display = "flex";
    return;
  }

  image.src = resolveAdminImageUrl(value);
  image.style.display = "block";
  empty.style.display = "none";
}

function openLocalImagePicker() {
  document.getElementById("menuImageFile").click();
}

function handleImageUrlInput() {
  clearLocalPreview();
  updateImagePreview();
}

function handleLocalImageSelection(event) {
  const file = event.target.files && event.target.files[0];
  if (!file) {
    return;
  }

  clearLocalPreview();
  localMenuPreviewUrl = URL.createObjectURL(file);

  const imageInput = document.getElementById("menuImageUrl");
  if (!imageInput.value.trim()) {
    imageInput.value = `images/${file.name}`;
  }

  updateImagePreview();
  showToast(
    "Apercu local charge. Pour l'affichage final, gardez aussi un chemin valide ou copiez le fichier dans le dossier images.",
    true,
  );
}

function clearLocalPreview() {
  if (!localMenuPreviewUrl) {
    return;
  }

  URL.revokeObjectURL(localMenuPreviewUrl);
  localMenuPreviewUrl = null;
}

function parseBadgeInput(value) {
  return String(value || "")
    .split(/[,|]/)
    .map((part) => part.trim())
    .filter(Boolean);
}

function renderAdminPrice(item) {
  return item.pieces ? `${item.price} (${item.pieces})` : item.price || "";
}

function resolveAdminImageUrl(value) {
  const fallback = "images/cafe.jpg";
  const normalized = String(value || "").trim().replace(/\\/g, "/");

  if (!normalized) return fallback;
  if (/^https?:\/\//i.test(normalized)) return normalized;
  if (normalized.startsWith("/")) return normalized;
  return normalized.replace(/^\.?\//, "");
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
