package com.teamproject.account.income;

import com.teamproject.account.member.Login.MemberTypeCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

        public Long memberNoMethod(Authentication auth){
            MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
            Map<String, Object> result = memberTypeCheck.check(auth);
            Long memberNo = (Long) result.get("memberNo");
            return memberNo;
        }

        @GetMapping("/income/write")
        public String write (@ModelAttribute IncomeDTO incomeDTO, Model model, Authentication auth){
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(memberNoMethod(auth), incomeDTO.getRegDt());
            model.addAttribute("searchedDate", formattedDate);
            model.addAttribute("incomeDTOList", incomeDTOList);
            return "/income/write";
        }

        @PostMapping("/income/write")
        public String write (@ModelAttribute IncomeDTO incomeDTO, Authentication auth){

            incomeDTO.setMemberNo(memberNoMethod(auth));
            incomeService.save(incomeDTO);

            String data = incomeDTO.getRegDt();
            String dataupdate = data.substring(0, 7);
            incomeDTO.setRegDt(dataupdate);

            return "redirect:/income/list?regDt=" + incomeDTO.getRegDt();
        }

        @GetMapping("/income/list")
        public String list (Model model, @ModelAttribute IncomeDTO incomeDTO, Authentication auth){
            if (incomeDTO.getRegDt() == null) {
                LocalDate today = LocalDate.now();
                String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                incomeDTO.setRegDt(formattedDate);
            }
            List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(memberNoMethod(auth), incomeDTO.getRegDt());
            model.addAttribute("incomeDTOList", incomeDTOList);
            model.addAttribute("searchedDate", incomeDTO.getRegDt());
            model.addAttribute("type", "income");
            return "/income/list";
        }

        @GetMapping("/income/update/{incomeId}")
        public String update (@ModelAttribute IncomeDTO incomeDTO, Model model, Authentication auth){

            List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(memberNoMethod(auth), incomeDTO.getRegDt());
            model.addAttribute("incomeDTOList", incomeDTOList);
            model.addAttribute("incomeId", incomeDTO.getIncomeId());
            model.addAttribute("regDt", incomeDTO.getRegDt());
            return "/income/update";
        }

        @PostMapping("/income/update")
        public String updateForm (@ModelAttribute IncomeDTO incomeDTO, Authentication auth){
            incomeDTO.setMemberNo(memberNoMethod(auth));

            incomeService.updateForm(incomeDTO);
            String data = incomeDTO.getRegDt();
            String dataupdate = data.substring(0, 7);

            incomeDTO.setRegDt(dataupdate);

            return "redirect:/income/list?regDt=" + incomeDTO.getRegDt();
        }

        @GetMapping("/income/delete/{incomeId}")
        public String delete (@ModelAttribute IncomeDTO incomeDTO){
            incomeService.deleteById(incomeDTO.getIncomeId());

            String data = incomeDTO.getRegDt();
            String dataupdate = data.substring(0, 7);
            incomeDTO.setRegDt(dataupdate);

            return "redirect:/income/list?regDt=" + incomeDTO.getRegDt();
        }

        @PostMapping("/income/search")
        public String search (Model model, @ModelAttribute IncomeDTO incomeDTO, Authentication auth){

            List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(memberNoMethod(auth), incomeDTO.getRegDt());
            model.addAttribute("incomeDTOList", incomeDTOList);
            model.addAttribute("searchedDate", incomeDTO.getRegDt());
            model.addAttribute("type", "income");
            return "/income/list";
        }

}
