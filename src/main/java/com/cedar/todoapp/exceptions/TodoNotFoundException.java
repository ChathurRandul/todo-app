package com.cedar.todoapp.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodoNotFoundException extends RuntimeException {
    private final String msg;
}
