// Created: 15.02.2017
package de.freese.pim.core.mail;

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
public class InternetAddress {
    public static String toString(final InternetAddress[] addresses) {
        return Stream.of(addresses).map(InternetAddress::toString).collect(Collectors.joining(", "));
    }

    private final String address;

    private final String personal;

    public InternetAddress(@JsonProperty("address") final String address) {
        this(address, null);
    }

    @JsonCreator
    public InternetAddress(@JsonProperty("address") final String address, @JsonProperty("personal") final String personal) {
        super();

        this.address = address;
        this.personal = personal;
    }

    public String getAddress() {
        return address;
    }

    public String getPersonal() {
        return personal;
    }

    // public void setAddress(final String address) {
    // this.address = address;
    // }

    // public void setPersonal(final String personal) {
    // this.personal = personal;
    // }

    @Override
    public String toString() {
        if (personal == null || personal.isEmpty()) {
            return getAddress();
        }

        return getPersonal() + " <" + getAddress() + ">";
    }
}
