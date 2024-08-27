package com.teamproject.account.income;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomeDTO {
    private Long incomeId;
    private String incomeCategoryName;
    private String incomeContent;
    private Long price;
    private String memo;
    private String regDt;
    private Long memberNo;
    private Long incomeCategoryId;
    // Getters and Setters

    public static IncomeDTO toIncomeDTO(IncomeEntity incomeEntity) {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setIncomeId(incomeEntity.getIncomeId());
        incomeDTO.setIncomeCategoryName(incomeEntity.getIncomeCategory().getIncomeCategoryName());
        incomeDTO.setIncomeContent(incomeEntity.getIncomeContent());
        incomeDTO.setPrice(incomeEntity.getPrice());
        incomeDTO.setMemo(incomeEntity.getMemo());
        incomeDTO.setRegDt(incomeEntity.getRegDt());
        incomeDTO.setMemberNo(incomeEntity.getMemberNo());
        return incomeDTO;
    }
}
