
-- liquibase formatted sql

-- changeset KrisEzh:1
CREATE TABLE notification_task (
                       id SERIAL,
                       text TEXT,
                       Local_Date_Time timestamp
)
