package com.teamproject.account.member;
import com.teamproject.account.member.Email.BannedEmail;
import com.teamproject.account.member.Email.BannedEmailRepository;
import com.teamproject.account.member.Email.EmailToken;
import com.teamproject.account.member.Email.EmailTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailTokenRepository emailTokenRepository;
    private final BannedEmailRepository bannedEmailRepository;
    private LocalDateTime bannedDate;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public Member findUserId(String username) throws Exception{
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent()){
            return member.get();
        }else{
            throw new Exception("오류발생");
        }
    }
//회원가입===============================================================================================================
    public String join(Member member,String joinCode) throws Exception{
        Optional<Member> idCheck = memberRepository.findByUsername(member.getUsername());
        Optional<Member> emailCheck = memberRepository.findByEmail(member.getEmail());
        Optional<EmailToken> emailTokenCheck = emailTokenRepository.findByEmail(member.getEmail());
        Optional<BannedEmail> bannedEmail = bannedEmailRepository.findByBannedEmail(member.getEmail());
        Map<String, String> errors = new HashMap<>();
        if(idCheck.isPresent() && !joinCode.equals("update")){ //DB의 ID중복체크
            errors.put("username", "등록된 아이디입니다.");
        }
        if(emailCheck.isPresent() && !joinCode.equals("update")){
            errors.put("email", "등록된 이메일입니다.");
        }
        //회원탈퇴한 이메일 확인
        if(bannedEmail.isPresent()){
            LocalDateTime now = LocalDateTime.now();
            if(bannedEmail.get().getBannedDate().isAfter(now)){
                errors.put("email", "등록된 이메일입니다.");
            }else{
                bannedEmailRepository.delete(bannedEmail.get());
            }
        }
        //이메일 토큰확인
        if (emailTokenCheck.isPresent()) {
            if (!emailTokenCheck.get().getToken().equals(member.getEmailTokenInput())) {
                errors.put("emailTokenInput", "이메일 인증번호가 틀렸습니다.");
            }
        } else {
            if(joinCode.equals("ok")) {
                if (!errors.containsKey("email")) {
                    errors.put("email", "이메일 인증은 필수입니다.");
                }
            }else if((joinCode.equals("update") && !emailCheck.isPresent())){
                errors.put("email", "이메일 인증은 필수입니다.");
            }
        }
        errors = nullCheck(member,errors,joinCode); // join에서 넘어오는 널,공백체크
        errors = checkConstraint(member,errors,joinCode); //join에서 넘어오는값들 제약조건을 체크
        if (!errors.isEmpty()) {
            throw new ValidationException(errors); //예외발생한것들 모아서 ValidationException class에 예외던지기
        }
        if(joinCode.equals("ok")){
            member.setPassword(passwordEncoder.encode(member.getPassword())); //비밀번호 암호화
            member.setLoginFailCount(0);
            memberRepository.save(member);//DB저장
        }else if(joinCode.equals("update")){
            member.setLoginFailCount(0);
            memberRepository.save(member);
        }
        return "회원가입이 성공적으로 완료되었습니다.";
    }
//회원정보 NULL,공백 체크=============================================================================================
    private static Map<String, String> nullCheck (Member member,Map<String, String> errors,String joinCode) throws Exception {
        if(!errors.containsKey("username")) {
            validateField(member.getUsername(), "username/아이디는 필수입력입니다.", errors);
        }
        if(!joinCode.equals("update")){
            validateField(member.getPassword(),"password/비밀번호는 필수입력입니다.",errors);
        }
        validateField(member.getEmail(), "email/이메일은 필수입력입니다.",errors);
        validateField(member.getMemberName(), "memberName/이름은 필수입력입니다.",errors);
        return errors;
    }
    private static void validateField(String field, String errorMessage,Map<String,String> errors) {
        int index = errorMessage.indexOf("/");
        String cutStr = errorMessage.substring(index);
        if (field == null || field.trim().isEmpty()) {
            errors.put(errorMessage.substring(0,index), errorMessage.substring(index+1));
        }
    }
