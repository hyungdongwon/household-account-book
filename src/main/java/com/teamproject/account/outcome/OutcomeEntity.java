package com.teamproject.account.outcome;



import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.income.IncomeEntity;
import com.teamproject.account.outcomeCategory.OutcomeCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "outcome")
public class OutcomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outcomeId;

    @Column(nullable = false)
    private Long outcomeCategoryId;

    @Column(nullable = false)
    private String outcomeContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outcomeCategoryId", referencedColumnName = "outcomeCategoryId", insertable = false, updatable = false)
    private OutcomeCategoryEntity outcomeCategory;

    @Column(nullable = false)
    private Long price;

    @Column
    private String memo;

    @Column(nullable = false)
    private String regDt;

    @Column(nullable = false)
    private Long memberNo;

    public static OutcomeEntity toOutcomeEntity(OutcomeDTO outcomeDTO){
        OutcomeEntity outcomeEntity = new OutcomeEntity();
        outcomeEntity.setOutcomeId(outcomeDTO.getOutcomeId());
        outcomeEntity.setOutcomeCategoryId(outcomeDTO.getOutcomeCategoryId());
        outcomeEntity.setOutcomeContent(outcomeDTO.getOutcomeContent());
        outcomeEntity.setPrice(outcomeDTO.getPrice());
        outcomeEntity.setMemo(outcomeDTO.getMemo());
        outcomeEntity.setRegDt(outcomeDTO.getRegDt());
        outcomeEntity.setMemberNo(outcomeDTO.getMemberNo());
        return outcomeEntity;
    }
}
