(function () {
  async function loadPublicSettings() {
    try {
      const response = await fetch(window.apiUrl('/api/settings/public'));
      if (!response.ok) return;
      const settings = await response.json();
      applySettings(settings);
    } catch (_) {
    }
  }

  function applySettings(settings) {
    if (!settings) return;

    const restaurantName = settings.restaurantName || 'Mondélys';
    document.querySelectorAll('.logo-main').forEach((el) => {
      el.textContent = restaurantName;
    });

    if (document.title && document.title.includes('–')) {
      const suffix = document.title.split('–').slice(1).join('–').trim();
      if (suffix) {
        document.title = `${restaurantName} – ${suffix}`;
      }
    }

    const phone = settings.publicPhone || '';
    const email = settings.publicEmail || '';
    const address = settings.address || '';

    if (phone) {
      document.querySelectorAll('a[href^="tel:"]').forEach((el) => {
        const normalized = phone.replace(/\s+/g, '');
        el.setAttribute('href', `tel:${normalized}`);
        el.textContent = phone;
      });

      document.querySelectorAll('.footer-contact p').forEach((p) => {
        if (p.textContent.includes('71 234 567')) {
          p.innerHTML = '<i class="fas fa-phone"></i> ' + phone;
        }
      });
    }

    if (email) {
      document.querySelectorAll('a[href^="mailto:"]').forEach((el) => {
        el.setAttribute('href', `mailto:${email}`);
        el.textContent = email;
      });

      document.querySelectorAll('.footer-contact p').forEach((p) => {
        if (p.textContent.toLowerCase().includes('@')) {
          p.innerHTML = '<i class="fas fa-envelope"></i> ' + email;
        }
      });
    }

    if (address) {
      document.querySelectorAll('.footer-contact p').forEach((p) => {
        if (p.textContent.includes('Avenue') || p.textContent.includes('Rue')) {
          p.innerHTML = '<i class="fas fa-map-marker-alt"></i> ' + address;
        }
      });
    }

    const weekHours = `${settings.lunchWeek || ''} / ${settings.dinnerWeek || ''}`.trim();
    const sundayHours = `${settings.lunchSunday || ''} / ${settings.dinnerSunday || ''}`.trim();

    const footerHours = document.querySelectorAll('.footer-hours p strong');
    if (footerHours.length >= 2) {
      footerHours[0].textContent = weekHours;
      footerHours[1].textContent = sundayHours;
    }

    const reservationHours = document.getElementById('reservationHoursValue');
    if (reservationHours) {
      reservationHours.innerHTML = `Mar–Ven : ${weekHours}<br/>Sam–Dim : ${sundayHours}`;
    }

    const reservationName = document.getElementById('reservationRestaurantName');
    if (reservationName) {
      reservationName.textContent = restaurantName;
    }
  }

  loadPublicSettings();
})();
