DROP TABLE IF EXISTS SETTINGS CASCADE;

CREATE FUNCTION SYSTEM_USER_NAME() RETURNS VARCHAR(10)
LANGUAGE JAVA DETERMINISTIC NO SQL
EXTERNAL NAME 'CLASSPATH:de.freese.pim.core.utils.Utils.getSystemUserName'
;

--DEFAULT SYSTEM_USER_NAME()
CREATE TABLE SETTINGS
(
   USER_ID	VARCHAR (50) NOT NULL,
   KEY		VARCHAR (50) NOT NULL,
   VALUE	VARCHAR (50)
);
--ALTER TABLE SETTINGS ADD CONSTRAINT SETTINGS_PK PRIMARY KEY (ID);
CREATE UNIQUE INDEX SETTINGS_UNQ ON SETTINGS (USER_ID, KEY, VALUE);
ALTER TABLE SETTINGS ADD CONSTRAINT SETTINGS_CK_USER CHECK(LENGTH(TRIM(USER_ID)) > 0);
ALTER TABLE SETTINGS ADD CONSTRAINT SETTINGS_CK_KEY CHECK(LENGTH(TRIM(KEY)) > 0);
COMMENT ON TABLE SETTINGS IS 'Tabelle für die User-Spezifische Konfiguration';
COMMENT ON COLUMN SETTINGS.USER_ID IS 'Eigentümer';
COMMENT ON COLUMN SETTINGS.KEY IS 'Schlüssel eines Attributes';
COMMENT ON COLUMN SETTINGS.VALUE IS 'Wert eines Attributes';

-- Oracle
-- ALTER TABLE <TABLE> DROP FOREIGN KEY <KEY>;
-- ALTER TABLE <TABLE> DROP PRIMARY KEY <KEY>;
-- ALTER TABLE <TABLE> DROP CONSTRAINT <CONSTRAINT>;
-- ALTER TABLE <TABLE> DROP KEY <KEY>;
-- DROP TABLE <TABLE> CASCADE CONSTRAINTS PURGE;
-- ALTER INDEX <INDEX> DISABLE / ENABLE; -- Besser mit UNUSABLE, da nur für function-based index
-- ALTER INDEX <INDEX> UNUSABLE; ALTER SESSION SET skip_unusable_indexes = true;
-- ALTER INDEX <INDEX> REBUILD [ONLINE] (PARALLEL 4 / NOPARALLEL);
-- EXECUTE DBMS_STATS.GATHER_INDEX_STATS(SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA'), '<INDEX>'); -- Geschied beim REBUILD
-- SEQUENCE.nextval, SEQUENCE.currval; select SEQUENCE.nextval from dual;
