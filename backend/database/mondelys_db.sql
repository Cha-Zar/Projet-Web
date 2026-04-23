CREATE DATABASE IF NOT EXISTS mondelys_db;
USE mondelys_db;

CREATE TABLE IF NOT EXISTS admins (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  role VARCHAR(40) NOT NULL,
  created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS app_settings (
  id BIGINT NOT NULL PRIMARY KEY,
  address VARCHAR(220) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  description VARCHAR(1200) NOT NULL,
  dinner_sunday VARCHAR(80) NOT NULL,
  dinner_week VARCHAR(80) NOT NULL,
  lunch_sunday VARCHAR(80) NOT NULL,
  lunch_week VARCHAR(80) NOT NULL,
  public_email VARCHAR(180) NOT NULL,
  public_phone VARCHAR(40) NOT NULL,
  restaurant_name VARCHAR(140) NOT NULL,
  updated_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS daily_menus (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  menu_date DATE NOT NULL UNIQUE,
  chef_note VARCHAR(2000),
  dish1_name VARCHAR(180),
  dish1_description VARCHAR(1200),
  dish1_price VARCHAR(40),
  dish1_origin VARCHAR(120),
  dish2_name VARCHAR(180),
  dish2_description VARCHAR(1200),
  dish2_price VARCHAR(40),
  dish2_origin VARCHAR(120),
  dish3_name VARCHAR(180),
  dish3_description VARCHAR(1200),
  dish3_price VARCHAR(40),
  dish3_origin VARCHAR(120),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS client_reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL,
  email VARCHAR(180) NOT NULL,
  full_name VARCHAR(140) NOT NULL,
  message VARCHAR(1800) NOT NULL,
  rating INT NOT NULL,
  responded BIT(1) NOT NULL,
  responded_at DATETIME(6),
  responded_by VARCHAR(180),
  response_message VARCHAR(1800),
  title VARCHAR(180),
  updated_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS contact_messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  admin_response VARCHAR(3000),
  created_at DATETIME(6) NOT NULL,
  email VARCHAR(180) NOT NULL,
  first_name VARCHAR(120) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  message VARCHAR(3000) NOT NULL,
  phone VARCHAR(40),
  read_by_admin BIT(1) NOT NULL,
  responded BIT(1) NOT NULL,
  responded_at DATETIME(6),
  responded_by VARCHAR(180),
  subject VARCHAR(180) NOT NULL,
  updated_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reference_code VARCHAR(30) NOT NULL UNIQUE,
  first_name VARCHAR(120) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL,
  phone VARCHAR(40) NOT NULL,
  reservation_date DATE NOT NULL,
  reservation_time TIME NOT NULL,
  guests_count INT NOT NULL,
  occasion VARCHAR(180),
  preorder VARCHAR(220),
  special_requests VARCHAR(1500),
  status VARCHAR(40) NOT NULL,
  admin_note VARCHAR(1200),
  verified_by VARCHAR(120),
  verified_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);
