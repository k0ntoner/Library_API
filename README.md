-- Створення таблиці users
CREATE IF NOT EXISTS TABLE users (
id BIGINT PRIMARY KEY,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) NOT NULL,
username VARCHAR(30) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
phone_number VARCHAR(14)
);

-- Створення таблиці customers
CREATE IF NOT EXISTS TABLE customers (
user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
date_of_birth DATE NOT NULL
);

-- Створення таблиці employees
CREATE  IF NOT EXISTS TABLE employees (
user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
salary DECIMAL(10,2) NOT NULL
);

-- Створення таблиці books
CREATE  IF NOT EXISTS TABLE books (
id BIGINT PRIMARY KEY,
title VARCHAR(30) NOT NULL,
description VARCHAR(255),
author VARCHAR(30) NOT NULL
);

-- Створення таблиці book_copies
CREATE IF NOT EXISTS TABLE book_copies (
id BIGINT PRIMARY KEY,
book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
status VARCHAR(10) CHECK (status IN ('available', 'borrowed')) NOT NULL
);

-- Створення таблиці orders
CREATE IF NOT EXISTS TABLE orders (
id BIGINT PRIMARY KEY,
user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
copy_id BIGINT NOT NULL REFERENCES book_copies(id) ON DELETE CASCADE,
subscription_type VARCHAR(15) CHECK (location IN ('reading_room', 'subscription')) NOT NULL,
order_date TIMESTAMP NOT NULL,
expiration_date TIMESTAMP NOT NULL,
status VARCHAR(10) CHECK (status IN ('borrowed', 'returned', 'overdue')) NOT NULL
);