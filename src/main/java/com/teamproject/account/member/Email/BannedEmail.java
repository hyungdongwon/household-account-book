package com.teamproject.account.member.Email;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class BannedEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성된 ID를 사용하고 싶다면 이 부분을 사용합니다.
    private Long id;

    @Column(unique = true)
    private String bannedEmail;

    private LocalDateTime bannedDate;
}
