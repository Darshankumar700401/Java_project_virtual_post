CREATE DATABASE dhachu;
USE dhachu;

CREATE TABLE users (
    username VARCHAR(20) PRIMARY KEY,
    password VARCHAR(15) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(50) NOT NULL
);


CREATE TABLE parcels (
    parcel_id VARCHAR(10) PRIMARY KEY,
    sender_name VARCHAR(100),
    sender_phone VARCHAR(20),
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(20),
    address VARCHAR(255),
    pincode VARCHAR(10),
    from_branch VARCHAR(50),
    to_branch VARCHAR(50),
    status VARCHAR(20),
    otp VARCHAR(10)
);





