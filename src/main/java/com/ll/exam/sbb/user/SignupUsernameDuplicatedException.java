package com.ll.exam.sbb.user;

public class SignupUsernameDuplicatedException extends RuntimeException {
    public SignupUsernameDuplicatedException(String message) {
        super(message);
    }
}
