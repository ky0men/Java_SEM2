CREATE DATABASE HotelManagement
USE HotelManagement
GO
-- DROP DATABASE FOR TESTING
DROP DATABASE HotelManagement

--CREATE TABLE ACCOUNT FOR LOGIN
CREATE TABLE Account (
    id INT IDENTITY (1, 1) PRIMARY KEY,
    username VARCHAR(15) NOT NULL UNIQUE,
    passwordHash BINARY(64) NOT NULL,
    position VARCHAR(30),
    accountStatus INT
)

CREATE TABLE RoomType(
    roomTypeID INT IDENTITY (1,1) PRIMARY KEY,
    roomTypeName VARCHAR(120) NOT NULL,

)

CREATE TABLE Room (
    roomID INT IDENTITY (1,1) PRIMARY KEY,
    roomName VARCHAR(15) NOT NULL,
    roomTypeID INT FOREIGN KEY REFERENCES RoomType(roomTypeID),
    --Room static Available, Rented, Dirty
    roomStatus VARCHAR(120),
    roomPrice MONEY,
)

CREATE TABLE Customer (
    cusID INT IDENTITY (1,1) PRIMARY KEY,
    cusIdentityNumber VARCHAR (20) UNIQUE NOT NULL, 
    cusName NVARCHAR (200) NOT NULL,
    cusGender VARCHAR(10),
    cusDOB DATE,
    cusPhone VARCHAR (12),
    cusAddress NVARCHAR (250),
    
)


CREATE TABLE RoomBooking(
    roomBookingID INT IDENTITY (1, 1) PRIMARY KEY,
    roomNumber VARCHAR(15),
    employeeName VARCHAR(120),
    customerName VARCHAR(120) NOT NULL,
    customerIdentityNumber VARCHAR(20) NOT NULL,
    customerPhoneNumber VARCHAR(14),
    checkinTime DATETIME NOT NULL,
    checkoutTime DATE NOT NULL,
    prepaid MONEY NOT NULL,
    discount MONEY NOT NULL
)

CREATE TABLE Checkin(
    checkinID INT IDENTITY (1,1) PRIMARY KEY,
    cusIdentityNumber VARCHAR(20) FOREIGN KEY REFERENCES Customer(cusIdentityNumber),
    roomNumber VARCHAR(15),
    checkinDate DATETIME,
    checkoutDate DATE,
    prepaid MONEY,
    discount MONEY,
)

DROP TABLE EmployeeInformation
--Phuc
CREATE TABLE EmployeeInformation(
	userID int foreign key references Account(id),
	fullName varchar(30),
	numberId varchar(30),
	startWork date,
	birthday date,
	userEmail varchar(30),
	userPhone varchar(20),
	userAddress varchar(50),
	deleted char(1)
)


INSERT INTO Account VALUES ('admin', HASHBYTES('SHA2_512', '123456'), 'Manager', 0);
INSERT INTO Account VALUES ('ppdien', HASHBYTES('SHA2_512', '123'), 'Employee', 0);
INSERT INTO Account VALUES ('nsan', HASHBYTES('SHA2_512', '456'), 'Employee', 0);
INSERT INTO Account VALUES ('ttphuc', HASHBYTES('SHA2_512', '556'), 'Employee', 0);

INSERT INTO RoomType VALUES ('Single')
INSERT INTO RoomType VALUES ('Double')
INSERT INTO RoomType VALUES ('Single VIP')
INSERT INTO RoomType VALUES ('Double VIP')

INSERT INTO Room VALUES ('101', 1, 'Available', 250000)
INSERT INTO Room VALUES ('102', 2, 'Rented', 300000)
INSERT INTO Room VALUES ('103', 3, 'Dirty', 350000)
INSERT INTO Room VALUES ('104', 4, 'Available', 400000)
INSERT INTO Room VALUES ('201', 1, 'Available', 250000)
INSERT INTO Room VALUES ('202', 2, 'Rented', 300000)
INSERT INTO Room VALUES ('203', 3, 'Available', 350000)
INSERT INTO Room VALUES ('204', 4, 'Available', 400000)
INSERT INTO Room VALUES ('301', 1, 'Available', 250000)
INSERT INTO Room VALUES ('302', 2, 'Rented', 300000)
INSERT INTO Room VALUES ('303', 3, 'Dirty', 350000)
INSERT INTO Room VALUES ('304', 4, 'Available', 400000)

