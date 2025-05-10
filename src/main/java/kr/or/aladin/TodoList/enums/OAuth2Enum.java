package kr.or.aladin.TodoList.enums;

public enum OAuth2Enum {
    GOOGLE, KAKAO, NAVER;

    public static OAuth2Enum from(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> GOOGLE;
            case "kakao" -> KAKAO;
            case "naver" -> NAVER;
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + registrationId);
        };
    }
}
