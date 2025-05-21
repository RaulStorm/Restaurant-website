// StatsService.java
package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.dto.ItemStats;
import org.example.restaurantwebsite.dto.RevenueStats;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис, который умеет резолвить границы по period и выполнять запросы.
 */
@Service
public class StatsService {

    private final NamedParameterJdbcTemplate jdbc;

    public StatsService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * DTO для границ периода
     */
    public record DateBounds(LocalDate start, LocalDate end) {}

    /**
     * Перевод period в конкретные даты start и end.
     */
    public DateBounds resolveBounds(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period.toLowerCase()) {
            case "day"   -> end;
            case "week"  -> end.minusWeeks(1);
            case "month" -> end.minusMonths(1);
            default -> throw new IllegalArgumentException("Unknown period: " + period);
        };
        return new DateBounds(start, end);
    }

    //--------------- SQL -----------------

    private static final String REVENUE_SQL = """
        SELECT
          DATE_FORMAT(o.order_time, '%Y-%m-%d') AS period,
          SUM(oi.quantity * mi.price)          AS total_revenue,
          COUNT(DISTINCT o.id)                  AS total_orders
        FROM orders o
          JOIN order_items oi ON o.id = oi.order_id
          JOIN menu_items mi  ON oi.menu_item_id = mi.id
        WHERE o.order_time BETWEEN :start AND :end
        GROUP BY period
        ORDER BY period
        """;

    private static final String POPULAR_ITEMS_SQL = """
        SELECT
          mi.name          AS name,
          SUM(oi.quantity) AS totalQuantity
        FROM order_items oi
          JOIN menu_items mi ON oi.menu_item_id = mi.id
          JOIN orders o      ON oi.order_id      = o.id
        WHERE o.order_time BETWEEN :start AND :end
        GROUP BY mi.id, mi.name
        ORDER BY totalQuantity DESC
        LIMIT 5
        """;

    private static final String RARE_ITEMS_SQL = """
        SELECT
          mi.name          AS name,
          SUM(oi.quantity) AS totalQuantity
        FROM order_items oi
          JOIN menu_items mi ON oi.menu_item_id = mi.id
          JOIN orders o      ON oi.order_id      = o.id
        WHERE o.order_time BETWEEN :start AND :end
        GROUP BY mi.id, mi.name
        ORDER BY totalQuantity ASC
        LIMIT 5
        """;

    //--------------- Методы -----------------

    public List<RevenueStats> getRevenueStats(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay()); // чтобы включить весь end-день
        return jdbc.query(REVENUE_SQL, params, (rs, rn) -> new RevenueStats(
                rs.getString("period"),
                rs.getBigDecimal("total_revenue"),
                rs.getInt("total_orders")
        ));
    }

    public List<ItemStats> getTopItems(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(POPULAR_ITEMS_SQL, params, (rs, rn) -> new ItemStats(
                rs.getString("name"),
                rs.getLong("totalQuantity")
        ));
    }

    public List<ItemStats> getRareItems(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(RARE_ITEMS_SQL, params, (rs, rn) -> new ItemStats(
                rs.getString("name"),
                rs.getLong("totalQuantity")
        ));
    }
}
