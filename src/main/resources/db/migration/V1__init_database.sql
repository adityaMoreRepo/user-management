CREATE TABLE `users` (
    `user_id` int NOT NULL AUTO_INCREMENT,
    `email_id` VARCHAR(255) NOT NULL,
    `first_name` VARCHAR(255) NOT NULL,
    `last_name` VARCHAR(255) NOT NULL,
    `mobile_no` VARCHAR(255) NOT NULL,
    PRIMARY KEY(`user_id`)
);

CREATE TABLE `address` (
    `address_id` int NOT NULL AUTO_INCREMENT,
    `address_type` VARCHAR(255) NOT NULL,
    `house_no` int NOT NULL,
    `street` VARCHAR(255),
    `locality` VARCHAR(255),
    `city` VARCHAR(255),
    `state` VARCHAR(255),
    `country` VARCHAR(255),
    `pincode` VARCHAR(255) NOT NULL,
    `user_id` int,
    PRIMARY KEY(`address_id`),
    FOREIGN KEY (`user_id`) REFERENCES users(`user_id`)
)
