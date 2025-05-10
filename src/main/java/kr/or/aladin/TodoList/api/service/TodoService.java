package kr.or.aladin.TodoList.api.service;

import jakarta.transaction.Transactional;
import kr.or.aladin.TodoList.api.domain.Todo;
import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.TodoDTO;
import kr.or.aladin.TodoList.api.repository.TodoRepository;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoDTO create(String username, TodoDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND));

        Todo todo = dto.toEntity(user);
        return todoRepository.save(todo).toDto();
    }

    public List<TodoDTO> findAll(String username) {
        return todoRepository.findAllByUserIdOrderByCreatedAtDesc(username)
                .stream()
                .map(Todo::toDto)
                .toList();
    }

    public TodoDTO findById(UUID id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));
        return todo.toDto();
    }

    public TodoDTO update(UUID id, TodoDTO dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));

        todo.update(dto.getTitle(), dto.getContent(), dto.isCompleted());
        return todoRepository.save(todo).toDto();
    }

    public void delete(UUID id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.TODO_DETAIL_NOT_FOUND));

        todoRepository.delete(todo);
    }

    public List<TodoDTO> search(String username, String keyword) {
        return todoRepository.findByUserIdAndTitleContaining(username, keyword)
                .stream()
                .map(Todo::toDto)
                .toList();
    }
}
