// StatsController.java
package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.dto.ItemStats;
import org.example.restaurantwebsite.dto.RevenueStats;
import org.example.restaurantwebsite.service.StatsService;
import org.example.restaurantwebsite.service.StatsService.DateBounds;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adm/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Возвращает полную статистику (выручка, топ-блюда, редкие блюда)
     * за период period = day | week | month.
     */
    @GetMapping
    public Map<String, Object> fullStats(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return Map.of(
                "revenue",   statsService.getRevenueStats(bounds.start(), bounds.end()),
                "topItems",  statsService.getTopItems(bounds.start(), bounds.end()),
                "rareItems", statsService.getRareItems(bounds.start(), bounds.end())
        );
    }

    /** Статистика выручки за указанный период */
    @GetMapping("/revenue")
    public List<RevenueStats> revenue(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getRevenueStats(bounds.start(), bounds.end());
    }

    /** Топ-5 самых часто заказываемых блюд */
    @GetMapping("/top-items")
    public List<ItemStats> topItems(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getTopItems(bounds.start(), bounds.end());
    }

    /** Топ-5 самых редко заказываемых блюд */
    @GetMapping("/rare-items")
    public List<ItemStats> rareItems(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getRareItems(bounds.start(), bounds.end());
    }
}
