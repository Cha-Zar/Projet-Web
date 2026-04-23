/* ═══════════════════════════════════════════════════════════════════════════
   mondelys-utils.js
   Utilitaires partagés entre toutes les pages Mondélys :
     1. Toast — notifications élégantes (remplace tous les alert())
     2. Validator — validation temps-réel champ par champ (rouge / vert)
     3. Skeleton — squelettes de chargement animés pour les avis
     4. LazyImage — lazy-loading avec placeholder blur
   ═══════════════════════════════════════════════════════════════════════════ */

/* ──────────────────────────────────────────────────────────────────────────
   1. TOAST SYSTEM
   ──────────────────────────────────────────────────────────────────────────
   Flux :
     Toast.show(message, type)
       → crée un <div class="toast toast-{type}"> dans #toast-container
       → anime l'entrée (slideIn) puis la sortie (fadeOut) après `duration` ms
       → supprime le nœud DOM après la transition CSS

   Types disponibles : 'success' | 'error' | 'info' | 'warning'

   CSS important :
     .toast-container  → position: fixed, coin inférieur-droit, z-index élevé
     .toast            → transform + opacity pour les animations d'entrée/sortie
     .toast.hide       → déclenche la transition de sortie
   ────────────────────────────────────────────────────────────────────────── */
const Toast = (() => {
  /* Injecte le CSS du système de toasts directement dans le <head>.
     Cela évite un fichier CSS supplémentaire et garantit que les styles
     sont disponibles avant le premier rendu. */
  const style = document.createElement("style");
  style.textContent = `
    /* ── Conteneur : empilage vertical en bas à droite ── */
    #toast-container {
      position: fixed;
      bottom: 1.5rem;
      right: 1.5rem;
      z-index: 99999;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: none;         /* ne bloque pas les clics derrière */
    }

    /* ── Toast de base ── */
    .toast {
      pointer-events: auto;
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 14px 18px;
      border-radius: 10px;
      background: #1a1208;
      color: #f0ece0;
      font-family: 'Raleway', sans-serif;
      font-size: 14px;
      line-height: 1.45;
      max-width: 340px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.28), 0 0 0 1px rgba(255,255,255,0.06);
      /* Entrée : glisse depuis la droite */
      transform: translateX(0) translateY(0);
      opacity: 1;
      transition: transform 0.45s cubic-bezier(0.22, 1, 0.36, 1),
                  opacity  0.45s cubic-bezier(0.22, 1, 0.36, 1);
    }

    /* ── État initial (avant l'insertion → permet l'animation d'entrée) ── */
    .toast.entering {
      transform: translateX(110%);
      opacity: 0;
    }

    /* ── État de sortie ── */
    .toast.hide {
      transform: translateX(110%);
      opacity: 0;
    }

    /* ── Variantes de couleur via la bordure gauche ── */
    .toast-success { border-left: 3px solid #4caf7d; }
    .toast-error   { border-left: 3px solid #e05252; }
    .toast-info    { border-left: 3px solid #c9a84c; }
    .toast-warning { border-left: 3px solid #e09a3a; }

    /* ── Icône colorée selon le type ── */
    .toast-icon {
      font-size: 1.1rem;
      flex-shrink: 0;
      margin-top: 1px;
    }
    .toast-success .toast-icon { color: #4caf7d; }
    .toast-error   .toast-icon { color: #e05252; }
    .toast-info    .toast-icon { color: #c9a84c; }
    .toast-warning .toast-icon { color: #e09a3a; }

    /* ── Bouton de fermeture manuel ── */
    .toast-close {
      margin-left: auto;
      background: none;
      border: none;
      color: rgba(240,236,224,0.45);
      cursor: pointer;
      font-size: 16px;
      line-height: 1;
      padding: 0 0 0 8px;
      flex-shrink: 0;
      transition: color 0.2s;
    }
    .toast-close:hover { color: rgba(240,236,224,0.9); }
  `;
  document.head.appendChild(style);

  /* Crée le conteneur une seule fois dans le DOM */
  let container = document.getElementById("toast-container");
  if (!container) {
    container = document.createElement("div");
    container.id = "toast-container";
    document.body.appendChild(container);
  }

  /* Icônes Font Awesome pour chaque type */
  const icons = {
    success: "fa-check-circle",
    error: "fa-times-circle",
    info: "fa-info-circle",
    warning: "fa-exclamation-triangle",
  };

  /**
   * Affiche un toast.
   * @param {string} message   - Texte à afficher
   * @param {'success'|'error'|'info'|'warning'} [type='info'] - Type visuel
   * @param {number}  [duration=4000] - Durée d'affichage en ms
   */
  function show(message, type = "info", duration = 4000) {
    const toast = document.createElement("div");
    toast.className = `toast toast-${type} entering`; // 'entering' → position hors écran
    toast.innerHTML = `
      <i class="fas ${icons[type] || icons.info} toast-icon"></i>
      <span>${message}</span>
      <button class="toast-close" aria-label="Fermer">✕</button>
    `;

    container.appendChild(toast);

    /* Force un reflow pour que la transition CSS soit jouée
       (sinon le navigateur "skippe" l'état 'entering') */
    toast.getBoundingClientRect();

    /* Déclenche l'animation d'entrée */
    toast.classList.remove("entering");

    /* Fermeture manuelle */
    toast
      .querySelector(".toast-close")
      .addEventListener("click", () => dismiss(toast));

    /* Fermeture automatique après `duration` ms */
    const timer = setTimeout(() => dismiss(toast), duration);

    /* Si l'utilisateur survole, on suspend le timer */
    toast.addEventListener("mouseenter", () => clearTimeout(timer));
    toast.addEventListener("mouseleave", () =>
      setTimeout(() => dismiss(toast), 1500),
    );
  }

  /**
   * Anime la sortie puis supprime le nœud du DOM.
   * @param {HTMLElement} toast
   */
  function dismiss(toast) {
    toast.classList.add("hide");
    /* Supprime après la fin de la transition CSS (450 ms) */
    toast.addEventListener("transitionend", () => toast.remove(), {
      once: true,
    });
  }

  return { show };
})();

