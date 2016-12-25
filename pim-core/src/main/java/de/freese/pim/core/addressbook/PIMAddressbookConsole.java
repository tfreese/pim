/**
 * Created on 24.05.2016
 */
package de.freese.pim.core.addressbook;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

import de.freese.pim.core.addressbook.dao.IAddressBookDAO;
import de.freese.pim.core.addressbook.dao.TxProxyAddressBookDAO;
import de.freese.pim.core.addressbook.model.Kontakt;
import de.freese.pim.core.addressbook.model.KontaktAttribut;
import de.freese.pim.core.db.HsqldbLocalFile;
import de.freese.pim.core.db.IDataSourceBean;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.core.utils.PreserveOrderOptionGroup;
import de.freese.pim.core.utils.Utils;

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
    public static PrintStream PRINTSTREAM = System.out;

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

        ISettingsService settingsService = new SettingService();

        Path home = settingsService.getHome();

        if (!Files.exists(home))
        {
            Files.createDirectories(home);
        }

        try (IDataSourceBean dataSourceBean = new HsqldbLocalFile())
        {
            dataSourceBean.configure(settingsService);
            dataSourceBean.testConnection();
            dataSourceBean.populateIfEmpty(null);

            PIMAddressbookConsole addressbook = new PIMAddressbookConsole();
            addressbook.setAddressBookDAO(new TxProxyAddressBookDAO(dataSourceBean.getDataSource()));
            addressbook.setPrintStream(PRINTSTREAM);

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

        // dataSource.destroy();
    }

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
    private IAddressBookDAO addressBookDAO = null;

    /**
     *
     */
    private PrintStream printStream = System.out;

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
        try
        {
            boolean success = this.addressBookDAO.deleteAttribut(kontakt_id, attribut);

            if (success)
            {
                this.printStream.printf("%nAttribut gelöscht%n");
            }
            else
            {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontakt_id);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param id long
     */
    private void deleteKontakt(final long id)
    {
        try
        {
            boolean success = this.addressBookDAO.deleteKontakt(id);

            if (success)
            {
                this.printStream.printf("%nKontakt gelöscht%n");
            }
            else
            {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }
        }
        catch (Exception ex)
        {
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

            kontakte.forEach(k ->
            {
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
        try
        {
            // boolean success =
            this.addressBookDAO.insertAttribut(kontakt_id, attribut, wert);

            getKontaktDetails(kontakt_id);
        }
        catch (SQLIntegrityConstraintViolationException ex)
        {
            this.printStream.printf("%nKontaktattribut existiert bereits%n");
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }

    /**
     * @param nachname String
     * @param vorname String
     */
    private void insertKontakt(final String nachname, final String vorname)
    {
        long id = 0;

        try
        {
            id = this.addressBookDAO.insertKontakt(nachname, vorname);

            getKontaktDetails(id);
        }
        catch (SQLIntegrityConstraintViolationException ex)
        {
            this.printStream.printf("%nKontakt existiert bereits%n");
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
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

        kontakte.forEach(kontakt ->
        {
            rows.add(new String[]
            {
                    Long.toString(kontakt.getID()), kontakt.getNachname(), kontakt.getVorname(), StringUtils.EMPTY, StringUtils.EMPTY
            });

            List<KontaktAttribut> kontaktAttribute = kontakt.getAttribute();

            if (kontaktAttribute.size() > 0)
            {
                rows.get(rows.size() - 1)[3] = kontaktAttribute.get(0).getAttribut();
                rows.get(rows.size() - 1)[4] = kontaktAttribute.get(0).getWert();
            }

            if (kontaktAttribute.size() > 1)
            {
                kontaktAttribute.stream().skip(1).forEach(ka ->
                {
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
     * @param addressBookDAO {@link IAddressBookDAO}
     */
    private void setAddressBookDAO(final IAddressBookDAO addressBookDAO)
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
     * @param kontakt_id long
     * @param attribut String
     * @param wert String
     */
    private void updateAttribut(final long kontakt_id, final String attribut, final String wert)
    {
        try
        {
            boolean success = this.addressBookDAO.updateAttribut(kontakt_id, attribut, wert);

            if (success)
            {
                this.printStream.printf("%nAttribut aktualisiert%n");
            }
            else
            {
                this.printStream.printf("%nKein Attribut gefunden%n");
            }

            getKontaktDetails(kontakt_id);
        }
        catch (Exception ex)
        {
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
        try
        {
            boolean success = this.addressBookDAO.updateKontakt(id, nachname, vorname);

            if (success)
            {
                this.printStream.printf("%nKontakt aktualisiert%n");
            }
            else
            {
                this.printStream.printf("%nKein Kontakt gefunden%n");
            }

            getKontaktDetails(id);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, Utils.getCause(ex));
        }
    }
}
