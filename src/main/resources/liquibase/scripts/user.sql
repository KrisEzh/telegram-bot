
-- liquibase formatted sql

-- changeset KrisEzh:1
CREATE TABLE notification_task (
                       id SERIAL PRIMARY KEY ,
                       chatId INT,
                       text TEXT,
                       Local_Date_Time timestamp
)

