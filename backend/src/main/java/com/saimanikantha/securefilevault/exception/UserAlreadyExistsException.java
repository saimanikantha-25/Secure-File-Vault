package com.saimanikantha.securefilevault.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApplicationException {

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
