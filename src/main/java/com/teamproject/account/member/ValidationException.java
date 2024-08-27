package com.teamproject.account.member;

import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("입력제대로해주세용");
        this.errors = errors;
    }
    public Map<String, String> getErrors() {
        return errors;
    }
}

