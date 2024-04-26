#!/bin/bash

#editing /etc/passwd file for everyuser to start with bash
sed -i 's|/bin/sh|/bin/bash|g' /etc/passwd

#rest up setup below

USER="root"
PASSWORD="password"
DATABASE="db"

mysqld_safe &

# Wait for MySQL to be ready
until mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" -e "SELECT 1"; do
    sleep 1
done

mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" -e "CREATE DATABASE $DATABASE"
mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" $DATABASE < /mockDraft.sql
mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" -e "SET PASSWORD FOR '$USER'@'localhost' = PASSWORD('$PASSWORD')"

# Example: Update the database by running a specific SQL command

java -jar /app.jar
