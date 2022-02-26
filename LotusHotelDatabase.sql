--Create database LotusHotel
CREATE DATABASE LotusHotel
USE LotusHotel
GO




--CREATE TABLE

CREATE TABLE Account (
    id INT IDENTITY (1, 1) PRIMARY KEY,
    username VARCHAR(15) NOT NULL UNIQUE,
    passwordHash BINARY(64) NOT NULL,
    position VARCHAR(30),
    accountStatus INT,
)

CREATE TABLE EmployeeInformation(
	userID int foreign key references Account(id),
	fullName nvarchar(30),
	numberId varchar(30),
	startWork date,
	birthday date,
	userEmail varchar(30),
	userPhone varchar(20),
	userAddress nvarchar(50),
	deleted char(1),
	primary key (userID)
)



CREATE TABLE RoomType(
    roomTypeID INT IDENTITY (1,1) PRIMARY KEY,
    roomTypeName VARCHAR(120) NOT NULL,
	isDeleteType bit DEFAULT '0'
)

CREATE TABLE Room (
    roomID INT IDENTITY (1,1),
    roomName VARCHAR(15) UNIQUE NOT NULL,
    roomTypeID INT FOREIGN KEY REFERENCES RoomType(roomTypeID),
    roomStatus VARCHAR(120),
    roomPrice MONEY,
    roomTimePrice MONEY,
    roomFloor INT,
	isDeleteRoom bit  DEFAULT '0'
	PRIMARY KEY(roomID, roomName)
)

CREATE TABLE Customer (
    cusID INT IDENTITY (1,1),
    cusIdentityNumber VARCHAR (20) UNIQUE NOT NULL, 
    cusName NVARCHAR (200) NOT NULL,
    cusGender VARCHAR(10),
    cusDOB DATE,
    cusPhone VARCHAR (12),
    cusAddress NVARCHAR (250),
    cusDeleted char(1),
	PRIMARY KEY (cusID, cusIdentityNumber)
)

CREATE TABLE Checkin(
    checkinID INT IDENTITY (1,1) PRIMARY KEY,
    cusIdentityNumber VARCHAR(20) FOREIGN KEY REFERENCES Customer(cusIdentityNumber),
    roomNumber VARCHAR(15) FOREIGN KEY REFERENCES Room(roomName),
    checkinDate DATETIME,
    checkoutDate DATE,
    prepaid MONEY,
    discount MONEY,
    wasPayment INT,
)

CREATE TABLE ServiceType(
ID INT IDENTITY(1,1),
ServiceType varchar(100) UNIQUE,
PRIMARY KEY (ID, ServiceType)
)

CREATE TABLE Service(
ID INT IDENTITY (1,1) PRIMARY KEY,
ServiceName varchar(100),
ServiceType varchar(100) FOREIGN KEY REFERENCES ServiceType(ServiceType),
Price int,
Unit varchar(20),
isDeleted int DEFAULT '0'
)

CREATE TABLE usedServices(
    bookingServiceID INT IDENTITY (1, 1) PRIMARY KEY,
    checkinID INT FOREIGN KEY REFERENCES Checkin(checkinID),
    usedServiceID INT FOREIGN KEY REFERENCES Service(ID),
    usedServiceQty INT,
)



CREATE TABLE bill(
    billID INT IDENTITY (1, 1),
    employeeID INT FOREIGN KEY REFERENCES Account(id),
    customerIdentity VARCHAR(20) FOREIGN KEY REFERENCES Customer(cusIdentityNumber),
    printDate DATE,
    printMonth INT,
    printYear INT,
    revenue MONEY,
	PRIMARY KEY (billID, employeeID, customerIdentity)
)

CREATE TABLE RoomBooking(
    roomBookingID INT IDENTITY (1, 1) PRIMARY KEY,
    roomNumber VARCHAR(15) FOREIGN KEY REFERENCES Room(roomName),
    employeeName VARCHAR(120),
    customerName VARCHAR(120) NOT NULL,
    customerIdentityNumber VARCHAR(20) FOREIGN KEY REFERENCES Customer(cusIdentityNumber),
    customerPhoneNumber VARCHAR(14),
    checkinTime DATETIME NOT NULL,
    checkoutTime DATE NOT NULL,
    prepaid MONEY NOT NULL,
    discount MONEY NOT NULL
)




--DEMO DATABASE
INSERT INTO Account VALUES ('admin', HASHBYTES('SHA2_512', '123456'), 'Manager', 0);
INSERT INTO Account VALUES ('ppdien', HASHBYTES('SHA2_512', '123'), 'Employee', 0);
INSERT INTO Account VALUES ('nsan', HASHBYTES('SHA2_512', '456'), 'Employee', 0);
INSERT INTO Account VALUES ('ttphuc', HASHBYTES('SHA2_512', '556'), 'Employee', 0);
INSERT INTO Account VALUES ('nvduc', HASHBYTES('SHA2_512', '123456'), 'Employee', 0);

