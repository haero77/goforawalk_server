```mermaid
sequenceDiagram
    actor  c as Client
    participant s as Server
    participant cs as Cloud Storage<br>Bucket

    c ->> s: 발자취 생성<br>POST /api/v1/footstep
    s ->> cs: 이미지 업로드 
```