/* ──────────────────────────────────────────────────────────────────────────
   2. VALIDATOR — Validation temps-réel
   ──────────────────────────────────────────────────────────────────────────
   Flux :
     Validator.attach(formEl, rulesMap)
       → Pour chaque champ, attache les événements 'input', 'blur', 'change'
       → À chaque événement, appelle validate(field, rule)
       → Ajoute / retire la classe CSS 'field-valid' ou 'field-invalid'
       → Affiche / masque un message d'erreur dans .field-feedback

   Validator.isFormValid(formEl, rulesMap)
       → Valide tous les champs d'un coup (pour la soumission)
       → Retourne un booléen

   CSS important :
     .form-group.field-valid   → bordure verte + icône ✓
     .form-group.field-invalid → bordure rouge + icône ✗ + message d'erreur
     .field-feedback           → message d'erreur visible uniquement en invalid
   ────────────────────────────────────────────────────────────────────────── */
const Validator = (() => {
  /* Injection CSS */
  const style = document.createElement("style");
  style.textContent = `
    /* ── Feedback container (créé dynamiquement sous le champ) ── */
    .field-feedback {
      font-family: 'Raleway', sans-serif;
      font-size: 12px;
      margin-top: 4px;
      height: 0;                        /* collapsed par défaut */
      overflow: hidden;
      opacity: 0;
      transition: height 0.25s ease, opacity 0.25s ease;
    }

    /* ── Transitions sur les inputs ── */
    .form-group input,
    .form-group select,
    .form-group textarea {
      transition: border-color 0.3s ease, box-shadow 0.3s ease, transform 0.25s ease !important;
    }

    /* ── État valide ── */
    .form-group.field-valid input,
    .form-group.field-valid select,
    .form-group.field-valid textarea {
      border-color: #4caf7d !important;
      box-shadow: 0 0 0 2px rgba(76,175,125,0.15) !important;
    }
    .form-group.field-valid .field-feedback {
      color: #4caf7d;
      height: 18px;
      opacity: 1;
    }

    /* ── État invalide ── */
    .form-group.field-invalid input,
    .form-group.field-invalid select,
    .form-group.field-invalid textarea {
      border-color: #e05252 !important;
      box-shadow: 0 0 0 2px rgba(224,82,82,0.15) !important;
    }
    .form-group.field-invalid .field-feedback {
      color: #e05252;
      height: 18px;
      opacity: 1;
    }

    /* ── Animation shake sur invalid (déclenché via JS) ── */
    @keyframes shake {
      0%,100% { transform: translateX(0); }
      20%     { transform: translateX(-5px); }
      40%     { transform: translateX(5px); }
      60%     { transform: translateX(-4px); }
      80%     { transform: translateX(4px); }
    }
    .form-group.shake {
      animation: shake 0.35s cubic-bezier(0.36, 0.07, 0.19, 0.97);
    }
  `;
  document.head.appendChild(style);

  /* ── Fonctions de règles de validation ── */
  const rules = {
    required: (v) => v.trim() !== "" || "Ce champ est obligatoire.",
    email: (v) =>
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || "Adresse email invalide.",
    phone: (v) =>
      v === "" ||
      /^[+\d\s()-]{7,20}$/.test(v) ||
      "Numéro de téléphone invalide.",
    minLen: (n) => (v) => v.trim().length >= n || `Minimum ${n} caractères.`,
    select: (v) => v !== "" || "Veuillez faire un choix.",
    checked: (v, el) => el.checked || "Vous devez accepter les conditions.",
  };

  /**
   * Valide un seul champ et met à jour son état visuel.
   * @returns {boolean}
   */
  function validateField(fieldEl, fieldRules, showFeedback = true) {
    const group = fieldEl.closest(".form-group");
    if (!group) return true;

    /* Créé le conteneur de feedback s'il n'existe pas encore */
    let feedback = group.querySelector(".field-feedback");
    if (!feedback) {
      feedback = document.createElement("div");
      feedback.className = "field-feedback";
      group.appendChild(feedback);
    }

    const value = fieldEl.value;
    let errorMsg = null;

    /* Parcourt toutes les règles définies pour ce champ */
    for (const rule of fieldRules) {
      const result =
        typeof rule === "function"
          ? rule(value, fieldEl)
          : rules[rule]?.(value, fieldEl);

      if (result !== true) {
        errorMsg = result || "Champ invalide.";
        break;
      }
    }

    if (showFeedback) {
      group.classList.remove("field-valid", "field-invalid");
      if (errorMsg) {
        group.classList.add("field-invalid");
        feedback.textContent = "✗ " + errorMsg;
      } else {
        group.classList.add("field-valid");
        feedback.textContent = "✓ OK";
      }
    }

    return !errorMsg;
  }

  /**
   * Attache la validation temps-réel à un formulaire.
   * @param {HTMLFormElement} formEl
   * @param {Object} rulesMap  - ex: { email: ['required', 'email'] }
   */
  function attach(formEl, rulesMap) {
    Object.entries(rulesMap).forEach(([id, fieldRules]) => {
      const el = formEl.querySelector(`#${id}`);
      if (!el) return;

      /* Validation au blur (perte de focus) : feedback immédiat */
      el.addEventListener("blur", () => validateField(el, fieldRules));

      /* Validation en temps réel à chaque frappe (après le premier blur) */
      let touched = false;
      el.addEventListener("blur", () => {
        touched = true;
      });
      el.addEventListener("input", () => {
        if (touched) validateField(el, fieldRules);
      });
      el.addEventListener("change", () => validateField(el, fieldRules));
    });
  }

  /**
   * Valide tous les champs définis dans rulesMap.
   * Déclenche une animation shake sur les champs invalides.
   * @returns {boolean} true si tous valides
   */
  function isFormValid(formEl, rulesMap) {
    let allValid = true;
    Object.entries(rulesMap).forEach(([id, fieldRules]) => {
      const el = formEl.querySelector(`#${id}`);
      if (!el) return;
      const valid = validateField(el, fieldRules, true);
      if (!valid) {
        allValid = false;
        /* Shake + scroll vers le premier champ invalide */
        const group = el.closest(".form-group");
        if (group) {
          group.classList.add("shake");
          group.addEventListener(
            "animationend",
            () => group.classList.remove("shake"),
            { once: true },
          );
        }
      }
    });

    /* Scroll vers le premier champ en erreur */
    const firstInvalid = formEl.querySelector(
      ".field-invalid input, .field-invalid select, .field-invalid textarea",
    );
    if (firstInvalid)
      firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });

    return allValid;
  }

  return { attach, isFormValid, rules };
})();

