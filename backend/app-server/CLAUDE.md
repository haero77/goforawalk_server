# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

"GoForAWalk"는 Spring Boot 3.4 + Kotlin 기반의 발자취 추적 API 서버입니다:
- OIDC 기반 인증 (카카오, 애플)
- JWT 액세스/리프레시 토큰 관리 및 리프레시 토큰 로테이션
- 사용자 프로필 관리
- 발자취 CRUD 및 GCP Storage 이미지 업로드
- Spring REST Docs를 사용한 RESTful API 문서화

JVM 17 기반이며, 프로덕션은 MySQL, 로컬 테스트는 H2를 사용합니다.

## 빌드 및 실행 명령어

### 개발
- `./gradlew bootRun` — 로컬 실행 (먼저 `local.env`를 `.env`로 복사하거나 환경 변수 설정)
- `./gradlew build` — 전체 빌드 (테스트 포함, bootable JAR 생성)
- `./gradlew test` — 모든 테스트 실행 (UTC 타임존으로 실행됨)
- `./gradlew clean build` — 클린 빌드

### 테스트
- `./gradlew test` — 모든 JUnit 5 테스트 실행
- `./gradlew generateRestDocs` — `@Tag("restdocs")` 태그가 붙은 테스트만 실행하여 API 스니펫 생성
- `./gradlew asciidoctor` — `src/docs/asciidoc`의 AsciiDoc 문서를 생성된 스니펫을 사용하여 렌더링
- `./gradlew clean generateRestDocs asciidoctor` — 전체 문서 재생성 파이프라인

`bootJar` 태스크는 자동으로 생성된 문서를 JAR의 `static/docs`에 복사합니다.

### 단일 테스트 실행
IntelliJ IDEA의 테스트 러너를 사용하거나:
```bash
./gradlew test --tests "side.flab.goforawalk.app.api.v1.profile.GET_specs"
```

## 아키텍처 및 코드 구조

### 패키지 구조
```
src/main/kotlin/side/flab/goforawalk/
├── app/
│   ├── api/            # 도메인별 REST 컨트롤러 (user, footstep, profile)
│   ├── auth/           # JWT 토큰 제공자, 필터, 핸들러
│   │   ├── filter/     # JwtAuthenticationFilter, RefreshTokenAuthenticationFilter
│   │   └── refreshtoken/ # RefreshToken 엔티티 및 리포지토리
│   ├── domain/         # 도메인 레이어 (애플리케이션 서비스 + 도메인 모델)
│   │   ├── user/
│   │   │   ├── application/  # UserSignUpService, UserDeleteService
│   │   │   └── domain/       # User 엔티티, UserRepository, UserReader
│   │   ├── footstep/
│   │   └── profile/
│   ├── support/        # 공통 관심사
│   │   ├── error/      # 전역 예외 처리
│   │   ├── gcp/        # GCP Storage 설정
│   │   ├── jpa/        # JPA 감사
│   │   ├── response/   # ApiResponse, ErrorResponse
│   │   └── web/        # CurrentUserIdArgumentResolver
│   └── docs/           # 문서 제공을 위한 ApiDocsController
└── security/
    └── oauth2/         # OIDC 인증 (OidcLoginAuthenticationFilter, validators)
```

### 계층화된 아키텍처
1. **API 레이어** (`app/api/*Controller.kt`): REST 엔드포인트, 요청/응답 DTO
2. **애플리케이션 레이어** (`app/domain/*/application/*Service.kt`): 비즈니스 로직 오케스트레이션
3. **도메인 레이어** (`app/domain/*/domain/*.kt`): 엔티티, 리포지토리, 도메인 리더
4. **보안 레이어** (`security/oauth2` + `app/auth`): OIDC 로그인 + JWT 기반 인증

### 인증 플로우
- **OIDC 로그인**: `OidcLoginAuthenticationFilter`가 카카오/애플의 ID 토큰을 provider별 validator로 검증
- **JWT 액세스 토큰**: `JwtAuthenticationFilter`를 통한 무상태(stateless) 인증
- **리프레시 토큰**: `RefreshTokenAuthenticationFilter` + DB 저장 토큰으로 상태 기반(stateful) 로테이션
  - 액세스 토큰 만료 (1시간) → 클라이언트는 401 응답 및 코드 "A_4101" 수신
  - 클라이언트가 리프레시 토큰으로 `POST /api/v1/auth/token/refresh` 요청
  - 성공: 새 액세스 + 리프레시 토큰 반환 (기존 리프레시 토큰은 교체됨)
  - 실패: 401 응답 및 코드 "A_4102" (만료), "A_4103" (불일치/탈취), "A_4104" (헤더 누락)

자세한 인증 시퀀스 다이어그램은 `docs/인증,인가.md` 참조.

## 테스트 전략

### 테스트 구조
- 테스트는 `src/test/kotlin/side/flab/goforawalk/app/`에 위치
- 베이스 클래스: `BaseRestAssuredTest` (OIDC 스텁용 WireMock 포함), `BaseIntegrationTest`
- 픽스처는 `app/support/fixture/`에: `AuthFixture`, `UserFixture`, `FootstepFixture`
- 테스트 파일 명명: `<HTTP_메서드>_specs.kt` (예: `GET_specs.kt`, `POST_specs.kt`)
- REST Docs 테스트: `@Tag("restdocs")` 태그 사용, `app/docs/*ApiDocsTest.kt`에 위치

