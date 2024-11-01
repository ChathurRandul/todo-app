package com.cedar.todoapp.dtos;

import com.cedar.todoapp.models.Priority;

import java.time.LocalDateTime;

public record TodoResponse(
        Integer id,
        String title,
        String description,
        LocalDateTime dueDate,
        Priority priority,
        boolean completed
) {
}
