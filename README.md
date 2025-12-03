## 쇼핑몰 백엔드 과제 안내서

이 저장소는 **백엔드 입문자**를 위한 쇼핑몰 백엔드 과제 템플릿입니다.  
과제는 **3단계**로 구성되어 있으며, 단계별로 요구사항이 점점 어려워집니다.

---

## 0. 공통 규칙

- **기술 스택**
  - **Java 17+**
  - **Spring Boot** (Spring Web, Spring Data JPA, Spring Security 권장)
  - DB: **H2 / MySQL / PostgreSQL 중 택 1**
    - 어떤 DB를 선택했는지 이 `README.md`에 꼭 명시하세요.
- **API 공통 규칙**
  - 모든 API는 **`/api/v1/**` 형태의 엔드포인트 사용
  - 공통 Response 구조는 아래 형식을 반드시 따릅니다.

    ```json
    {
      "status": "SUCCESS 또는 ERROR 등 상태 문자열",
      "data": {}
    }
    ```

- **Soft Delete 정책**
  - 실제 DB 삭제(DELETE 쿼리)를 날리지 않고, 예를 들어 아래와 같은 필드를 두고 논리 삭제 처리합니다.
    - `deletedAt` (nullable `LocalDateTime`)
    - 또는 `isDeleted` (boolean)
  - **조회 시에는 Soft Delete 된 데이터는 보이지 않도록 처리**해야 합니다.
- **에러 응답 규칙 (예시)**
  - Validation, 인증/인가, 비즈니스 에러 등은 아래와 같이 응답하는 것을 권장합니다.

    ```json
    {
      "status": "ERROR",
      "data": {
        "code": "VALIDATION_ERROR",
        "message": "필수 값이 비어 있습니다."
      }
    }
    ```

- **레이어드 아키텍처 권장**
  - `controller` / `service` / `repository` / `domain` 등으로 패키지를 분리해서 구현해보세요.

- **실행 방법 문서화**
  - 이 프로젝트를 실행하기 위한 최소한의 설정과 명령어를 `README.md` 최하단에 간단히 정리해주세요.

---

## 1단계: 기본 기능 (상품 CRUD + 회원/로그인 + Validation + 페이지네이션)

1단계의 목표는 **기본적인 REST API 설계와 JPA 기반 CRUD 구현, Validation, 페이지네이션, 로그인**을 경험하는 것입니다.

### 1-1. 도메인 설계

- **상품 (`Product`)**
  - **필수 필드 예시**
    - `id` (Long, PK)
    - `name` (String, 필수, 길이 제한)
    - `price` (Long 또는 Integer, 0 이상)
    - `stock` (Integer, 0 이상)
    - `description` (String, 옵션)
    - `createdAt`, `updatedAt`
    - `deletedAt` 또는 `isDeleted` (Soft Delete 용)

- **회원 (`User`)**
  - **필수 필드 예시**
    - `id` (Long, PK)
    - `email` (String, unique, 필수)
    - `password` (String, 필수, **암호화 저장 필수**)
    - `name` (String)
    - `role` (enum: `USER`, `ADMIN`)  → 2단계에서 사용
    - `createdAt`, `updatedAt`

필드는 예시이며, 필요에 따라 자유롭게 확장해도 됩니다. 다만 **Soft Delete 필드와 Role 필드는 꼭 포함**해주세요.

### 1-2. 상품 관련 API 요구사항

- **상품 등록**
  - **HTTP Method / Path**
    - `POST /api/v1/products`
  - **요구사항**
    - Request Body로 `name`, `price`, `stock`, `description` 등을 받는다.
    - Bean Validation 사용 (`@NotBlank`, `@Min`, `@Size` 등).
    - 성공 시 공통 Response 형식으로 생성된 상품 정보를 응답한다.

- **상품 목록 조회 (페이지네이션 + Query Parameter)**
  - **HTTP Method / Path**
    - `GET /api/v1/products`
  - **Query Parameter 예시**
    - `page` (기본값 0)
    - `size` (기본값 10)
    - `minPrice` (옵션)
    - `maxPrice` (옵션)
    - `name` (옵션, 상품명 부분 검색)
  - **요구사항**
    - Soft Delete 된 상품은 조회되면 안 된다.
    - `data`에 아래와 같은 정보를 포함하는 형태를 권장합니다.
      - `content`: 상품 목록 배열
      - `totalElements`: 전체 개수
      - `totalPages`: 전체 페이지 수
      - `page`: 현재 페이지
      - `size`: 페이지 크기

- **상품 단건 조회**
  - **HTTP Method / Path**
    - `GET /api/v1/products/{id}`
  - **요구사항**
    - Soft Delete 된 상품은 조회 시 404 또는 적절한 에러 응답 처리.

- **상품 수정**
  - **HTTP Method / Path**
    - `PUT /api/v1/products/{id}`
  - **요구사항**
    - `name`, `price`, `stock`, `description` 등의 필드를 업데이트할 수 있다.
    - Validation 적용.
    - Soft Delete 된 상품은 수정 불가 → 에러 응답.

