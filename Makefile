.PHONY: check format check-leaders check-workers check-first check-optimizer format-first format-optimizer docker help gradle python black

# Phony targets
gradle:
	./gradlew check

python:
	python3 -m mypy --strict app.py utils/ && python3 -m pylint app.py utils/ && python3 -m black --check app.py utils/

black:
	python3 -m black main.py utils/

# Start of real targets
check: check-leaders check-workers check-first check-optimizer

format: format-first format-optimizer

check-leaders:
	@cd leaders && $(MAKE) -f ../Makefile -s gradle

check-workers:
	@cd workers && $(MAKE) -f ../Makefile -s gradle

check-first:
	@cd microservices/first && $(MAKE) -f ../../Makefile -s python

check-optimizer:
	@cd microservices/optimizer && $(MAKE) -f ../../Makefile -s python

format-first:
	@cd microservices/first && $(MAKE) -f ../../Makefile -s black

format-optimizer:
	@cd microservices/optimizer && $(MAKE) -f ../../Makefile -s black

docker:
	docker build -t ghcr.io/jakejack13/speed-workers workers/
	docker build -t ghcr.io/jakejack13/speed-leaders leaders/
	docker build -t ghcr.io/jakejack13/speed-first microservices/first/
	docker build -t ghcr.io/jakejack13/speed-optimizer microservices/optimizer

define HELP_BODY
	docker: build all Docker images for SPEED
	check: runs all check commands
	format: runs all format commands
	check-leaders: runs all style and lint checks for leaders project
	check-workers: runs all style and lint checks for workers project
	check-first: runs all style and lint checks for first project
	check-optimizer: runs all style and lint checks for optimizer project
	format-first: formats first project
	format-optimizer: formats optimizer project
endef
export HELP_BODY

help:
	@echo "$$HELP_BODY"
