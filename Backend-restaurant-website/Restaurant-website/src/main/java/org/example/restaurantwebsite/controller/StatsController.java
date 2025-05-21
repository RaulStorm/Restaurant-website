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
     * Полная статистика (выручка, топ- и редкие блюда) за период.
     * Параметр period = day | week | month | all
     */
// В StatsController.java

    @GetMapping
    public Map<String,Object> fullStats(@RequestParam String period) {
        DateBounds b = statsService.resolveBounds(period);

        var revenue   = statsService.getRevenueStats(b.start(), b.end());
        var topItems  = statsService.getTopItems(b.start(), b.end());
        var rareItems = statsService.getRareItems(b.start(), b.end());
        var avgCheck  = statsService.getAverageCheck(b.start(), b.end());
        var daily     = statsService.getDailyOrders(b.start(), b.end());

        return Map.of(
                "revenue",     revenue,
                "topItems",    topItems,
                "rareItems",   rareItems,
                "averageCheck", avgCheck,
                "dailyOrders",  daily
        );
    }


    /** Отдельный endpoint только для выручки */
    @GetMapping("/revenue")
    public List<RevenueStats> revenue(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getRevenueStats(bounds.start(), bounds.end());
    }

    /** Отдельный endpoint только для топ-5 блюд */
    @GetMapping("/top-items")
    public List<ItemStats> topItems(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getTopItems(bounds.start(), bounds.end());
    }

    /** Отдельный endpoint только для редких блюд */
    @GetMapping("/rare-items")
    public List<ItemStats> rareItems(@RequestParam String period) {
        DateBounds bounds = statsService.resolveBounds(period);
        return statsService.getRareItems(bounds.start(), bounds.end());
    }
}
