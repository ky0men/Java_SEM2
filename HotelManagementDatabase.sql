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
    accountStatus INT,
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
    roomTimePrice MONEY,
    roomFloor INT,
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
    wasPayment INT,
)

CREATE TABLE usedServices(
    bookingServiceID INT IDENTITY (1, 1) PRIMARY KEY,
    checkinID INT FOREIGN KEY REFERENCES Checkin(checkinID),
    usedServiceID INT FOREIGN KEY REFERENCES Service(ID),
    usedServiceQty INT,
)
--DROP TABLE usedServices

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
--DUC DATABASE
CREATE TABLE ServiceType(
ID INT IDENTITY(1,1),
ServiceType varchar(100) PRIMARY KEY
)

CREATE TABLE Service(
ID INT IDENTITY (1,1) PRIMARY KEY,
ServiceName varchar(100),
ServiceType varchar(100) FOREIGN KEY REFERENCES ServiceType,
Price int,
Unit varchar(20),
Volume int,
isDeleted int DEFAULT '0'
)
--DROP TABLE Service

INSERT INTO ServiceType VALUES ('Food Service')
INSERT INTO ServiceType VALUES ('Sport - Entertainment Service')
INSERT INTO ServiceType VALUES ('Traveling Service')
INSERT INTO ServiceType VALUES ('Relaxing Service')
INSERT INTO ServiceType VALUES ('Others Service')

INSERT INTO Service VALUES ('Beverage - Coca','Food Service',20000,'bottle',400,'')
INSERT INTO Service VALUES ('Beverage - Beer Heiniken','Food Service',25000,'bottle',500,'')
INSERT INTO Service VALUES ('Beverage - Aqua','Food Service',15000,'bottle',600,'')
INSERT INTO Service VALUES ('Beverage - Snack','Food Service',10000,'can',500,'')
INSERT INTO Service VALUES ('Buffet - 4 Stars European Restaurant','Food Service',899000,'person',100,'')
INSERT INTO Service VALUES ('Morning Service','Food Service',100000,'person',500,'')
INSERT INTO Service VALUES ('Bar Service','Food Service',200000,'person',100,'')
INSERT INTO Service VALUES ('Motobike Rental Service','Traveling Service',20000,'date',20,'')
INSERT INTO Service VALUES ('Car Rental Service','Traveling Service',100000,'date',10,'')
INSERT INTO Service VALUES ('Swimming Pool Service','Relaxing Service',100000,'person',100,'')
INSERT INTO Service VALUES ('Massage Service','Relaxing Service',100000,'person',100,'')
INSERT INTO Service VALUES ('Spa Service','Relaxing Service',100000,'person',100,'')
INSERT INTO Service VALUES ('Fitness & Yoga Service','Sport - Entertainment Service',50000,'person',100,'')
INSERT INTO Service VALUES ('Tennis Service','Sport - Entertainment Service',500000,'person',10,'')
INSERT INTO Service VALUES ('Goft Service','Sport - Entertainment Service',1499000,'person',10,'')
INSERT INTO Service VALUES ('Laundry Service','Others Service',100000,'time',100,'')

SELECT * FROM ServiceType
SELECT * FROM Service

Select ServiceName, ServiceType, Unit FROM Service
            WHERE ServiceName = 'Beverage - Coca'AND (ServiceType = 'Food Service') AND (Unit = 'bottle') AND isDeleted = 0;


--Procedure update service
CREATE PROC updateService @id int, @serviceName varchar(100), @serviceType varchar(100), @price int, @unit varchar(20), @volume int AS
    UPDATE Service SET ServiceName = @serviceName, ServiceType = @serviceType, Price = @price, Unit = @unit, Volume = @volume , isDeleted = '0' WHERE ID = @id
    GO
--DROP PROC updateService
--Procedure add service
CREATE PROC addService  @serviceName varchar(100), @serviceType varchar(100), @price int, @unit varchar(20), @volume int, @isdeleted int AS
	INSERT INTO Service VALUES (@serviceName, @serviceType, @price, @unit, @volume, '')
	GO


--END DUC

SELECT * FROM Service S WHERE S.ServiceType = 'Food Service' 

INSERT INTO Account VALUES ('admin', HASHBYTES('SHA2_512', '123456'), 'Manager', 0);
INSERT INTO Account VALUES ('ppdien', HASHBYTES('SHA2_512', '123'), 'Employee', 0);
INSERT INTO Account VALUES ('nsan', HASHBYTES('SHA2_512', '456'), 'Employee', 0);
INSERT INTO Account VALUES ('ttphuc', HASHBYTES('SHA2_512', '556'), 'Employee', 0);

INSERT INTO RoomType VALUES ('Single')
INSERT INTO RoomType VALUES ('Double')
INSERT INTO RoomType VALUES ('Single VIP')
INSERT INTO RoomType VALUES ('Double VIP')

