

## 🧾 Profile & Payment API 서비스

> 회원 프로필 관리, 포인트 충전, 결제, 상품 구매까지 포함한 통합 API 백엔드 시스템입니다.<br>

### 프로젝트 목적

- 최대한 외부 라이브러리를 안쓰고 코드 기반으로 문제를 해결해보았습니다.
- 최대한 간단한 테스트를 위하여 모든 기능을 제공하지 않습니다.
- MSA 환경을 고려하여 최대한 서로 의존성을 줄이고, 각 모듈을 독립적으로 설계했습니다.
- 인증 인가에 최대한 영향을 받지 않게 userId가 필요한 경우 pathVariable이나 parameter로 처리했습니다.
- 프로젝트 실행시 init data 가 저장됩니다.


---

### 🏗 프로젝트 개요

이 프로젝트는 **회원 기반 서비스**에서 다음과 같은 기능을 제공합니다:

- **프로필 관리**: 회원의 프로필 리스트 조회 및 조회수 관리
- **포인트 시스템**: 원화 기반 포인트 충전/적립 기능
- **결제 연동**: Toss, Kakao Pay 결제 연동 후 포인트로 환산 (mock으로 처리)
- **상품 구매**: 포인트 및 쿠폰을 이용한 상품 결제
- **REST API 문서화**: Spring REST Docs 기반의 정적 명세서 제공

---

### 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.5 |
| Build Tool | Gradle |
| DB | MySQL |
| ORM | JPA + QueryDSL |
| Docs | Spring REST Docs |
| Container | Docker |
| Test | JUnit 5, MockMvc |

---
## 🏛 아키텍처 설명 (Architecture Overview)

이 프로젝트는 **도메인 중심 설계(Domain-Driven Design Lite)** 의 철학을 적용하여,  
**기능별(Feature-Based)** 로 코드를 나누고, **명확한 계층화(Layered Architecture)** 를 통해 유지보수성과 확장성을 극대화했습니다.

### 📦 주요 구조 설명

| 계층 (Layer) | 역할 |
|:------------|:----|
| `presentation` | Controller 계층 (API Endpoint, 요청-응답 핸들링) |
| `application` | Service 계층 (비즈니스 로직 처리) |
| `domain` | Entity, Repository (DB와 직접 연관된 도메인 모델) |
| `infra` | QueryDSL Custom Repository 등 구현체 모음 |
| `global` | 공통 설정, 공통 응답 DTO, Exception 핸들러, 외부 클라이언트 |
| `init` | 개발/테스트용 초기 데이터 삽입 모듈 |
| `scheduler` | 배치/주기적 작업 스케줄링 담당 |

---

## 🧩 전체 흐름

```
클라이언트 → Controller → Service → Domain (Repository + Entity) → MySQL
                               ↓
                        External API (Toss, Kakao)
```

- Controller: 요청 수신 → 요청 검증 및 서비스 호출
- Service: 트랜잭션 처리, 비즈니스 로직 수행
- Repository: JPA + QueryDSL 기반으로 DB 액세스 최적화
- Entity: DB 테이블과 1:1 매핑
- 외부 API 호출 (Toss, Kakao) 도 Service Layer에서 분리 관리


---




### 📦 주요 기능 요약

#### ✅ 프로필 API
- `GET /api/v1/profiles` 프로필 목록 조회 (이름, 조회순, 최신순 정렬 지원)
- `GET /api/v1/profiles/{profileId}` 프로필 상세 조회 시 조회수 증가
- 현재 ConcurrencyMap과 spring scheduler를 사용하여 조회수 동시성 관리 

#### 개선점 
    1. 수평적 확장을 위해 redis 와 batch 서버를 사용해서 동시성을 관리 해야함
#### 평가 방법
    1. GET http://localhost:8080/api/v1/profiles 
        프로필들 확인

    2. GET http://localhost:8080/api/v1/profiles/{profileId}
        프로필 상세 조회 조회수 증가

    3. GET http://localhost:8080/api/v1/profiles?sort=viewCount
        조회수 정렬 및 확인

