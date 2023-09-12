#!/bin/bash

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


#rap "mysqladmin -h localhost -P 3306 -u $USER --password=\"$PASSWORD\" shutdown" EXIT

java -jar /app.jar
