SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `tbreach` ;
CREATE SCHEMA IF NOT EXISTS `tbreach` DEFAULT CHARACTER SET latin1 ;
USE `tbreach` ;

-- -----------------------------------------------------
-- Table `tbreach`.`Contact`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Contact` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Contact` (
  `PID` VARCHAR(12) NOT NULL ,
  `AddressHouse` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressStreet` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressSector` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressColony` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressTown` VARCHAR(50) NULL DEFAULT NULL COMMENT 'State or Province' ,
  `AddressUC` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressLandMark` VARCHAR(50) NULL DEFAULT NULL ,
  `CityID` INT(11) NULL DEFAULT NULL COMMENT 'From SetupCity' ,
  `CountryID` INT(11) NULL DEFAULT NULL COMMENT 'From SetupCountry' ,
  `Region` INT(11) NULL DEFAULT NULL COMMENT 'From SetupRegion' ,
  `Phone` VARCHAR(20) NULL DEFAULT NULL ,
  `Mobile` VARCHAR(20) NULL DEFAULT NULL ,
  `Email` VARCHAR(50) NULL DEFAULT NULL ,
  `SecondaryAddressHouse` VARCHAR(10) NULL DEFAULT NULL ,
  `SecondaryStreetStreet` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryAddressSector` VARCHAR(10) NULL DEFAULT NULL ,
  `SecondaryAddressColony` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryAddressTown` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryAddressUC` VARCHAR(10) NULL DEFAULT NULL ,
  `SecondaryAddressLandMark` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryCityID` INT(11) NULL DEFAULT NULL ,
  `SecondaryCountryID` INT(11) NULL DEFAULT NULL ,
  `SecondaryRegion` INT(11) NULL DEFAULT NULL ,
  `SecondaryPhone` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryMobile` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryEmail` VARCHAR(50) NULL DEFAULT NULL ,
  `AddressLocationLat` FLOAT NULL DEFAULT NULL ,
  `AddressLocationLon` FLOAT NULL DEFAULT NULL ,
  `SecondaryAddressLocationLat` FLOAT NULL DEFAULT NULL ,
  `SecondaryAddressLocationLon` FLOAT NULL DEFAULT NULL ,
  `MeterNo` VARCHAR(10) NULL DEFAULT NULL ,
  `IRDStructureNo` VARCHAR(10) NULL DEFAULT NULL ,
  `SecondaryAddressStreet` VARCHAR(10) NULL DEFAULT NULL ,
  `HouseHoldAdults` INT(11) NULL DEFAULT NULL ,
  `HouseHoldChildren` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY USING BTREE (`PID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Contact details of a Person';


-- -----------------------------------------------------
-- Table `tbreach`.`Definition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Definition` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Definition` (
  `DefinitionType` VARCHAR(10) NOT NULL ,
  `Key` VARCHAR(10) NOT NULL ,
  `Value` VARCHAR(50) NOT NULL ,
  PRIMARY KEY (`DefinitionType`, `Key`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Definition stores value of statuses, types and specific role';


-- -----------------------------------------------------
-- Table `tbreach`.`DrugHistory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`DrugHistory` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`DrugHistory` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `Revision` INT(11) NOT NULL ,
  `RevisionDate` DATETIME NOT NULL ,
  `Mark` CHAR(1) NOT NULL ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`PatientID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Encounter`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Encounter` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Encounter` (
  `EncounterID` INT(11) NOT NULL ,
  `PID1` VARCHAR(12) NOT NULL ,
  `PID2` VARCHAR(12) NOT NULL ,
  `EncounterType` VARCHAR(10) NOT NULL ,
  `LocationID` VARCHAR(10) NULL DEFAULT NULL ,
  `DateEncounterStart` DATETIME NOT NULL ,
  `DateEncounterEnd` DATETIME NOT NULL ,
  `Details` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`EncounterID`, `PID1`, `PID2`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`EncounterResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`EncounterResults` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`EncounterResults` (
  `EncounterID` INT(11) NOT NULL ,
  `PID1` VARCHAR(12) NOT NULL ,
  `PID2` VARCHAR(12) NOT NULL ,
  `Element` VARCHAR(50) NOT NULL ,
  `Value` VARCHAR(50) NOT NULL ,
  PRIMARY KEY (`EncounterID`, `PID1`, `PID2`, `Element`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'EncounterResults stores results of Inspection/Test';


-- -----------------------------------------------------
-- Table `tbreach`.`Feedback`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Feedback` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Feedback` (
  `FeedbackID` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UserName` VARCHAR(50) NOT NULL ,
  `FeedbackType` VARCHAR(20) NOT NULL ,
  `Detail` MEDIUMTEXT NOT NULL ,
  PRIMARY KEY (`FeedbackID`) )
ENGINE = MyISAM
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = latin1
COMMENT = 'Feedback form';


-- -----------------------------------------------------
-- Table `tbreach`.`GP`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`GP` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`GP` (
  `GPID` VARCHAR(12) NOT NULL ,
  `Qualification1` VARCHAR(10) NULL DEFAULT NULL ,
  `Qualification2` VARCHAR(10) NULL DEFAULT NULL ,
  `Qualification3` VARCHAR(10) NULL DEFAULT NULL ,
  `Speciality1` VARCHAR(100) NULL DEFAULT NULL ,
  `Speciality2` VARCHAR(100) NULL DEFAULT NULL ,
  `Speciality3` VARCHAR(100) NULL DEFAULT NULL ,
  `WorkplaceID1` VARCHAR(10) NULL DEFAULT NULL ,
  `ScheduleID1` VARCHAR(10) NULL DEFAULT NULL ,
  `WorkplaceID2` VARCHAR(10) NULL DEFAULT NULL ,
  `ScheduleID2` VARCHAR(10) NULL DEFAULT NULL ,
  `WorkplaceID3` VARCHAR(10) NULL DEFAULT NULL ,
  `ScheduleID3` VARCHAR(10) NULL DEFAULT NULL ,
  PRIMARY KEY (`GPID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'General Practicioner';


-- -----------------------------------------------------
-- Table `tbreach`.`GPMapping`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`GPMapping` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`GPMapping` (
  `GPID` VARCHAR(12) NOT NULL ,
  `MonitorID` VARCHAR(12) NOT NULL ,
  PRIMARY KEY (`GPID`, `MonitorID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`GeneXpertResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`GeneXpertResults` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`GeneXpertResults` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `IRS` INT(11) NULL DEFAULT NULL ,
  `SputumTestID` INT(11) NOT NULL DEFAULT '0' ,
  `LaboratoryID` VARCHAR(10) NULL DEFAULT NULL ,
  `CollectedBy` VARCHAR(12) NULL DEFAULT NULL ,
  `DateSubmitted` DATETIME NULL DEFAULT NULL ,
  `DateTested` DATETIME NULL DEFAULT NULL ,
  `IsPositive` BIT(1) NULL DEFAULT NULL ,
  `DrugResistance` VARCHAR(50) NULL DEFAULT NULL ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`SputumTestID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Household`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Household` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Household` (
  `PatientMRNo` VARCHAR(12) NOT NULL ,
  `HouseholdID` VARCHAR(12) NOT NULL ,
  `Relationship` VARCHAR(50) NOT NULL ,
  `FullName` VARCHAR(100) NOT NULL ,
  `GuardianName` VARCHAR(100) NULL DEFAULT NULL ,
  `Gender` CHAR(1) NOT NULL ,
  `DOB` DATETIME NOT NULL ,
  `Screened` BIT(1) NOT NULL ,
  `DateEntered` DATETIME NOT NULL ,
  `Details` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`PatientMRNo`, `HouseholdID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Exclusive to the main database, used only to store household';


-- -----------------------------------------------------
-- Table `tbreach`.`Incentive`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Incentive` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Incentive` (
  `PID` VARCHAR(12) NOT NULL ,
  `IncentiveID` VARCHAR(10) NOT NULL ,
  `TransactionNo` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Auto-Incremental Transaction No.' ,
  `Mode` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Mode of Incentive Delivery' ,
  `DateTransferred` DATETIME NOT NULL ,
  `Status` VARCHAR(10) NOT NULL COMMENT 'PENDING, DELIVERED, FAILED' ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY USING BTREE (`PID`, `IncentiveID`, `TransactionNo`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Transaction Table for Incentives';


-- -----------------------------------------------------
-- Table `tbreach`.`IncentiveAccount`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`IncentiveAccount` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`IncentiveAccount` (
  `PID` VARCHAR(12) NOT NULL ,
  `TotalCredit` DECIMAL(6,2) NOT NULL ,
  `TotalTransferred` DECIMAL(6,2) NOT NULL ,
  `Details` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Location` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Location` (
  `LocationID` VARCHAR(10) NOT NULL ,
  `LocationName` VARCHAR(100) NOT NULL ,
  `LocationType` VARCHAR(10) NOT NULL ,
  `AddressHouse` VARCHAR(10) NULL DEFAULT NULL ,
  `AddressStreet` VARCHAR(10) NULL DEFAULT NULL ,
  `AddressSector` VARCHAR(10) NULL DEFAULT NULL ,
  `AddressColony` VARCHAR(20) NULL DEFAULT NULL ,
  `AddressTown` VARCHAR(20) NULL DEFAULT NULL ,
  `CityID` INT(11) NULL DEFAULT NULL ,
  `CountryID` INT(11) NULL DEFAULT NULL ,
  `AddressLocationLat` FLOAT NULL DEFAULT NULL ,
  `AddressLocationLon` FLOAT NULL DEFAULT NULL ,
  `Phone` VARCHAR(20) NULL DEFAULT NULL ,
  `Mobile` VARCHAR(20) NULL DEFAULT NULL ,
  `Fax` VARCHAR(20) NULL DEFAULT NULL ,
  `Email` VARCHAR(100) NULL DEFAULT NULL ,
  `SecondaryPhone` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryMobile` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryFax` VARCHAR(20) NULL DEFAULT NULL ,
  `SecondaryEmail` VARCHAR(100) NULL DEFAULT NULL ,
  `Detail` VARCHAR(255) NULL DEFAULT NULL ,
  `AddressLandMark` VARCHAR(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`LocationID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Stores any type of location.';


-- -----------------------------------------------------
-- Table `tbreach`.`Log_DataChange`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Log_DataChange` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Log_DataChange` (
  `ChangeNo` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UserID` VARCHAR(12) NOT NULL ,
  `DateChanged` DATETIME NOT NULL ,
  `Entity` VARCHAR(50) NOT NULL ,
  `NewValue` MEDIUMTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`ChangeNo`) )
ENGINE = MyISAM
AUTO_INCREMENT = 5780
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Log_DataDelete`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Log_DataDelete` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Log_DataDelete` (
  `DeleteNo` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UserID` VARCHAR(12) NOT NULL ,
  `DateDelete` DATETIME NOT NULL ,
  `Entity` VARCHAR(50) NOT NULL ,
  `Record` MEDIUMTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`DeleteNo`) )
ENGINE = MyISAM
AUTO_INCREMENT = 324
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Log_DataInsert`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Log_DataInsert` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Log_DataInsert` (
  `InsertNo` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UserID` VARCHAR(12) NOT NULL ,
  `DateInserted` DATETIME NOT NULL ,
  `Entity` VARCHAR(50) NOT NULL ,
  `NewValue` MEDIUMTEXT NOT NULL ,
  PRIMARY KEY (`InsertNo`) )
ENGINE = MyISAM
AUTO_INCREMENT = 56084
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Log_UserLogin`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Log_UserLogin` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Log_UserLogin` (
  `LoginNo` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UserID` VARCHAR(12) NOT NULL ,
  `LoginTime` DATETIME NULL DEFAULT NULL ,
  `LogoutTime` DATETIME NULL DEFAULT NULL ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`LoginNo`) )
ENGINE = MyISAM
AUTO_INCREMENT = 6060
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Monitor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Monitor` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Monitor` (
  `MonitorID` VARCHAR(12) NOT NULL ,
  `SupervisorID` VARCHAR(12) NOT NULL ,
  PRIMARY KEY (`MonitorID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Patient`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Patient` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Patient` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `CHWID` VARCHAR(12) NULL DEFAULT NULL ,
  `GPID` VARCHAR(12) NULL DEFAULT NULL ,
  `MonitorID` VARCHAR(12) NULL DEFAULT NULL ,
  `MRNo` VARCHAR(11) NULL DEFAULT NULL COMMENT 'Case/File/Patient/Medical Record No. used in treatment center.' ,
  `ExternalMRNo` VARCHAR(11) NULL DEFAULT NULL COMMENT 'Case/File/Patient No. used outside treatment center (District level etc.)' ,
  `Weight` FLOAT NULL DEFAULT NULL ,
  `Height` FLOAT NULL DEFAULT NULL ,
  `BloodGroup` VARCHAR(5) NULL DEFAULT NULL ,
  `DateRegistered` DATETIME NULL DEFAULT NULL ,
  `TreatmentSupporter` VARCHAR(12) NULL DEFAULT NULL ,
  `TreatmentCenter` VARCHAR(10) NULL DEFAULT NULL ,
  `DiseaseSuspected` BIT(1) NULL DEFAULT NULL ,
  `DiseaseConfirmed` BIT(1) NULL DEFAULT NULL ,
  `DiseaseCategory` VARCHAR(10) NULL DEFAULT NULL ,
  `DiseaseSite` VARCHAR(50) NULL DEFAULT NULL ,
  `Severity` VARCHAR(10) NULL DEFAULT NULL ,
  `Regimen` VARCHAR(10) NULL DEFAULT NULL ,
  `DoseCombination` FLOAT NULL DEFAULT NULL ,
  `OtherDoseDescription` VARCHAR(255) NULL DEFAULT NULL ,
  `TreatmentPhase` VARCHAR(10) NULL DEFAULT NULL ,
  `PatientStatus` VARCHAR(10) NULL DEFAULT NULL ,
  `PatientType` VARCHAR(10) NULL DEFAULT NULL ,
  `DiseaseHistory` VARCHAR(50) NULL DEFAULT NULL ,
  `TreatedPreviously` BIT(1) NULL DEFAULT NULL ,
  `CompletedPreviousTreatment` BIT(1) NULL DEFAULT NULL ,
  `FullDescription` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`PatientID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Person` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Person` (
  `PID` VARCHAR(12) NOT NULL COMMENT 'Formatted as: [P][yymmdd][#####]' ,
  `Salutation` VARCHAR(5) NULL DEFAULT NULL ,
  `FirstName` VARCHAR(50) NOT NULL ,
  `MiddleName` VARCHAR(50) NULL DEFAULT NULL ,
  `LastName` VARCHAR(50) NULL DEFAULT NULL ,
  `SurName` VARCHAR(50) NULL DEFAULT NULL ,
  `GuardianName` VARCHAR(50) NULL DEFAULT NULL ,
  `Gender` CHAR(1) NOT NULL ,
  `DOB` DATE NULL DEFAULT NULL ,
  `MaritalStatus` VARCHAR(20) NULL DEFAULT NULL ,
  `Domicile` VARCHAR(50) NULL DEFAULT NULL ,
  `NIC` VARCHAR(20) NULL DEFAULT NULL ,
  `NICOwnerName` VARCHAR(50) NULL DEFAULT NULL ,
  `Religion` VARCHAR(20) NULL DEFAULT NULL ,
  `Caste` VARCHAR(20) NULL DEFAULT NULL ,
  `RoleInSystem` VARCHAR(10) NULL DEFAULT NULL COMMENT 'Defines the Role of Person in the system e.g. Patient, Doctor, etc.' ,
  `Alive` BIT(1) NULL DEFAULT NULL ,
  `Picture` BLOB NULL DEFAULT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Relationship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Relationship` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Relationship` (
  `PID` VARCHAR(12) NOT NULL ,
  `RelativePID` VARCHAR(12) NOT NULL ,
  `Relationship` VARCHAR(20) NOT NULL ,
  PRIMARY KEY (`PID`, `RelativePID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Relationship with Person';


-- -----------------------------------------------------
-- Table `tbreach`.`SMS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SMS` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SMS` (
  `SMSID` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Auto-incremental field' ,
  `TargetNumber` VARCHAR(20) NOT NULL COMMENT 'Phone number on which SMS is to be delivered' ,
  `MessageText` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Message body of the SMS' ,
  `DueDateTime` DATETIME NOT NULL COMMENT 'Date and Time when SMS is to be sent' ,
  `SentDateTime` DATETIME NULL DEFAULT NULL COMMENT 'Date and Time after the SMS was sent' ,
  `Status` VARCHAR(10) NULL DEFAULT NULL COMMENT 'PENDING, SENT, FAILED, SKIPPED' ,
  `ErrorMessage` VARCHAR(255) NULL DEFAULT NULL ,
  `FailureCause` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`SMSID`) )
ENGINE = MyISAM
AUTO_INCREMENT = 2363
DEFAULT CHARACTER SET = latin1
COMMENT = 'General table used by SMS Sender application which monitors ';


-- -----------------------------------------------------
-- Table `tbreach`.`SetupCity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SetupCity` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SetupCity` (
  `CountryID` INT(11) NOT NULL ,
  `CityID` INT(11) NOT NULL ,
  `CityName` VARCHAR(100) NULL DEFAULT NULL ,
  `ShortName` VARCHAR(5) NULL DEFAULT NULL ,
  PRIMARY KEY (`CountryID`, `CityID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`SetupCountry`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SetupCountry` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SetupCountry` (
  `CountryID` INT(11) NOT NULL ,
  `CountryName` VARCHAR(100) NOT NULL ,
  `ShortName` VARCHAR(3) NULL DEFAULT NULL ,
  PRIMARY KEY (`CountryID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`SetupIncentive`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SetupIncentive` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SetupIncentive` (
  `IncentiveID` VARCHAR(10) NOT NULL ,
  `IncentiveName` VARCHAR(50) NOT NULL ,
  `BeneficiaryGroup` VARCHAR(10) NOT NULL ,
  `Amount` DECIMAL(10,0) NOT NULL ,
  `Currency` VARCHAR(3) NOT NULL ,
  `ScheduleID` VARCHAR(10) NOT NULL COMMENT 'Linked to Job Scheduler' ,
  `Detail` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`IncentiveID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`SetupRole`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SetupRole` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SetupRole` (
  `Role` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`Role`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`SputumResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`SputumResults` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`SputumResults` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `SputumTestID` INT(11) NOT NULL ,
  `LaboratoryID` VARCHAR(10) NULL DEFAULT NULL ,
  `Month` INT(11) NOT NULL ,
  `DateSubmitted` DATETIME NULL DEFAULT NULL ,
  `DateTested` DATETIME NULL DEFAULT NULL ,
  `TestedBy` VARCHAR(12) NULL DEFAULT NULL ,
  `SmearResult` VARCHAR(10) NULL DEFAULT NULL ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  `IRS` INT(11) NULL DEFAULT '0' ,
  `OtherRemarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`PatientID`, `SputumTestID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Supervisor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Supervisor` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Supervisor` (
  `SupervisorID` VARCHAR(12) NOT NULL ,
  PRIMARY KEY (`SupervisorID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`TreatmentRefusal`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`TreatmentRefusal` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`TreatmentRefusal` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `ElementRefused` VARCHAR(50) NULL DEFAULT NULL ,
  `MonthOfSputum` INT(11) NULL DEFAULT NULL ,
  `Reason` VARCHAR(255) NULL DEFAULT NULL ,
  `CaseClosedOnDate` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`PatientID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Contains info about Patients who refused treatment';


-- -----------------------------------------------------
-- Table `tbreach`.`UserRights`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`UserRights` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`UserRights` (
  `Role` VARCHAR(10) NOT NULL ,
  `MenuName` VARCHAR(50) NOT NULL ,
  `SearchAccess` BIT(1) NOT NULL ,
  `InsertAccess` BIT(1) NOT NULL ,
  `UpdateAccess` BIT(1) NOT NULL ,
  `DeleteAccess` BIT(1) NOT NULL ,
  `PrintAccess` BIT(1) NOT NULL ,
  PRIMARY KEY (`Role`, `MenuName`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Users` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Users` (
  `PID` VARCHAR(12) NOT NULL ,
  `UserName` VARCHAR(50) NOT NULL ,
  `Role` VARCHAR(10) NOT NULL ,
  `Status` VARCHAR(10) NOT NULL ,
  `Password` VARCHAR(255) NOT NULL COMMENT 'Password is not stored directly, instead, it\'s hashcode is stored. Hashcode is generated using an appropriate hashing algorithm.' ,
  `SecretQuestion` VARCHAR(255) NOT NULL ,
  `SecretAnswer` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tbreach`.`Worker`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`Worker` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`Worker` (
  `WorkerID` VARCHAR(12) NOT NULL ,
  `SupervisorID` VARCHAR(12) NOT NULL ,
  `MonitorID` VARCHAR(12) NOT NULL ,
  PRIMARY KEY (`WorkerID`) ,
  INDEX `PK_Worker_Person2` (`SupervisorID` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1
COMMENT = 'Field Worker/Lady Health Worker';


-- -----------------------------------------------------
-- Table `tbreach`.`XRayResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tbreach`.`XRayResults` ;

CREATE  TABLE IF NOT EXISTS `tbreach`.`XRayResults` (
  `PatientID` VARCHAR(12) NOT NULL ,
  `IRS` INT(11) NOT NULL ,
  `XRayDate` DATETIME NULL DEFAULT NULL ,
  `DateReported` DATETIME NULL DEFAULT NULL ,
  `XRayResults` VARCHAR(255) NULL DEFAULT NULL ,
  `Remarks` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY USING BTREE (`PatientID`, `IRS`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
