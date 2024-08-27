package com.teamproject.account.outcomeReport;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutcomeReportDTO {
    private String outcomeCategoryName;
    private Long totalOutcome;
    private Long memberNo;
}
