package de.freese.pim.core.utils;

import java.io.Serial;

import javax.crypto.spec.SecretKeySpec;

/**
 * DefaultKey für Verschlüsselungen.
 *
 * @author Thomas Freese
 * @since 31.10.2006
 */
class PimSecretKey extends SecretKeySpec {
    /**
     * Konstante für Ver- und Entschlüsselung.
     */
    private static final byte[] KEY = {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};

    @Serial
    private static final long serialVersionUID = -7021783843804998439L;

    PimSecretKey() {
        super(KEY, "AES");
    }
}
