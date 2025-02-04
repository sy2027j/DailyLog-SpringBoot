# DailyLog 📝  
> **Threads와 유사한 SNS 플랫폼** - 게시물 작성, 댓글, 좋아요, 팔로우 기능 제공  

DailyLog는 사용자가 자신의 일상을 기록하고 공유할 수 있는 SNS 플랫폼입니다.  
OAuth2 로그인 및 JWT 인증을 통해 사용자의 안전한 인증을 보장하고, 베스트 게시물 기능으로 사용자 경험을 향상시켰습니다.

---

## 🌟 **주요 기능**
✅ **사용자 인증** - 일반 로그인 + OAuth2 로그인(Google, Kakao, Naver)  
✅ **게시물** - 텍스트 및 이미지 기반 게시물 작성, 수정, 삭제, 좋아요 기능 지원  
✅ **댓글** - 댓글 - 대댓글 지원  
✅ **베스트 게시물** - 주간/월간 인기글 추천 (좋아요 수 기반)  
✅ **JWT 인증** - Access Token, Refresh Token 관리  

---

## 🛠 **기술 스택**
### **Backend**
- **Spring Boot** - RESTful API 구현  
- **Spring Security + JWT** - 인증 및 보안  
- **JPA + Querydsl** - 동적 쿼리 최적화  
- **MySQL** - 관계형 데이터베이스  

### **Frontend**
- **Vue.js** - SPA
- **Vuex** - 상태 관리  
- **Axios** - API 호출  

---

### 🏠 로그인 화면  
![DailyLog 로그인 화면](https://storage.googleapis.com/dailylog-images/LOGIN.png)

### 🌟 BEST 화면  
![DailyLog BEST 화면](https://storage.googleapis.com/dailylog-images/BEST.png)

### 📌 구독 화면  
![DailyLog 구독 화면](https://storage.googleapis.com/dailylog-images/SUBSCRIBE.png)

### 📝 게시글 상세 조회 화면  
![DailyLog 게시글 상세 조회 화면](https://storage.googleapis.com/dailylog-images/POST.png)
