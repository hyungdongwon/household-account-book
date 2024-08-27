package com.teamproject.account.income;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    public void save(IncomeDTO incomeDTO) {
        IncomeEntity incomeEntity = IncomeEntity.toIncomeEntity(incomeDTO);
        incomeRepository.save(incomeEntity);
    }

    public List<IncomeDTO> findAllByregDtContains(long memberNo, String formattedDate) {
        List<IncomeEntity> incomeEntityList = incomeRepository.findAllByMemberNoAndRegDtContainsOrderByRegDtAsc(memberNo, formattedDate);
        List<IncomeDTO> incomeDTOList = new ArrayList<>();

        for(IncomeEntity incomeEntity : incomeEntityList){
            IncomeDTO incomeDTO =IncomeDTO.toIncomeDTO(incomeEntity);
            incomeDTOList.add(incomeDTO);
        }
        return incomeDTOList;
    }

    public void updateForm(IncomeDTO incomeDTO) {
        IncomeEntity incomeEntity = IncomeEntity.toIncomeEntity(incomeDTO);
        incomeRepository.save(incomeEntity);
    }

    public void deleteById(Long incomeId) {
        incomeRepository.deleteById(incomeId);
    }


}
