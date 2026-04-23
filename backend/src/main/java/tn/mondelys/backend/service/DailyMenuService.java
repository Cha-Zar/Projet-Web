package tn.mondelys.backend.service;

import org.springframework.stereotype.Service;
import tn.mondelys.backend.dto.DailyMenuDtos;
import tn.mondelys.backend.model.DailyMenu;
import tn.mondelys.backend.repository.DailyMenuRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class DailyMenuService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Africa/Tunis");

    private final DailyMenuRepository dailyMenuRepository;

    public DailyMenuService(DailyMenuRepository dailyMenuRepository) {
        this.dailyMenuRepository = dailyMenuRepository;
    }

    public Optional<DailyMenuDtos.DailyMenuResponse> getTodayMenu() {
        return dailyMenuRepository.findByMenuDate(today())
                .filter(this::hasVisibleContent)
                .map(DailyMenuDtos.DailyMenuResponse::from);
    }

    public DailyMenuDtos.DailyMenuResponse saveTodayMenu(DailyMenuDtos.UpsertDailyMenuRequest request) {
        LocalDate today = today();
        DailyMenu menu = dailyMenuRepository.findByMenuDate(today).orElseGet(DailyMenu::new);
        menu.setMenuDate(today);
        menu.setChefNote(normalize(request.getChefNote()));
        menu.setDish1Name(normalize(request.getDish1Name()));
        menu.setDish1Description(normalize(request.getDish1Description()));
        menu.setDish1Price(normalize(request.getDish1Price()));
        menu.setDish1Origin(normalize(request.getDish1Origin()));
        menu.setDish2Name(normalize(request.getDish2Name()));
        menu.setDish2Description(normalize(request.getDish2Description()));
        menu.setDish2Price(normalize(request.getDish2Price()));
        menu.setDish2Origin(normalize(request.getDish2Origin()));
        menu.setDish3Name(normalize(request.getDish3Name()));
        menu.setDish3Description(normalize(request.getDish3Description()));
        menu.setDish3Price(normalize(request.getDish3Price()));
        menu.setDish3Origin(normalize(request.getDish3Origin()));

        return DailyMenuDtos.DailyMenuResponse.from(dailyMenuRepository.save(menu));
    }

    private LocalDate today() {
        return LocalDate.now(BUSINESS_ZONE);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasVisibleContent(DailyMenu menu) {
        return menu.getChefNote() != null
                || menu.getDish1Name() != null
                || menu.getDish2Name() != null
                || menu.getDish3Name() != null;
    }
}