INSERT INTO RoomType VALUES ('Single', '0')
INSERT INTO RoomType VALUES ('Double', '0')
INSERT INTO RoomType VALUES ('Single VIP', '0')
INSERT INTO RoomType VALUES ('Double VIP', '0')

INSERT INTO Room VALUES ('101', 1, 'Available', 250000, 70000, 1, '0')
INSERT INTO Room VALUES ('102', 2, 'Available', 300000, 80000, 1, '0')
INSERT INTO Room VALUES ('103', 3, 'Available', 350000, 90000, 1, '0')
INSERT INTO Room VALUES ('104', 4, 'Available', 400000, 100000, 1, '0')
INSERT INTO Room VALUES ('201', 1, 'Available', 250000, 70000, 2, '0')
INSERT INTO Room VALUES ('202', 2, 'Available', 300000, 80000, 2, '0')
INSERT INTO Room VALUES ('203', 3, 'Available', 350000, 90000, 2, '0')
INSERT INTO Room VALUES ('204', 4, 'Available', 400000, 100000, 2, '0')
INSERT INTO Room VALUES ('301', 1, 'Available', 250000, 70000, 3, '0')
INSERT INTO Room VALUES ('302', 2, 'Available', 300000, 80000, 3, '0')
INSERT INTO Room VALUES ('303', 3, 'Available', 350000, 90000, 3, '0')
INSERT INTO Room VALUES ('304', 4, 'Available', 400000, 100000, 3, '0')

INSERT INTO Customer VALUES ('0123456789', N'Nguyen Van Bach', 'Male', '05/28/1995', '0905115448', N'Hai Chau, Da Nang', '0');
INSERT INTO Customer VALUES ('1112223334', N'Phan Tran Nam', 'Male', '03/18/1999', '0905253664', N'Sơn Trà, Đà Nẵng', '0');
INSERT INTO Customer VALUES ('2223334445', N'Nguyen My Hanh', 'Female', '08/21/1994', '0984557998', N'Ngu Hanh Son, Da Nang', '0');
INSERT INTO Customer VALUES ('5556667778', N'Tran Minh Toan', 'Male', '05/16/1986', '0903558115', N'Thang Binh, Quang Nam', '0');
INSERT INTO Customer VALUES ('8889991110', N'Ly Thi Hoa', 'Female', '09/22/1996', '0964889223', N'Hai Chau Da, Nang', '0');
INSERT INTO Customer VALUES ('2526235989', N'Nguyen Minh', 'Male', '04/29/1992', '0986547521', N'Son Tra, Da Nang', '0');
INSERT INTO Customer VALUES ('5866989989', N'Tran Tam', 'Male', '03/21/1996', '0939895632', N'Cam Le, Da Nang', '0');
INSERT INTO Customer VALUES ('8798989787', N'Nguyen My My', 'Female', '08/21/1999', '0936455878', N'Ngu Hanh Son, Da Nang', '0');
INSERT INTO Customer VALUES ('9898787877', N'Pham Van Toan', 'Male', '09/16/1989', '0976558669', N'Thang Binh, Quang Nam', '0');
INSERT INTO Customer VALUES ('9898989789', N'Le Thi My Khanh', 'Female', '09/29/1998', '0966988979', N'Hai Chau Da, Nang', '0');

