# :computer:프로젝트 소개 
Spring Boot + JPA 웹 프로젝트

## :blue_book: 나의 Diary 블로그 개발
이 프로젝트는 CRUD 기반 블로그 애플리케이션입니다. 

코드의 형상 관리는 Git을 사용하였습니다.

AWS를 통해 배포를 경험해봤으며 CI/CD 파이프라인 구축을 위해 GithubActions를 사용했습니다.

## :walking: 개발인원
1인 프로젝트

## :watch: 개발기간

2024.05 ~ 2024.07 (2달)

## :mag: 기능

 
    
- **게시글 CRUD**
  - 게시글 생성, 읽기, 수정, 삭제 기능
 
  
- **댓글 CRUD**
  - 게시글에 댓글을 생성, 읽기, 수정, 삭제 할수있는 기능
   
  

- **조회수 기능** - [상세 설명](https://github.com/creamleeminsoo/blog-developer/wiki/view_count)

  - 게시글 조회수를 기록하고 표시하는 기능
 
    
  - 쿠키를 활용해 조회수 중복처리를 하였습니다
      
  
- **게시글 검색**
  - 게시글을 검색할 수 있는 기능
      
  
- **페이징 처리**
  - 게시글 목록을 페이지 단위로 나누어 표시하는 기능
 
  
- **이미지 기능** - [상세 설명](https://github.com/creamleeminsoo/blog-developer/wiki/image)
  - 게시글에 이미지를 업로드 하고 관리 할수있는 기능
 

- **인증**  - [상세 설명](https://github.com/creamleeminsoo/blog-developer/wiki/authentication)
   - JWT 및 OAuth2를 이용한 사용자 인증
## :eye: UIUX

![Diary-Chrome2024-07-2717-47-23-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/45c9e414-9148-42c2-9793-18ca1a9aa317)


*애플리케이션의 UI/UX 디자인을 소개합니다. 애니메이션을 통해 사용자의 상호작용 흐름을 시각적으로 이해할 수 있습니다.*


## :low_brightness: 개발 환경
- `Java (JDK 17)`
- `JavaScript`
- **프레임워크**: SpringBoot 3.2.0
- **ORM**: JPA
- **템플릿 엔진**: Thymeleaf

## :wrench: 사용한 도구
- **인증**: JWT, OAuth2 (Google, Kakao, Naver), Spring Security
- **테스트**: JUnit
- **형상 관리**: Git
- **CI/CD**: GitHub Actions
- **배포**: AWS(ElasticBeanstalk,RDS)







