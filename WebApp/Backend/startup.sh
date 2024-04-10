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
mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" $DATABASE < /mockDraft2.sql
mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" -e "SET PASSWORD FOR '$USER'@'localhost' = PASSWORD('$PASSWORD')"

# Example: Update the database by running a specific SQL command
mysql -h localhost -P 3306 -u $USER --password="$PASSWORD" -D $DATABASE -e "INSERT INTO users (username, salt, hash_pass) VALUES ('a', '7abf883fa820fa5097be8cd2f7013b51', 'ea93af66de80eb734a52a3c8671ed393631dca350cc67ccbdf6e1d3ac21e9bc3');"

java -jar /app.jar
