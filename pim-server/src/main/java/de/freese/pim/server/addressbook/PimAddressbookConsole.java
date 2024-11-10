// Created on 24.05.2016
package de.freese.pim.server.addressbook;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import de.freese.pim.core.dao.AddressBookDao;
import de.freese.pim.core.dao.DefaultAddressBookDao;
import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.model.addressbook.KontaktAttribut;
import de.freese.pim.core.utils.PreserveOrderOptionGroup;
import de.freese.pim.core.utils.Utils;

/**
 * Console-Client für das Addressbuch.<br>
 * Als Default wird die lokale HSQLDB (Utils#HSQLDB_URL) verwendet.
 *
 * @author Thomas Freese
 */
public final class PimAddressbookConsole {
    public static final Logger LOGGER = LoggerFactory.getLogger(PimAddressbookConsole.class);

    public static final PrintStream PRINT_STREAM = System.out;

    public static void main(final String[] args) throws Exception {
        String[] arguments = args;

        if (arguments.length == 0) {
            usage();
        }

        CommandLine line = null;

        try {
            final CommandLineParser parser = new DefaultParser();
            line = parser.parse(getCommandOptions(), arguments);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());

            usage();
        }

        // SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        final Path home = Paths.get(System.getProperty("user.home"), ".pim");

        if (!Files.exists(home)) {
            Files.createDirectories(home);
        }

        final SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:file:" + System.getProperty("user.home") + "/.pim/pimdb;shutdown=true");
        dataSource.setAutoCommit(true);
        dataSource.setSuppressClose(true);

