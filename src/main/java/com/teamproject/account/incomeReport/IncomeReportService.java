package com.teamproject.account.incomeReport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeReportService {
    private final IncomeReportRepository incomeReportRepository;
    public List<IncomeReportDTO> getIncomeSummary(Long memberNo, String regDt) {

        return incomeReportRepository.findIncomeSummaryByMemberNoAndRegDt(memberNo, regDt);
    }

}
