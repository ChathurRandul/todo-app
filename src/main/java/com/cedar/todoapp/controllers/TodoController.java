package com.cedar.todoapp.controllers;

import com.cedar.todoapp.dtos.CreateTodoRequest;
import com.cedar.todoapp.dtos.PaginatedResponse;
import com.cedar.todoapp.dtos.TodoResponse;
import com.cedar.todoapp.dtos.UpdateTodoRequest;
import com.cedar.todoapp.services.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody @Valid CreateTodoRequest request) {
        return ResponseEntity.ok(service.createTodo(request));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateTodoRequest request) {
        service.updateTodo(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponse> findById(@PathVariable Integer todoId) {
        return ResponseEntity.ok(service.findByIdAndUser(todoId));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable Integer todoId) {
        service.deleteTodo(todoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<TodoResponse>> findAllTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Pageable pageable = createPageRequest(page, size, sort);
        Page<TodoResponse> todos = service.findAllTodos(pageable);
        return buildPaginatedResponse(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<TodoResponse>> searchTodos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Pageable pageable = createPageRequest(page, size, sort);
        Page<TodoResponse> todos = service.searchTodos(keyword, pageable);
        return buildPaginatedResponse(todos);
    }

    @GetMapping("/status")
    public ResponseEntity<PaginatedResponse<TodoResponse>> getTodosByStatus(
            @RequestParam boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Pageable pageable = createPageRequest(page, size, sort);
        Page<TodoResponse> todos = service.findTodosByCompletionStatus(completed, pageable);
        return buildPaginatedResponse(todos);
    }

    @PatchMapping("/{todoId}/completion")
    public ResponseEntity<Void> updateCompletionStatus(
            @PathVariable Integer todoId,
            @RequestParam boolean completed) {

        service.updateTodoCompletionStatus(todoId, completed);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageRequest(int page, int size, String sort) {
        page = Math.max(page, 0);
        size = Math.max(size, 1);

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams.length > 1 ? sortParams[1] : "asc");
        String property = sortParams[0];

        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private ResponseEntity<PaginatedResponse<TodoResponse>> buildPaginatedResponse(Page<TodoResponse> pageData) {
        PaginatedResponse<TodoResponse> response = new PaginatedResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
        return pageData.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}
