package com.teamproject.account.member;

import com.teamproject.account.member.Email.EmailToken;
import com.teamproject.account.member.Email.EmailTokenRepository;
import com.teamproject.account.member.Login.CustomUserDetails;
import com.teamproject.account.member.Login.MemberTypeCheck;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailTokenRepository emailTokenRepository;
    private final S3Service s3Service;
//회원가입 ==============================================================================================================
    @GetMapping("/member/join")
    public String join(Authentication auth) {
        return "member/join";
    }
    @PostMapping("/member/joinProc")
    @ResponseBody
    public ResponseEntity<?> join(
            @ModelAttribute Member member,
            @RequestParam("joinCode") String joinCode
            ) {
        try {
            // 로그 추가
            System.out.println("Received Member: " + member.toString());
            System.out.println("Received joinCode: " + joinCode);

            String successMessage = memberService.join(member, joinCode);

            // 로그 추가
            System.out.println("Member successfully joined. Message: " + successMessage);

            return ResponseEntity.ok(Map.of("message", successMessage, "email", member.getEmail()));
        } catch (ValidationException e) {
            // 로그 추가
            System.err.println("Validation failed: " + e.getErrors());
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            // 로그 추가
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

//회원수정 ==============================================================================================================
    @PostMapping("/member/updateProc")
    @ResponseBody
    public ResponseEntity<?> updateProc(
            @ModelAttribute Member member,
            @RequestParam("joinCode") String joinCode
    ) {
        try {
            Member member2 = memberService.findUserId(member.getUsername());
            member2.setMemberName(member.getMemberName());
            member2.setEmail(member.getEmail());
            member2.setMemberFile(member.getMemberFile());
            member2.setEmailTokenInput(member.getEmailTokenInput());
            String successMessage = memberService.join(member2,joinCode);
            return ResponseEntity.ok(Map.of("message", successMessage,"email",member.getEmail()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

//이메일 인증=============================================================================================================
    //회원가입시 이메일 처리
    @PostMapping("/member/email-verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) throws Exception {
        try{
            memberService.emailCheck(email); // 이미 존재하는 이메일,이메일토큰 인지 확인
        }catch (ValidationException e){
            return ResponseEntity.badRequest().body(e.getErrors());
        }
        return ResponseEntity.ok(Map.of("message", "이메일 인증 링크가 발송되었습니다."));
    }

//로그인시 이메일 처리
    @PostMapping("/member/email-verify2")
    public ResponseEntity<?> verifyEmail2(@RequestParam String email) throws Exception {
        try{
            memberService.emailCheck2(email); // 이미 존재하는 이메일인지 확인
        }catch (ValidationException e){
            return ResponseEntity.badRequest().body(e.getErrors());
        }
        // 이메일 인증 토큰 생성 및 발송
        String token = generateAlphaNumericToken().toString();
        memberService.sendVerificationEmail(email, token);
        // 토큰 저장
        EmailToken emailToken = new EmailToken(token, email);
        emailTokenRepository.save(emailToken);
        return ResponseEntity.ok(Map.of("message", "이메일 인증 링크가 발송되었습니다.","token", token));
    }
//로그인시 이메일코드 확인
    @PostMapping("/member/email-token-verify")
    public ResponseEntity<?> emailTokenVerify(@RequestParam String email,@RequestParam String token){
        try {
            memberService.emailTokenVerify(email,token);
            return ResponseEntity.ok(Map.of("message", "인증완료"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
//이메일 토큰6자리 랜덤생성메소드
    public String generateAlphaNumericToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }
//이메일 token 삭제
    @GetMapping("/joinProc2/{email}")
    public String joinProc2(@PathVariable String email){
        memberService.emailTokenDelete(email);
        return "redirect:/login";
    }
    @GetMapping("/updateProc2/{email}")
    public String updateProc2(@PathVariable String email){
        memberService.emailTokenDelete(email);
        return "redirect:/member/mypage";
    }
//로그인 처리=============================================================================================================
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
    @GetMapping("/loginFail")
    public String loginFail(RedirectAttributes redirectAttributes, HttpSession session){
        String username = (String) session.getAttribute("loginFailUsername");
        if(!username.isEmpty()){
            int loginCount = memberService.loginFailCount(username);
            redirectAttributes.addFlashAttribute("loginCount", "남은 로그인 기회: "+(5-loginCount)+"회");
            redirectAttributes.addFlashAttribute("count",loginCount);
            if(loginCount == 5){
                String email = memberService.emailSearch(username);
                redirectAttributes.addFlashAttribute("email",email);
            }
        }
        return "redirect:/login?error";
    }
    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpSession session){
        String username = (String) session.getAttribute("loginSuccessUsername");
        memberService.loginSuccessCount(username);
        return "index";
    }

    @GetMapping("/logout")
    public void logout(Authentication auth){
    }

//회원 마이페이지=========================================================================================================

    @GetMapping("/member/mypage")
    public String myPage(Authentication auth,Model model){
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        String username = (String) result.get("username");
        try{
            Member member = memberService.findUserId(username);
            String email = member.getEmail();
            if(member.getMemberFile() != null) {
                String fileUrl = s3Service.getS3FileUrl(member.getMemberFile());
                model.addAttribute("fileUrl",fileUrl);
            }
            String usernameMasking = memberService.replaceSubstringWithChar(username,2,member.getUsername().length(),"*");
            String emailMaking = memberService.replaceSubstringWithChar(email,2,member.getEmail().indexOf("@"),"*");
            model.addAttribute("username",usernameMasking);
            model.addAttribute("email", emailMaking);
            model.addAttribute("member",member);
        }catch (Exception e){

        }
        return "member/mypage";
    }
//회원 비밀번호 변경======================================================================================================
    @GetMapping("/member/passwordChange")
    public String passwordChange(){
        return "member/passwordChange";
    }

    @PostMapping("/member/passwordChangeProc")
    public ResponseEntity<?> passwordChangeProc(@ModelAttribute Member member,Authentication auth){
        try{
            MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
            Map<String, Object> result = memberTypeCheck.check(auth);
            String username = (String) result.get("username");
            member.setUsername(username);
            String successMessage = memberService.newPasswordChange(member);
            return ResponseEntity.ok(successMessage);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((e.getMessage()));
        }
    }
//회원탈퇴==============================================================================================================
    @GetMapping("/member/delete")
    public String delete(Model model,Authentication auth) throws Exception{
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        String username = (String) result.get("username");
        Member member = memberService.findUserId(username);
        model.addAttribute("member",member);
        return "member/delete";
    }
    @PostMapping("/member/deleteProc")
    public ResponseEntity<?> memberDelete(Authentication auth,@ModelAttribute Member member){

        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        String username = (String) result.get("username");
        try{
            String successMessage = memberService.memberDelete(username,member);
            return ResponseEntity.ok(successMessage);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((e.getMessage()));
        }
    }
//S3버킷에 파일저장=======================================================================================================
    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename){
        var result = s3Service.createPresignedUrl("test/"+filename);
        System.out.println("S3URL: "+result);
        return result;
    }
//S3버킷 파일삭제=========================================================================================================
    @GetMapping("/deleteFile")
    @ResponseBody
    String deleteFile(@RequestParam String filename){
       String result =  s3Service.createDeletePresignedUrl("test/"+filename);
       return result;
    }
}
