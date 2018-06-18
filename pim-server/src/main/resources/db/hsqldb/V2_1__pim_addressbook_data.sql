--SET AUTOCOMMIT FALSE;

TRUNCATE TABLE KONTAKT_ATTRIBUT;
TRUNCATE TABLE KONTAKT;

INSERT INTO KONTAKT (id, user_id, nachname, vorname) VALUES (next value for kontakt_seq, SYSTEM_USER_NAME(), 'Freese', 'Thomas');
INSERT INTO KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) VALUES (current value for kontakt_seq, 'GEBURTSTAG', '1975-05-13');

--COMMIT;