INSERT INTO Room VALUES ('101', 1, 'Available', 250000, 70000, 1)
INSERT INTO Room VALUES ('102', 2, 'Rented', 300000, 80000, 1)
INSERT INTO Room VALUES ('103', 3, 'Dirty', 350000, 90000, 1)
INSERT INTO Room VALUES ('104', 4, 'Available', 400000, 100000, 1)
INSERT INTO Room VALUES ('201', 1, 'Available', 250000, 70000, 2)
INSERT INTO Room VALUES ('202', 2, 'Rented', 300000, 80000, 2)
INSERT INTO Room VALUES ('203', 3, 'Available', 350000, 90000, 2)
INSERT INTO Room VALUES ('204', 4, 'Available', 400000, 100000, 2)
INSERT INTO Room VALUES ('301', 1, 'Available', 250000, 70000, 3)
INSERT INTO Room VALUES ('302', 2, 'Rented', 300000, 80000, 3)
INSERT INTO Room VALUES ('303', 3, 'Dirty', 350000, 90000, 3)
INSERT INTO Room VALUES ('304', 4, 'Available', 400000, 100000, 3)

INSERT INTO Customer VALUES ('0123456789', N'Nguyễn Văn Tèo', 'Male', '05/28/1995', '0905115448', N'Hải Châu, Đà Nẵng');
INSERT INTO Customer VALUES ('1112223334', N'Nguyễn Văn Tí', 'Male', '03/18/1999', '0905253664', N'Sơn Trà, Đà Nẵng');
INSERT INTO Customer VALUES ('2223334445', N'Quách Thị Tĩnh', 'Female', '08/21/1994', '0984557998', N'Ngũ Hành Sơn, Đà Nẵng');
INSERT INTO Customer VALUES ('5556667778', N'Trần Dần', 'Male', '05/16/1986', '0903558115', N'Thăng Bình, Quảng Nam');
INSERT INTO Customer VALUES ('8889991110', N'Lý Kim Thoa', 'Female', '09/22/1996', '0964889223', N'Hải Châu, Đà Nẵng');


INSERT INTO EmployeeInformation VALUES (1, 'Admin of Lotus Hotel', '0000000000', '02/13/2022', '05/05/1991', 'lotus.info@gmail.com', '0905887889', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (2, 'Pham Phu Dien', '387684285', '02/13/2022', '05/05/1991', 'ppdien@gmail.com', '0353135698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (3, 'Nguyen Si An', '125684255', '02/13/2022', '05/06/1992', 'ngsian@gmail.com', '0353125698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (4, 'Ton That Hao Phuc', '012583285', '02/13/2022', '05/07/1993', 'tthphuc@gmail.com', '0353175698', 'Da Nang', '0' );

SELECT * FROM Account join EmployeeInformation on Account.id = EmployeeInformation.userID

 SELECT S.ServiceName, S.Price, SUM(US.usedServiceQty), S.Unit  FROM usedServices US JOIN Service S ON US.usedServiceID = S.ID JOIN Checkin CI ON CI.checkinID = US.checkinID WHERE CI.roomNumber = '103' GROUP BY S.ServiceName, S.Price, S.Unit


--Procedure check account
CREATE PROC checkLogin @username VARCHAR(15), @pass VARCHAR(60) AS 
    SELECT * FROM Account AD JOIN EmployeeInformation EM ON AD.id = EM.userID
    WHERE AD.username = @username AND AD.passwordHash = HASHBYTES('SHA2_512', @pass) AND EM.deleted = '0'
GO 


--Procudure check is manager or not
CREATE PROC checkIsManager @username VARCHAR(15), @position VARCHAR(30) AS
    SELECT * FROM Account AC WHERE AC.username = @username AND AC.position = @position
GO

-- Procedure get account is in use
SELECT AC.position, EM.fullName FROM Account AC JOIN EmployeeInformation EM ON AC.id = EM.userID 
    WHERE AC.accountStatus = '1'


--Procudure change account status (is in use or not)
CREATE PROC changeAccountStatusInUse @username VARCHAR(15) AS
    UPDATE Account SET accountStatus = '1' WHERE username = @username
GO


UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 101
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 104
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 202
UPDATE Room SET roomStatus = 'Dirty' WHERE roomName = 304
GO


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
    INSERT INTO Checkin VALUES(@cusIndentityNumber, @roomNumber, @checkinDate, @checkoutDate, @prepaid, @discount, 0)
GO



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

--Procedure add used service
CREATE PROC addUsedService @checkinID INT, @serviceID INT, @serviceQty INT AS
    INSERT INTO usedServices(checkinID, usedServiceID, usedServiceQty) VALUES (@checkinID, @serviceID, @serviceQty)
GO    


SELECT Room.roomName FROM Room       

