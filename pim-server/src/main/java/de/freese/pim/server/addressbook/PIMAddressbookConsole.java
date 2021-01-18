/**
 * Created on 24.05.2016
 */
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import de.freese.pim.common.utils.PreserveOrderOptionGroup;
import de.freese.pim.common.utils.Utils;
import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.addressbook.model.KontaktAttribut;

/**
 * Console-Client für das Addressbuch.<br>
 * Als Default wird die lokale HSQLDB (Utils#HSQLDB_URL) verwendet.
 *
 * @author Thomas Freese
 */
public class PIMAddressbookConsole
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(PIMAddressbookConsole.class);

    /**
     *
     */
    public static PrintStream PRINT_STREAM = System.out;

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     *
     * @return {@link Options}
     */
    private static Options getCommandOptions()
    {
        OptionGroup group = new PreserveOrderOptionGroup();

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

        Options options = new Options();
        options.addOptionGroup(group);

        return options;
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            usage();
        }

        CommandLine line = null;

        try
        {
            CommandLineParser parser = new DefaultParser();
            line = parser.parse(getCommandOptions(), args);
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage());

            usage();
        }

        // SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        Path home = Paths.get(System.getProperty("user.home"), ".pim");

        if (!Files.exists(home))
        {
            Files.createDirectories(home);
        }

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:file:" + System.getProperty("user.home") + "/.pim/pimdb;shutdown=true");
        dataSource.setAutoCommit(true);
        dataSource.setSuppressClose(true);

        try
        {
            PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

            DefaultAddressBookDAO addressBookDAO = new DefaultAddressBookDAO();
            addressBookDAO.setDataSource(dataSource);

            PIMAddressbookConsole addressbook = new PIMAddressbookConsole();
            addressbook.setAddressBookDAO(addressBookDAO);
            addressbook.setTransactionManager(transactionManager);
            addressbook.setPrintStream(PRINT_STREAM);

            if (line.hasOption("ik"))
            {
                args = line.getOptionValues("insert-kontakt");
                addressbook.insertKontakt(args[0], args[1]);
            }
            else if (line.hasOption("uk"))
            {
                args = line.getOptionValues("update-kontakt");
                long id = Long.parseLong(args[0]);
                addressbook.updateKontakt(id, args[1], args[2]);
            }
            else if (line.hasOption("dk"))
            {
                long id = Long.parseLong(line.getOptionValue("delete-kontakt"));
                addressbook.deleteKontakt(id);
            }
            else if (line.hasOption("ia"))
            {
                args = line.getOptionValues("insert-attribut");
                long id = Long.parseLong(args[0]);
                addressbook.insertAttribut(id, args[1], args[2]);
            }
            else if (line.hasOption("ua"))
            {
                args = line.getOptionValues("update-attribut");
                long id = Long.parseLong(args[0]);
                addressbook.updateAttribut(id, args[1], args[2]);
                // long id = Long.parseLong(args[1]);
                // addressbook.updateAttribut(id, args[2], StringUtils.join(args, ' ', 3, args.length));
            }
            else if (line.hasOption("da"))
            {
                args = line.getOptionValues("delete-attribut");
                long id = Long.parseLong(args[0]);
                addressbook.deleteAttribut(id, args[1]);
            }
            else if (line.hasOption("lk"))
            {
                addressbook.getKontakte();
            }
            else if (line.hasOption("vk"))
            {
                long id = Long.parseLong(line.getOptionValue("view-kontakt"));
                addressbook.getKontaktDetails(id);
            }
            else if (line.hasOption("s"))
            {
                args = line.getOptionValues("search");
                addressbook.search(args[0]);
            }
        }
        finally
        {
            dataSource.destroy();
        }
    }

    /**
     *
     */
    private static void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        // formatter.setWidth(120);
        // formatter.printHelp("Addressbook\n", getCommandOptions(), true);

        StringBuilder footer = new StringBuilder();
        footer.append("\nNamen / Werte mit Leerzeichen sind mit \"'... ...'\" anzugeben.");
        footer.append("\n@Thomas Freese");

        formatter.printHelp(120, "Addressbook\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

        System.exit(-1);
    }

    /**
     *
     */
    private AddressBookDAO addressBookDAO;

    /**
     *
     */
    private PrintStream printStream = System.out;

    /**
     *
     */
    private PlatformTransactionManager transactionManager;

    /**
     *
     */
    private PIMAddressbookConsole()
    {
        super();
    }

    /**
     * @param kontakt_id long
     * @param attribut String
     */
    private void deleteAttribut(final long kontakt_id, final String attribut)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try
        {
            int affectedRows = this.addressBookDAO.deleteAttribut(kontakt_id, attribut);

            if (affectedRows > 0)
            {
                this.printStream.printf("%nAttribut gelöscht%n");
            }
            else
            {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontakt_id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param id long
     */
    private void deleteKontakt(final long id)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try
        {
            int affectedRows = this.addressBookDAO.deleteKontakt(id);

            if (affectedRows > 0)
            {
                this.printStream.printf("%nKontakt gelöscht%n");
            }
            else
            {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param id long
     */
    private void getKontaktDetails(final long id)
    {
        try
        {
            List<Kontakt> kontakts = this.addressBookDAO.getKontaktDetails(id);

            printDetails(kontakts);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     */
    private void getKontakte()
    {
        try
        {
            List<Kontakt> kontakte = this.addressBookDAO.getKontakte();

            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]
            {
                    "ID", "VORNAME", "NACHNAME"
            });

            kontakte.forEach(k -> {
                String[] row = new String[3];
                rows.add(row);

                row[0] = Long.toString(k.getID());
                row[1] = k.getNachname();
                row[2] = k.getVorname();
            });

            print(rows, this.printStream);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param kontakt_id long
     * @param attribut String
     * @param wert String
     */
    private void insertAttribut(final long kontakt_id, final String attribut, final String wert)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try
        {
            // boolean success =
            this.addressBookDAO.insertAttribut(kontakt_id, attribut, wert);

            getKontaktDetails(kontakt_id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException)
            {
                this.printStream.printf("%nKontaktattribut existiert bereits%n");
            }
            else
            {
                LOGGER.error(null, cause);
            }
        }
    }

    /**
     * @param nachname String
     * @param vorname String
     */
    private void insertKontakt(final String nachname, final String vorname)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);
        long id = 0;

        try
        {
            id = this.addressBookDAO.insertKontakt(nachname, vorname);

            getKontaktDetails(id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            Exception cause = Utils.getCause(ex);

            if (cause instanceof SQLIntegrityConstraintViolationException)
            {
                this.printStream.printf("%nKontaktattribut existiert bereits%n");
            }
            else
            {
                LOGGER.error(null, cause);
            }
        }
    }

    /**
     * @param rows {@link List}
     * @param printStream {@link PrintStream}
     */
    private void print(final List<String[]> rows, final PrintStream printStream)
    {
        Utils.padding(rows, " ");
        Utils.addHeaderSeparator(rows, "-");
        Utils.write(rows, printStream, " | ");
    }

    /**
     * @param kontakte {@link List}
     */
    private void printDetails(final List<Kontakt> kontakte)
    {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]
        {
                "ID", "VORNAME", "NACHNAME", "ATTRIBUT", "WERT"
        });

        kontakte.forEach(kontakt -> {
            rows.add(new String[]
            {
                    Long.toString(kontakt.getID()), kontakt.getNachname(), kontakt.getVorname(), StringUtils.EMPTY, StringUtils.EMPTY
            });

            List<KontaktAttribut> kontaktAttribute = kontakt.getAttribute();

            if (!kontaktAttribute.isEmpty())
            {
                rows.get(rows.size() - 1)[3] = kontaktAttribute.get(0).getAttribut();
                rows.get(rows.size() - 1)[4] = kontaktAttribute.get(0).getWert();
            }

            if (kontaktAttribute.size() > 1)
            {
                kontaktAttribute.stream().skip(1).forEach(ka -> {
                    String[] row = new String[5];
                    rows.add(row);

                    row[0] = StringUtils.EMPTY;
                    row[1] = StringUtils.EMPTY;
                    row[2] = StringUtils.EMPTY;
                    row[3] = ka.getAttribut();
                    row[4] = ka.getWert();
                });
            }
        });

        print(rows, this.printStream);
    }

    /**
     * @param name String
     */
    private void search(final String name)
    {
        try
        {
            List<Kontakt> kontakte = this.addressBookDAO.searchKontakte(name);

            printDetails(kontakte);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     */
    private void setAddressBookDAO(final AddressBookDAO addressBookDAO)
    {
        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @param printStream {@link PrintStream}
     */
    private void setPrintStream(final PrintStream printStream)
    {
        this.printStream = printStream;
    }

    /**
     * @param transactionManager {@link PlatformTransactionManager}
     */
    private void setTransactionManager(final PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    /**
     * @param kontakt_id long
     * @param attribut String
     * @param wert String
     */
    private void updateAttribut(final long kontakt_id, final String attribut, final String wert)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try
        {
            int affectedRows = this.addressBookDAO.updateAttribut(kontakt_id, attribut, wert);

            if (affectedRows > 0)
            {
                this.printStream.printf("%nAttribut aktualisiert%n");
            }
            else
            {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontakt_id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param id long
     * @param nachname String
     * @param vorname String
     */
    private void updateKontakt(final long id, final String nachname, final String vorname)
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

        try
        {
            int affectedRows = this.addressBookDAO.updateKontakt(id, nachname, vorname);

            if (affectedRows > 0)
            {
                this.printStream.printf("%nKontakt aktualisiert%n");
            }
            else
            {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }

            getKontaktDetails(id);

            this.transactionManager.commit(transactionStatus);
        }
        catch (Exception ex)
        {
            this.transactionManager.rollback(transactionStatus);
            LOGGER.error(null, Utils.getCause(ex));
        }
    }
}
