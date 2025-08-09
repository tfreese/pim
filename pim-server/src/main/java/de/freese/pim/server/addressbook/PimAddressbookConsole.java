// Created on 24.05.2016
package de.freese.pim.server.addressbook;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
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

    static void main(final String[] args) throws Exception {
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

        group.addOption(Option.builder()
                .option("ik")
                .longOpt("insert-kontakt")
                .hasArgs()
                .numberOfArgs(2)
                .argName("NACHNAME VORNAME")
                .desc("Hinzufügen eines Kontakts")
                .get());

        group.addOption(Option.builder()
                .option("uk")
                .longOpt("update-kontakt")
                .hasArgs()
                .numberOfArgs(3)
                .argName("ID NACHNAME VORNAME")
                .desc("Aktualisieren eines Kontakts")
                .get());

        group.addOption(Option.builder()
                .option("dk")
                .longOpt("delete-kontakt")
                .hasArgs()
                .numberOfArgs(1)
                .argName("ID")
                .type(Long.class)
                .desc("Löscht einen Kontakt")
                .get());

        group.addOption(Option.builder()
                .option("ia")
                .longOpt("insert-attribut")
                .hasArgs()
                .numberOfArgs(3)
                .argName("ID ATTRIBUT WERT")
                .desc("Hinzufügen eines Kontaktattributs")
                .get());

        group.addOption(Option.builder()
                .option("ua")
                .longOpt("update-attribut")
                .hasArgs()
                .numberOfArgs(3)
                .argName("ID ATTRIBUT WERT")
                .desc("Aktualisieren eines Kontaktattributs")
                .get());

        group.addOption(Option.builder()
                .option("da")
                .longOpt("delete-attribut")
                .hasArgs()
                .numberOfArgs(2)
                .argName("ID ATTRIBUT")
                .desc("Löschen eines Kontaktattributs")
                .get());

        group.addOption(Option.builder()
                .option("lk")
                .longOpt("list-kontakte")
                .desc("Auflisten aller Kontakte")
                .get());

        group.addOption(Option.builder()
                .option("vk")
                .longOpt("view-kontakt")
                .hasArgs()
                .numberOfArgs(1)
                .argName("ID")
                .type(Long.class)
                .desc("Liefert Details eines Kontakts")
                .get());

        group.addOption(Option.builder()
                .option("s")
                .longOpt("search")
                .hasArgs()
                .numberOfArgs(1)
                .argName("Pattern")
                .type(String.class)
                .desc("Suchen nach Kontakten")
                .get());

        final Options options = new Options();
        options.addOptionGroup(group);

        return options;
    }

    private static void usage() {
        final HelpFormatter formatter = HelpFormatter.builder()
                .setShowSince(false)
                .get();

        final StringBuilder footer = new StringBuilder();
        footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        try {
            formatter.printHelp("Addressbook", "", getCommandOptions(), footer.toString(), true);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        System.exit(-1);
    }

    private AddressBookDao addressBookDAO;

    private PrintStream printStream = System.out;

    private PlatformTransactionManager transactionManager;

    private void deleteAttribut(final long kontaktId, final String attribut) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = addressBookDAO.deleteAttribut(kontaktId, attribut);

            if (affectedRows > 0) {
                printStream.printf("%nAttribut gelöscht%n");
            }
            else {
                printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontaktId);

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void deleteKontakt(final long id) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = addressBookDAO.deleteKontakt(id);

            if (affectedRows > 0) {
                printStream.printf("%nKontakt gelöscht%n");
            }
            else {
                printStream.printf("%nKein Kontakt gefunden%n");
            }

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void getKontaktDetails(final long id) {
        try {
            final List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(id);

            printDetails(kontakts);
        }
        catch (Exception ex) {
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void getKontakte() {
        try {
            final List<Kontakt> kontakte = addressBookDAO.getKontakte();

            final List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"ID", "VORNAME", "NACHNAME"});

            kontakte.forEach(k -> {
                final String[] row = new String[3];
                rows.add(row);

                row[0] = Long.toString(k.getID());
                row[1] = k.getNachname();
                row[2] = k.getVorname();
            });

            print(rows, printStream);
        }
        catch (Exception ex) {
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void insertAttribut(final long kontaktId, final String attribut, final String wert) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            // boolean success =
            addressBookDAO.insertAttribut(kontaktId, attribut, wert);

            getKontaktDetails(kontaktId);

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException) {
                printStream.printf("%nKontaktattribut existiert bereits%n");
            }
            else {
                LOGGER.error(cause.getMessage(), cause);
            }
        }
    }

    private void insertKontakt(final String nachname, final String vorname) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        long id = 0;

        try {
            id = addressBookDAO.insertKontakt(nachname, vorname);

            getKontaktDetails(id);

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException) {
                printStream.printf("%nKontaktattribut existiert bereits%n");
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

        print(rows, printStream);
    }

    private void search(final String name) {
        try {
            final List<Kontakt> kontakte = addressBookDAO.searchKontakte(name);

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
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = addressBookDAO.updateAttribut(kontaktId, attribut, wert);

            if (affectedRows > 0) {
                printStream.printf("%nAttribut aktualisiert%n");
            }
            else {
                printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontaktId);

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }

    private void updateKontakt(final long id, final String nachname, final String vorname) {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            final int affectedRows = addressBookDAO.updateKontakt(id, nachname, vorname);

            if (affectedRows > 0) {
                printStream.printf("%nKontakt aktualisiert%n");
            }
            else {
                printStream.printf("%nKein Kontakt gefunden%n");
            }

            getKontaktDetails(id);

            transactionManager.commit(transactionStatus);
        }
        catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            final Exception cause = Utils.getCause(ex);
            LOGGER.error(cause.getMessage(), cause);
        }
    }
}
