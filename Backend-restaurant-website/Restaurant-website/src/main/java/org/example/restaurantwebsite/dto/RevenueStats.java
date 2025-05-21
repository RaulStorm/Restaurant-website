// RevenueStats.java
package org.example.restaurantwebsite.dto;

import java.math.BigDecimal;

public record RevenueStats(String period, BigDecimal totalRevenue, int totalOrders) {

}
