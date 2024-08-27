package com.teamproject.account.outcome;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutcomeRepository extends JpaRepository<OutcomeEntity, Long> {
    List<OutcomeEntity> findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(long memberNo, String regDt);

    List<OutcomeEntity> findAllByRegDt(String regDt);
}
