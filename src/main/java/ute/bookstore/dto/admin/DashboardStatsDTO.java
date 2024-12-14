package ute.bookstore.dto.admin;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long totalShops;
    private Long totalBooks;
    private Long totalPromotions;
    private Long totalActiveShops;
    private Long totalPendingShops;
}
