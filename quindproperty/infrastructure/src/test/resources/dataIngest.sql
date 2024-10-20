INSERT INTO "User" (userId, email, firstName, lastName, age, password, role) VALUES
    ('a7b5f905-c5fc-481d-9389-11b4b9e4a123', 'guest1@example.com', 'Guest', 'One', 30, 'password123encode', 'GUEST'),
    ('b8d6e716-c7bd-4720-a2db-22c5c7d4b456', 'admin@example.com', 'Admin', 'User', 40, 'adminpasswordencode', 'ADMIN'),
    ('c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789', 'user1@example.com', 'User', 'One', 25, 'userpassword1encode', 'USER'),
    ('d0f8f938-ebdf-4732-b7fa-44e7f8f7d012', 'user2@example.com', 'User', 'Two', 28, 'userpassword2encode', 'USER'),
    ('e1f9f938-fdec-4832-b7fa-44e7f8e7d345', 'guest2@example.com', 'Guest', 'Two', 35, 'password456encode', 'GUEST');

INSERT INTO Property (propertyId, name, cityId, img, price, available, active, dateCreated) VALUES
    ('48a234c4-ef02-4f96-8a04-82307b1d31a4', 'Luxury Apartment Medellin', 'b81e8a80-4375-4b8a-b5cf-12e5699f4cd3', 'img1.jpg', 500000.00, TRUE, TRUE, '2024-01-01'),
    ('3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee', 'Penthouse Bogota', 'a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75', 'img2.jpg', 1000000.00, FALSE, TRUE, '2024-02-15'),
    ('2c8c7608-4422-4498-951f-7c091865db78', 'Beach House Cartagena', 'e9c1a570-dbe4-4a2e-a71e-2fd5a7b7f123', 'img3.jpg', 750000.00, TRUE, TRUE, '2024-03-20'),
    ('8d3014ff-0508-44ab-8a44-8c8caecb34df', 'Urban Loft Cali', 'c21d6f5e-7b58-4d81-9fc8-91e7c69d6e9a', 'img4.jpg', 300000.00, TRUE, TRUE, '2024-04-10'),
    ('c8447bdf-559f-465b-9333-2c2dc38addbf', 'Suburban House Bogota', 'a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75', 'img5.jpg', 600000.00, TRUE, TRUE, '2024-05-05');

INSERT INTO Rent (rentId, userId, propertyId, rentDate) VALUES
    ('a807be88-04fc-4f47-842c-4f2ed2948cc6', 'a7b5f905-c5fc-481d-9389-11b4b9e4a123', '48a234c4-ef02-4f96-8a04-82307b1d31a4', '2024-06-01'),
    ('a3ac38a0-dc0b-4825-a578-14d4cd000e9a', 'b8d6e716-c7bd-4720-a2db-22c5c7d4b456', '3a9f2dab-ae3a-437f-aeaa-f0b88a60a3ee', '2024-06-15'),
    ('3112061e-bb20-4700-acc1-5c1bd6156bf7', 'c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789', '2c8c7608-4422-4498-951f-7c091865db78', '2024-07-01'),
    ('bc27fc3f-d755-4f3b-a016-d6b93d17ae22', 'd0f8f938-ebdf-4732-b7fa-44e7f8f7d012', '8d3014ff-0508-44ab-8a44-8c8caecb34df', '2024-07-15'),
    ('7421e3b6-93c2-4838-aeba-9cbb8dcf45f3', 'e1f9f938-fdec-4832-b7fa-44e7f8e7d345', 'c8447bdf-559f-465b-9333-2c2dc38addbf', '2024-08-01'),
    ('1feb3f3c-f4f3-420e-a645-ff99c89530ab', 'c9e7f827-d9ce-4b31-b4ec-33d6d9e6c789', '48a234c4-ef02-4f96-8a04-82307b1d31a4', '2024-08-15');
