package com.cedar.todoapp.dtos;


import com.cedar.todoapp.models.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTodoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be 100 characters or fewer")
        String title,

        @Size(max = 500, message = "Description must be 500 characters or fewer")
        String description,

        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,

        @NotNull(message = "Priority is required")
        Priority priority
) {
}
