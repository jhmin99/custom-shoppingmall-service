-- Insert data into USERS table
INSERT INTO USERS(identification, password, role)
VALUES('admin12345', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'ADMIN');

INSERT INTO USERS(identification, password, name, birth_date, phone_number, role) VALUES
('abc12345', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user1','1999-12-30','01012341234','USER'),
('abc12346', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user2','1999-12-30','01012341234','USER'),
('abc12347', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user3','1999-12-30','01012341234','USER'),
('abc12348', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user4','1999-12-30','01012341234','USER'),
('abc12349', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user5','1999-12-30','01012341234','USER'),
('abc12340', '$2a$12$c7I6U0HS/0Q97.tUjhAQkeXcd9RGRSmHkyLldnXlBDavkyGModZeO', 'user6','1999-12-30','01012341234','USER');


-- Insert data into DELIVERY_ADDRESS table
INSERT INTO DELIVERY_ADDRESS(user_id, name, phone_number, zip_code, address, address_detail)
VALUES(2, '지홍민', '01011112222', 14502, '홍쇼핑로 331', '111-3333');

-- Insert data into CATEGORY table
INSERT INTO CATEGORY(name) VALUES
('Electronics'),
('Books'),
('Clothing'),
('Home Appliances');

-- Insert data into ITEM table
INSERT INTO ITEM(name, price, inventory, keyword) VALUES
('Laptop', 1400000, 50, '#노트북#전자제품#맥북'),
('Smartphone', 700000, 100, '#휴대폰#갤럭시#갤럭시21'),
('Fiction Book', 8000, 15, '#소설#피터팬'),
('T-shirt', 27000, 500, '#티셔츠#남성#유니섹스'),
('Jeans', 34000, 500, '#청바지#빅사이즈'),
('Microwave', 160000, 30, '#전자레인지#국산#정품#삼성'),
('Refrigerator', 4000000, 80, '#냉장고#1등급#에코#LG#신제품');

-- Insert data into CATEGORY_ITEM table
INSERT INTO CATEGORY_ITEM(item_id, category_id) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 3),
(5, 3),
(6, 4),
(7, 4);




