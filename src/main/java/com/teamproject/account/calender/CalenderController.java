package com.teamproject.account.calender;

import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.member.Login.MemberTypeCheck;
import com.teamproject.account.outcome.OutcomeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CalenderController {

    private final CalenderService calenderService;

    @GetMapping("/account/calender")
    public String calender(Model model) {
        model.addAttribute("name", "최영솔");
        return "/account/calender";
    }

    @GetMapping("/api/financial-info")
    public ResponseEntity<Map<String, Object>> getFinancialInfo(@RequestParam("date") String date, Authentication auth) {
        // 예시 데이터 (실제 데이터베이스에서 데이터를 가져오는 로직이 필요)

        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");

        List<IncomeDTO> incomeDTOList = calenderService.findByIncome(memberNo, date);
        List<OutcomeDTO> outcomeDTOList = calenderService.findByoutcome(memberNo, date);

        int incometotal = 0;
        int outcometotal = 0;

        for(int i = 0; i<incomeDTOList.size(); i++){
            incometotal += incomeDTOList.get(i).getPrice();
        }
        for(int i = 0; i<outcomeDTOList.size(); i++){
            outcometotal += outcomeDTOList.get(i).getPrice();
        }



        Map<String, Object> data = new HashMap<>();
        data.put("date", date);
        data.put("incomeDTOList", incomeDTOList);
        data.put("outcomeDTOList", outcomeDTOList);
        data.put("incometotal", incometotal); // 해당 날짜의 수입 예시 값
        data.put("outcometotal", outcometotal); // 해당 날짜의 지출 예시 값


        return ResponseEntity.ok(data);
    }
}
