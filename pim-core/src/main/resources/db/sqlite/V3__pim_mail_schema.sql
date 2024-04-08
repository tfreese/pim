PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS MAIL;
DROP TABLE IF EXISTS MAILFOLDER;
DROP TABLE IF EXISTS MAILACCOUNT;

CREATE TABLE MAILACCOUNT --Tabelle für MailAccounts
(
    ID                  BIGINT NOT NULL PRIMARY KEY, --Primary Key
	USER_ID				VARCHAR (50) NOT NULL, --Eigentümer
	MAIL				VARCHAR (50) NOT NULL, --Account
	PASSWORT			VARCHAR (100) NOT NULL, --Passwort
	IMAP_HOST			VARCHAR (50) NOT NULL CHECK(LENGTH(TRIM(IMAP_HOST)) > 0), --IMAP Host
	IMAP_PORT			INTEGER DEFAULT 993 NOT NULL CHECK(IMAP_PORT > 0), --IMAP Port
	IMAP_LEGITIMATION   BOOLEAN DEFAULT TRUE NOT NULL, --IMAP Legitimation
	SMTP_HOST			VARCHAR (50) NOT NULL CHECK(LENGTH(TRIM(SMTP_HOST)) > 0), --SMTP Host
	SMTP_PORT			INTEGER DEFAULT 587 NOT NULL CHECK(SMTP_PORT > 0), --SMTP Port
	SMTP_LEGITIMATION	BOOLEAN DEFAULT TRUE NOT NULL --SMTP Legitimation
);
CREATE UNIQUE INDEX MAILACCOUNT_UNQ ON MAILACCOUNT (USER_ID, MAIL);
CREATE INDEX MAILACCOUNT_IDX_ID ON MAILACCOUNT (ID);
CREATE INDEX MAILACCOUNT_IDX_USERID ON MAILACCOUNT (USER_ID);

CREATE TABLE MAILFOLDER --Tabelle für MailFolder
(
    ID              BIGINT NOT NULL PRIMARY KEY, --Primary Key
    ACCOUNT_ID      BIGINT NOT NULL, --ID des Accounts
    FULLNAME        VARCHAR (100) NOT NULL CHECK(LENGTH(TRIM(FULLNAME)) > 0), --Hierarchischer Name
    NAME            VARCHAR (100) NOT NULL CHECK(LENGTH(TRIM(NAME)) > 0), --Einfacher Name
    ABONNIERT       BOOLEAN DEFAULT TRUE NOT NULL, --Wenn TRUE wird der Folder auf neue Mails geprüft
    FOREIGN KEY (ACCOUNT_ID) REFERENCES MAILACCOUNT (ID)
);
CREATE UNIQUE INDEX MAILFOLDER_UNQ ON MAILFOLDER (ACCOUNT_ID, FULLNAME);
CREATE INDEX MAILFOLDER_IDX_ID ON MAILFOLDER (ID);
CREATE INDEX MAILFOLDER_IDX_ACCOUNTID ON MAILFOLDER (ACCOUNT_ID);

CREATE TABLE MAIL --Tabelle für Mails
(
	FOLDER_ID		BIGINT NOT NULL, --Folder
    UID             BIGINT NOT NULL CHECK(UID > 0), --UID	
	MSG_NUM         INTEGER NOT NULL CHECK(MSG_NUM > 0), --Message-Nummer der Mail im Folder
	SENDER			VARCHAR (200), --Absender: leer bei gesendeten Mails
	RECIPIENT_TO	TEXT, --Empfänger
	RECIPIENT_CC    TEXT, --Empfänger, Copy
	RECIPIENT_BCC   TEXT, --Empfänger, Blind Copy
	RECEIVED_DATE   TIMESTAMP, --Empfangsdatum: leer bei gesendeten Mails
	SEND_DATE		TIMESTAMP, --Sendedatum: leer bei empfangenen Mails
	SUBJECT         VARCHAR (400), --Betreff
	SIZE            INTEGER NOT NULL CHECK(SIZE > 0), --Größe in Bytes
	SEEN			BOOLEAN DEFAULT FALSE NOT NULL, --SEEN-Flag
	FOREIGN KEY (FOLDER_ID) REFERENCES MAILFOLDER (ID)
);
CREATE UNIQUE INDEX MAIL_UNQ ON MAIL (FOLDER_ID, UID);
CREATE INDEX MAIL_IDX_FOLDERID ON MAIL (FOLDER_ID);