/* ──────────────────────────────────────────────────────────────────────────
   3. SKELETON LOADERS
   ──────────────────────────────────────────────────────────────────────────
   Flux :
     Skeleton.showCards(containerEl, count)
       → Insère `count` cartes squelettes dans le conteneur
       → Chaque squelette mime la structure d'une vraie carte d'avis

     Skeleton.hide(containerEl)
       → Vide le conteneur (les vraies cartes prendront la place)

   CSS important :
     .skeleton-card → structure identique à .testi-card
     .skel-line     → blocs gris animés (shimmer)
     @keyframes shimmer → effet de brillance qui parcourt le bloc
   ────────────────────────────────────────────────────────────────────────── */
const Skeleton = (() => {
  const style = document.createElement("style");
  style.textContent = `
    /* ── Conteneur squelette : même mise en page que la grille d'avis ── */
    .skeleton-card {
      background: var(--white, #fff);
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 2px 12px rgba(0,0,0,0.06);
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    /* ── Bloc générique de squelette ── */
    .skel-line {
      border-radius: 6px;
      background: linear-gradient(
        90deg,
        rgba(201,168,76,0.08) 25%,   /* couleur de base (or très atténué) */
        rgba(201,168,76,0.18) 50%,   /* brillance centrale */
        rgba(201,168,76,0.08) 75%
      );
      background-size: 200% 100%;
      animation: skel-shimmer 1.6s ease-in-out infinite;
    }

    /* ── Tailles prédéfinies ── */
    .skel-title  { height: 14px; width: 45%; }
    .skel-text   { height: 11px; }
    .skel-text.w80 { width: 80%; }
    .skel-text.w60 { width: 60%; }
    .skel-avatar {
      width: 40px; height: 40px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .skel-author-row {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-top: 4px;
    }
    .skel-author-lines {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    /* ── L'animation shimmer : une vague lumineuse de gauche à droite ── */
    @keyframes skel-shimmer {
      0%   { background-position: 200% 0; }
      100% { background-position: -200% 0; }
    }
  `;
  document.head.appendChild(style);

  /**
   * Affiche N cartes squelettes dans le conteneur.
   * @param {HTMLElement} container
   * @param {number} count
   */
  function showCards(container, count = 6) {
    container.innerHTML = Array.from(
      { length: count },
      () => `
      <div class="skeleton-card" aria-hidden="true">
        <div class="skel-line skel-title"></div>
        <div class="skel-line skel-text w80"></div>
        <div class="skel-line skel-text w60"></div>
        <div class="skel-line skel-text w80"></div>
        <div class="skel-author-row">
          <div class="skel-line skel-avatar"></div>
          <div class="skel-author-lines">
            <div class="skel-line skel-text" style="width:55%"></div>
            <div class="skel-line skel-text" style="width:35%"></div>
          </div>
        </div>
      </div>
    `,
    ).join("");
  }

  /** Vide le conteneur (prêt pour le vrai contenu). */
  function hide(container) {
    container.innerHTML = "";
  }

  return { showCards, hide };
})();

