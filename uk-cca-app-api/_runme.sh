#!/bin/bash

docker compose up -d
mvn spring-boot:run -Pdev-maven -Dspring-boot.run.profiles=dev
