package com.cedar.todoapp.dtos;

public record LoginUserResponse(
        String token,
        long expiresIn
) {
}
