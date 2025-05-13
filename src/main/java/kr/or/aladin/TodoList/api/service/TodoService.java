package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.domain.Todo;
import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.TodoDTO;
import kr.or.aladin.TodoList.api.repository.TodoRepository;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoDTO create(UUID userId, TodoDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND));

        Todo todo = dto.toEntity(user);
        return todoRepository.save(todo).toDto();
    }

    public List<TodoDTO> findAll(UUID userId) {
        return todoRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(Todo::toDto)
                .toList();
    }

    public TodoDTO findById(UUID userId, UUID todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));
        if (!todo.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCodeEnum.ACCESS_DENIED);
        }
        return todo.toDto();
    }

    @Transactional
    public TodoDTO update(UUID userId, UUID todoId, TodoDTO dto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));
        if (!todo.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCodeEnum.ACCESS_DENIED);
        }
        todo.update(dto.getTitle(), dto.getDescription(), dto.isCompleted());
        return todoRepository.save(todo).toDto();
    }

    @Transactional
    public void delete(UUID userId, UUID todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));
        if (!todo.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCodeEnum.ACCESS_DENIED);
        }
        todoRepository.delete(todo);
    }

    public List<TodoDTO> search(UUID userId, String keyword) {
        return todoRepository.findByUserIdAndTitleContaining(userId, keyword)
                .stream()
                .map(Todo::toDto)
                .toList();
    }
}