- **상품 삭제 (Soft Delete)**
  - **HTTP Method / Path**
    - `DELETE /api/v1/products/{id}`
  - **요구사항**
    - 실제 삭제 대신 Soft Delete 필드(`deletedAt` 또는 `isDeleted`)를 변경한다.
    - 이후 목록/단건 조회에서 보이지 않아야 한다.

### 1-3. 회원 가입 / 로그인 / 인증

- **회원 가입**
  - **HTTP Method / Path**
    - `POST /api/v1/auth/signup`
  - **요구사항**
    - `email`, `password`, `name` 등을 입력받는다.
    - 비밀번호는 **반드시 암호화(예: BCrypt)** 해서 저장한다.

- **로그인**
  - **HTTP Method / Path**
    - `POST /api/v1/auth/login`
  - **요구사항**
    - `email`, `password`를 입력받아 인증한다.
    - 성공 시 **JWT Access Token**을 발급하여 응답한다.
    - 이후 인증이 필요한 API 호출 시, 다음과 같이 헤더로 토큰을 전달한다.
      - `Authorization: Bearer <access_token>`

※ JWT 구현은 간단한 수준으로 해도 괜찮습니다. 다만, 토큰에 `userId`와 `role` 정보는 포함하는 것을 추천합니다.

---

## 2단계: Role 기반 접근 제어 + 주문 도메인 + 상태 머신

2단계의 목표는 **Role 기반 권한 제어, 자기 데이터만 조회하기, enum 기반 상태 머신**을 경험하는 것입니다.

### 2-1. Role 기반 접근 제어

- **Role 정의**
  - `USER`: 일반 사용자
  - `ADMIN`: 관리자

- **접근 제어 정책 예시**
  - **`ADMIN`**
    - 상품 생성 / 수정 / 삭제 가능
  - **`USER`**
    - 상품 조회만 가능 (등록/수정/삭제 불가)

- **구현 방법 예시**
  - Spring Security를 사용하여 인증/인가를 구현합니다.
  - JWT 토큰 안에 `role` 정보를 포함합니다.
  - 컨트롤러 단에서 예를 들면 아래와 같이 설정할 수 있습니다.
    - `@PreAuthorize("hasRole('ADMIN')")`
    - 또는 URL 별 Security 설정 클래스에서 Ant Matcher로 권한 제어

### 2-2. 주문 도메인 설계

- **주문 (`Order`)**
  - **필드 예시**
    - `id`
    - `user` (주문한 사용자)
    - `status` (enum: `CREATED`, `PAID`, `CANCELLED`, `COMPLETED` 등)
    - `totalPrice`
    - `createdAt`, `updatedAt`

- **주문상품 (`OrderItem`)**
  - **필드 예시**
    - `id`
    - `order` (Order)
    - `product` (Product)
    - `price` (주문 시점의 상품 가격 스냅샷)
    - `quantity`

### 2-3. 간단한 enum 기반 상태 머신

- **상태 전이 규칙 예시**
  - `CREATED` → `PAID` → `COMPLETED`
  - `CREATED` → `CANCELLED`
  - 이미 `CANCELLED` 또는 `COMPLETED` 된 주문은 더 이상 상태 변경 불가.

- **상태 변경 API 예시**
  - `PATCH /api/v1/orders/{id}/pay` → `CREATED` → `PAID`
  - `PATCH /api/v1/orders/{id}/cancel` → `CREATED` → `CANCELLED`
  - `PATCH /api/v1/orders/{id}/complete` → `PAID` → `COMPLETED`

- **요구사항**
  - 잘못된 상태 전이 시 공통 에러 Response 구조로 응답해야 합니다.
    - 예: `status: "ERROR"`, `data: { "code": "INVALID_ORDER_STATUS", "message": "이미 완료된 주문입니다." }`

### 2-4. 주문 관련 API

- **주문 생성**
  - **HTTP Method / Path**
    - `POST /api/v1/orders`
  - **요청 예시**

    ```json
    {
      "items": [
        { "productId": 1, "quantity": 2 },
        { "productId": 3, "quantity": 1 }
      ]
    }
    ```

  - **요구사항**
    - 로그인한 사용자 기준으로 주문을 생성한다.
    - 각 상품의 재고를 검증한다. (재고 부족 시 에러 응답)
    - 주문 생성 시 재고를 차감한다. (동시성 문제는 3단계에서 다룹니다.)

- **내 주문 목록 조회**
  - **HTTP Method / Path**
    - `GET /api/v1/orders/my`
  - **요구사항**
    - 로그인한 사용자의 주문만 조회한다.
    - 페이지네이션 및 상태 필터(optional: `status=CREATED` 등)를 지원하면 좋습니다.

- **주문 상세 조회**
  - **HTTP Method / Path**
    - `GET /api/v1/orders/{id}`
  - **요구사항**
    - 일반 사용자는 **본인 주문만** 조회할 수 있다.
    - `ADMIN`은 모든 사용자의 주문을 조회할 수 있다.

---

## 3단계: 고급 주제 (동시성, N+1, Refresh Token, 캐싱, 비동기)

