#!/bin/bash

docker compose up -d
mvn spring-boot:run # | jq -R 'try fromjson catch .'
