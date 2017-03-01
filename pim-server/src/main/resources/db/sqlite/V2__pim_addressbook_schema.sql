PRAGMA foreign_keys = ON;

DROP VIEW IF EXISTS V_ADDRESSBOOK;
DROP TABLE IF EXISTS KONTAKT_ATTRIBUT;
DROP TABLE IF EXISTS KONTAKT;

CREATE TABLE KONTAKT --Tabelle für eindeutige Kontakte (Vorname, Nachname)
(
    ID          BIGINT NOT NULL PRIMARY KEY, --Primary Key
	USER_ID		VARCHAR (50) NOT NULL, --Eigentümer
	NACHNAME	VARCHAR (50) NOT NULL CHECK(LENGTH(TRIM(NACHNAME)) > 0), --Der Nachname es Kontakts
	VORNAME		VARCHAR (50) CHECK(VORNAME IS NULL OR LENGTH(TRIM(VORNAME)) > 0) --Der Vorname es Kontakts
);
CREATE INDEX KONTAKT_IDX_ID ON KONTAKT (ID);
CREATE INDEX KONTAKT_IDX_USERID ON KONTAKT (USER_ID);
CREATE UNIQUE INDEX KONTAKT_UNQ ON KONTAKT (USER_ID, NACHNAME, VORNAME);

CREATE TABLE KONTAKT_ATTRIBUT --Tabelle für Key-Value Attribute eines Kontakts
(
	KONTAKT_ID	BIGINT NOT NULL, --Foreign Key des Kontakts
	ATTRIBUT	VARCHAR (20) NOT NULL CHECK(LENGTH(TRIM(ATTRIBUT)) > 0), --Name des Attributs (BIRTHDAY, STREET, ...)
	WERT		VARCHAR (1000) NOT NULL CHECK(LENGTH(TRIM(WERT)) > 0), --Wert des Attributs (BIRTHDAY, STREET, ...)
	FOREIGN KEY (KONTAKT_ID) REFERENCES KONTAKT (ID)
);
CREATE UNIQUE INDEX KONTAKTATTRIBUT_UNQ ON KONTAKT_ATTRIBUT (KONTAKT_ID, ATTRIBUT);
CREATE INDEX KONTAKTATTRIBUT_IDX_KONTAKTID ON KONTAKT_ATTRIBUT (KONTAKT_ID);


CREATE VIEW V_ADDRESSBOOK AS
SELECT k.USER_ID, k.ID, k.NACHNAME, k.VORNAME, ka.ATTRIBUT, ka.WERT
    FROM KONTAKT k LEFT OUTER JOIN KONTAKT_ATTRIBUT ka ON ka.KONTAKT_ID = k.ID
ORDER BY k.NACHNAME ASC;
