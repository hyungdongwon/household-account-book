package com.teamproject.account.outcomeCategory;



import com.teamproject.account.outcome.OutcomeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "outcome_Category")
public class OutcomeCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outcomeCategoryId;

    @Column(nullable = false)
    private String outcomeCategoryName;

    @OneToMany(mappedBy = "outcomeCategory")
    private List<OutcomeEntity> outcomes;
}