INSERT INTO EmployeeInformation VALUES (1, 'Admin of Lotus Hotel', '0000000000', '02/13/2022', '05/05/1991', 'lotus.info@gmail.com', '0905887889', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (2, 'Pham Phu Dien', '387684285', '02/13/2022', '05/05/1991', 'phamphudien@gmail.com', '0353135698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (3, 'Nguyen Si An', '125684255', '02/13/2022', '05/06/1992', 'ngsian@gmail.com', '0353125698', 'Da Nang', '0' );
INSERT INTO EmployeeInformation VALUES (4, 'Ton That Hao Phuc', '012583285', '02/13/2022', '05/07/1993', 'tthphuc@gmail.com', '0353175698', 'Da Nang', '0' );

INSERT INTO ServiceType VALUES ('Food Service')
INSERT INTO ServiceType VALUES ('Sport - Entertainment Service')
INSERT INTO ServiceType VALUES ('Traveling Service')
INSERT INTO ServiceType VALUES ('Relaxing Service')
INSERT INTO ServiceType VALUES ('Others Service')

INSERT INTO Service VALUES ('Beverage - Coca','Food Service',20000,'bottle','')
INSERT INTO Service VALUES ('Beverage - Beer Heiniken','Food Service',25000,'bottle','')
INSERT INTO Service VALUES ('Beverage - Aqua','Food Service',15000,'bottle','')
INSERT INTO Service VALUES ('Beverage - Snack','Food Service',10000,'can','')
INSERT INTO Service VALUES ('Buffet - 4 Stars European Restaurant','Food Service',899000,'person','')
INSERT INTO Service VALUES ('Morning Service','Food Service',100000,'person','')
INSERT INTO Service VALUES ('Bar Service','Food Service',200000,'person','')
INSERT INTO Service VALUES ('Motobike Rental Service','Traveling Service',20000,'date','')
INSERT INTO Service VALUES ('Car Rental Service','Traveling Service',100000,'date','')
INSERT INTO Service VALUES ('Swimming Pool Service','Relaxing Service',100000,'person','')
INSERT INTO Service VALUES ('Massage Service','Relaxing Service',100000,'person','')
INSERT INTO Service VALUES ('Spa Service','Relaxing Service',100000,'person','')
INSERT INTO Service VALUES ('Fitness & Yoga Service','Sport - Entertainment Service',50000,'person','')
INSERT INTO Service VALUES ('Tennis Service','Sport - Entertainment Service',500000,'person','')
INSERT INTO Service VALUES ('Goft Service','Sport - Entertainment Service',1499000,'person','')
INSERT INTO Service VALUES ('Laundry Service','Others Service',100000,'time','')


INSERT INTO bill VALUES ('2','0123456789', '02-25-2021', '2', '2021', '340000')
INSERT INTO bill VALUES ('2','1112223334', '02-24-2021', '2', '2021', '580000')
INSERT INTO bill VALUES ('3','2223334445', '03-20-2021', '3', '2021', '800000')
INSERT INTO bill VALUES ('3','5556667778', '03-21-2020', '3', '2020', '1000000')
INSERT INTO bill VALUES ('4','8889991110', '03-22-2020', '3', '2020', '950000')
INSERT INTO bill VALUES ('4','2526235989', '03-23-2020', '3', '2020', '640000')
INSERT INTO bill VALUES ('2','5866989989', '02-20-2022', '2', '2022', '700000')
INSERT INTO bill VALUES ('2','8798989787', '02-21-2022', '2', '2022', '820000')
INSERT INTO bill VALUES ('3','9898787877', '02-22-2022', '2', '2022', '1100000')
INSERT INTO bill VALUES ('3','0123456789', '02-23-2022', '2', '2022', '1050000')
INSERT INTO bill VALUES ('2','9898989789', '02-24-2022', '2', '2022', '960000')



--Procedure check account
CREATE PROC checkLogin @username VARCHAR(15), @pass VARCHAR(60) AS 
    SELECT * FROM Account AD JOIN EmployeeInformation EM ON AD.id = EM.userID
    WHERE AD.username = @username AND AD.passwordHash = HASHBYTES('SHA2_512', @pass) AND EM.deleted = '0'
GO 

--Procudure check is manager or not
CREATE PROC checkIsManager @username VARCHAR(15), @position VARCHAR(30) AS
    SELECT * FROM Account AC WHERE AC.username = @username AND AC.position = @position
GO

--Procudure change account status (is in use or not)
CREATE PROC changeAccountStatusInUse @username VARCHAR(15) AS
    UPDATE Account SET accountStatus = '1' WHERE username = @username
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
GO
-- Procudure add booking
CREATE PROC addBooking @cusIndentityNumber VARCHAR(20), @roomNumber VARCHAR(15), @checkinDate DATETIME, @checkoutDate DATE, @prepaid MONEY, @discount MONEY AS
    INSERT INTO RoomBooking (customerIdentityNumber, roomNumber, checkinTime, checkoutTime, prepaid, discount) VALUES(@cusIndentityNumber, @roomNumber, @checkinDate, @checkoutDate, @prepaid, @discount)
GO

--Procedure add used service
CREATE PROC addUsedService @checkinID INT, @serviceID INT, @serviceQty INT AS
    INSERT INTO usedServices(checkinID, usedServiceID, usedServiceQty) VALUES (@checkinID, @serviceID, @serviceQty)
GO    

--Procedure update service
CREATE PROC updateService @id int, @serviceName varchar(100), @serviceType varchar(100), @price int, @unit varchar(20) AS
    UPDATE Service SET ServiceName = @serviceName, ServiceType = @serviceType, Price = @price, Unit = @unit, isDeleted = '0' WHERE ID = @id
    GO

--Procedure add service
CREATE PROC addService  @serviceName varchar(100), @serviceType varchar(100), @price int, @unit varchar(20) AS
	INSERT INTO Service VALUES (@serviceName, @serviceType, @price, @unit, '')
	GO

--PROCEDURE add Bill
CREATE PROC addBill @employeeID INT, @cusID VARCHAR(20), @date DATE, @month INT, @year INT, @revenue MONEY AS
    INSERT INTO bill VALUES (@employeeID, @cusID, @date, @month, @year, @revenue)


