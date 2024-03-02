check: check-leaders check-workers check-first check-optimizer

format: format-first format-optimizer

check-leaders:
	cd leaders && ./gradlew check

check-workers:
	cd workers && ./gradlew check

check-first:
	cd microservices/first && python3 -m mypy --strict . && python3 -m pylint main.py utils/ && python3 -m black --check main.py utils 

check-optimizer:
	cd microservices/optimizer && python3 -m mypy --strict . && python3 -m pylint main.py utils/ && python3 -m black --check main.py utils 

format-first:
	cd microservices/first && python3 -m black main.py utils 

format-optimizer:
	cd microservices/first && python3 -m black main.py utils 

docker:
	docker build -t ghcr.io/jakejack13/speed-workers workers/
	docker build -t ghcr.io/jakejack13/speed-leaders leaders/
	docker build -t ghcr.io/jakejack13/speed-first microservices/first/
	docker build -t ghcr.io/jakejack13/speed-optimizer microservices/optimizer

define HELP_BODY
	docker: build all Docker images for SPEED
	check: runs check-java and check-python
	check-leaders: runs all style and lint checks for leaders project
	check-workers: runs all style and lint checks for workers project
	check-first: runs all style and lint checks for first project
	check-optimizer: runs all style and lint checks for optimizer project
	check-first: formats first project
	check-optimizer: formats optimizer project
endef
export HELP_BODY

help:
	@echo "$$HELP_BODY"
