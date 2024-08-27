package com.teamproject.account.utils;


import com.teamproject.account.member.Login.MemberTypeCheck;
import com.teamproject.account.outcome.OutcomeDTO;
import com.teamproject.account.outcome.OutcomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExcelController {

    private final OutcomeService outcomeService;
    private final ExcelUtils excelUtils;

    public Long memberNoMethod(Authentication auth){
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");
        return memberNo;
    }

    @GetMapping("/excel/download")
    public void excelDownLoad(HttpServletResponse response, @ModelAttribute OutcomeDTO outcomeDTO, Authentication auth){
        if(outcomeDTO.getRegDt() == null){
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            outcomeDTO.setRegDt(formattedDate);
        }
        List<OutcomeDTO> outcomeDTOList = outcomeService.findAllByregDtContains(memberNoMethod(auth), outcomeDTO.getRegDt());

        for (int i = 0 ; i<outcomeDTOList.size(); i++){

            String commaprice = String.format("%,d", outcomeDTOList.get(i).getPrice());
            outcomeDTOList.get(i).setCommaprice(commaprice);
        }

        excelUtils.outcomeExcelDownload(outcomeDTOList, response);
    }
}
