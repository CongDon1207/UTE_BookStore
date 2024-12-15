package ute.bookstore.dto.admin;

import lombok.Data;

@Data
public class ShopRevenueStats {
    private String shopName;
    private RevenueByTime todayStats;
    private RevenueByTime weekStats;
    private RevenueByTime monthStats;
    private RevenueByTime totalStats;

    @Data
    public static class RevenueByTime {
        private Double revenue;
        private Long totalOrders;
        private Long deliveredOrders; 
        private Long cancelledOrders;
        private Double commission;
    }
}
