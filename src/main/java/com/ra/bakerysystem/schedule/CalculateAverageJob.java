//package com.ra.bakerysystem.schedule;
//
//import com.ra.bakerysystem.common.AverageType;
//import com.ra.bakerysystem.model.entity.Product;
//import com.ra.bakerysystem.model.entity.SaleAverageCalculateEntity;
//import com.ra.bakerysystem.repository.OrderItemRepository;
//import com.ra.bakerysystem.repository.ProductRepository;
//import com.ra.bakerysystem.repository.SalesAverageCalculateRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.List;
//
//@EnableScheduling
//@Component
//@RequiredArgsConstructor
//@Log4j2
//public class CalculateAverageJob {
//
//    private final OrderItemRepository orderItemRepository;
////    private final SalesAverageCalculateRepository salesAverageCalculateRepository;
//    private final ProductRepository productRepository;
////    @Scheduled(cron = "${job.calculate.cron:0 0 0 * * ?}") // 00:00 mỗi ngày
//    public void calculateDailySnapshot() {
//        LocalDate day = LocalDate.now().minusDays(1);
//        log.info("🕛 Running SaleAverageCalculateEntityJob for {}", day);
//        List<Product> products = productRepository.findAllByActive(Boolean.TRUE);
//        for (Product product : products) {
//            try {
//                calculateDaily(product.getId(), day);
//                if (day.getDayOfMonth() == 1) {
//                    calculateMonth(product.getId(), day);
//                }
//
//                if (day.getDayOfYear() == 1) {
//                    calculateYear(product.getId(), day);
//                }
//
//            } catch (Exception ex) {
//                log.error("❌ Failed calculate snapshot product : {}", product.getName(), ex);
//            }
//        }
//    }
//    private void calculateDaily(Long productId, LocalDate day) {
//
//        int soldToday = orderItemRepository.getSoldByProductAndType(productId, day, AverageType.DAY.name() );
//
//        SaleAverageCalculateEntity prev = salesAverageCalculateRepository
//                .findLatestAverageByDate(productId, AverageType.DAY.name(), day.minusDays(1))
//                .orElse(null);
//
//        int totalQuantity;
//        int totalUnits;
//
//        if (prev == null) {
//            totalQuantity = soldToday;
//            totalUnits = 1;
//        } else {
//            totalQuantity = prev.getTotalQuantity() + soldToday;
//            totalUnits = prev.getTotalUnits() + 1;
//        }
//
//        SaleAverageCalculateEntity saleAverageCalculate = new SaleAverageCalculateEntity();
//        saleAverageCalculate.setProductId(productId);
//        saleAverageCalculate.setAvgType(AverageType.DAY);
//        saleAverageCalculate.setEffectiveDate(day.plusDays(1));
//        saleAverageCalculate.setTotalQuantity(totalQuantity);
//        saleAverageCalculate.setTotalUnits(totalUnits);
//        saleAverageCalculate.setAverageQuantity(totalQuantity / totalUnits);
//        salesAverageCalculateRepository.save(saleAverageCalculate);
//    }
//    private void calculateMonth(Long productId, LocalDate localDate) {
//
//        int totalSold = orderItemRepository.getSoldByProductAndType(productId, localDate,AverageType.MONTH.name());
//
//        save(productId, AverageType.MONTH, localDate, totalSold, localDate.getDayOfMonth());
//    }
//    private void calculateYear(Long productId, LocalDate localDate) {
//        int totalSold = orderItemRepository.getSoldByProductAndType(productId, localDate ,AverageType.YEAR.name());
//        save(productId, AverageType.YEAR, localDate, totalSold, localDate.getDayOfYear());
//    }
//    private void save(Long productId, AverageType type, LocalDate effectiveDate, int totalSold, int totalUnits) {
//        SaleAverageCalculateEntity SaleAverageCalculateEntity = new SaleAverageCalculateEntity();
//        SaleAverageCalculateEntity.setProductId(productId);
//        SaleAverageCalculateEntity.setAvgType(type);
//        SaleAverageCalculateEntity.setEffectiveDate(effectiveDate);
//        SaleAverageCalculateEntity.setTotalQuantity(totalSold);
//        SaleAverageCalculateEntity.setTotalUnits(totalUnits);
//        SaleAverageCalculateEntity.setAverageQuantity((int) Math.ceil((double) totalSold / totalUnits));
//         salesAverageCalculateRepository.save(SaleAverageCalculateEntity);
//    }
//}
