import os
from fastapi import FastAPI, HTTPException
from faster_whisper import WhisperModel
from pydantic import BaseModel

# 1. 모델 로드
MODEL_SIZE = os.getenv("WHISPER_MODEL", "base")
DEVICE = os.getenv("WHISPER_DEVICE", "cpu")
COMPUTE_TYPE = os.getenv("WHISPER_COMPUTE_TYPE", "int8")
DEFAULT_LANGUAGE = os.getenv("WHISPER_LANGUAGE", "ko")

print(f"Whisper 모델 로드 중: {MODEL_SIZE} (device: {DEVICE}, compute: {COMPUTE_TYPE})")
try:
    model = WhisperModel(MODEL_SIZE, device=DEVICE, compute_type=COMPUTE_TYPE)
    print("Whisper 모델 로드 완료")
except Exception as e:
    print(f"모델 로드 실패: {e}")
    model = None

app = FastAPI()


# 2. 요청/응답 모델 정의
class TranscriptionRequest(BaseModel):
    file_path: str


class TranscriptionResponse(BaseModel):
    transcription: str
    language: str
    language_probability: float


# 3. API 엔드포인트
@app.post("/transcribe/", response_model=TranscriptionResponse)
def transcribe_audio_path(request: TranscriptionRequest):
    if model is None:
        raise HTTPException(status_code=503, detail="모델이 로드되지 않았습니다. 서버 로그를 확인하세요.")

    file_path = request.file_path

    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail=f"컨테이너 내에서 파일을 찾을 수 없습니다: {file_path}")

    if not file_path.lower().endswith((".wav", ".mp3", ".m4a")):
        raise HTTPException(status_code=400, detail="지원되는 오디오 형식이 아닙니다. (.wav, .mp3, .m4a 등)")

    print(f"변환 요청 수신: {file_path}")

    try:
        # faster-whisper로 텍스트 변환 수행
        segments, info = model.transcribe(file_path, beam_size=5, language=DEFAULT_LANGUAGE)

        # 변환된 텍스트 조합
        transcription = " ".join(segment.text for segment in segments).strip()

        print(f"변환 완료 (언어: {info.language}, 정확도: {info.language_probability})")

        return TranscriptionResponse(
            transcription=transcription,
            language=info.language,
            language_probability=info.language_probability
        )

    except Exception as e:
        print(f"오류 발생: {e}")
        raise HTTPException(status_code=500, detail=f"파일 처리 중 오류 발생: {str(e)}")


@app.get("/")
def read_root():
    return {"message": "Faster-Whisper API (Docker/Path) 실행 중입니다."}
