package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.dto.ItemStats;
import org.example.restaurantwebsite.dto.RevenueStats;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис для сбора и вычисления статистики по заказам ресторана.
 */
@Service
public class StatsService {

    /**
     * JdbcTemplate с поддержкой именованных параметров для выполнения SQL-запросов.
     */
    private final NamedParameterJdbcTemplate jdbc;

    public StatsService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * DTO, инкапсулирующий нижнюю и верхнюю границы диапазона дат.
     */
    public record DateBounds(LocalDate start, LocalDate end) {}

    /**
     * Преобразует текстовый параметр period в конкретные даты:
     * - "day"   → сегодня
     * - "week"  → 7 дней назад
     * - "month" → 1 месяц назад
     * - "all"   → от даты первого заказа до сегодня
     */
    public DateBounds resolveBounds(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period.toLowerCase()) {
            case "day"   -> end;
            case "week"  -> end.minusWeeks(1);
            case "month" -> end.minusMonths(1);
            case "all"   -> getFirstOrderDate();
            default -> throw new IllegalArgumentException("Unknown period: " + period);
        };
        return new DateBounds(start, end);
    }

    /**
     * Возвращает дату самого первого заказа в базе (используется для period="all").
     */
    public LocalDate getFirstOrderDate() {
        String sql = "SELECT MIN(DATE(order_time)) FROM orders";
        return jdbc.queryForObject(sql, Map.of(), LocalDate.class);
    }

    //--------------- SQL SCRIPTS -----------------

    /**
     * SQL для расчёта ежедневной выручки и числа заказов:
     * - группировка по дате заказа (YYYY-MM-DD)
     * - total_revenue = сумма(quantity * price)
     * - total_orders  = число уникальных заказов в этот день
     */
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

    /**
     * SQL для выбора топ-5 самых продаваемых блюд:
     * - суммирует по количеству в заказах
     * - сортировка по убыванию
     */
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

    /**
     * SQL для выбора топ-5 наименее продаваемых блюд:
     * - суммирует по количеству в заказах
     * - сортировка по возрастанию
     */
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

    //--------------- SERVICE METHODS -----------------

    /**
     * Возвращает время́вой ряд по дням: выручка и число заказов.
     */
    public List<RevenueStats> getRevenueStats(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(REVENUE_SQL, params, (rs, rn) ->
                new RevenueStats(
                        rs.getString("period"),
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("total_orders")
                )
        );
    }

    /**
     * Возвращает ТОП-5 самых популярных блюд за указанный период.
     */
    public List<ItemStats> getTopItems(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(POPULAR_ITEMS_SQL, params, (rs, rn) ->
                new ItemStats(
                        rs.getString("name"),
                        rs.getLong("totalQuantity")
                )
        );
    }

    /**
     * Возвращает ТОП-5 самых редко заказываемых блюд за указанный период.
     */
    public List<ItemStats> getRareItems(LocalDate start, LocalDate end) {
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(RARE_ITEMS_SQL, params, (rs, rn) ->
                new ItemStats(
                        rs.getString("name"),
                        rs.getLong("totalQuantity")
                )
        );
    }

    /**
     * 1) Средний чек за период:
     *    SUM(oi.quantity * mi.price) / COUNT(DISTINCT o.id)
     */
    public BigDecimal getAverageCheck(LocalDate start, LocalDate end) {
        String sql = """
            SELECT
              SUM(oi.quantity * mi.price) / COUNT(DISTINCT o.id)
            FROM orders o
              JOIN order_items oi ON o.id = oi.order_id
              JOIN menu_items mi ON oi.menu_item_id = mi.id
            WHERE o.order_time BETWEEN :start AND :end
            """;
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.queryForObject(sql, params, BigDecimal.class);
    }

    /**
     * 2) Количество заказов по дням:
     *    период – дата, total_orders – число заказов
     */
    public List<RevenueStats> getDailyOrders(LocalDate start, LocalDate end) {
        String sql = """
            SELECT
              DATE_FORMAT(o.order_time, '%Y-%m-%d') AS period,
              COUNT(DISTINCT o.id)                AS total_orders
            FROM orders o
            WHERE o.order_time BETWEEN :start AND :end
            GROUP BY period
            ORDER BY period
            """;
        var params = new MapSqlParameterSource()
                .addValue("start", start.atStartOfDay())
                .addValue("end",   end.plusDays(1).atStartOfDay());
        return jdbc.query(sql, params, (rs, rn) ->
                new RevenueStats(
                        rs.getString("period"),
                        BigDecimal.ZERO,            // выручка не заполняется в этом запросе
                        rs.getInt("total_orders")
                )
        );
    }
}



