.PHONY: build up down check format check-leaders check-workers check-first check-optimizer format-first format-optimizer docker help gradle python black

### Docker commands
build: down
	docker compose build --pull

up: build
	docker compose up -d --build

down:
	docker compose down -v
	docker rm -vf $(docker ps -aq) 2> /dev/null || echo No containers to remove


### Kubernetes commands
apply:
	cd kubernetes && ./apply.sh -t

delete:
	cd kubernetes && ./delete.sh -t


### Host commands
gradle:
	./gradlew check

python:
	python3 -m mypy --strict app.py utils/ && python3 -m pylint app.py utils/ && python3 -m black --check app.py utils/

black:
	python3 -m black app.py utils/

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

define HELP_BODY
	# DOCKER
	build: build all Docker images for SPEED
	up: runs SPEED with docker compose
	down: stops and removes containers 

	# KUBERNETES
	apply: runs SPEED with Kubernetes
	delete: stops and removes services

	# HOST
	The below commands are all run on the host machine, not inside Docker. We recommend using the above commands for a more standard experience.

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
