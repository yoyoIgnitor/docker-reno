include .env
export
all: init-dev build

PYTHON_BASE_IMAGE=kiranrenovate.jfrog.io/docker-remote/python38:2.0.39

uname_s := $(shell uname -s)
ifeq ($(shell uname -s), Darwin)
	ifeq ($(shell uname -p), arm)
		M1_BUILD=true
		PYTHON_BASE_IMAGE=kiranrenovate.jfrog.io/docker-remote/python:3.9-bullseye
	endif
endif

init-dev-python:
	pip3 install -r requirements.txt

init-dev: init-dev-python
	$(MAKE) -C mgmt_api init-dev
	$(MAKE) -C api_docs init-dev
	$(MAKE) -C go/rules_manager init-dev
	$(MAKE) -C go/alerts_manager init-dev
	$(MAKE) -C go/commands_publisher init-dev
	$(MAKE) -C go/maintenance_manager init-dev
	$(MAKE) -C go/mgmt_entities_loader init-dev
	$(MAKE) -C go/uam_manager init-dev

build-all:
	$(MAKE) build-role ROLE_DIR_NAME=mgmt_api
	$(MAKE) build-role ROLE_DIR_NAME=go/rules_manager
	$(MAKE) build-role ROLE_DIR_NAME=go/alerts_manager
	$(MAKE) build-role ROLE_DIR_NAME=go/commands_publisher
	$(MAKE) build-role ROLE_DIR_NAME=go/maintenance_manager
	$(MAKE) build-role ROLE_DIR_NAME=go/mgmt_entities_loader
	$(MAKE) build-role ROLE_DIR_NAME=go/uam_manager
	$(MAKE) build-role ROLE_DIR_NAME=mms_migration
	$(MAKE) build-role ROLE_DIR_NAME=alerts_table_update

validate-git-token:
# no tabs must precede these lines
ifndef GIT_TOKEN
	$(error GIT_TOKEN is not set)
endif
ifndef GHE_TOKEN
	$(error GHE_TOKEN is not set)
endif
ifndef USER
	$(error USER is not set)
endif

build-role:
	${MAKE} validate-git-token
	echo "build role $(ROLE_DIR_NAME)"
	$(MAKE) -C $(ROLE_DIR_NAME) build

build-test:
	docker build -t "tests-docker" --secret id=pip-conf,src=${HOME}/.pip/pip.conf  --build-arg PYTHON_BASE_IMAGE=${PYTHON_BASE_IMAGE} -f ./tests/tests-docker/Dockerfile  .

dc-up:
	$(MAKE) -C dev up

dc-down:
	$(MAKE) -C dev down

dc-restart:
	$(MAKE) dc-down
	sleep 2
	$(MAKE) dc-up

dc-restart-service:
	$(MAKE) -C dev restart SERVICE=$(SERVICE)

lint:
	cd go && golangci-lint run ./uam_manager/...