# TodoList API

간단한 할 일(Todo) 관리 REST API입니다.  
**Spring Boot 3.2.3 · Spring Security(JWT) · Spring Data JPA · springdoc-openapi** 등을 사용했습니다.

---

## 실행 방법

### 요구 사항

- **Java 17** 이상
- Git
- (선택) Docker & Docker Compose – 로컬 DB 컨테이너 사용 시

### 1) 소스 코드 가져오기

### 2) 애플리케이션 구동

| 빌드 도구          | 실행 명령                    |
|----------------|--------------------------|
| **Gradle**(권장) | `./gradlew bootRun`      |
| **Maven**      | `./mvnw spring-boot:run` |

> Windows라면 `./gradlew` → `gradlew.bat`, `./mvnw` → `mvnw.cmd` 로 변경해 실행하세요.

실행형 JAR 빌드:

### 3) 환경 변수(선택)

JWT 시크릿 키나 DB 접속 정보를 외부에 두고 싶다면 예시처럼 지정합니다.

값을 주지 않으면 **내장 H2 인메모리 DB** 로 실행됩니다.

---

## 데이터베이스 모드

현재 설정은 **`create-drop`** 입니다.  
애플리케이션이 시작될 때 스키마가 자동 생성되고 종료 시 삭제됩니다.  
영속적인 데이터가 필요하면 `spring.jpa.hibernate.ddl-auto` 값을 `update` 또는 `none` 으로 바꿔주세요.

---

## API 명세

| 구분           | URL                                           |
|--------------|-----------------------------------------------|
| Swagger UI   | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs`           |

Swagger UI에서 모든 엔드포인트와 요청·응답 스키마를 확인하고 **Try it out** 버튼으로 바로 호출해 보세요.