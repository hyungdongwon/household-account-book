package com.teamproject.account.outcomeReport;

import com.teamproject.account.outcome.OutcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutcomeReportRepository extends JpaRepository<OutcomeEntity, Long> {

    @Query("SELECT new com.teamproject.account.outcomeReport.OutcomeReportDTO(" +
            "c.outcomeCategoryName, SUM(i.price), i.memberNo) " +
            "FROM OutcomeEntity i " +
            "JOIN i.outcomeCategory c " +
            "WHERE i.memberNo = :memberNo " +
            "AND i.regDt LIKE :regDt% " +  // regDt를 LIKE 연산자로 필터링
            "GROUP BY c.outcomeCategoryName, i.memberNo " +
            "ORDER BY SUM(i.price) DESC")
    List<OutcomeReportDTO> findOutcomeSummaryByMemberNoAndRegDt(@Param("memberNo") Long memberNo, @Param("regDt") String regDt);


}
