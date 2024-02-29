FROM python:3.12

WORKDIR /app

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .
RUN python3 -m mypy --strict . && python3 -m pylint main.py utils/ && python3 -m black --check main.py utils 

LABEL org.opencontainers.image.source=https://github.com/jakejack13/speed
LABEL org.opencontainers.image.description="first image"

ENTRYPOINT [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]