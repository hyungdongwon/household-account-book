package com.teamproject.account.incomeReport;

import com.teamproject.account.income.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncomeReportRepository extends JpaRepository<IncomeEntity, Long> {

    @Query("SELECT new com.teamproject.account.incomeReport.IncomeReportDTO(" +
            "c.incomeCategoryName, SUM(i.price), i.memberNo) " +
            "FROM IncomeEntity i " +
            "JOIN i.incomeCategory c " +
            "WHERE i.memberNo = :memberNo " +
            "AND i.regDt LIKE :regDt% " +  // regDt를 LIKE 연산자로 필터링
            "GROUP BY c.incomeCategoryName, i.memberNo " +
            "ORDER BY SUM(i.price) DESC")  // 합계금액을 기준으로 내림차순 정렬
    List<IncomeReportDTO> findIncomeSummaryByMemberNoAndRegDt(@Param("memberNo") Long memberNo, @Param("regDt") String regDt);
}
