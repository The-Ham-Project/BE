# 더함(Theham)

![더함](https://github.com/The-Ham-Project/BE/assets/150704638/d1b03e86-d633-436b-89e3-440a0f2c88f7)

> 더함에서 필요한 물건들 함께 쓰고 나눠봐요 !

- 함께 쓰고 나누어 경제적 부담을 줄이고 사람들 간의 소통과 마음의 따뜻함을 증진시키는 동네 쉐어 서비스입니다.
- 1인 가구의 온라인 연결, 상호 지원의 기회를 마련해주고 우산부터 주거 물품까지 다양한 서비스를 제공해주는 공유 플랫폼입니다.
- [바로가기](https://www.theham.me/)

## 📆 프로젝트 기간

- 2024/03/26 ~ 2024/05/07

## 📚 기술 스택

- Java 17
- Spring Boot 3.1.10
- Spring Boot Data Jpa
- Spring Boot Security
- AWS Beanstalk, Lambda, CloudWatch, EC2, RDS, S3
- WebSocket, Stomp
- MySQL, Redis
- OAuth 2.0
- JWT
- Swagger
- Slack
- Github Actions
- JUnit5, Mockito

## 💡 주요 기능

<details>
<summary>소셜 로그인</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 08 57" src="https://github.com/The-Ham-Project/BE/assets/150704638/6a15ce73-8676-45f4-8061-8b21beeaab44">

- OAuth2.0을 통해 회원가입 및 로그인 프로세스를 간소화했습니다.

</details>

<details>
<summary>사용자 위치 설정</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 09 45" src="https://github.com/The-Ham-Project/BE/assets/150704638/8da8371f-e195-4c16-9f9e-6b4678ca660f">

- 카카오 지도 API를 이용하여 사용자의 현재 위치를 설정할 수 있습니다.

</details>

<details>
<summary>함께쓰기 등록</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 11 28" src="https://github.com/The-Ham-Project/BE/assets/150704638/8a516eab-353a-4dc3-954c-5970a1dc673f">

- 이미지를 최대 3장까지 올릴 수 있습니다.
- 나머지 정보들을 모두 입력하여 게시글을 등록할 수 있습니다.

</details>

<details>
<summary>함께쓰기 조회</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 10 18" src="https://github.com/The-Ham-Project/BE/assets/150704638/382fe1b0-2bb9-4f62-9580-18dcf6b72d3f">
<img width="1680" alt="스크린샷 2024-05-02 오전 11 11 03" src="https://github.com/The-Ham-Project/BE/assets/150704638/adfda370-3d42-411a-ab24-38602299bd29">

- 로그인시 사용자 위치 반경 4KM 이내의 게시글만 조회됩니다.
- 비로그인시 최신순으로 게시글이 조회됩니다.

</details>

<details>
<summary>함께쓰기 검색</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 11 50" src="https://github.com/The-Ham-Project/BE/assets/150704638/3a366f56-c119-4c62-a155-c6cbef99b1bd">

- 함께쓰기 게시글 제목 또는 내용에 포함된 키워드를 검색할 수 있습니다.

</details>

<details>
<summary>함께쓰기 좋아요</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 23 57" src="https://github.com/The-Ham-Project/BE/assets/150704638/7f390fba-3326-454a-8a9a-1dc3f3dc6c4c">

- 함께쓰기 게시글에 좋아요를 누를 수 있습니다.

</details>

<details>
<summary>함께쓰기 채팅</summary>
<img width="1680" alt="스크린샷 2024-05-02 오전 11 12 50" src="https://github.com/The-Ham-Project/BE/assets/150704638/d1172a6c-82a1-436d-bc09-60e5936c80f6">
<img width="1680" alt="스크린샷 2024-05-02 오전 11 12 42" src="https://github.com/The-Ham-Project/BE/assets/150704638/899901ee-e604-4037-bc9f-d21725bf527d">

- 함께쓰기 게시글을 작성한 이용자와 1대1 채팅을 할 수 있습니다.

</details>

## 🏗️ 서비스 아키텍처

![아키텍처](https://github.com/The-Ham-Project/BE/assets/150704638/61628531-2a1b-4206-9477-14c856a457ee)

## 🔖 ERD

![diagram](https://github.com/The-Ham-Project/BE/assets/150704638/8e8a2cc7-398a-4813-a9ea-27766029bb54)

## 🤔 기술적 의사결정

| 기술                             | 설명                                                                                                                                                                   |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Github Actions`               | 러닝 커브가 낮아서 새로운 사용자도 빠르게 파이프라인을 설정하고 자동화 과정을 시작할 수 있습니다. 이를 통해 개발부터 배포까지의 프로세스를 빠르고 안정적으로 진행할 수 있었습니다.                                                                |
| `AWS Beanstalk`                | 서버 관리의 복잡성을 줄여주며, 애플리케이션의 배포 및 확장을 자동화하는 데 유용합니다. 덕분에 서비스를 안정적으로 운영 했으며, 시간을 절약할 수 있었습니다.                                                                            |
| `OAuth2.0`                     | 사용자가 별도의 회원가입 절차 없이도 소셜 미디어 계정과 같은 외부 시스템을 이용해 로그인할 수 있게 해줍니다.                                                                                                       |
| `Spring Security`              | JWT를 통한 보안 및 인증 절차를 관리했습니다. 세션 상태를 서버에 저장할 필요 없이 클라이언트 측에서 토큰을 관리할 수 있게 해, 확장성과 성능을 개선시켰습니다.                                                                         |
| `WebSocket`, `Stomp`, `sockJs` | 낮은 지연시간을 보장하며, 풀 더플레스 통신을 지원 서버와 클라이언트 사이에 소켓 커넥션을 유지하면서 양방향 통신이 가능합니다., WebSocket Emulation 기술을 제공하는 SockJS 라이브러리를 함게 사용하여 WebSocket을 지원하지 않는 브라우저에 대응할 수 있게 하였습니다. |
| `Redis`                        | Redis를 도입하여 리프레쉬 토큰 관리와 자주 사용되는 데이터를 캐싱함으로써 응답 속도를 개선하고 데이터베이스 부하를 줄였습니다.                                                                                            |
| `AWS Lambda`                   | 서버리스 아키텍처를 도입하여 특정 기능의 처리를 간소화하고 비용 효율성을 높였습니다. 사용자가 업로드한 이미지를 자동으로 리사이징하는 기능을 처리하는 데 사용되었습니다. 이로 인해 서버의 리소스 부담을 줄일 수 있었습니다.                                         |
| `AWS CloudWatch`, `Slack`      | EC2와 RDS의 CPU 사용률을 주요 지표로 설정하여 지속적으로 관찰했으며, 경보가 발생하면 Slack을 통해 즉각적인 알림을 받아 이슈에 빠르게 대응할 수 있었습니다.                                                                      |

## 🔥 트러블 슈팅

<details>
<summary>이미지 크기 제한 설정 문제</summary>

`문제사항`

- 이미지 업로드시 18MB라는 제한을 걸어뒀지만 적용되지 않던 문제가 발생 했습니다.

`해결시도`

- application.yml에 아래와 같이 설정 해줬지만 해결되지 않았습니다.

```yaml
spring:
  servlet:
    multipart:
      maxFileSize: 6MB
      maxRequestSize: 18MB
```

`해결방법`

- nginx의 설정 파일에 client_max_body_size 18MB를 추가해줬습니다.

</details>

<details>
<summary>Redis 날짜/시간 데이터 직렬화, 역직렬화 문제</summary>

`문제사항`

- 날짜 데이터를 직렬화/역직렬화 하는 과정에서 날짜와 시간 타입을 지원하지 않는다는 오류 발생 했습니다.

`문제원인`

- 스프링 부트에서는 대부분의 데이터 타입에 대한 직렬화와 역직렬화를 자동으로 처리해주지만, LocalDateTime과 LocalDate 같은 Java 8에서 도입된 날짜와 시간을 다루는 타입들은 특별한 처리가 필요합니다.

```yaml
spring:
  servlet:
    multipart:
      maxFileSize: 6MB
      maxRequestSize: 18MB
```

`해결방법`

- Jackson 라이브러리 같은 직렬화 대안 라이브러리를 사용하여 객체를 직렬화할 수 있었습니다.

```groovy
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3'
```

```java
@JsonSerialize(using = LocalDateTimeSerializer.class)
@JsonDeserialize(using = LocalDateTimeDeserializer.class)
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
private LocalDateTime createdAt;
```

</details>

<details>
<summary>AWS Lambda를 이용한 썸네일 처리 과정에서 발생하는 2~3초의 공백 문제</summary>

`문제사항`

- 썸네일 처리하는 과정에서 의도치 않은 지연이 발생하여 전체 시스템의 반응성이 저하되었습니다.

`문제원인`

- 썸네일 처리하는 과정에서 2~3초 정도 소모 되는게 원인이였습니다.

`해결방법`

- 원본 이미지와 썸네일 이미지 경로를 저장하기 위해 별도의 테이블을 만들었습니다.
- AWS Lambda를 활용하여 썸네일을 생성하고, 해당 경로들을 테이블에 저장하도록 설정했습니다.
- 사용자가 게시글을 조회할 때, 테이블에서 원본 이미지 경로를 먼저 확인합니다.
- 원본 이미지 경로가 존재하면, 연결된 썸네일 이미지 경로를 불러옵니다.
- 만약 썸네일 이미지 경로가 존재하지 않는 경우, 게시글에 저장된 원본 이미지 경로를 사용합니다.

</details>

<details>
<summary>채팅방 나가기 영속성 컨텍스트 불일치 문제</summary>

`문제사항`

- 채팅방에 A와 B 두 사용자가 있을 때, A가 채팅방에서 나간 상태에서 B가 나가기를 시도할 경우, B의 나가기 기능이 작동하지 않는 문제가 발생하였습니다.

`문제원인`

- chatRoom.getSenderIsDeleted() 또는 chatRoom.getReceiverIsDeleted()의 결과가 참이면 chatRoomRepository.delete(chatRoom)을 호출하여 채팅방을 삭제하지만, 해당 트랜잭션 내에서 chatRoom.disableChatRoom(isSender)로 채팅방 상태를 비활성화하는 변경 사항이 데이터베이스에 반영되기 전에 트랜잭션이 종료됩니다.
- 따라서 영속성 컨텍스트 내의 1차 캐시와 데이터베이스의 상태 사이에 불일치가 발생합니다.

```java
@Transactional
public void leaveChatRoom(String email, Long chatRoomId) {
    ChatRoom chatRoom = findChatRoomById(chatRoomId); // 채팅방 존재여부
    Member member = findMemberByEmail(email); // 존재하는 회원여부
    boolean isSender = chatRoom.getSender().equals(member);

    checkValidChatRoomParticipant(chatRoom, member);

    // 1. 한명 이미 나간상태 -> 채팅방, 해당 메세지 완전 삭제 진행
    if (chatRoom.getSenderIsDeleted() || chatRoom.getReceiverIsDeleted()) {
        List<Chat> chats = chatRoom.getChatList();
        chatRoomRepository.delete(chatRoom);
        return;
    }

    // 2. 두명 다 채팅방에 있는 상태
    List<Chat> deleteChatList = chatRepository.findByChatRoomAndVisible(chatRoom, isSender ? VisibleType.ONLY_SENDER : VisibleType.ONLY_RECEIVER);
    chatRepository.deleteAll(deleteChatList);
    List<Chat> chatList = chatRepository.findByChatRoomAndVisible(chatRoom, VisibleType.BOTH);
    chatList.forEach(chat -> {
        chat.updateChatVisible(isSender ? VisibleType.ONLY_RECEIVER : VisibleType.ONLY_SENDER);
    });
    chatRoom.disableChatRoom(isSender);
}
```

`해결방법`

- chatRoom.disableChatRoom(isSender) 호출을 통해 채팅방 상태를 변경한 후, chatRoomRepository.save(chatRoom)를 호출하여 변경 사항을 데이터베이스에 즉시 반영하고, 영속성 컨텍스트와 데이터베이스의 상태를 동기화합니다.
- 이로 인해 어떤 사용자가 나가기를 시도하더라도, 채팅방 상태의 변경이 올바르게 데이터베이스에 저장되어, 다음 요청이 들어왔을 때 일관된 상태를 유지할 수 있습니다.

```java

@Transactional
public void leaveChatRoom(String email, Long chatRoomId) {
    ChatRoom chatRoom = validateChatRoom(chatRoomId);
    Member member = validateMember(email);
    boolean isSender = chatRoom.getSender().equals(member);
    
    // ...중간생략...

    chatRoom.disableChatRoom(isSender);
    chatRoomRepository.save(chatRoom);
}

```

</details>

<details>
<summary>실시간 채팅방 목록 업데이트 문제</summary>

`문제사항`

- 사용자가 직접 새로고침을 해야만 최신의 채팅방 목록을 확인할 수 있는 문제가 있었습니다. 

`문제원인`

- 클라이언트가 서버에 GET 요청을 보내 채팅방 목록을 받아오고, 단순 클라이언트의 요청이 있일때만 서버에서는 갱신된 채팅방 목록을 보내주기 때문에 새로고침하지 않으면, 채팅방에 새로운 활동이 있어도 사용자는 그 정보를 실시간으로 받아볼 수 없습니다.

`해결방법`

- 서버가 새로운 채팅방 정보가 있을 때마다 클라이언트에게 자동으로 푸시할 수 있도록 SSE를 구현함으로써 실시간으로 채팅방 목록을 업데이트 할 수 있게 되었습니다. 
- 이 방법은 클라이언트가 서버로부터 데이터를 주기적으로 요청하지 않아도 되므로, 서버와 클라이언트 간의 네트워크 트래픽을 줄이고, 사용자 경험을 향상시킬 수 있습니다.
  - 채팅 메세지 전송시 SSE 이벤트 발생, 업데이트가 발생한 채팅방 정보를 채팅방 참여 멤버에게 전송합니다.
  - 클라이언트는 실시간으로 업데이트가 일어난 채팅방 정보를 받을 수 있습니다.
![Untitled](https://github.com/The-Ham-Project/BE/assets/150704638/9a9cab56-9541-4d3d-92d0-76cada7d4ee7)

</details>

## 📄 API 명세서

<img width="487" alt="스크린샷 2024-05-02 오전 11 45 34" src="https://github.com/The-Ham-Project/BE/assets/150704638/7bd283e4-cc5a-471d-a081-ba96ef511ea1">

[바로가기](https://api.openmpy.com/swagger-ui/index.html)

## 👥 팀원 소개

| 이름    | 깃허브                            | 이메일                      |
|-------|--------------------------------|--------------------------|
| `강상훈` | https://github.com/totohoon02  | totohoon01@pukyong.ac.kr |
| `김수환` | https://github.com/openmpy     | suhwan@kakao.com         |
| `김엄지` | https://github.com/kimeomji333 | deppll6239@gmail.com     |
| `민가람` | https://github.com/ramizzang   | rkfkaals@gmail.com       |
