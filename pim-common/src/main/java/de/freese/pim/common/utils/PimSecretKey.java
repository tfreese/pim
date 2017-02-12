/**
 * 31.10.2006
 */
package de.freese.pim.common.utils;

import javax.crypto.spec.SecretKeySpec;

/**
 * DefaultKey für Verschlüsselungen.
 *
 * @author Thomas Freese
 */
class PimSecretKey extends SecretKeySpec
{
    /**
     * Konstante für Ver- und Entschlüsselung.
     */
    private static final byte[] KEY = new byte[]
    {
            0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
    };

    /**
     *
     */
    private static final long serialVersionUID = -7021783843804998439L;

    /**
     * Erstellt ein neues {@link PimSecretKey} Object.
     */
    PimSecretKey()
    {
        super(KEY, "AES");
    }
}
