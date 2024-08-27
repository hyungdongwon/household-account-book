package com.teamproject.account.incomeReport;

import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.member.Login.MemberTypeCheck;
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
public class IncomeReportController {

    private final IncomeReportService incomeReportService;

    public Long memberNoMethod(Authentication auth){
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");
        return memberNo;
    }

    @GetMapping("/income/report")
    public String getIncomeSummaryReport(Model model, Authentication auth,
                                         @RequestParam("option") String option,
                                         @RequestParam("value") String value,
                                         @ModelAttribute IncomeDTO incomeDTO) {

        if (incomeDTO.getRegDt() == null) {
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            incomeDTO.setRegDt(formattedDate);
        }
        List<IncomeReportDTO> incomeReportDTOList = incomeReportService.getIncomeSummary(memberNoMethod(auth), incomeDTO.getRegDt());

        Long[] price = new Long[incomeReportDTOList.size()];
        String[] categoryName = new String[incomeReportDTOList.size()];

        for(int i = 0; i<incomeReportDTOList.size(); i++) {
            categoryName[i] = incomeReportDTOList.get(i).getIncomeCategoryName();
            price[i] = incomeReportDTOList.get(i).getTotalIncome();

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
        model.addAttribute("searchedDate", incomeDTO.getRegDt());

        return "/account/"+chart;
    }
}
