package tn.mondelys.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.mondelys.backend.dto.MenuDtos;
import tn.mondelys.backend.model.MenuItem;
import tn.mondelys.backend.repository.MenuItemRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class MenuService {

    private static final List<String> ALLOWED_CUISINES = List.of("JAPON", "MEXIQUE", "ITALIE", "ORIENTALE", "DESSERTS");

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuDtos.MenuItemResponse> getPublicMenu() {
        return menuItemRepository.findAll(Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("createdAt")))
                .stream()
                .filter(MenuItem::isActive)
                .sorted(menuComparator())
                .map(MenuDtos.MenuItemResponse::from)
                .toList();
    }

    public List<MenuDtos.MenuItemResponse> getAdminMenu() {
        return menuItemRepository.findAll(Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("createdAt")))
                .stream()
                .sorted(menuComparator())
                .map(MenuDtos.MenuItemResponse::from)
                .toList();
    }

    public MenuDtos.MenuItemResponse createMenuItem(MenuDtos.UpsertMenuItemRequest request) {
        MenuItem item = new MenuItem();
        applyRequest(item, request, true);
        return MenuDtos.MenuItemResponse.from(menuItemRepository.save(item));
    }

    public MenuDtos.MenuItemResponse updateMenuItem(Long id, MenuDtos.UpsertMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Element du menu introuvable"));

        applyRequest(item, request, false);
        return MenuDtos.MenuItemResponse.from(menuItemRepository.save(item));
    }

    public void deleteMenuItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Element du menu introuvable"));
        menuItemRepository.delete(item);
    }

    private void applyRequest(MenuItem item, MenuDtos.UpsertMenuItemRequest request, boolean isNew) {
        String cuisine = normalizeCuisine(request.getCuisine());
        boolean cuisineChanged = item.getCuisine() != null && !item.getCuisine().equals(cuisine);

        item.setName(normalizeRequired(request.getName()));
        item.setCuisine(cuisine);
        item.setLocation(normalizeOptional(request.getLocation()));
        item.setPrice(normalizeRequired(request.getPrice()));
        item.setPieces(normalizeOptional(request.getPieces()));
        item.setDescription(normalizeRequired(request.getDescription()));
        item.setImageUrl(normalizeImageUrl(request.getImageUrl()));
        item.setBadgeLabels(MenuDtos.encodeBadges(request.getBadges()));
        item.setActive(request.getActive() == null || request.getActive());

        if (request.getDisplayOrder() != null) {
            item.setDisplayOrder(request.getDisplayOrder());
        } else if (isNew || cuisineChanged) {
            item.setDisplayOrder(nextDisplayOrder(cuisine));
        } else if (item.getDisplayOrder() == null) {
            item.setDisplayOrder(nextDisplayOrder(cuisine));
        }
    }

    private String normalizeCuisine(String cuisine) {
        String normalized = normalizeRequired(cuisine).toUpperCase(Locale.ROOT);
        if (!ALLOWED_CUISINES.contains(normalized)) {
            throw new IllegalArgumentException("Cuisine invalide");
        }
        return normalized;
    }

    private String normalizeRequired(String value) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Un champ obligatoire du menu est vide");
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeImageUrl(String imageUrl) {
        String normalized = normalizeRequired(imageUrl).replace("\\", "/");
        if (!normalized.startsWith("http://")
                && !normalized.startsWith("https://")
                && !normalized.startsWith("/")
                && !normalized.startsWith("images/")) {
            normalized = "images/" + normalized;
        }
        return normalized;
    }

    private int nextDisplayOrder(String cuisine) {
        return menuItemRepository.findAllByCuisineIgnoreCase(cuisine).stream()
                .map(MenuItem::getDisplayOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(-1) + 1;
    }

    private Comparator<MenuItem> menuComparator() {
        return Comparator.<MenuItem>comparingInt(item -> cuisineRank(item.getCuisine()))
                .thenComparingInt(item -> item.getDisplayOrder() == null ? Integer.MAX_VALUE : item.getDisplayOrder())
                .thenComparing(MenuItem::getName, String.CASE_INSENSITIVE_ORDER);
    }

    private int cuisineRank(String cuisine) {
        int index = ALLOWED_CUISINES.indexOf(cuisine);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }
}
