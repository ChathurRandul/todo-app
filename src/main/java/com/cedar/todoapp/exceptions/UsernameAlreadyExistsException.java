package com.cedar.todoapp.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UsernameAlreadyExistsException extends RuntimeException {
    private final String msg;
}