//// StatsService.java
//package org.example.restaurantwebsite.service;
//
//import org.example.restaurantwebsite.dto.ItemStats;
//import org.example.restaurantwebsite.dto.RevenueStats;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class StatsService {
//
//    private final NamedParameterJdbcTemplate jdbc;
//
//    public StatsService(NamedParameterJdbcTemplate jdbc) {
//        this.jdbc = jdbc;
//    }
//
//    /**
//     * DTO для границ периода
//     */
//    public record DateBounds(LocalDate start, LocalDate end) {}
//
//    /**
//     * Перевод period в конкретные даты start и end.
//     * Поддерживает "day", "week", "month", "all".
//     */
//    public DateBounds resolveBounds(String period) {
//        LocalDate end = LocalDate.now();
//        LocalDate start = switch (period.toLowerCase()) {
//            case "day"   -> end;
//            case "week"  -> end.minusWeeks(1);
//            case "month" -> end.minusMonths(1);
//            case "all"   -> getFirstOrderDate();
//            default -> throw new IllegalArgumentException("Unknown period: " + period);
//        };
//        return new DateBounds(start, end);
//    }
//
//    /**
//     * Дата самого первого заказа (для period=all)
//     */
//    public LocalDate getFirstOrderDate() {
//        String sql = "SELECT MIN(DATE(order_time)) FROM orders";
//        return jdbc.queryForObject(sql, Map.of(), LocalDate.class);
//    }
//
//    //--------------- SQL -----------------
//
//    private static final String REVENUE_SQL = """
//        SELECT
//          DATE_FORMAT(o.order_time, '%Y-%m-%d') AS period,
//          SUM(oi.quantity * mi.price)          AS total_revenue,
//          COUNT(DISTINCT o.id)                  AS total_orders
//        FROM orders o
//          JOIN order_items oi ON o.id = oi.order_id
//          JOIN menu_items mi  ON oi.menu_item_id = mi.id
//        WHERE o.order_time BETWEEN :start AND :end
//        GROUP BY period
//        ORDER BY period
//        """;
//
//    private static final String POPULAR_ITEMS_SQL = """
//        SELECT
//          mi.name          AS name,
//          SUM(oi.quantity) AS totalQuantity
//        FROM order_items oi
//          JOIN menu_items mi ON oi.menu_item_id = mi.id
//          JOIN orders o      ON oi.order_id      = o.id
//        WHERE o.order_time BETWEEN :start AND :end
//        GROUP BY mi.id, mi.name
//        ORDER BY totalQuantity DESC
//        LIMIT 5
//        """;
//
//    private static final String RARE_ITEMS_SQL = """
//        SELECT
//          mi.name          AS name,
//          SUM(oi.quantity) AS totalQuantity
//        FROM order_items oi
//          JOIN menu_items mi ON oi.menu_item_id = mi.id
//          JOIN orders o      ON oi.order_id      = o.id
//        WHERE o.order_time BETWEEN :start AND :end
//        GROUP BY mi.id, mi.name
//        ORDER BY totalQuantity ASC
//        LIMIT 5
//        """;
//
//    //--------------- Методы -----------------
//
//    /** Статистика выручки */
//    public List<RevenueStats> getRevenueStats(LocalDate start, LocalDate end) {
//        var params = new MapSqlParameterSource()
//                .addValue("start", start.atStartOfDay())
//                .addValue("end",   end.plusDays(1).atStartOfDay());
//        return jdbc.query(REVENUE_SQL, params, (rs, rn) -> new RevenueStats(
//                rs.getString("period"),
//                rs.getBigDecimal("total_revenue"),
//                rs.getInt("total_orders")
//        ));
//    }
//
//    /** Топ-5 самых популярных блюд */
//    public List<ItemStats> getTopItems(LocalDate start, LocalDate end) {
//        var params = new MapSqlParameterSource()
//                .addValue("start", start.atStartOfDay())
//                .addValue("end",   end.plusDays(1).atStartOfDay());
//        return jdbc.query(POPULAR_ITEMS_SQL, params, (rs, rn) -> new ItemStats(
//                rs.getString("name"),
//                rs.getLong("totalQuantity")
//        ));
//    }
//
//    /** Топ-5 самых редких блюд */
//    public List<ItemStats> getRareItems(LocalDate start, LocalDate end) {
//        var params = new MapSqlParameterSource()
//                .addValue("start", start.atStartOfDay())
//                .addValue("end",   end.plusDays(1).atStartOfDay());
//        return jdbc.query(RARE_ITEMS_SQL, params, (rs, rn) -> new ItemStats(
//                rs.getString("name"),
//                rs.getLong("totalQuantity")
//        ));
//    }
//    // В StatsService.java
//
//    // 1) Средний чек
//    public BigDecimal getAverageCheck(LocalDate start, LocalDate end) {
//        String sql = """
//        SELECT
//          SUM(oi.quantity * mi.price) / COUNT(DISTINCT o.id)
//        FROM orders o
//          JOIN order_items oi ON o.id = oi.order_id
//          JOIN menu_items mi ON oi.menu_item_id = mi.id
//        WHERE o.order_time BETWEEN :start AND :end
//    """;
//        var params = new MapSqlParameterSource()
//                .addValue("start", start.atStartOfDay())
//                .addValue("end",   end.plusDays(1).atStartOfDay());
//        return jdbc.queryForObject(sql, params, BigDecimal.class);
//    }
//
//    // 2) Заказы по дням
//    public List<RevenueStats> getDailyOrders(LocalDate start, LocalDate end) {
//        String sql = """
//        SELECT
//          DATE_FORMAT(o.order_time, '%Y-%m-%d') AS period,
//          COUNT(DISTINCT o.id) AS total_orders
//        FROM orders o
//        WHERE o.order_time BETWEEN :start AND :end
//        GROUP BY period
//        ORDER BY period
//    """;
//        var params = new MapSqlParameterSource()
//                .addValue("start", start.atStartOfDay())
//                .addValue("end",   end.plusDays(1).atStartOfDay());
//        return jdbc.query(sql, params, (rs, rn) ->
//                new RevenueStats(
//                        rs.getString("period"),
//                        BigDecimal.ZERO,            // выручку не заполняем
//                        rs.getInt("total_orders")
//                )
//        );
//    }
//
//}
