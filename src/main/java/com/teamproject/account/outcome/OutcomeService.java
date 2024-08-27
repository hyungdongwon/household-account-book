package com.teamproject.account.outcome;


import com.teamproject.account.income.IncomeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutcomeService {
    private final OutcomeRepository outcomeRepository;
    public List<OutcomeDTO> findAllByregDtContains(long memberNo, String formattedDate) {
        List<OutcomeEntity> outcomeEntityList = outcomeRepository.findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(memberNo, formattedDate);
        List<OutcomeDTO>  outcomeDTOList = new ArrayList<>();

        for(OutcomeEntity outcomeEntity : outcomeEntityList){
            OutcomeDTO outcomeDTO =OutcomeDTO.toOutcomeDTO(outcomeEntity);
            outcomeDTOList.add(outcomeDTO);
        }
        return outcomeDTOList;
    }

    public void save(OutcomeDTO outcomeDTO) {
        OutcomeEntity outcomeEntity = OutcomeEntity.toOutcomeEntity(outcomeDTO);
        outcomeRepository.save(outcomeEntity);
    }

    public void updateForm(OutcomeDTO outcomeDTO) {
        OutcomeEntity outcomeEntity = OutcomeEntity.toOutcomeEntity(outcomeDTO);
        outcomeRepository.save(outcomeEntity);
    }

    public void deleteById(Long outcomeDTO) {
        outcomeRepository.deleteById(outcomeDTO);
    }
}
