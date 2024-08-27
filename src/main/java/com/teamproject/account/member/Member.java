package com.teamproject.account.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @Column(length = 20,unique = true)
    private String username;

    @Column(length=255)
    private String password;

    @Column(length=50)
    private String memberName;

    private String email;

    private String memberFile;

    private String provider;

    private String providerId;

    private int loginFailCount;

    //애는 테이블 필드와는 무관한 변수(테이블 필드에 추가안됨)
    private transient String passwordChk;
    private transient String newPassword;
    private transient String emailTokenInput;

}