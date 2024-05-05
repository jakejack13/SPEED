FROM python:3.12.3-slim

RUN apt update \
 && DEBIAN_FRONTEND=noninteractive \
    apt install --assume-yes --no-install-recommends \
      docker-compose \
      docker.io \
      iputils-ping \
 && pip install poetry

WORKDIR /app

COPY pyproject.toml pyproject.toml
COPY poetry.lock poetry.lock
RUN python3 -m venv /venv && . /venv/bin/activate && poetry install

COPY . .
RUN . /venv/bin/activate && python3 -m mypy --strict app.py utils/ && python3 -m pylint app.py utils/ && python3 -m black --check app.py utils/

EXPOSE 5001

LABEL org.opencontainers.image.source=https://github.com/jakejack13/speed
LABEL org.opencontainers.image.description="first image"

CMD . /venv/bin/activate && python3 app.py