//회원정보 제약조건 체크=============================================================================================
    private static Map<String, String> checkConstraint(Member member,Map<String,String> errors,String joinCode) throws ValidationException{
        //errors.containsKey("password")는
        // 주어진 키(예: "password")가 errors 맵에 존재하는지 여부를 확인하는 함수임
        if(!errors.containsKey("username") && !joinCode.equals("update")){
            //회원아이디의 영소문자,숫자,문자열의 공백을 확인한다.
            if(!member.getUsername().matches("[a-z0-9]{4,20}")){
                errors.put("username", "4~20자의 영문 소문자, 숫자만 사용 가능합니다.");
            }
        }
        if(!errors.containsKey("password") && !joinCode.equals("update")) {
            //회원비밀번호의 영대/소문자,숫자,문자열의 공백을 확인한다.
            if (!member.getPassword().matches("[a-zA-Z0-9]{8,16}")) {
                errors.put("password", "8~16자의 영문 대/소문자, 숫자만 사용 가능합니다.");
            }
            //비밀번호 확인과 비밀번호가 동일한값인지 체크합니다.
            if (!member.getPassword().equals(member.getPasswordChk()) ){
                errors.put("passwordChk", "비밀번호와 다릅니다.");
            }
        }
        if(!errors.containsKey("email")){
            //회원이메일값 체크
            if(!member.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                errors.put("email", "이메일을 다시 입력해주세요.");
            }
        }
        return errors;
    }
//이메일 인증======================================================================================================
    //아이디로 이메일찾기
    public String emailSearch(String username){
        Optional<Member> emailSearch = memberRepository.findByUsername(username);
        String email = emailSearch.get().getEmail();
        return email;
    }
    
    //회원가입시 중복이메일토큰 체크
    public Optional<Member> emailCheck(String email) throws Exception{
        Optional<Member> result = memberRepository.findByEmail(email);
        Optional<BannedEmail> bannedEmail = bannedEmailRepository.findByBannedEmail(email);
        Map<String, String> errors = new HashMap<>();

        if(email == null || email.trim().isEmpty()){
            errors.put("email", "이메일을 입력해주세요.");
            throw new ValidationException(errors);
        }

        if(bannedEmail.isPresent()){
            LocalDateTime now = LocalDateTime.now();
            if(bannedEmail.get().getBannedDate().isAfter(now)){
                errors.put("email", "등록된 이메일입니다.");
                throw new ValidationException(errors);
            }
        }
        if(result.isPresent()){
            errors.put("email", "등록된 이메일입니다.");
            throw new ValidationException(errors);
        }
        //이메일 인증 토큰 생성 및 발송
        String token = generateAlphaNumericToken().toString();
        //토큰객체 생성
        EmailToken emailToken = new EmailToken(token, email);

        //중복되는 이메일에 해당하는 토큰삭제
        Optional<EmailToken> emailTokenCheck = emailTokenRepository.findByEmail(email);
        if(emailTokenCheck.isPresent()){
            emailTokenRepository.delete(emailTokenCheck.get());
            emailTokenRepository.save(emailToken);
            sendVerificationEmail(email, token);
        }else{
            emailTokenRepository.save(emailToken);
            sendVerificationEmail(email, token);
        }
        return result;
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

    //로그인시 중복이메일토큰 체크
    public Optional<Member> emailCheck2(String email) throws Exception{
        Map<String, String> errors = new HashMap<>();
        Optional<Member> result = memberRepository.findByEmail(email);
        //중복되는 이메일에 해당하는 토큰삭제
        Optional<EmailToken> emailTokenCheck = emailTokenRepository.findByEmail(email);
        if(emailTokenCheck.isPresent()){
            emailTokenRepository.delete(emailTokenCheck.get());
        }
        return result;
    }

    //이메일전송
    public void sendVerificationEmail(String email, String token) {
        // 이메일 제목
        String subject = "이메일 인증을 완료해주세요";
        // 인증 링크 생성
        String confirmationUrl = token;
        // 이메일 본문 내용
        String message = "회원가입을 완료하려면 다음 인증번호를 입력해주세요 : " + confirmationUrl;
        // 이메일 객체 생성
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);
        emailMessage.setFrom(fromAddress);
        // 이메일 전송
        mailSender.send(emailMessage);
    }
    //회원가입처리후 해당이메일토큰 삭제
    @Transactional
    public void emailTokenDelete(String email){
        emailTokenRepository.deleteByEmail(email);
    }
    //로그인 이메일인증코드 확인시
    public void emailTokenVerify(String email,String token){
        Map<String, String> errors = new HashMap<>();
        Optional<EmailToken> emailToken = emailTokenRepository.findByEmail(email);
        Optional<Member> member = memberRepository.findByEmail(email);
        if(emailToken.get().getToken().equals(token)){
            member.get().setLoginFailCount(0);
            memberRepository.save(member.get());
        }else{
            errors.put("emailTokenInput", "이메일 인증번호가 틀렸습니다.");
            throw new ValidationException(errors);
        }
    }

