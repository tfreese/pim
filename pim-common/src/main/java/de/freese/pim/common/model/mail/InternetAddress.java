// Created: 15.02.2017
package de.freese.pim.common.model.mail;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enthält die Addressen einer Mail von: FROM, TO, CC, BCC.<br>
 * Siehe javax.mail.internet.InternetAddress
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternetAddress
{
    /**
     * @param addresses {@link InternetAddress}[]
     *
     * @return String
     */
    public static String toString(final InternetAddress[] addresses)
    {
        String s = Stream.of(addresses).map(InternetAddress::toString).collect(Collectors.joining(", "));

        return s;
    }

    /**
     *
     */
    private final String address;
    /**
     *
     */
    private final String personal;

    /**
     * Erzeugt eine neue Instanz von {@link InternetAddress}
     *
     * @param address String
     */
    public InternetAddress(@JsonProperty("address") final String address)
    {
        this(address, null);
    }

    /**
     * Erzeugt eine neue Instanz von {@link InternetAddress}
     *
     * @param address String
     * @param personal String
     */
    @JsonCreator
    public InternetAddress(@JsonProperty("address") final String address, @JsonProperty("personal") final String personal)
    {
        super();

        this.address = address;
        this.personal = personal;
    }

    /**
     * @return String
     */
    public String getAddress()
    {
        return this.address;
    }

    /**
     * @return String
     */
    public String getPersonal()
    {
        return this.personal;
    }

    // /**
    // * @param address String
    // */
    // public void setAddress(final String address)
    // {
    // this.address = address;
    // }

    // /**
    // * @param personal String
    // */
    // public void setPersonal(final String personal)
    // {
    // this.personal = personal;
    // }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if ((this.personal == null) || (this.personal.length() == 0))
        {
            return getAddress();
        }

        return getPersonal() + " <" + getAddress() + ">";
    }
}