3단계는 **시간/실력에 따라 선택적으로 구현**해도 됩니다.  
다만, **동시성 관련 내용은 꼭 한 번 경험**해보는 것을 추천합니다.

### 3-1. Refresh Token

- **목표**
  - Access Token의 만료 시간을 짧게 설정.
  - Refresh Token으로 Access Token을 재발급하는 흐름을 경험해보기.

- **요구사항 예시**
  - `POST /api/v1/auth/refresh`
    - 유효한 Refresh Token을 보내면 새로운 Access Token을 발급.
  - Refresh Token 저장 전략 예시
    - DB 테이블에 저장
    - 또는 In-memory (간단 구현용) 저장소
  - 로그아웃 시 Refresh Token을 무효화하는 로직을 구현해보면 좋습니다.

### 3-2. N+1 문제 경험하기

- **예시 포인트**
  - 주문 목록 조회 시 `Order` + `OrderItem` + `Product`를 함께 조회하는 API에서 N+1 문제가 발생하기 쉽습니다.
  - 처음에는 단순히 연관관계만 설정해서 구현해보고, 실제 로그에서 **쿼리 개수**를 확인해보세요.

- **개선 방법 예시**
  - JPQL `fetch join`
  - `@EntityGraph`
  - `hibernate.default_batch_fetch_size` 설정 등

- **요구사항**
  - 어느 API에서 N+1이 발생했는지,
  - 어떤 방법으로 해결했는지를 `README`에 간단히 정리해주세요.

### 3-3. 동시성 문제 / Race Condition 해결 (중요)

- **시나리오 예시**
  - 재고가 1개 남은 인기 상품에 두 명이 동시에 주문을 넣었을 때,
  - 재고가 0 아래로 내려가거나, 실제 재고보다 많이 팔리는 문제가 발생할 수 있습니다.

- **필수 요구**
  - 이 문제를 **재현할 수 있는 간단한 테스트 코드**나 설명을 남겨주세요.
  - 해결 방법 중 **하나 이상**을 적용해보세요.
    - 비관적 락 (`@Lock(PESSIMISTIC_WRITE)` 등)
    - 낙관적 락 (`@Version`)
    - Redis 분산 락 (선택)
  - 해결 전/후에 대해 `README`에 간단히 설명을 남기면 좋습니다.

### 3-4. 캐싱 (선택)

- **예시**
  - 자주 조회되는 **상품 목록** 또는 **상품 상세** API에 캐시를 적용합니다.
  - Spring Cache (`@Cacheable`) 등을 활용할 수 있습니다.
- **요구사항 예시**
  - 캐시 적용 전/후의 차이를 간단하게 로그나 설명으로 남겨주세요.

### 3-5. 비동기 처리 (선택)

- **예시 아이디어**
  - 주문 완료 후, **알림 메일 전송**을 비동기로 처리한다고 가정합니다.
  - `@Async` 또는 이벤트 발행(`ApplicationEventPublisher`)을 이용해 구현해볼 수 있습니다.
  - 실제 메일 전송 대신 **로그 출력**으로 대체해도 됩니다.

---

## 제출 시 포함하면 좋은 내용

- **README.md**
  - 프로젝트 실행 방법 (필수 의존성, DB 설정, `application.yml` 예시 등)
  - 주요 API 목록 (엔드포인트, 메서드, 간단 설명)
  - 2단계, 3단계에서 구현한
    - **권한 정책**
    - **주문 상태 전이 규칙**
    - **동시성 / N+1 / 캐싱 / 비동기 처리** 등에 대한 간단한 정리

- **테스트 코드**
  - 최소한 서비스 레이어 또는 도메인 레벨의 단위/통합 테스트 몇 개를 작성해보세요.

---

## 평가 관점 (가이드)

아래 항목들을 기준으로 스스로 점검해보거나, 코드 리뷰 시 참고용으로 사용할 수 있습니다.

- **1단계**
  - CRUD, Validation, Soft Delete, 공통 Response 구조 준수 여부
  - 기본적인 레이어드 아키텍처 적용 여부
- **2단계**
  - 인증/인가 설계, Role 기반 접근 제어 구현 여부
  - 주문 상태 전이 로직이 명확하게 구현되었는지
  - 본인 데이터만 안전하게 조회/수정 가능한지
- **3단계**
  - 실무에서 자주 나오는 문제들에 대한 이해 및 해결 시도
    - N+1 문제 분석 및 해결
    - 동시성 이슈 재현 및 해결
    - Refresh Token, 캐싱, 비동기 처리 등 (선택 구현)

---

## (TODO) 프로젝트 실행 방법 작성

아래 내용을 참고해서 본인이 구현한 방식에 맞게 내용을 채워 넣으세요.

- **빌드 & 실행 예시**
  - `./gradlew bootRun` 또는 `mvn spring-boot:run`
- **필수 환경 변수 또는 설정**
  - DB 주소, 계정 정보
  - JWT 시크릿 값 및 만료 시간
- **테스트 실행**
  - `./gradlew test` 또는 `mvn test`

실행 방법과 주요 설정을 명확하게 적어두면, 다른 사람이 프로젝트를 받아서 실행해보기 편해집니다.