//로그인 실패횟수체크==================================================================================================
    public int loginFailCount(String username){
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent()){
            int count = member.get().getLoginFailCount();
            if(count != 5) {
                count++;
                member.get().setLoginFailCount(count);
                memberRepository.save(member.get());
            }
            return count;
        }else{
            return 6;
        }
    }
//로그인 성공시 실패횟수 초기화==========================================================================================
    public void loginSuccessCount(String username){
        Optional<Member> member = memberRepository.findByUsername(username);
        member.get().setLoginFailCount(0);
        memberRepository.save(member.get());
    }
    public int count(String username){
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isPresent()){
            int count = member.get().getLoginFailCount();
            return count;
        }else{
            return 0;
        }
    }
//중요 정보 마스킹========================================================================================================
    public String replaceSubstringWithChar(String str,int start,int end,String replaceChar){
        int length = end - start;
        // 시작 인덱스 이전의 문자열 + 교체 문자가 반복된 부분 + 끝 인덱스 이후의 문자열을 합침
        return str.substring(0, start) + replaceChar.repeat(length) + str.substring(end);
    }
//비밀번호 변경===========================================================================================================
    public String newPasswordChange(Member member)throws Exception{
        Optional<Member> member2 = memberRepository.findByUsername(member.getUsername());
        BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();
        if(member2.isPresent()){
            if(member.getPassword() == null || member.getPassword().trim().isEmpty()){
                throw new Exception("현재 비밀번호를 입력해주세요");
            }
            if(!passwordEncoder.matches(member.getPassword(), member2.get().getPassword())){
                throw new Exception("현재 비밀번호가 다릅니다.");
            }
            if(member.getNewPassword() == null || member.getNewPassword().trim().isEmpty()){
                throw new Exception("새 비밀번호를 입력해주세요");
            }
            if (!member.getNewPassword().matches("[a-zA-Z0-9]{8,16}")) {
                throw new Exception("비밀번호는 8~16자의 영문 대/소문자, 숫자만 사용 가능합니다.");
            }
            if(member.getPasswordChk() == null || member.getPasswordChk().trim().isEmpty()){
                throw new Exception("새 비밀번호 확인을 입력해주세요");
            }
            //비밀번호 확인과 비밀번호가 동일한값인지 체크합니다.
            if (!member.getNewPassword().equals(member.getPasswordChk()) ){
                throw new Exception("새 비밀번호 확인이 비밀번호와 다릅니다.");
            }
            member2.get().setPassword(passwordEncoder.encode(member.getNewPassword()));
            memberRepository.save(member2.get());
            return "비밀번호 변경이 완료되었습니다.";
        }else{
            throw new Exception("에러! 새로고침을해주세요");
        }
    }
//회원탈퇴===============================================================================================================
    public String memberDelete(String username,Member member2)throws Exception{
        Optional<Member> member = memberRepository.findByUsername(username);
        BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();
        if(member.isPresent()){
            if (member2.getPassword() == null || member2.getPassword().trim().isEmpty()) {
                throw new Exception("현재 비밀번호를 입력해주세요");
            }
            if (!passwordEncoder.matches(member2.getPassword(),member.get().getPassword() )) {
                throw new Exception("현재 비밀번호가 다릅니다.");
            }
            try {
                BannedEmail bannedEmail = new BannedEmail();
                bannedEmail.setBannedEmail(member.get().getEmail());
                //현재날짜
                LocalDateTime now = LocalDateTime.now();
                // 30일 더하기
                LocalDateTime bannedUntil = now.plusDays(30);
                bannedEmail.setBannedDate(bannedUntil);
                bannedEmailRepository.save(bannedEmail);
                memberRepository.delete(member.get());
            }catch (Exception e){
                throw new Exception("에러!");
            }
            return "회원탈퇴가 정상적으로 완료되었습니다. 그동안 이용해주셔서 감사합니다.";
        }else{
            throw new Exception("에러!");
        }
    }

}
