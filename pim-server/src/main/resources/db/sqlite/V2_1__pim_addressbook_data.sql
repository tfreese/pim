--BEGIN TRANSACTION;

--TRUNCATE TABLE KONTAKT_ATTRIBUT;
--TRUNCATE TABLE KONTAKT;

INSERT INTO KONTAKT (id, user_id, nachname, vorname) VALUES (count(*) + 1, 'TOMMY', 'Freese', 'Thomas');
INSERT INTO KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) VALUES (count(*), 'GEBURTSTAG', '1975-05-13');

--COMMIT;
