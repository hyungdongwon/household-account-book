package com.teamproject.account.calender;

import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.income.IncomeEntity;
import com.teamproject.account.income.IncomeRepository;
import com.teamproject.account.outcome.OutcomeDTO;
import com.teamproject.account.outcome.OutcomeEntity;
import com.teamproject.account.outcome.OutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalenderService {

    private final IncomeRepository incomeRepository;
    private final OutcomeRepository outcomeRepository;

    public List<IncomeDTO> findByIncome(long memberNo, String date) {
        List<IncomeEntity> incomeEntityList = incomeRepository.findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(memberNo, date);

        List<IncomeDTO> incomeDTOList = new ArrayList<>();

        for(IncomeEntity incomeEntity : incomeEntityList){
            IncomeDTO incomeDTO =IncomeDTO.toIncomeDTO(incomeEntity);
            incomeDTOList.add(incomeDTO);
        }
        return incomeDTOList;
    }

    public List<OutcomeDTO> findByoutcome(long memberNo, String date) {
        List<OutcomeEntity> outcomeEntityList = outcomeRepository.findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(memberNo, date);

        List<OutcomeDTO> outcomeDTOList = new ArrayList<>();

        for(OutcomeEntity outcomeEntity : outcomeEntityList){
            OutcomeDTO outcomeDTO =OutcomeDTO.toOutcomeDTO(outcomeEntity);
            outcomeDTOList.add(outcomeDTO);
        }
        return outcomeDTOList;
    }
}
