# sync-github-contribution

## 설명
Github Enterprise의 기여도를 Github에 동기화하는 프로젝트입니다.

## 사용법
1. https://github.com/psg107/sync-github-contribution/generate 에서 레포지토리를 생성합니다.
2. 로컬에 레포지토리를 클론합니다.
3. config.properties 파일을 만든 후 아래와 같이 작성합니다.
```properties
domain={github 도메인}
session={user_session 쿠키}
```
4. 프로젝트를 실행 후 사용자명과 년도를 입력합니다. (오래된 년도부터 입력을 추천합니다.)
5. 기여도가 동기화됩니다.