### 테스트 모범 사례
- MockMvc와 함께 RestAssured를 사용한 통합 테스트
- 인증 시나리오는 `app/scenario/auth/`에서 테스트
- 시간 의존적 동작(예: 토큰 만료) 제어를 위해 `FakeClockHolder` 사용
- 항상 성공 및 에러 케이스 모두 테스트 (예상 에러 코드는 `docs/API 명세.md` 참조)
- 응답에서 닉네임이 8글자로 잘리는지 검증

## 환경 설정

### 환경 파일
- `local.env` — 로컬 개발 (실행 전 `.env`로 복사)
- `local-docker-db.env` — Docker MySQL을 사용한 로컬
- `prod.env`, `prod2.env`, `aws-prod.env` — 프로덕션 환경
- **시크릿 커밋 금지**: `JWT_*_SECRET_KEY`, `GCP_*`, OAuth 클라이언트 ID는 환경 변수 사용

### 주요 설정
- Spring 프로필: `local` (H2), `prod` (MySQL)
- OAuth 제공자: 카카오 (`KAKAO_CLIENT_ID`), 애플 (`APPLE_CLIENT_ID`)
- JWT 설정: `application.yml`의 `app.jwt.*`
- GCP Storage: `gcp.storage.*` with base64 인코딩된 자격 증명

## 커밋 규칙

git 히스토리의 기존 패턴을 따릅니다:
- 형식: `[GW-###] 명령형 요약` (예: `[GW-201] 기록 - 발자취 캘린더 조회 API 추가`)
- 한글 또는 영어로 명령형 어조 사용
- 머지 전 사소한 수정사항은 스쿼시
- 푸시 전 `./gradlew build` 통과 확인
- API 변경 시 문서 재생성 (`generateRestDocs` + `asciidoctor` 실행)

## 일반적인 개발 작업

### 새 API 엔드포인트 추가
1. `app/api/<domain>/<Domain>Controller.kt`에 컨트롤러 메서드 추가
2. `app/domain/<domain>/application/`에 애플리케이션 서비스 생성
3. 필요시 도메인 로직/리포지토리 추가
4. `src/test/.../app/api/v1/<endpoint>/<METHOD>_specs.kt`에 API 테스트 작성
5. `src/test/.../app/docs/<Domain>ApiDocsTest.kt`에 `@Tag("restdocs")` 포함한 REST Docs 테스트 생성
6. `docs/API 명세.md`에 명세 업데이트
7. `./gradlew clean generateRestDocs asciidoctor` 실행하여 문서 재생성

### 인증 이슈 디버깅
- 필터 실행 순서 확인: `OidcLoginAuthenticationFilter` → `JwtAuthenticationFilter` → `RefreshTokenAuthenticationFilter`
- `spy.log` (p6spy) 사용하여 SQL 쿼리 확인
- `AppAuthTokenProvider.parseToken()`으로 JWT 클레임 검증
- 앱과 테스트 모두 타임존이 UTC인지 확인 (`TimeZone.setDefault(TimeZone.getTimeZone("UTC"))`)

### GCP Storage 작업
- 이미지는 `ImageUploader`를 통해 업로드 (`app/support/image/` 참조)
- 테스트에서는 `FakeImageUploader` 사용
- 자격 증명은 환경 변수 `GCP_STORAGE_CREDENTIALS_ENCODED_KEY`에 base64 인코딩

## 알려진 이슈 및 특수 케이스

### 리프레시 토큰 보안
리프레시 토큰 플로우에는 토큰 탈취 감지 기능이 포함되어 있습니다:
- 리프레시 토큰이 DB 레코드와 일치하지 않으면 → 에러 코드 "A_4103" (잠재적 탈취)
- 테스트 시나리오는 `app/scenario/auth/리프레쉬토큰_갱신.kt` 참조
- 만료 테스트를 위해 `FakeClockHolder`를 사용한 시간 조작

### 닉네임 표시 규칙
- 닉네임은 그대로 저장되지만 API 응답에서는 **8글자로 잘림**
- JSON 직렬화를 위해 `UserShortNicknameSerializer` 사용
- 테스트에서 잘림 검증 (예: `ProfileApiDocsTest`)

## 문서

- **API 명세**: `docs/API 명세.md` (한글, 테스트 체크리스트 포함)
- **인증 플로우**: `docs/인증,인가.md` (Mermaid 다이어그램 포함)
- **REST Docs 출력**: `build/docs/asciidoc/index.html`에 생성, bootJar에서 `/docs`로 제공
- **ERD**: `docs/ERD.md`
- 에러 모니터링: `docs/에러 모니터링.md` (Sentry 시퀀스 다이어그램 포함)

실행 중인 앱의 API 문서 접근: `http://localhost:8080/docs/index.html` (로컬) 또는 `/local/docs` (README 참조)