package com.teamproject.account.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private String password;
    private String newPassword;
    private String newPasswordCheck;

}
