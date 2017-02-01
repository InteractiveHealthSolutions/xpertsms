CREATE DATABASE  IF NOT EXISTS `xpertsms`;
USE `xpertsms`;

-- Table structure for table `encounter`
DROP TABLE IF EXISTS `encounter`;
CREATE TABLE `encounter` (
  `EncounterID` int(11) NOT NULL,
  `PID1` varchar(50) NOT NULL,
  `PID2` varchar(50) NOT NULL,
  `EncounterType` varchar(50) NOT NULL,
  `LocationID` varchar(50) DEFAULT NULL,
  `DateEncounterStart` datetime NOT NULL,
  `DateEncounterEnd` datetime NOT NULL,
  `DateEncounterEntered` datetime DEFAULT NULL,
  `Details` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`EncounterID`,`PID1`,`PID2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `encounterresults`
DROP TABLE IF EXISTS `encounterresults`;
CREATE TABLE `encounterresults` (
  `EncounterID` int(11) NOT NULL,
  `PID1` varchar(50) NOT NULL,
  `PID2` varchar(50) NOT NULL,
  `Element` varchar(50) NOT NULL,
  `Value` varchar(50) NOT NULL,
  PRIMARY KEY (`EncounterID`,`PID1`,`PID2`,`Element`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `genexpertresults`
DROP TABLE IF EXISTS `genexpertresults`;
CREATE TABLE `genexpertresults` (
  `TestID` int(11) NOT NULL AUTO_INCREMENT,
  `PatientID` varchar(50) NOT NULL,
  `SputumTestID` varchar(50) NOT NULL,
  `LaboratoryID` varchar(50) DEFAULT NULL,
  `CollectedBy` varchar(50) DEFAULT NULL,
  `DateSubmitted` datetime DEFAULT NULL,
  `DateTested` datetime DEFAULT NULL,
  `GeneXpertResult` varchar(50) DEFAULT NULL,
  `IsPositive` bit(1) DEFAULT NULL,
  `MTBBurden` varchar(50) DEFAULT NULL,
  `DrugResistance` varchar(50) DEFAULT NULL,
  `ErrorCode` int(11) DEFAULT NULL,
  `Remarks` varchar(255) DEFAULT NULL,
  `PCID` varchar(50) DEFAULT NULL,
  `HostID` varchar(50) DEFAULT NULL,
  `InstrumentID` varchar(255) DEFAULT NULL,
  `ModuleID` varchar(255) DEFAULT NULL,
  `CartridgeID` varchar(255) DEFAULT NULL,
  `CartridgeExpiryDate` datetime DEFAULT NULL,
  `ReagentLotID` varchar(255) DEFAULT NULL,
  `OperatorID` varchar(255) DEFAULT NULL,
  `ProbeResultA` varchar(255) DEFAULT NULL,
  `ProbeResultB` varchar(255) DEFAULT NULL,
  `ProbeResultC` varchar(255) DEFAULT NULL,
  `ProbeResultD` varchar(255) DEFAULT NULL,
  `ProbeResultE` varchar(255) DEFAULT NULL,
  `ProbeResultSPC` varchar(255) DEFAULT NULL,
  `ProbeCtA` double DEFAULT NULL,
  `ProbeCtB` double DEFAULT NULL,
  `ProbeCtC` double DEFAULT NULL,
  `ProbeCtD` double DEFAULT NULL,
  `ProbeCtE` double DEFAULT NULL,
  `ProbeCtSPC` double DEFAULT NULL,
  `ProbeEndptA` double DEFAULT NULL,
  `ProbeEndptB` double DEFAULT NULL,
  `ProbeEndptC` double DEFAULT NULL,
  `ProbeEndptD` double DEFAULT NULL,
  `ProbeEndptE` double DEFAULT NULL,
  `ProbeEndptSPC` double DEFAULT NULL,
  PRIMARY KEY (`TestID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- Table structure for table `location`
DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `LocationID` varchar(50) NOT NULL,
  `locationName` varchar(50) DEFAULT NULL,
  `LocationType` varchar(20) DEFAULT NULL,
  `AddressHouse` varchar(50) DEFAULT NULL,
  `AddressStreet` varchar(50) DEFAULT NULL,
  `AddressSector` varchar(50) DEFAULT NULL,
  `AddressColony` varchar(50) DEFAULT NULL,
  `AddressTown` varchar(50) DEFAULT NULL,
  `CityID` varchar(50) DEFAULT NULL,
  `CountryID` varchar(50) DEFAULT NULL,
  `AddressLocationLAt` float DEFAULT NULL,
  `AddressLocationLon` float DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Mobile` varchar(50) DEFAULT NULL,
  `Fax` varchar(20) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `SecondaryPhone` varchar(20) DEFAULT NULL,
  `SecondaryMobile` varchar(20) DEFAULT NULL,
  `SecondaryFax` varchar(20) DEFAULT NULL,
  `SecondaryEmail` varchar(255) DEFAULT NULL,
  `Detail` varchar(255) DEFAULT NULL,
  `AddressLandmark` varchar(50) DEFAULT NULL,
  `ParentLocation` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`LocationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `messagesettings`
DROP TABLE IF EXISTS `messagesettings`;
CREATE TABLE `messagesettings` (
  `SettingsID` int(11) NOT NULL AUTO_INCREMENT,
  `SendToPatient` bit(1) DEFAULT NULL,
  `SendToProvider` bit(1) DEFAULT NULL,
  `SendToProgram` bit(1) DEFAULT NULL,
  `SendToManager` bit(1) DEFAULT NULL,
  `AttachPatientId` bit(1) DEFAULT NULL,
  `AttachSampleId` bit(1) DEFAULT NULL,
  `AttachMtb` bit(1) DEFAULT NULL,
  `AttachRif` bit(1) DEFAULT NULL,
  `AttachOperatorId` bit(1) DEFAULT NULL,
  `AttachLocationId` bit(1) DEFAULT NULL,
  `AttachModuleId` bit(1) DEFAULT NULL,
  `AttachCartridgeId` bit(1) DEFAULT NULL,
  `AttachTestDate` bit(1) DEFAULT NULL,
  `AlertOnAll` bit(1) DEFAULT NULL,
  `AlertOnAllMtb` bit(1) DEFAULT NULL,
  `AlertOnMtbHigh` bit(1) DEFAULT NULL,
  `AlertOnMtbMedium` bit(1) DEFAULT NULL,
  `AlertOnRif` bit(1) DEFAULT NULL,
  `AlertOnError` bit(1) DEFAULT NULL,
  `ProgramNumber` varchar(20) DEFAULT NULL,
  `ManagerNumber` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`SettingsID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `patient`
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `PatientID` varchar(50) NOT NULL,
  `ProviderID` varchar(50) DEFAULT NULL,
  `MRNo` varchar(50) DEFAULT NULL,
  `Weight` double DEFAULT NULL,
  `Height` double DEFAULT NULL,
  `BloodGroup` varchar(5) DEFAULT NULL,
  `DateRegistered` datetime DEFAULT NULL,
  `TreatmentCenter` varchar(20) DEFAULT NULL,
  `DiseaseSuspected` bit(1) DEFAULT NULL,
  `DiseaseConfirmed` bit(1) DEFAULT NULL,
  `DiseaseCategory` varchar(50) DEFAULT NULL,
  `DiseaseSite` varchar(50) DEFAULT NULL,
  `PatientStatus` varchar(50) DEFAULT NULL,
  `PatientType` varchar(50) DEFAULT NULL,
  `DiseaseHistory` varchar(255) DEFAULT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `Surname` varchar(50) DEFAULT NULL,
  `Gender` varchar(1) DEFAULT NULL,
  `DOB` datetime DEFAULT NULL,
  `Address1` varchar(50) DEFAULT NULL,
  `Address2` varchar(50) DEFAULT NULL,
  `Town` varchar(50) DEFAULT NULL,
  `Landmark` varchar(50) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `Latitude` double DEFAULT NULL,
  `Longitude` double DEFAULT NULL,
  `SecondaryAddress1` varchar(50) DEFAULT NULL,
  `SecondaryAddress2` varchar(50) DEFAULT NULL,
  `SecondaryTown` varchar(50) DEFAULT NULL,
  `SecondaryLandmark` varchar(50) DEFAULT NULL,
  `SecondaryCity` varchar(50) DEFAULT NULL,
  `SecondaryCountry` varchar(50) DEFAULT NULL,
  `FullDescription` longtext,
  PRIMARY KEY (`PatientID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `setuprole`
DROP TABLE IF EXISTS `setuprole`;
CREATE TABLE `setuprole` (
  `Role` varchar(50) NOT NULL,
  PRIMARY KEY (`Role`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
-- Dumping data for table `setuprole`
INSERT INTO `setuprole` VALUES ('ADMIN');

-- Table structure for table `userrights`
DROP TABLE IF EXISTS `userrights`;
CREATE TABLE `userrights` (
  `Role` varchar(50) NOT NULL,
  `MenuName` varchar(50) NOT NULL,
  `SearchAccess` bit(1) NOT NULL,
  `InsertAccess` bit(1) NOT NULL,
  `UpdateAccess` bit(1) NOT NULL,
  `DeleteAccess` bit(1) NOT NULL,
  `PrintAccess` bit(1) NOT NULL,
  PRIMARY KEY (`Role`,`MenuName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table `userrights`
INSERT INTO `userrights` VALUES ('ADMIN','DATALOG','\0','\0','\0','\0','\0');

-- Table structure for table `users`
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `PID` varchar(50) NOT NULL,
  `UserName` varchar(50) NOT NULL,
  `Role` varchar(50) NOT NULL,
  `Status` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL COMMENT 'Password is not stored directly, instead, it''s hashcode is stored. Hashcode is generated using an appropriate hashing algorithm.',
  `SecretQuestion` varchar(255) NOT NULL,
  `SecretAnswer` varchar(255) NOT NULL,
  PRIMARY KEY (`PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Dumping data for table `users`
INSERT INTO `users` VALUES ('ADMIN','ADMIN','ADMIN','ACTIVE','222117643951139901822193410624612819104125573923922','Dexter says: That number again is...','2191831962519017590304654193246402381779617073140137');
