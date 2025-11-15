#!/bin/zsh

brew install ffmpeg

cd "$(dirname "$0")"

python3 -m venv venv
source venv/bin/activate

pip install -r requirements.txt

export WHISPER_MODEL="mlx-community/whisper-large-v3-mlx"
export WHISPER_LANGUAGE="ko"

uvicorn main:app --host 0.0.0.0 --port 8000
