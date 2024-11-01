package com.cedar.todoapp.services;

import com.cedar.todoapp.dtos.CreateTodoRequest;
import com.cedar.todoapp.dtos.TodoResponse;
import com.cedar.todoapp.dtos.UpdateTodoRequest;
import com.cedar.todoapp.exceptions.TodoNotFoundException;
import com.cedar.todoapp.exceptions.UserNotFoundException;
import com.cedar.todoapp.mappers.TodoMapper;
import com.cedar.todoapp.models.Todo;
import com.cedar.todoapp.models.User;
import com.cedar.todoapp.repositories.TodoRepository;
import com.cedar.todoapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository repository;
    private final UserRepository userRepository;
    private final TodoMapper mapper;

    private static final String TODO_NOT_FOUND = "Todo with ID %d not found for this user";

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Todo getTodoForUserById(Integer id, User user) {
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TodoNotFoundException(String.format(TODO_NOT_FOUND, id)));
    }

    public Integer createTodo(CreateTodoRequest request) {
        User user = getAuthenticatedUser();
        Todo todo = mapper.toEntity(request);
        todo.setUser(user);
        Todo createdTodo = repository.save(todo);
        log.info("Todo created with ID: {}", todo.getId());
        return createdTodo.getId();
    }

    @Transactional
    public void updateTodo(UpdateTodoRequest request) {
        User user = getAuthenticatedUser();
        Todo todo = getTodoForUserById(request.id(), user);
        updateTodoFields(todo, request);
        repository.save(todo);
        log.info("Todo updated with ID: {}", todo.getId());
    }

    private void updateTodoFields(Todo todo, UpdateTodoRequest request) {
        if (request.title() != null) todo.setTitle(request.title());
        if (request.description() != null) todo.setDescription(request.description());
        if (request.dueDate() != null) todo.setDueDate(request.dueDate());
        if (request.priority() != null) todo.setPriority(request.priority());
        if (request.completed() != null) todo.setCompleted(request.completed());
    }

    @Transactional
    public void deleteTodo(Integer id) {
        User user = getAuthenticatedUser();
        Todo todo = getTodoForUserById(id, user);
        repository.delete(todo);
        log.info("Todo deleted with ID: {}", id);
    }

    public TodoResponse findByIdAndUser(Integer id) {
        User user = getAuthenticatedUser();
        return mapper.toResponse(getTodoForUserById(id, user));
    }

    public Page<TodoResponse> findAllTodos(Pageable pageable) {
        User user = getAuthenticatedUser();
        return repository.findByUser(user, pageable).map(mapper::toResponse);
    }

    public Page<TodoResponse> searchTodos(String keyword, Pageable pageable) {
        User user = getAuthenticatedUser();
        return repository.findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(user, keyword, keyword, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public void updateTodoCompletionStatus(Integer id, boolean completed) {
        User user = getAuthenticatedUser();
        Todo todo = getTodoForUserById(id, user);
        todo.setCompleted(completed);
        repository.save(todo);
        log.info("Todo completion status updated for ID: {} to {}", id, completed);
    }

    public Page<TodoResponse> findTodosByCompletionStatus(boolean completed, Pageable pageable) {
        User user = getAuthenticatedUser();
        return repository.findByUserAndCompleted(user, completed, pageable)
                .map(mapper::toResponse);
    }
}
