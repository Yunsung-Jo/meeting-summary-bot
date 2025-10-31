# Meeting Summary Bot
faster-whisper와 gemini를 기반으로 디스코드 음성 대화를 요약하는 봇입니다.

## 명령어
1. `/회의`: 지정한 음성 채널을 녹음합니다.
2. `/요약`: 진행 중인 회의를 요약합니다.
3. `/종료`: 진행 중인 회의를 종료하고 요약합니다.

## 실행 방법
1. [Releases](https://github.com/Yunsung-Jo/meeting-summary-bot/releases)에서 `meeting-summary-bot.zip`을 다운로드 및 압축 해제하세요.
2. [Discord for Developers](https://discord.com/developers/applications)에서 새로운 앱을 생성하고 토큰을 발급 받아주세요.
3. [Google AI Studio](https://aistudio.google.com/)에서 API 키를 만들어주세요.
4. 발급받은 키를 `.env` 파일에 넣어주세요.

    ```env
    # Discord
    DISCORD_TOKEN=
    
    # GenAI
    GEMINI_API_KEY=
    MODEL_NAME=gemini-2.5-flash-lite
    ```

5. 실행에는 도커가 필요하고 `docker compose up --build -d` 명령어로 실행할 수 있습니다.

## 커스텀
1. 실행 환경에 따라 동작하지 않을 수 있습니다.<br>
  [faster-whisper](https://github.com/SYSTRAN/faster-whisper)를 참고하여 `docker-compose.yml`의 `environment`를 적절하게 수정해주세요.
2. `PROMPT` 파일을 수정하면 규칙이나 요약 형식을 수정할 수 있고, 프롬프트 변수를 사용할 수 있습니다.

    - `%timestamp`: 프롬프트에 시간 정보를 제공합니다.
    - `%attendees`: 프롬프트에 참석자 정보를 제공합니다.
    - `%script`: 프롬프트에 대화 내용을 제공합니다.

## 주의 사항
1. 첫 실행에는 `whisper-api`가 `./build/whisper` 경로에 선택한 모델을 다운로드하기 때문에 시간이 오래 걸립니다.
2. `whisper-api` 컨테이너에서 `INFO: Uvicorn running on http://0.0.0.0:8000⁠ (Press CTRL+C to quit)` 로그를 확인한 후, 디스코드에서 명령어를 실행하면 됩니다.

## 사용 예시
<img width="800" alt="example" src="https://github.com/user-attachments/assets/cad288df-ffc2-4da2-9454-6fb455ed8a70" />
