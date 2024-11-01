package com.cedar.todoapp.mappers;

import com.cedar.todoapp.dtos.CreateTodoRequest;
import com.cedar.todoapp.dtos.TodoResponse;
import com.cedar.todoapp.models.Priority;
import com.cedar.todoapp.models.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TodoMapperTest {

    private TodoMapper todoMapper;

    @BeforeEach
    void setUp() {
        todoMapper = new TodoMapper();
    }

    @Test
    void testToEntitySuccessful() {
        CreateTodoRequest createTodoRequest = new CreateTodoRequest(
                "Title",
                "Description",
                LocalDateTime.of(2024, 11, 1, 12, 0),
                Priority.HIGH
        );

        Todo todo = todoMapper.toEntity(createTodoRequest);

        assertNotNull(todo);
        assertEquals(createTodoRequest.title(), todo.getTitle());
        assertEquals(createTodoRequest.description(), todo.getDescription());
        assertEquals(createTodoRequest.dueDate(), todo.getDueDate());
        assertEquals(createTodoRequest.priority(), todo.getPriority());
        assertFalse(todo.getCompleted()); // Default value
    }

    @Test
    void testToEntityNullInput() {
        Todo todo = todoMapper.toEntity(null);

        assertNull(todo);
    }

    @Test
    void testToResponseSuccessful() {
        Todo todo = Todo.builder()
                .id(1)
                .title("Title")
                .description("Description")
                .dueDate(LocalDateTime.of(2024, 11, 1, 12, 0))
                .priority(Priority.HIGH)
                .completed(false)
                .build();

        TodoResponse todoResponse = todoMapper.toResponse(todo);

        assertNotNull(todoResponse);
        assertEquals(todo.getId(), todoResponse.id());
        assertEquals(todo.getTitle(), todoResponse.title());
        assertEquals(todo.getDescription(), todoResponse.description());
        assertEquals(todo.getDueDate(), todoResponse.dueDate());
        assertEquals(todo.getPriority(), todoResponse.priority());
        assertEquals(todo.getCompleted(), todoResponse.completed());
    }

    @Test
    void testToResponseNullInput() {
        TodoResponse todoResponse = todoMapper.toResponse(null);

        assertNull(todoResponse);
    }
}