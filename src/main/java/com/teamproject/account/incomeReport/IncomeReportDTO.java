package com.teamproject.account.incomeReport;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomeReportDTO {
    private String incomeCategoryName;
    private Long totalIncome;
    private Long memberNo;
}
