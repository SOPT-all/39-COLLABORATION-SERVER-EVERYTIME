<div align="center">

<img src="docs/images/everytime-logo.png" alt="에브리타임 로고" width="96" height="96" />

# Everytime Redesign Server

**39기 SOPT 합동세미나 - 모바일 앱 4조**

강의를 찾고, 비교하고, 시간표에 담기까지.

![Java 21](https://img.shields.io/badge/Java-21-374151?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-374151?style=flat-square&logo=springboot)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-374151?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.4-374151?style=flat-square&logo=mysql)
![Gradle](https://img.shields.io/badge/Gradle-374151?style=flat-square&logo=gradle)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-374151?style=flat-square&logo=docker)

[Overview](#-overview) · [Contributors](#-contributors) · [Tech Stack](#-tech-stack) · [Architecture](#-architecture) · [Getting Started](#-getting-started) · [Convention](#-convention)

</div>

## 📌 Overview

수강신청을 준비하는 학생은 후보 강의를 비교하려고 강의 목록과 강의평을 오가고, 강의를 고른 뒤 시간표로 이동합니다. 에브리타임 리디자인 프로젝트는 **강의 탐색 → 강의평 확인 → 시간표 담기** 흐름을
연결합니다. 이 저장소는 모바일 앱에 필요한 API와 데이터를 제공하는 백엔드 프로젝트입니다.

## 👥 Contributors

| <img src="https://github.com/Erichong7.png?size=240" width="120" height="120" alt="홍정우 프로필" /> | <img src="https://github.com/sssthnnhee.png?size=240" width="120" height="120" alt="이승희 프로필" /> | <img src="https://github.com/ucaeon.png?size=240" width="120" height="120" alt="유채원 프로필" /> |
|:----------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|
|                                          **Backend**                                           |                                           **Backend**                                           |                                         **Backend**                                         |
|                              [홍정우](https://github.com/Erichong7)                               |                              [이승희](https://github.com/sssthnnhee)                               |                              [유채원](https://github.com/ucaeon)                               |

## 🛠️ Tech Stack

| 영역       | 기술                                                                                                                                                                                                                                                                                                                                        |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 언어·빌드    | ![Java 21](https://img.shields.io/badge/Java-21-374151?style=flat-square&logo=openjdk&logoColor=white) ![Gradle Wrapper](https://img.shields.io/badge/Gradle%20Wrapper-374151?style=flat-square&logo=gradle&logoColor=white)                                                                                                              |
| 웹 API    | ![Spring Boot 4.1.2](https://img.shields.io/badge/Spring%20Boot-4.1.2-374151?style=flat-square&logo=springboot&logoColor=white) ![Spring MVC](https://img.shields.io/badge/Spring%20MVC-374151?style=flat-square&logo=spring&logoColor=white) ![Bean Validation](https://img.shields.io/badge/Bean%20Validation-374151?style=flat-square) |
| 데이터 접근   | ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-374151?style=flat-square&logo=spring&logoColor=white) ![MySQL 8.4](https://img.shields.io/badge/MySQL-8.4-374151?style=flat-square&logo=mysql&logoColor=white)                                                                                                        |
| 로컬 DB 실행 | ![Docker Compose](https://img.shields.io/badge/Docker%20Compose-374151?style=flat-square&logo=docker&logoColor=white)                                                                                                                                                                                                                     |

## 🏗️ Architecture

<p align="center">
  <img src="docs/images/architecture.svg" alt="모바일 앱, Spring Boot 서버의 내부 계층 설계안, MySQL 연결 구조" width="880" />
</p>

모바일 앱은 HTTP로 Spring Boot 서버에 요청합니다. 서버는 Spring Data JPA를 통해 MySQL에서 데이터를 읽고 저장합니다.

**서버 내부 설계안**은 Controller, Service, Repository로 역할을 나눕니다. Controller는 요청 검증과 응답을, Service는 업무 규칙과 트랜잭션을, Repository는 데이터
접근을 맡습니다. 강의 탐색·강의평·시간표 코드를 도메인별로 모으면 세 명이 나눠 작업할 때 코드 위치와 변경 범위를 파악하기 쉽습니다.

## 🚀 Getting Started

JDK 21과 Docker를 준비하고 프로젝트 루트에서 실행합니다.

1. `.env.example`을 `.env`로 복사합니다.

   ```bash
   cp .env.example .env
   ```

2. `.env`에 아래 값을 설정합니다.

   | 키 | 설정할 값 |
   | --- | --- |
   | `DB_NAME` | 로컬 데이터베이스 이름. 기본값은 `everytime`입니다. |
   | `DB_USERNAME` | Spring이 접속할 로컬 MySQL 계정 이름 |
   | `DB_PASSWORD` | 위 계정의 비밀번호 |
   | `DB_ROOT_PASSWORD` | 로컬 MySQL 관리자 계정의 비밀번호 |
   | `DB_PORT` | Mac에서 접속할 포트. 기본값은 `3306`이며, 사용 중이면 `3307`로 설정합니다. |

3. MySQL을 시작합니다.

   ```bash
   docker compose up -d mysql
   docker compose ps mysql
   ```

4. MySQL 상태가 `healthy`가 되면 서버를 실행합니다.

   ```bash
   ./gradlew bootRun
   ```

   로그에 `Started EverytimeApplication`이 표시되면 서버가 시작된 상태입니다.

## 💻 Convention

**이슈 생성 → 브랜치 생성 → PR → 리뷰 → 머지**

[Code Style](#code-style) · [Commit Tags](#commit-tags) · [Commit Message](#commit-message) · [Branch & Merge](#branch--merge) · [Issue & PR](#issue--pr) · [API Convention](#api-convention)

### Code Style

- [네이버 캠퍼스 핵데이 Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)을 따릅니다.
- IntelliJ에 팀 포매터를 적용하고 저장 시 코드 서식과 import를 정리합니다.
- 들여쓰기는 탭 4칸, 줄 길이는 120자를 기준으로 합니다. 한 줄 제어문에도 중괄호를 사용합니다.
- 클래스는 `UpperCamelCase`, 메서드와 변수는 `lowerCamelCase`, 상수는 `UPPER_SNAKE_CASE`로 작성합니다.

### Commit Tags

|     태그     | 사용 범위                      |
|:----------:|----------------------------|
|   `feat`   | 새 기능 추가                    |
|   `fix`    | 버그 수정                      |
| `refactor` | 동작을 유지하면서 코드 구조 개선         |
|  `style`   | 포맷과 공백 등 코드 형식 수정          |
|   `docs`   | README, Swagger 설명 등 문서 수정 |
|   `test`   | 테스트 코드 추가·수정               |
|  `chore`   | 설정, 의존성 등 기타 작업            |
|    `ci`    | CI/CD 설정 변경                |
|  `rename`  | 파일·폴더 이름 변경 또는 이동          |
|  `remove`  | 파일 삭제                      |
|  `hotfix`  | 배포 환경 긴급 수정                |
|   `init`   | 프로젝트 초기 세팅                 |

### Commit Message

1. `[type/#이슈번호] 제목` 형식을 사용합니다. PR 제목도 같은 형식으로 작성합니다.
2. 타입은 영어 소문자, 제목은 한국어 50자 이내로 작성합니다. 제목은 명사형으로 끝내고 마침표를 생략합니다.
3. 커밋 하나에 논리적 변경 하나를 담습니다. 본문이 필요하면 변경 내용과 이유를 적습니다.

```text
[chore/#3] 프로젝트 README 작성
[feat/#12] 강의 목록 조회 API 구현
[fix/#21] 시간표 중복 추가 오류 수정
```

### Branch & Merge

브랜치 이름은 `type/#이슈번호-작업-요약` 형식입니다. 작업 요약은 영어 소문자와 하이픈으로 최대 세 단어를 사용합니다.

```text
chore/#3-write-readme
feat/#12-lecture-list
```

| 구간                 | 머지 방식                |
|--------------------|----------------------|
| 작업 브랜치 → `develop` | **Squash and merge** |
| `develop` → `main` | **Rebase and merge** |

일반 작업은 `develop`에서 분기합니다. 긴급 수정은 `main`에서 분기하고 `main`과 `develop`에 반영합니다. 두 통합 브랜치의 변경은 PR을 통해 반영합니다.

### Issue & PR

- 작업 전 이슈 템플릿을 선택하고 본인을 담당자로 지정한 뒤 타입 라벨을 붙입니다. 이슈 하나에 브랜치와 PR을 하나씩 연결합니다.
- PR에는 관련 이슈, 변경 이유, 확인 결과를 적습니다. 서브 담당자를 리뷰어로 지정하고 최소 1명 승인 후 작성자가 머지합니다. 머지한 작업 브랜치는 삭제합니다.
- 리뷰 머리말은 `[필수]`, `[제안]`, `[질문]`, `[칭찬]`을 사용합니다.

### API Convention

- 성공과 실패는 `BaseResponse`로 응답하고, 본문의 `code`는 실제 HTTP 상태 코드와 같은 문자열을 사용합니다.
- DTO는 요청 형식을 검증하고 Service는 업무 규칙을 확인합니다. 비즈니스 예외는 `BusinessException`으로 던지고 `GlobalExceptionHandler`에서 처리합니다.
- 목록의 페이지 번호는 요청과 응답 모두 1부터 시작합니다. 응답 계약을 변경하면 Swagger 설명도 함께 수정합니다.
