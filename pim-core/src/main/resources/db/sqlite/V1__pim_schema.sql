PRAGMA foreign_keys = ON;
--BEGIN TRANSACTION;



DROP TABLE IF EXISTS SETTINGS;

CREATE TABLE SETTINGS --Tabelle für die User-Spezifische Konfiguration
(
   USER_ID  VARCHAR (50) NOT NULL CHECK(LENGTH(TRIM(USER_ID)) > 0), --Eigentümer
   KEY      VARCHAR (50) NOT NULL CHECK(LENGTH(TRIM(KEY)) > 0), --Schlüssel eines Attributes
   VALUE    VARCHAR (50) --Wert eines Attributes
);
CREATE UNIQUE INDEX SETTINGS_UNQ ON SETTINGS (USER_ID, KEY);



--COMMIT;