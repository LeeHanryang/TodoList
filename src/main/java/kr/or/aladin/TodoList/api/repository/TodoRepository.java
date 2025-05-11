package kr.or.aladin.TodoList.api.repository;

import kr.or.aladin.TodoList.api.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findAllByUserIdOrderByCreatedAtDesc(UUID userid);

    List<Todo> findByUserIdAndTitleContaining(UUID userid, String keyword);

}
