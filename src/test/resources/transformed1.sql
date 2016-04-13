--@SCOPE BASELINE BEGIN
--@UNSCOPED@--DROP TABLE IF EXISTS `POEMS`;
--@UNSCOPED@--CREATE TABLE `POEMS` (
--@UNSCOPED@--  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
--@UNSCOPED@--  `POEM_TYPE_ID` int(10) unsigned NULL,
--@UNSCOPED@--  `AUTHOR` varchar(45) NOT NULL,
--@UNSCOPED@--  `TITLE` varchar(200) NOT NULL,
--@UNSCOPED@--  `CONTENT` varchar(4000) NOT NULL,
--@UNSCOPED@--  PRIMARY KEY (`ID`)
--@UNSCOPED@--) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--@SCOPE END


--@SCOPE OP-3456 BEGIN
--@UNSCOPED@--DROP TABLE IF EXISTS `POEM_TYPES`;
--@UNSCOPED@--CREATE TABLE `POEM_TYPES` (
--@UNSCOPED@--  `ID` int(10) unsigned NOT NULL,
  --@SCOPE OP-4567 BEGIN
  --@UNSCOPED@--  `LABEL` varchar(45) NOT NULL,
  --@SCOPE END
--@UNSCOPED@--  PRIMARY KEY (`ID`)
--@UNSCOPED@--) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--@SCOPE END

--@SCOPE OP-4567 BEGIN
ALTER TABLE POEMS ADD CONSTRAINT POEMS_POEM_TYPES FOREIGN KEY (POEM_TYPE_ID) REFERENCES POEM_TYPES (ID);
--@SCOPE END
