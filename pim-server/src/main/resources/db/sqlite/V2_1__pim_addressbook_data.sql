--BEGIN TRANSACTION;

--TRUNCATE TABLE KONTAKT_ATTRIBUT;
--TRUNCATE TABLE KONTAKT;

INSERT INTO KONTAKT (id, user_id, nachname, vorname) select (ifnull(max(id), 0) + 1) , 'TOMMY', 'Freese', 'Thomas' from kontakt;
INSERT INTO KONTAKT_ATTRIBUT (kontakt_id, attribut, wert) select max(id), 'GEBURTSTAG', '1975-05-13' from kontakt;

--COMMIT;
