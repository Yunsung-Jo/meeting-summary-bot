# Meeting Summary Bot

faster-whisper와 gemini를 기반으로 디스코드 음성 대화를 요약하는 봇입니다.

## 명령어

1. `/회의 시작`: 지정한 음성 채널을 녹음합니다.
2. `/회의 요약`: 진행 중인 회의를 요약합니다.
3. `/회의 종료`: 진행 중인 회의를 종료하고 요약합니다.
4. `/백업`: 요약한 내용을 다른 저장소에 백업합니다.

## 실행 방법

1. [Releases](https://github.com/Yunsung-Jo/meeting-summary-bot/releases)에서 `meeting-summary-bot.zip`을 다운로드 및 압축 해제하세요.
2. [Discord for Developers](https://discord.com/developers/applications)에서 새로운 앱을 생성하고 토큰을 발급받아 주세요.
3. [Google AI Studio](https://aistudio.google.com/)에서 API 키를 만들어주세요.
4. 발급받은 키를 `.env` 파일에 넣어주세요.
    ```env
    # Discord
    DISCORD_TOKEN=
    
    # GenAI
    GEMINI_API_KEY=
    ```
5. 실행에는 도커가 필요하고 `docker compose up -d` 명령어로 실행할 수 있습니다.
6. [Discord for Developers](https://discord.com/developers/applications)에서 Bot → Message Content Intent 설정을 체크해 주세요.
   <img width="1401" height="85" alt="message-content-intent" src="https://github.com/user-attachments/assets/04a9f47a-38bc-4df2-9444-485386b7aff4" />
8. [Discord for Developers](https://discord.com/developers/applications)에서 Settings → OAuth2 → OAuth2 URL Generator의
   bot을 체크하고 Bot Permissions는 필요한 권한만 체크해 주세요.
   <img width="800" alt="permissions" src="https://github.com/user-attachments/assets/3ee22868-341c-4d15-aaa3-43b0d031118f" />
9. 생성된 URL로 원하는 서버에 봇을 추가하세요.

## 커스텀

1. 실행 환경에 따라 동작하지 않을 수 있습니다.<br>
   [faster-whisper](https://github.com/SYSTRAN/faster-whisper)를 참고하여 `docker-compose.yml`의 `environment`를 적절하게 수정해주세요.
2. `PROMPT` 파일을 수정하면 규칙이나 요약 형식을 수정할 수 있고, 프롬프트 변수를 사용할 수 있습니다.
    - `%timestamp`: 프롬프트에 시간 정보를 제공합니다.
    - `%attendees`: 프롬프트에 참석자 정보를 제공합니다.
    - `%script`: 프롬프트에 대화 내용을 제공합니다.
3. `USE_BACKUP`을 `true`로 설정하면 백업 명령을 사용할 수 있습니다.

### Confluence 설정 방법

1. `BACKUP_STRATEGY`를 `confluence`로 설정해 주세요.
2. Confluence URL에서 각 위치에 있는 문자열을 `.env` 파일에 넣어주세요.
    ```env
    https://{CONFLUENCE_DOMAIN}.atlassian.net/wiki/spaces/{CONFLUENCE_SPACE}
    ```
3. `CONFLUENCE_EMAIL`에 사용자 이메일을 넣어주세요.
4. Account settings → 보안 → API 토큰에서 토큰을 발급받고 `CONFLUENCE_API_TOKEN`에 넣어주세요.
5. `BACKUP_PATH`에 페이지가 생성될 폴더의 ID를 다음과 같은 형식으로 입력해 주세요. ','로 여러 경로를 입력할 수 있습니다.
    ```env
    BACKUP_PATH="{폴더 이름}:{폴더 ID},{폴더 이름}:{폴더 ID}"
    ```
6. 폴더 ID는 링크 복사를 통해 쉽게 찾을 수 있습니다.
   ```env
   https://{CONFLUENCE_DOMAIN}.atlassian.net/wiki/spaces/{CONFLUENCE_SPACE}/folder/{FOLDER_ID}?atlOrigin=
   ```

## 주의 사항

1. 첫 실행에는 `whisper-api`가 `./build/whisper` 경로에 선택한 모델을 다운로드하기 때문에 시간이 오래 걸립니다.
2. `whisper-api` 컨테이너에서 `INFO: Uvicorn running on http://0.0.0.0:8000⁠ (Press CTRL+C to quit)` 로그를 확인한 후, 디스코드에서 명령어를
   실행하면 됩니다.

## 사용 예시

<img width="800" alt="example" src="https://github.com/user-attachments/assets/bf9a15ae-edc5-486b-adf1-947f42ee4505" />
