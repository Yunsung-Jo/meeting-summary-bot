import mlx_whisper
import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI()
mlx_lock = threading.Lock()  # 전역 락

# 1. 모델 설정
# Hugging Face MLX 커뮤니티의 모델 저장소 ID
# https://huggingface.co/collections/mlx-community/whisper
MODEL_HF_REPO = os.getenv("WHISPER_MODEL", "mlx-community/whisper-large-v3-mlx")
LANGUAGE = os.getenv("WHISPER_LANGUAGE")

print(f"MLX Whisper ({MODEL_HF_REPO}) 준비 완료.")
if LANGUAGE:
    print(f"인식 언어: {LANGUAGE}")
else:
    print("인식 언어: 자동 감지")
print("(첫 실행 시 모델을 자동으로 다운로드합니다.)")


# 2. 요청/응답 모델 정의
class TranscriptionRequest(BaseModel):
    file_path: str


class TranscriptionResponse(BaseModel):
    transcription: str


# 3. API 엔드포인트
@app.post("/transcribe/", response_model=TranscriptionResponse)
def transcribe_audio(req: TranscriptionRequest):
    file_path = f"./{req.file_path}"

    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail=f"파일이 존재하지 않음: {file_path}")

    with mlx_lock:
        try:
            print(f"변환 시작 (락 획득): {file_path}")

            # language가 설정되어 있으면 인자 추가, 없으면 자동 감지
            if LANGUAGE:
                result = mlx_whisper.transcribe(
                    file_path,
                    path_or_hf_repo=MODEL_HF_REPO,
                    language=LANGUAGE,
                    fp16=True
                )
            else:
                result = mlx_whisper.transcribe(
                    file_path,
                    path_or_hf_repo=MODEL_HF_REPO,
                    fp16=True
                )

            transcription = result["text"]
            print(f"변환 완료 (락 해제): {file_path}")

            return TranscriptionResponse(transcription=transcription)

        except Exception as e:
            print(f"오류 발생: {e}")
            raise HTTPException(status_code=500, detail=f"파일 처리 중 오류 발생: {str(e)}")


@app.get("/")
def read_root():
    return {"message": "mlx-whisper API 실행 중입니다."}
