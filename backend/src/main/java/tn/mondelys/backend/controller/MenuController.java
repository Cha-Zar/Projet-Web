package tn.mondelys.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tn.mondelys.backend.dto.MenuDtos;
import tn.mondelys.backend.service.MenuService;

import java.util.List;
import java.util.Map;

@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/api/menu")
    public ResponseEntity<List<MenuDtos.MenuItemResponse>> publicMenu() {
        return ResponseEntity.ok(menuService.getPublicMenu());
    }

    @GetMapping("/api/admin/menu")
    public ResponseEntity<List<MenuDtos.MenuItemResponse>> adminMenu() {
        return ResponseEntity.ok(menuService.getAdminMenu());
    }

    @PostMapping("/api/admin/menu")
    public ResponseEntity<MenuDtos.MenuItemResponse> createMenuItem(
            @Valid @RequestBody MenuDtos.UpsertMenuItemRequest request
    ) {
        return ResponseEntity.ok(menuService.createMenuItem(request));
    }

    @PutMapping("/api/admin/menu/{id}")
    public ResponseEntity<MenuDtos.MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuDtos.UpsertMenuItemRequest request
    ) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, request));
    }

    @DeleteMapping("/api/admin/menu/{id}")
    public ResponseEntity<Map<String, String>> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(Map.of("message", "Element supprime du menu."));
    }
}
