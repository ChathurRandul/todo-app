package com.cedar.todoapp.mappers;

import com.cedar.todoapp.dtos.CreateTodoRequest;
import com.cedar.todoapp.dtos.TodoResponse;
import com.cedar.todoapp.models.Todo;
import org.springframework.stereotype.Service;

@Service
public class TodoMapper {

    public Todo toEntity(CreateTodoRequest dto) {
        if (dto == null) {
            return null;
        }

        return Todo.builder()
                .title(dto.title())
                .description(dto.description())
                .dueDate(dto.dueDate())
                .priority(dto.priority())
                .completed(false)
                .build();
    }

    public TodoResponse toResponse(Todo todo) {
        if (todo == null) {
            return null;
        }

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getDueDate(),
                todo.getPriority(),
                todo.getCompleted()
        );
    }
}
