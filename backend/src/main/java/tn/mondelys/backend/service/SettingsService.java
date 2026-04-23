package tn.mondelys.backend.service;

import org.springframework.stereotype.Service;
import tn.mondelys.backend.dto.SettingsDtos;
import tn.mondelys.backend.model.AppSettings;
import tn.mondelys.backend.repository.AppSettingsRepository;
import tn.mondelys.backend.repository.ClientReviewRepository;
import tn.mondelys.backend.repository.ContactMessageRepository;
import tn.mondelys.backend.repository.ReservationRepository;

@Service
public class SettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final AppSettingsRepository appSettingsRepository;
    private final ReservationRepository reservationRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ClientReviewRepository clientReviewRepository;

    public SettingsService(AppSettingsRepository appSettingsRepository,
                           ReservationRepository reservationRepository,
                           ContactMessageRepository contactMessageRepository,
                           ClientReviewRepository clientReviewRepository) {
        this.appSettingsRepository = appSettingsRepository;
        this.reservationRepository = reservationRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.clientReviewRepository = clientReviewRepository;
    }

    public SettingsDtos.SettingsResponse getSettings() {
        return SettingsDtos.SettingsResponse.from(getOrCreate());
    }

    public SettingsDtos.SettingsResponse updateSettings(SettingsDtos.UpdateSettingsRequest request) {
        AppSettings settings = getOrCreate();
        settings.setRestaurantName(request.getRestaurantName().trim());
        settings.setPublicPhone(request.getPublicPhone().trim());
        settings.setPublicEmail(request.getPublicEmail().trim());
        settings.setAddress(request.getAddress().trim());
        settings.setDescription(request.getDescription().trim());
        settings.setLunchWeek(request.getLunchWeek().trim());
        settings.setDinnerWeek(request.getDinnerWeek().trim());
        settings.setLunchSunday(request.getLunchSunday().trim());
        settings.setDinnerSunday(request.getDinnerSunday().trim());

        return SettingsDtos.SettingsResponse.from(appSettingsRepository.save(settings));
    }

    public void resetOperationalData() {
        reservationRepository.deleteAllInBatch();
        contactMessageRepository.deleteAllInBatch();
        clientReviewRepository.deleteAllInBatch();
    }

    private AppSettings getOrCreate() {
        return appSettingsRepository.findById(SETTINGS_ID).orElseGet(() -> {
            AppSettings settings = new AppSettings();
            settings.setId(SETTINGS_ID);
            return appSettingsRepository.save(settings);
        });
    }
}
