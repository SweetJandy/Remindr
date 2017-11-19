-- MYSQL Database Setup
-- LOGIN USING ROOT
mysql -u root -p
-- Enter PASSWORD

-- CREATE USER
CREATE USER 'remindr_user'@'localhost' IDENTIFIED BY 'remindr_text210!';

-- CREATE DATABASE
CREATE DATABASE IF NOT EXISTS remindr_db; 

-- GRANT ALL PRIVILEGES TO USER
GRANT ALL ON remindr_db.* TO 'remindr_user'@'localhost'; 



