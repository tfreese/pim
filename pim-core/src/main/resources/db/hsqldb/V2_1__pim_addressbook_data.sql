--SET AUTOCOMMIT FALSE;

TRUNCATE TABLE KONTAKT_ATTRIBUT;
TRUNCATE TABLE KONTAKT;

INSERT INTO KONTAKT (user_id, id, nachname, vorname) VALUES (SYSTEM_USER_NAME(), next value for kontakt_seq, 'Freese', 'Thomas');
INSERT INTO KONTAKT_ATTRIBUT (user_id, kontakt_id, attribut, wert) VALUES (SYSTEM_USER_NAME(), current value for kontakt_seq, 'GEBURTSTAG', '1975-05-13');
--INSERT INTO KONTAKT (id, nachname, vorname) VALUES ((select nvl(max(id), 0) + 1 from kontakt), 'Freese', 'Thomas');
--INSERT INTO KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) VALUES ((select max(id) from kontakt), 'GEBURTSTAG', '1975-05-13');

--COMMIT;
