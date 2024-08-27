package com.teamproject.account.outcomeReport;

import com.teamproject.account.member.Login.MemberTypeCheck;
import com.teamproject.account.outcome.OutcomeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OutcomeReportController {

    private final OutcomeReportService outcomeReportService;

    public Long memberNoMethod(Authentication auth){
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");
        return memberNo;
    }

    @GetMapping("/outcome/report")
    public String getIncomeSummaryReport(Model model, Authentication auth,
                                         @RequestParam("option") String option,
                                         @RequestParam("value") String value,
                                         @ModelAttribute OutcomeDTO outcomeDTO) {

        if (outcomeDTO.getRegDt() == null) {
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            outcomeDTO.setRegDt(formattedDate);
        }
        List<OutcomeReportDTO> outcomeReportDTOList = outcomeReportService.getOutcomeSummary(memberNoMethod(auth), outcomeDTO.getRegDt());

        Long[] price = new Long[outcomeReportDTOList.size()];
        String[] categoryName = new String[outcomeReportDTOList.size()];

        for(int i = 0; i<outcomeReportDTOList.size(); i++) {
            categoryName[i] = outcomeReportDTOList.get(i).getOutcomeCategoryName();
            price[i] = outcomeReportDTOList.get(i).getTotalOutcome();
        }
        String chart = "";
        if(option.equals("bar")){
            chart = "reportBar";
        }

        if(option.equals("pie")){
            chart = "reportPie";
        }
        model.addAttribute("price", price);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("value", value);
        model.addAttribute("searchedDate", outcomeDTO.getRegDt());
        return "/account/"+chart;

    }
}
