package com.teamproject.account.income;

import com.teamproject.account.incomeCategory.IncomeCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(name = "income")
public class IncomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @Column(nullable = false)
    private Long incomeCategoryId;

    @Column(nullable = false)
    private String incomeContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incomeCategoryId", referencedColumnName = "incomeCategoryId", insertable = false, updatable = false)
    private IncomeCategoryEntity incomeCategory;

    @Column(nullable = false)
    private Long price;

    @Column
    private String memo;

    @Column(nullable = false)
    private String regDt;

    @Column(nullable = false)
    private Long memberNo;

    public static IncomeEntity toIncomeEntity(IncomeDTO incomeDTO){
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setIncomeId(incomeDTO.getIncomeId());
        incomeEntity.setIncomeCategoryId(incomeDTO.getIncomeCategoryId());
        incomeEntity.setIncomeContent(incomeDTO.getIncomeContent());
        incomeEntity.setPrice(incomeDTO.getPrice());
        incomeEntity.setMemo(incomeDTO.getMemo());
        incomeEntity.setRegDt(incomeDTO.getRegDt());
        incomeEntity.setMemberNo(incomeDTO.getMemberNo());
        return incomeEntity;
    }
}
