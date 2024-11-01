package com.cedar.todoapp.dtos;

import com.cedar.todoapp.models.Priority;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateTodoRequest(
        @NotNull(message = "ID is mandatory")
        Integer id,
        String title,
        String description,
        LocalDateTime dueDate,
        Priority priority,
        Boolean completed
) {
}
