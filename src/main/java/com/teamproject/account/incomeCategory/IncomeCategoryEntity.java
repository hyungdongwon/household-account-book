package com.teamproject.account.incomeCategory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.teamproject.account.income.IncomeEntity;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "income_Category")
public class IncomeCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeCategoryId;

    @Column(nullable = false)
    private String incomeCategoryName;

    @OneToMany(mappedBy = "incomeCategory")
    private List<IncomeEntity> incomes;
}
