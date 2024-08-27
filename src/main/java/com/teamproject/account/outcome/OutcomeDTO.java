package com.teamproject.account.outcome;


import com.teamproject.account.annotation.ExcelColumn;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutcomeDTO {
    private Long outcomeId;
    @ExcelColumn(headerName = "일자")
    private String regDt;
    @ExcelColumn(headerName = "카테고리")
    private String outcomeCategoryName;
    @ExcelColumn(headerName = "사용내역")
    private String outcomeContent;
    @ExcelColumn(headerName = "금액")
    private String commaprice;

    private Long price;
    @ExcelColumn(headerName = "메모")
    private String memo;

    private Long memberNo;
    private Long outcomeCategoryId;


    public static OutcomeDTO toOutcomeDTO(OutcomeEntity outcomeEntity) {
        OutcomeDTO outcomeDTO = new OutcomeDTO();
        outcomeDTO.setOutcomeId(outcomeEntity.getOutcomeId());
        outcomeDTO.setOutcomeCategoryName(outcomeEntity.getOutcomeCategory().getOutcomeCategoryName());
        outcomeDTO.setOutcomeContent(outcomeEntity.getOutcomeContent());
        outcomeDTO.setPrice(outcomeEntity.getPrice());
        outcomeDTO.setMemo(outcomeEntity.getMemo());
        outcomeDTO.setRegDt(outcomeEntity.getRegDt());
        outcomeDTO.setMemberNo(outcomeEntity.getMemberNo());
        return outcomeDTO;
    }
}
