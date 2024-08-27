package com.teamproject.account.member.Email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken,Long> {
    Optional<EmailToken> findByEmail(String email);
    void deleteByEmail(String email);
}
