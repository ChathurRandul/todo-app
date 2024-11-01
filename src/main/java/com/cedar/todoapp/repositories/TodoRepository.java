package com.cedar.todoapp.repositories;

import com.cedar.todoapp.models.Todo;
import com.cedar.todoapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {

    Optional<Todo> findByIdAndUser(Integer id, User user);
    Page<Todo> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(User user, String title, String description, Pageable pageable);
    Page<Todo> findByUserAndCompleted(User user, boolean completed, Pageable pageable);
    Page<Todo> findByUser(User user, Pageable pageable);
}
