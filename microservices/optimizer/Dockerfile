FROM python:3.12.2-slim

WORKDIR /app

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .
RUN python3 -m mypy --strict app.py utils/ && python3 -m pylint app.py utils/ && python3 -m black --check app.py utils/

EXPOSE 5002

LABEL org.opencontainers.image.source=https://github.com/jakejack13/speed
LABEL org.opencontainers.image.description="optimizer image"
CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0", "--port=5002"]
