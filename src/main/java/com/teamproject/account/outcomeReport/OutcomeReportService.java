package com.teamproject.account.outcomeReport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutcomeReportService {
    private final OutcomeReportRepository outcomeReportRepository;
    public List<OutcomeReportDTO> getOutcomeSummary(Long memberNo, String regDt) {

        return outcomeReportRepository.findOutcomeSummaryByMemberNoAndRegDt(memberNo, regDt);
    }

}
