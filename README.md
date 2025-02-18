# sync-github-contribution

## 설명
Github Enterprise의 기여도를 Github에 동기화하는 프로젝트입니다.

## 사용법
1. https://github.com/psg107/sync-github-contribution/generate 에서 레포지토리를 생성합니다.
2. 레포지토리를 클론합니다.
3. config.properties 파일을 생성한 후 아래와 같이 작성합니다.
```properties
domain={github 도메인}
session={user_session 쿠키, 브라우저 개발자 도구를 통해 확인}
```
4. 프로젝트를 실행 후 사용자명과 년도를 입력합니다. (가장 마지막 커밋 메시지의 날짜 이후로 기여도가 동기화되기에 오래된 년도부터 동기화를 진행하는 것을 권장합니다.)
5. 기여도가 동기화됩니다.

## 기타
- 모든 기여도를 커밋으로 간주합니다. (리뷰, 이슈 및 PR 생성 등)
- 사내 Github Enterprise 환경 기준으로 작성되었기에 다른 환경에서는 동작하지 않을 수 있습니다.
