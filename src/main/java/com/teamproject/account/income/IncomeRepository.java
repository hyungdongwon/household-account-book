package com.teamproject.account.income;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    List<IncomeEntity> findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(long memberNo, String regDt);

    List<IncomeEntity> findAllByRegDt(String regDt);
}
