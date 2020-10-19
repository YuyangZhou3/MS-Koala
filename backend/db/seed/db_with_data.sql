-- Adminer 4.7.3 MySQL dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

DROP DATABASE IF EXISTS `medsec`;
CREATE DATABASE `medsec` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `medsec`;

DROP TABLE IF EXISTS `Appointment`;
CREATE TABLE `Appointment` (
  `id` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `did` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `detail` longtext DEFAULT NULL,
  `date_create` datetime NOT NULL,
  `date_change` datetime NOT NULL,
  `date` datetime NOT NULL,
  `duration` int(45) NOT NULL,
  `note` longtext DEFAULT NULL,
  `user_note` longtext DEFAULT NULL,
  `status` enum('UNCONFIRMED','CONFIRMED','CANCELLED') DEFAULT 'UNCONFIRMED',
  PRIMARY KEY (`id`),
  KEY `fk_Appointment_Patient1_idx` (`uid`),
  KEY `did` (`did`),
  CONSTRAINT `Appointment_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `User` (`id`),
  CONSTRAINT `Appointment_ibfk_3` FOREIGN KEY (`did`) REFERENCES `Doctor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Appointment` (`id`, `uid`, `did`, `title`, `detail`, `date_create`, `date_change`, `date`, `duration`, `note`, `user_note`, `status`) VALUES
(1,	1,	1,	'Day Oncology Unit',	'Education session',	'2020-05-01 00:00:00',	'2020-05-18 04:05:16',	'2020-06-18 14:15:00',	15,	'Looking after yourself during chemotherapy - Watch Patient Health History Sheet - Please fill in and email back to daychemo.wrp@ramsayhealth.com.au Parking Information - ReadQuestions Sheet - Read',	NULL,	'UNCONFIRMED');

DROP TABLE IF EXISTS `Doctor`;
CREATE TABLE `Doctor` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `expertise` mediumtext DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Doctor` (`id`, `name`, `bio`, `address`, `phone`, `fax`, `email`, `website`, `expertise`) VALUES
(1,	'Callum',	NULL,	'14 Fake st',	'555',	NULL,	'doctor@doctor.com',	NULL,	'Radiology'),
(2,	'Dr. Who',	NULL,	'16 tardis street',	'555',	'555',	'timelord_01@Gallifrey',	'test.com',	'Electronic screwdriver'),
(3,	'Dr. No',	NULL,	'Bond st ',	'655',	'555',	'drno@gmail.com',	'www.no.com',	'Lasers, sharks');

DROP TABLE IF EXISTS `File`;
CREATE TABLE `File` (
  `id` int(11) NOT NULL,
  `apptid` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `link` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `link` (`link`),
  KEY `apptid` (`apptid`),
  CONSTRAINT `File_ibfk_2` FOREIGN KEY (`apptid`) REFERENCES `Appointment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `Hospital`;
CREATE TABLE `Hospital` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `emergencyDept` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `aftPhone` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Hospital` (`id`, `name`, `address`, `emergencyDept`, `phone`, `aftPhone`, `fax`, `email`, `website`) VALUES
(1,	'Warringal Private Hospital',	'216 burgundy Street, Heidelberg VIC 3084',	NULL,	'(03) 9274 1300',	NULL,	'(03) 9459 7606',	NULL,	'https://www.warringalprivate.com.au'),
(2,	'Warringal Day Oncology Unit',	'Warringal Private Hospital, 8 Martin Street, HEIDELBERG VIC ',	NULL,	'(03) 9274 1423',	'9274 1371',	NULL,	'daychemo.wrp@ramsayhealth.com.au',	'https://www.warringalprivate.com.au/Our-Services/Day-Oncology-Centre'),
(3,	'Austin Hospital',	'145 Studley Rd, Heidelberg VIC 3084',	'Open 24 hours',	'(03) 9496 5000',	NULL,	'(03) 9458 4779',	NULL,	'https://www.austin.org.au'),
(4,	'Austin Repatriation Hospital',	'300 Waterdale Road, Ivanhoe Victoria 3079',	NULL,	'(03) 9496 5000',	NULL,	'(03) 9496 2541',	NULL,	'https://www.austin.org.au/heidelberg-repatriation-hospital'),
(5,	'Olivia Newton-John Cancer Wellness and Research Centre',	'145 Studley Road, Heidelberg Victoria 3084',	NULL,	'(03) 9496 5000',	NULL,	'(03) 9458 4779',	NULL,	'https://www.onjcancercentre.org');

DROP TABLE IF EXISTS `NotificationToken`;
CREATE TABLE `NotificationToken` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `fcm_token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid_fcm_token` (`uid`,`fcm_token`),
  KEY `fk_NotificationToken_User_idx` (`uid`),
  CONSTRAINT `NotificationToken_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `NotificationToken` (`id`, `uid`, `fcm_token`) VALUES
(114,	1,	'cTt68GCxobs:APA91bEksB-nhK7ZrIu1l91l2I3-JMwX-QvJ7uFAMK3WKzOltY2GX5z1tgN69xjW1CYTy9koUWKgBO-DZ-rLHq0UbLCVdrHbjU_KkxY6RlYq4Jj6-T3ZPSsXvUseABaLAip7J78VR0qq');

DROP TABLE IF EXISTS `Pathology`;
CREATE TABLE `Pathology` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `hours` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Pathology` (`id`, `name`, `address`, `phone`, `hours`, `website`) VALUES
(1,	'Dorevitch Pathology',	'66 Darebin Street, HEIDELBERG VIC 3084',	'(03) 9457 2200',	'Monday	Closed\nTuesday	12-5pm\nWednesday	9am-1pm\nThursday	9am-1pm\nFriday	9am-1pm\nSaturday	Closed\nSunday	Closed ',	'https://www.dorevitch.com.au/patients/find-a-collection-centre/'),
(2,	'Melbourne Pathology',	NULL,	NULL,	NULL,	'https://www.mps.com.au/locations/'),
(3,	'Austin Pathology',	'Level 6, Harold Stokes Building, Austin Hospital Studley Road, Heidelberg, VIC 3084',	'9496-3100 (24/7)',	NULL,	'https://www.austinpathology.org.au'),
(4,	'Australian Clinical Labs',	NULL,	NULL,	NULL,	'https://www.clinicallabs.com.au');

DROP TABLE IF EXISTS `Radiology`;
CREATE TABLE `Radiology` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `hours` varchar(255) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Radiology` (`id`, `name`, `address`, `phone`, `fax`, `hours`, `email`, `website`) VALUES
(1,	'I-MED ',	'Level 1/10 Martin St, Heidelberg VIC 3084',	'(03) 9450 1800',	'(03) 9450 1888',	'Monday - Friday, 8:30am - 5:30pm',	NULL,	'https://i-med.com.au/clinics/clinic/Heidelberg'),
(2,	'I-MED Warringal Radiology',	'Warringal Medical Centre Level 2, 214 Burgundy Street Heidelberg VIC 3084',	'(03) 9450 2100',	'(03) 9450 2114',	'Monday - Friday, 8:30am - 5:30pm',	NULL,	'https://i-med.com.au/clinics/clinic/Warringal'),
(3,	'Austin Nuclear Medicine and PET',	'Level 1, Harold Stoke Building, 145 Studley Road',	'(03) 9496 5718',	'(03) 9457 6605',	NULL,	'enquiries.miat@austin.org.au',	'https://www.austin.org.au/MIaT_Contact_Us/'),
(4,	'Austin Radiology - Repatriation Hospital',	'300 Waterdale Road, Ivanhoe Victoria 3079',	'(03) 9496 5000',	'(03) 9496 2541',	NULL,	'enquiries.radiology@austin.org.au',	'https://www.austin.org.au/heidelberg-repatriation-hospital');

DROP TABLE IF EXISTS `Resource`;
CREATE TABLE `Resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `resource` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `Resource_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Resource` (`id`, `uid`, `name`, `resource`) VALUES
(1,	1,	'Download Information',	'https://www.google.com');

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `id` int(11) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `firstname` varchar(45) NOT NULL,
  `middlename` varchar(45) DEFAULT NULL,
  `surname` varchar(45) NOT NULL,
  `dob` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `street` varchar(45) DEFAULT NULL,
  `suburb` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `token_valid_from` datetime DEFAULT NULL,
  `token_expire_date` datetime DEFAULT NULL,
  `role` enum('PATIENT','ADMIN') DEFAULT 'PATIENT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `User` (`id`, `password`, `firstname`, `middlename`, `surname`, `dob`, `email`, `street`, `suburb`, `state`, `token`, `token_valid_from`, `token_expire_date`, `role`) VALUES
(1,	'123',	'Alex',	'Mileston',	'Williamson',	'1986-06-24',	'williamson@example.com',	'97 Masthead Drive',	'ROCKHAMPTON',	'QLD',	'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiUEFUSUVOVCIsImp0aSI6InF1Nm0yZ2pwNTJoNTcxc21pbWo2a2xnYXQ1IiwiZXhwIjoxNTkyMzY0NDgzLCJpYXQiOjE1OTIyNzgwODMsInN1YiI6IjEifQ.q4lqimYa0mN7eBiHtJva2_tm9OMEClQeoEjNoFs5mWAgVC13TvQjqHZgKwrT006Frt2qghYnaBAh_N4DhyjuHg',	'2020-06-16 03:28:03',	'2020-06-17 03:28:03',	'PATIENT');

-- 2020-06-16 04:51:20