/* ──────────────────────────────────────────────────────────────────────────
   4. LAZY IMAGE LOADING avec placeholder blur
   ──────────────────────────────────────────────────────────────────────────
   Flux :
     LazyImage.init()
       → Sélectionne toutes les <img data-src="...">
       → Observe chaque image avec IntersectionObserver
       → Quand l'image entre dans le viewport :
           1. Charge l'URL depuis data-src dans un objet Image() temporaire
           2. Quand chargé (onload) → swaps src + retire la classe .img-lazy
           3. Ajoute la classe .img-loaded → transition CSS (blur → net)

   CSS important :
     .img-lazy   → filtre flou + faible opacité (placeholder)
     .img-loaded → filtre retiré + pleine opacité (transition douce)

   Pour utiliser : ajouter data-src="url" et class="img-lazy" sur les <img>
   ────────────────────────────────────────────────────────────────────────── */
const LazyImage = (() => {
  const style = document.createElement("style");
  style.textContent = `
    /* ── État initial : image floue + semi-transparente ── */
    img.img-lazy {
      filter: blur(8px);
      opacity: 0.55;
      transition: filter 0.6s ease, opacity 0.6s ease;
      /* Evite le layout shift en réservant l'espace */
      background: linear-gradient(135deg, rgba(201,168,76,0.1), rgba(201,168,76,0.05));
    }

    /* ── État chargé : image nette ── */
    img.img-loaded {
      filter: blur(0);
      opacity: 1;
    }
  `;
  document.head.appendChild(style);

  function init() {
    /* Sélectionne les images lazy (data-src présent) */
    const lazyImgs = document.querySelectorAll("img[data-src]");
    if (!lazyImgs.length) return;

    /* IntersectionObserver : déclenche le chargement quand l'image
       est à 50px du viewport (rootMargin) → évite le flash */
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;

          const img = entry.target;
          const src = img.dataset.src;
          if (!src) return;

          /* Précharge en mémoire */
          const tempImg = new Image();
          tempImg.onload = () => {
            img.src = src;
            img.removeAttribute("data-src");
            /* Petit délai pour que la transition soit visible */
            requestAnimationFrame(() => img.classList.add("img-loaded"));
          };
          tempImg.onerror = () => {
            /* En cas d'erreur, on affiche quand même l'image */
            img.src = src;
            img.classList.add("img-loaded");
          };
          tempImg.src = src;

          observer.unobserve(img);
        });
      },
      { rootMargin: "50px 0px" },
    );

    lazyImgs.forEach((img) => {
      img.classList.add("img-lazy");
      observer.observe(img);
    });
  }

  return { init };
})();

/* ──────────────────────────────────────────────────────────────────────────
   INITIALISATION GLOBALE
   ────────────────────────────────────────────────────────────────────────── */
document.addEventListener("DOMContentLoaded", () => {
  /* Lance le lazy loading sur toutes les pages */
  LazyImage.init();
});