INSERT INTO Customer VALUES ('0123456789', N'Nguyễn Văn Tèo', 'Male', '05/28/1995', '0905115448', N'Hải Châu, Đà Nẵng');
INSERT INTO Customer VALUES ('1112223334', N'Nguyễn Văn Tí', 'Male', '03/18/1999', '0905253664', N'Sơn Trà, Đà Nẵng');
INSERT INTO Customer VALUES ('2223334445', N'Quách Thị Tĩnh', 'Female', '08/21/1994', '0984557998', N'Ngũ Hành Sơn, Đà Nẵng');
INSERT INTO Customer VALUES ('5556667778', N'Trần Dần', 'Male', '05/16/1986', '0903558115', N'Thăng Bình, Quảng Nam');
INSERT INTO Customer VALUES ('8889991110', N'Lý Kim Thoa', 'Female', '09/22/1996', '0964889223', N'Hải Châu, Đà Nẵng');

INSERT INTO EmployeeInformation VALUES (2, 'Pham Phu Dien', '125684285', '02/13/2022', '05/05/1991', 'ppdien@gmail.com', '0353135698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (3, 'Nguyen Si An', '125684255', '02/13/2022', '05/06/1992', 'ngsian@gmail.com', '0353125698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (4, 'Ton That Hao Phuc', '125683285', '02/13/2022', '05/07/1993', 'tthphuc@gmail.com', '0353175698', 'Da Nang', '0' );

SELECT * FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID

--Procedure check account
CREATE PROC checkLogin @username VARCHAR(15), @pass VARCHAR(60) AS 
    SELECT * FROM Account AD WHERE AD.username = @username AND AD.passwordHash = HASHBYTES('SHA2_512', @pass)
GO 

--Procudure check is manager or not
CREATE PROC checkIsManager @username VARCHAR(15), @position VARCHAR(30) AS
    SELECT * FROM Account AC WHERE AC.username = @username AND AC.position = @position
GO    

UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 101
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 104
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 202
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 304
GO

-- Procudure confirm cleaned and change status room
--CREATE PROC confirmCleanedRoom @roomName VARCHAR(15) AS
--    UPDATE Room SET roomStatus = 'Available' WHERE roomName = @roomName
--GO
-- Procedure check and get customer name from customer Identiry number
CREATE PROC getCusNameFromIDNumber @idNum VARCHAR(20) AS
    SELECT CUS.cusName FROM Customer CUS WHERE CUS.cusIdentityNumber = @idNum
GO

--Procedure get room type from room name
CREATE PROC getRoomType @roomName VARCHAR(15) AS
    SELECT RT.roomTypeName FROM Room R JOIN RoomType RT ON R.roomTypeID = RT.roomTypeID WHERE R.roomName = @roomName
GO

-- Procudure add checkin
CREATE PROC addCheckin @cusIndentityNumber VARCHAR(20), @roomNumber VARCHAR(15), @checkinDate DATETIME, @checkoutDate DATE, @prepaid MONEY, @discount MONEY AS
    INSERT INTO Checkin VALUES(@cusIndentityNumber, @roomNumber, @checkinDate, @checkoutDate, @prepaid, @discount)
GO

-- Procudure was checkin and change status room
--CREATE PROC checkinAndChangeStatus @roomName VARCHAR(15) AS
 --   UPDATE Room SET roomStatus = 'Rented' WHERE roomName = @roomName
--GO

-- Procudure change status room
CREATE PROC changeStatusRoom @roomName VARCHAR(15), @status VARCHAR(120) AS
    UPDATE Room SET roomStatus = @status WHERE roomName = @roomName
GO

--Procudure add customer (only name and id)
CREATE PROC addNameAndIDCustomer @cusIdentityNumber VARCHAR(20), @cusName NVARCHAR(200) AS
    INSERT INTO Customer(cusIdentityNumber, cusName) VALUES (@cusIdentityNumber, @cusName)


-- Procudure add booking
CREATE PROC addBooking @cusIndentityNumber VARCHAR(20), @roomNumber VARCHAR(15), @checkinDate DATETIME, @checkoutDate DATE, @prepaid MONEY, @discount MONEY AS
    INSERT INTO RoomBooking (customerIdentityNumber, roomNumber, checkinTime, checkoutTime, prepaid, discount) VALUES(@cusIndentityNumber, @roomNumber, @checkinDate, @checkoutDate, @prepaid, @discount)
GO

SELECT Room.roomName FROM Room       

