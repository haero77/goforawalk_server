> API 목록 & 테스트 명세

<!-- TOC -->
* [발자취](#발자취-)
  * [발자취 조회](#발자취-조회-)
    * [요청](#요청-)
    * [성공 응답](#성공-응답)
    * [정책](#정책)
    * [테스트](#테스트)
  * [발자취 생성](#발자취-생성)
    * [요청](#요청)
    * [성공 응답](#성공-응답-1)
    * [정책](#정책-1)
    * [테스트](#테스트-)
  * [발자취 삭제](#발자취-삭제)
    * [요청](#요청-1)
    * [성공 응답](#성공-응답-2)
    * [정책](#정책-2)
    * [테스트](#테스트--1)
<!-- TOC -->

---

# 발자취 

## 발자취 조회 

### 요청 

- 엔드포인트: GET /api/v1/foosteps
- 헤더 
  ```
  Authorization: Bearer {accessToken}
  ```
- 본문: 없음

### 성공 응답

- 상태 코드: 200 OK
- 본문:
  ```json
  {
    "data": {
      "footsteps": [
        {
          "footstepId": 1,
          "userNickname": "abc",
          "content": "abcdefg", (nullable)
          "imageUrl": "htts://google/image1.png",
          "createdAt": "2025-05-10T22:00+09:00"
        },
        {
          "footstepId": 2,
          "userNickname": "abc",
          "content": null,
          "imageUrl": "htts://google/image2.png",
          "createdAt": "2025-05-11T07:00+09:00"
        }  
      ]
    }
  }
  ```

### 정책

- 로그인한 유저의 발자취만 조회되어야한다.


### 테스트

- [ ] 올바르게 요청하면 200 OK 상태 코드를 반환한다.
- [ ] 인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다.

## 발자취 생성

### 요청
- 엔드포인트: `POST /api/v1/footsteps`
- 헤더:
```
Content-Type: multipart/form-data
Authorization: Bearer {accessToken}
```

- 본문:
```
Multipart Form Data {
  data: MultipartFile (이미지 파일),
  content: string (선택사항)
}
```
  
### 성공 응답

- 상태 코드: 200 OK
- 본문: 
```json
{
  "data": {
    "userId": 123,
    "userNickname": "산책왕",
    "footstepId": 67890,
    "date": "2025-05-10",
    "imageUrl": "https://example.com/image.png",
    "content": "오늘 방문한 멋진 카페입니다! 커피가 정말 맛있었어요!",
    "createdAt": "2025-05-10T22:00:00+09:00"
  }
}
```
  
### 정책

- 로그인한 유저만 발자취를 생성할 수 있다.
- 이미지는 JPEG 파일 포맷이어야 한다.
- 콘텐츠는 최대 50자까지 입력 가능하다.

### 테스트 

- [ ] 올바르게 요청하면 200 OK 상태코드를 반환한다.
- [ ] 인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다.
- [ ] data 속성이 지정되지 않으면 400 Bad Request 상태 코드를 반환한다.
- [ ] data 속성이 올바른 형식을 따르지 않으면(JPEG 파일 포맷이 아닌 경우) 400 Bad Request 상태 코드를 반환한다.

## 발자취 삭제

### 요청
- 엔드포인트: `DELETE /api/v1/footsteps/{footstepId}`
- 헤더:
```
Authorization: Bearer {accessToken}
```

- 본문: 없음
  
### 성공 응답

- 상태 코드: 200 OK
- 본문: 없음
  
### 정책

- 로그인한 유저만 자신의 발자취를 삭제할 수 있다.

### 테스트 

- [ ] 올바르게 요청하면 200 OK 상태 코드를 반환한다.
- [ ] 인증 토큰이 유효하지 않을 경우 401 Unauthorized 상태 코드를 반환한다.