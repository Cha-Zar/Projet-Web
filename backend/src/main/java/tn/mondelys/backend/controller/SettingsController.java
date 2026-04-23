package tn.mondelys.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tn.mondelys.backend.dto.DailyMenuDtos;
import tn.mondelys.backend.dto.SettingsDtos;
import tn.mondelys.backend.service.DailyMenuService;
import tn.mondelys.backend.service.SettingsService;

import java.util.Map;

@RestController
public class SettingsController {

    private final SettingsService settingsService;
    private final DailyMenuService dailyMenuService;

    public SettingsController(SettingsService settingsService, DailyMenuService dailyMenuService) {
        this.settingsService = settingsService;
        this.dailyMenuService = dailyMenuService;
    }

    @GetMapping("/api/settings/public")
    public ResponseEntity<SettingsDtos.SettingsResponse> publicSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @GetMapping("/api/admin/settings")
    public ResponseEntity<SettingsDtos.SettingsResponse> adminSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @GetMapping("/api/daily-menu")
    public ResponseEntity<DailyMenuDtos.DailyMenuResponse> publicDailyMenu() {
        return dailyMenuService.getTodayMenu()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/api/admin/daily-menu")
    public ResponseEntity<DailyMenuDtos.DailyMenuResponse> adminDailyMenu() {
        return dailyMenuService.getTodayMenu()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/api/admin/settings")
    public ResponseEntity<SettingsDtos.SettingsResponse> updateSettings(
            @Valid @RequestBody SettingsDtos.UpdateSettingsRequest request
    ) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }

    @PutMapping("/api/admin/daily-menu")
    public ResponseEntity<DailyMenuDtos.DailyMenuResponse> updateDailyMenu(
            @Valid @RequestBody DailyMenuDtos.UpsertDailyMenuRequest request
    ) {
        return ResponseEntity.ok(dailyMenuService.saveTodayMenu(request));
    }

    @PostMapping("/api/admin/settings/reset-operational-data")
    public ResponseEntity<Map<String, String>> resetOperationalData() {
        settingsService.resetOperationalData();
        return ResponseEntity.ok(Map.of("message", "Les réservations, messages et avis ont été supprimés."));
    }
}