#### ✅ 포인트 충전 API
- `POST /api/v1/users/{userId}/points`: 포인트 충전 요청
- `GET /api/v1/payments/toss/callback`: Toss 결제 콜백 수신
- `POST /api/v1/users/{userId}/points/confirm`: 포인트 충전 확정
- Toss와 Kakao Pay 결제 연동 (docs 읽고 최대한 맞춰 작성 mock 처리)

#### 개선점
    1. 외부 API(TOSS, KAKAO PAY) 연동 시, 확실한 테스트가 필요 함
#### 평가 방법
    1. GET http://localhost:8080/api/v1/profiles 
        userId 얻기위해 프로필 조회

    2. GET http://localhost:8080/api/v1/users/{userId} 
        userpoint 확인

    3. POST http://localhost:8080/api/v1/users/{userId}/points 
        body : {"amount": 500,"pgType": "TOSS","supportKey": "abc"} 
        orderId 획득

    4. GET http://localhost:8080/api/v1/payments/toss/callback?paymentKey=paymentKey&amount=500&orderId={orderId}
        api 결제 확인 

    5. POST http://localhost:8080/api/v1/users/{userId}/points/confirm
        body : {"orderId": {orderId}}
        포인트 충전 확인 및 확정

    6. GET http://localhost:8080/api/v1/users/{userId}
        userpoint 확인


#### ✅ 상품 구매 API
- `POST http://localhost:8080/api/v1/products/{productId}/purchase` 포인트만 사용한 상품 구매
- `POST http://localhost:8080/api/v1/products/{productId}/purchase-with-coupon` 포인트 + 쿠폰 사용한 상품 구매
- 포인트 + 쿠폰 결제 (퍼센트 할인, 금액 할인 모두 지원)

#### 개선점
    1. 현재는 테스트 용으로만 이루어져 있음 (coupon 및 product 고정)  
    2. 상품 구매 시, 재고 관리 필요

#### 평가 방법
    1. GET http://localhost:8080/api/v1/products
        상품 리스트 확인
    2. GET http://localhost:8080/api/v1/profiles
        userId 얻기위해 프로필 조회
    3. GET http://localhost:8080/api/v1/users/{userId}
        userpoint 및 couponId 확인
    4. POST http://localhost:8080/api/v1/products/{productId}/purchase?userId={userId}
        상품 구매 
    4-1. POST http://localhost:8080/api/v1/products/{productId}/purchase-with-coupon?userId={userId}
        body : {"userCouponId": {userCouponId}}
        상품 구매 (쿠폰 사용)
    5. GET http://localhost:8080/api/v1/users/{userId}
        userpoint 및 couponId 확인

#### 문제점
    1. 현재는 테스트 용으로만 이루어져 있음 (coupon 및 product 고정) 

#### ✅ 공통 응답 형식
```json
{
  "success": true,
  "statusCode": 200,
  "data": { ... },
  "message": "성공 메시지",
  "timestamp": "2025-04-28T12:00:00"
}
```

---

### 🧪 테스트

- 모든 API는 **MockMvc 기반 통합 테스트**로 검증
- test/java/com/plus/profile/integration 통합 테스트 내용 존재
- postman collection 파일 제공 (postman/*.json) id는 수정 필요
- 조회수 증가 부분은 jmeter(user: 10, loop: 100) 성공


---

### 📄 API 명세서

Spring REST Docs 기반의 `.adoc` 문서 제공  
📁 `src/main/static/docs/index.html` 에서 확인 가능

---

### 🐳 Docker 실행

docker로 실행 하기 위해서는 mysql이 필요하기에 docker-compose 실행을 추천합니다.

docker-compose 기반으로 앱을 실행할 수 있습니다.

```bash
docker-compose up -d
```


---

### 💬 향후 개선점

- 인증/인가 추가
- 외부 API(TOSS, KAKAO PAY) 연동 시, 확실한 테스트가 필요 함
- 조회수 동시성 관리 redis 로 처리
- 현재는 테스트 용으로만 이루어져 있음 (coupon 및 product 고정)
- 상품 구매 시, 재고 관리 필요


---
