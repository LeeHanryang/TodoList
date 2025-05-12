# TodoList 서비스

## 개요

Spring Boot 기반의 TodoList RESTful API 프로젝트입니다. 로그인 방식으로 JWT 인증과 OAuth2(구글, 네이버 등)를 지원하며, SQLite3를 데이터베이스로 사용합니다.

* Spring Boot 버전: 3.2.3
* 빌드 도구: Gradle
* Java 버전: 17
* 데이터베이스: SQLite3 (JPA 사용)
* 인증: Spring Security (JWT) 및 OAuth2.0
* DB 모드: `create-drop` (애플리케이션 실행 시 DB 생성, 종료 시 DB 삭제)

## 주요 기능

1. **회원 가입 / 로그인**

    * 이메일/비밀번호 기반
    * JWT 토큰 발급 및 검증
2. **OAuth2 로그인**

    * Google, Naver 등 OAuth2 제공자 연동
    * 소셜 계정 최초 로그인 시 사용자 정보 저장
3. **Todo 관리**

    * CRUD(Create, Read, Update, Delete)
    * JWT 인증 필요

## 기술 스택

| Layer  | 기술                           |
|--------|------------------------------|
| API 서버 | Spring Boot 3.2.3            |
| 언어     | Java 17                      |
| 빌드 도구  | Gradle                       |
| 데이터 접근 | Spring Data JPA              |
| DB     | SQLite3 (`create-drop` 모드)   |
| 보안     | Spring Security, JWT, OAuth2 |
| 문서화    | Swagger UI                   |

## 요구사항

* Java 17 이상
* Gradle
* Internet 연결 (OAuth2 인증)

## 설치 및 실행

1. 레포지토리 클론

   ```bash
   git clone <레포지토리 URL>
   cd <프로젝트 디렉토리>
   ```
2. 의존성 다운로드 및 빌드

   ```bash
   ./gradlew build
   ```
3. 애플리케이션 실행

   ```bash
   ./gradlew bootRun
   ```
4. 실행 후 자동으로 SQLite3 인메모리 DB가 생성되며, 종료 시 삭제됩니다.

## API 명세

애플리케이션 실행 후 Swagger UI를 통해 상세 API 문서를 확인할 수 있습니다:

> [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

또는 `/v3/api-docs` 엔드포인트로 JSON 문서를 조회 가능합니다.

## 추가 설정

* `application.properties`에서 OAuth2 클라이언트 ID, 시크릿 등을 설정해주세요.
* JWT 비밀 키, 토큰 만료 시간 등도 설정 가능합니다.
