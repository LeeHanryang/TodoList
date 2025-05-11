package kr.or.aladin.TodoList.api.controller;

import jakarta.validation.Valid;
import kr.or.aladin.TodoList.api.dto.TodoDTO;
import kr.or.aladin.TodoList.api.service.TodoService;
import kr.or.aladin.TodoList.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /* 1. Todo 생성 */
    @PostMapping
    public ResponseEntity<Void> create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody TodoDTO dto
    ) {
        TodoDTO saved = todoService.create(principal.id(), dto);
        return ResponseEntity                     // 201 Created + Location 헤더
                .created(URI.create("/todos/" + saved.getId()))
                .build();
    }

    /* 2. 전체 Todo 목록 */
    @GetMapping
    public ResponseEntity<List<TodoDTO>> findAll(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(todoService.findAll(principal.id()));
    }

    /* 3. 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(todoService.findById(id));
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> update(@PathVariable UUID id,
                                          @Valid @RequestBody TodoDTO dto) {
        return ResponseEntity.ok(todoService.update(id, dto));
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* 6. 검색 (예: ?q=keyword) */
    @GetMapping("/search")
    public ResponseEntity<List<TodoDTO>> search(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(todoService.search(principal.id(), keyword));
    }
}