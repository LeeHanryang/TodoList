# TodoList API

간단한 할 일(Todo) 관리 REST API 입니다.  
Spring Boot 3, Spring Security(JWT), Spring Data JPA, springdoc-openapi 를 사용합니다.

---

## 실행 방법

### 요구 사항
* Java 17 이상
* Git
* (선택) Docker & Docker Compose – 로컬 데이터베이스를 컨테이너로 띄우려는 경우

### 1) 소스 코드 가져오기

### 2) 애플리케이션 구동

| 빌드 도구 | 명령어 |
|-----------|--------|
| **Gradle** (권장) | `./gradlew bootRun` |
| **Maven**        | `./mvnw spring-boot:run` |

> Windows OS라면 `./gradlew` 대신 `gradlew.bat`, `./mvnw` 대신 `mvnw.cmd` 를 사용하세요.

### 3) 환경 변수(선택)
JWT 시크릿 키, DB 접속 정보 등을 외부에서 주입하려면 다음 예시처럼 실행 시 지정합니다.

별도 설정이 없으면 내장 H2 메모리 DB 와 기본 시크릿 값으로 실행됩니다.

---

## API 명세 요약

| 구분 | URL |
|------|-----|
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |

브라우저에서 Swagger UI 링크로 접속하면 모든 엔드포인트와 요청/응답 스키마를 확인하고,  
`Try it out` 버튼으로 직접 호출해볼 수 있습니다.
