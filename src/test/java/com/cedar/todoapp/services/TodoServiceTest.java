package com.cedar.todoapp.services;

import com.cedar.todoapp.dtos.CreateTodoRequest;
import com.cedar.todoapp.dtos.TodoResponse;
import com.cedar.todoapp.dtos.UpdateTodoRequest;
import com.cedar.todoapp.exceptions.TodoNotFoundException;
import com.cedar.todoapp.exceptions.UserNotFoundException;
import com.cedar.todoapp.mappers.TodoMapper;
import com.cedar.todoapp.models.Priority;
import com.cedar.todoapp.models.Todo;
import com.cedar.todoapp.models.User;
import com.cedar.todoapp.repositories.TodoRepository;
import com.cedar.todoapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoMapper todoMapper;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private Todo todo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("user@example.com");

        todo = Todo.builder()
                .id(1)
                .title("Title")
                .description("Description")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(1))
                .completed(false)
                .user(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testCreateTodo() {
        CreateTodoRequest request = new CreateTodoRequest("Title", "Description", LocalDateTime.now().plusDays(1), Priority.HIGH);

        when(todoMapper.toEntity(request)).thenReturn(todo);
        when(todoRepository.save(todo)).thenReturn(todo);

        Integer todoId = todoService.createTodo(request);

        assertNotNull(todoId);
        assertEquals(todo.getId(), todoId);
        verify(todoRepository).save(todo);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void testCreateTodo_UserNotFound() {
        CreateTodoRequest request = new CreateTodoRequest("Title", "Description", LocalDateTime.now().plusDays(1), Priority.MEDIUM);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> todoService.createTodo(request));
    }

    @Test
    void testUpdateTodo() {
        UpdateTodoRequest request = new UpdateTodoRequest(1, "Updated Title", "Updated Description", LocalDateTime.now().plusDays(2), Priority.LOW, true);

        when(todoRepository.findByIdAndUser(request.id(), user)).thenReturn(Optional.of(todo));

        todoService.updateTodo(request);

        assertEquals("Updated Title", todo.getTitle());
        assertTrue(todo.getCompleted());

        verify(todoRepository).save(todo);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void testUpdateTodo_NotFound() {
        UpdateTodoRequest request = new UpdateTodoRequest(999, "Title", null, null, null, null);

        when(todoRepository.findByIdAndUser(request.id(), user)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.updateTodo(request));
    }

    @Test
    void testDeleteTodo() {
        when(todoRepository.findByIdAndUser(todo.getId(), user)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(todo.getId());

        verify(todoRepository).delete(todo);
        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void testDeleteTodo_NotFound() {
        when(todoRepository.findByIdAndUser(999, user)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodo(999));
    }

    @Test
    void testFindByIdAndUser() {
        when(todoRepository.findByIdAndUser(todo.getId(), user)).thenReturn(Optional.of(todo));
        when(todoMapper.toResponse(todo)).thenReturn(
                new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getDueDate(),
                        todo.getPriority(),
                        todo.getCompleted())
        );

        TodoResponse response = todoService.findByIdAndUser(todo.getId());

        assertNotNull(response);
        assertEquals(todo.getId(), response.id());
    }

    @Test
    void testFindByIdAndUser_NotFound() {
        when(todoRepository.findByIdAndUser(999, user)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.findByIdAndUser(999));
    }

    @Test
    void testFindAllTodos() {
        Page<Todo> todosPage = new PageImpl<>(List.of(todo));
        when(todoRepository.findByUser(user, Pageable.unpaged())).thenReturn(todosPage);
        when(todoMapper.toResponse(todo)).thenReturn(
                new TodoResponse(todo.getId(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getDueDate(),
                        todo.getPriority(),
                        todo.getCompleted())
        );

        Page<TodoResponse> responsePage = todoService.findAllTodos(Pageable.unpaged());

        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void testSearchTodos() {
        Page<Todo> todosPage = new PageImpl<>(List.of(todo));
        when(todoRepository.findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                user, "Title", "Title", Pageable.unpaged())).thenReturn(todosPage);
        when(todoMapper.toResponse(todo)).thenReturn(
                new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getDueDate(),
                        todo.getPriority(),
                        todo.getCompleted())
        );

        Page<TodoResponse> responsePage = todoService.searchTodos("Title", Pageable.unpaged());

        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void testUpdateTodoCompletionStatus() {
        when(todoRepository.findByIdAndUser(todo.getId(), user)).thenReturn(Optional.of(todo));

        todoService.updateTodoCompletionStatus(todo.getId(), true);

        verify(todoRepository).save(todo);
        assertTrue(todo.getCompleted());
    }

    @Test
    void testFindTodosByCompletionStatus() {
        Page<Todo> todosPage = new PageImpl<>(List.of(todo));
        when(todoRepository.findByUserAndCompleted(user, false, Pageable.unpaged())).thenReturn(todosPage);
        when(todoMapper.toResponse(todo)).thenReturn(new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(), todo.getDueDate(), todo.getPriority(), todo.getCompleted()));

        Page<TodoResponse> responsePage = todoService.findTodosByCompletionStatus(false, Pageable.unpaged());

        assertEquals(1, responsePage.getTotalElements());
    }
}