#!/bin/bash

USER="root"
PASSWORD="password"
DATABASE="db"

mysqld_safe & sleep 5
mysql -u $USER -p$PASSWORD -e "CREATE DATABASE $DATABASE"
mysql -u $USER -p$PASSWORD $DATABASE < /mockDraft.sql

java -jar /app.jar
