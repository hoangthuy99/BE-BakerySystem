package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FactoryRequestRepository
        extends JpaRepository<FactoryRequest, Long> {
    @Query("""
        SELECT fr
        FROM FactoryRequest fr
        WHERE (:date IS NULL OR DATE(fr.createdAt) = :date)
          AND (:status IS NULL OR fr.status = :status)
    """)
    List<FactoryRequest> findByDateAndStatus(
        @Param("date") LocalDate date,
        @Param("status") FactoryRequestStatus status
    );

//    @Query("""
//        SELECT fr
//        FROM FactoryRequest fr
//        WHERE (:date IS NULL OR DATE(fr.createdAt) = :date)
//          AND (:status IS NULL OR fr.status in (:status))
//    """)
//    List<FactoryRequest> findByDateAndStatus(
//        @Param("date") LocalDate date,
//        @Param("status") List<FactoryRequestStatus> status
//    );

}