        try {
            final PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

            final DefaultAddressBookDao addressBookDAO = new DefaultAddressBookDao();
            addressBookDAO.setDataSource(dataSource);

            final PimAddressbookConsole addressbook = new PimAddressbookConsole();
            addressbook.setAddressBookDAO(addressBookDAO);
            addressbook.setTransactionManager(transactionManager);
            addressbook.setPrintStream(PRINT_STREAM);

            if (line.hasOption("ik")) {
                arguments = line.getOptionValues("insert-kontakt");
                addressbook.insertKontakt(arguments[0], arguments[1]);
            }
            else if (line.hasOption("uk")) {
                arguments = line.getOptionValues("update-kontakt");
                final long id = Long.parseLong(arguments[0]);
                addressbook.updateKontakt(id, arguments[1], arguments[2]);
            }
            else if (line.hasOption("dk")) {
                final long id = Long.parseLong(line.getOptionValue("delete-kontakt"));
                addressbook.deleteKontakt(id);
            }
            else if (line.hasOption("ia")) {
                arguments = line.getOptionValues("insert-attribut");
                final long id = Long.parseLong(arguments[0]);
                addressbook.insertAttribut(id, arguments[1], arguments[2]);
            }
            else if (line.hasOption("ua")) {
                arguments = line.getOptionValues("update-attribut");
                final long id = Long.parseLong(arguments[0]);
                addressbook.updateAttribut(id, arguments[1], arguments[2]);
                // long id = Long.parseLong(args[1]);
                // addressbook.updateAttribut(id, args[2], StringUtils.join(args, ' ', 3, args.length));
            }
            else if (line.hasOption("da")) {
                arguments = line.getOptionValues("delete-attribut");
                final long id = Long.parseLong(arguments[0]);
                addressbook.deleteAttribut(id, arguments[1]);
            }
            else if (line.hasOption("lk")) {
                addressbook.getKontakte();
            }
            else if (line.hasOption("vk")) {
                final long id = Long.parseLong(line.getOptionValue("view-kontakt"));
                addressbook.getKontaktDetails(id);
            }
            else if (line.hasOption("s")) {
                arguments = line.getOptionValues("search");
                addressbook.search(arguments[0]);
            }
        }
        finally {
            dataSource.destroy();
        }
    }

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     */
    private static Options getCommandOptions() {
        final OptionGroup group = new PreserveOrderOptionGroup();

        Option option = new Option("ik", "insert-kontakt", true, "Hinzufügen eines Kontakts");
        option.setArgs(2);
        option.setArgName("NACHNAME VORNAME");
        group.addOption(option);

        option = new Option("uk", "update-kontakt", true, "Aktualisieren eines Kontakts");
        option.setArgs(3);
        option.setArgName("ID NACHNAME VORNAME");
        group.addOption(option);

        option = new Option("dk", "delete-kontakt", true, "Löscht einen Kontakt");
        option.setArgs(1);
        option.setArgName("ID");
        group.addOption(option);

        option = new Option("ia", "insert-attribut", true, "Hinzufügen eines Kontaktattributs");
        option.setArgs(3);
        option.setArgName("ID ATTRIBUT WERT");
        group.addOption(option);

        option = new Option("ua", "update-attribut", true, "Aktualisieren eines Kontaktattributs");
        option.setArgs(3);
        option.setArgName("ID ATTRIBUT WERT");
        group.addOption(option);

        option = new Option("da", "delete-attribut", true, "Löschen eines Kontaktattributs");
        option.setArgs(2);
        option.setArgName("ID ATTRIBUT");
        group.addOption(option);

        option = new Option("lk", "list-kontakte", false, "Auflisten aller Kontakte");
        group.addOption(option);

        option = new Option("vk", "view-kontakt", true, "Liefert Details eines Kontakts");
        option.setArgs(1);
        option.setArgName("ID");
        group.addOption(option);

        option = new Option("s", "search", true, "Suchen nach Kontakten");
        option.setArgs(1);
        option.setArgName("Pattern");
        group.addOption(option);

        final Options options = new Options();
        options.addOptionGroup(group);

        return options;
    }

    private static void usage() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        // formatter.setWidth(120);
        // formatter.printHelp("Addressbook\n", getCommandOptions(), true);

        final StringBuilder footer = new StringBuilder();
        footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        formatter.printHelp(120, "Addressbook\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

        System.exit(-1);
    }

    private AddressBookDao addressBookDAO;

    private PrintStream printStream = System.out;

    private PlatformTransactionManager transactionManager;

    private void deleteAttribut(final long kontaktId, final String attribut) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = this.addressBookDAO.deleteAttribut(kontaktId, attribut);

            if (affectedRows > 0) {
                this.printStream.printf("%nAttribut gelöscht%n");
            }
            else {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontaktId);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void deleteKontakt(final long id) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = this.addressBookDAO.deleteKontakt(id);

            if (affectedRows > 0) {
                this.printStream.printf("%nKontakt gelöscht%n");
            }
            else {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void getKontaktDetails(final long id) {
        try {
            final List<Kontakt> kontakts = this.addressBookDAO.getKontaktDetails(id);

            printDetails(kontakts);
        }
        catch (Exception ex) {
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void getKontakte() {
        try {
            final List<Kontakt> kontakte = this.addressBookDAO.getKontakte();

            final List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"ID", "VORNAME", "NACHNAME"});

            kontakte.forEach(k -> {
                final String[] row = new String[3];
                rows.add(row);

                row[0] = Long.toString(k.getID());
                row[1] = k.getNachname();
                row[2] = k.getVorname();
            });

            print(rows, this.printStream);
        }
        catch (Exception ex) {
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void insertAttribut(final long kontaktId, final String attribut, final String wert) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try {
            // boolean success =
            this.addressBookDAO.insertAttribut(kontaktId, attribut, wert);

            getKontaktDetails(kontaktId);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException) {
                this.printStream.printf("%nKontaktattribut existiert bereits%n");
            }
            else {
                LOGGER.error(cause.getMessage(), cause);
            }
        }
    }

    private void insertKontakt(final String nachname, final String vorname) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);
        long id = 0;

        try {
            id = this.addressBookDAO.insertKontakt(nachname, vorname);

            getKontaktDetails(id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException) {
                this.printStream.printf("%nKontaktattribut existiert bereits%n");
            }
            else {
                LOGGER.error(cause.getMessage(), cause);
            }
        }
    }

    private void print(final List<String[]> rows, final PrintStream printStream) {
        Utils.padding(rows, " ");
        Utils.addHeaderSeparator(rows, "-");
        Utils.write(rows, printStream, " | ");
    }

    private void printDetails(final List<Kontakt> kontakte) {
        final List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "VORNAME", "NACHNAME", "ATTRIBUT", "WERT"});

        kontakte.forEach(kontakt -> {
            rows.add(new String[]{Long.toString(kontakt.getID()), kontakt.getNachname(), kontakt.getVorname(), "", ""});

            final List<KontaktAttribut> kontaktAttribute = kontakt.getAttribute();

            if (!kontaktAttribute.isEmpty()) {
                rows.getLast()[3] = kontaktAttribute.getFirst().getAttribut();
                rows.getLast()[4] = kontaktAttribute.getFirst().getWert();
            }

            if (kontaktAttribute.size() > 1) {
                kontaktAttribute.stream().skip(1).forEach(ka -> {
                    final String[] row = new String[5];
                    rows.add(row);

                    row[0] = "";
                    row[1] = "";
                    row[2] = "";
                    row[3] = ka.getAttribut();
                    row[4] = ka.getWert();
                });
            }
        });

        print(rows, this.printStream);
    }

    private void search(final String name) {
        try {
            final List<Kontakt> kontakte = this.addressBookDAO.searchKontakte(name);

            printDetails(kontakte);
        }
        catch (Exception ex) {
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void setAddressBookDAO(final AddressBookDao addressBookDAO) {
        this.addressBookDAO = addressBookDAO;
    }

    private void setPrintStream(final PrintStream printStream) {
        this.printStream = printStream;
    }

    private void setTransactionManager(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private void updateAttribut(final long kontaktId, final String attribut, final String wert) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = this.addressBookDAO.updateAttribut(kontaktId, attribut, wert);

            if (affectedRows > 0) {
                this.printStream.printf("%nAttribut aktualisiert%n");
            }
            else {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontaktId);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void updateKontakt(final long id, final String nachname, final String vorname) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = this.addressBookDAO.updateKontakt(id, nachname, vorname);

            if (affectedRows > 0) {
                this.printStream.printf("%nKontakt aktualisiert%n");
            }
            else {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }

            getKontaktDetails(id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            this.transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }
}
