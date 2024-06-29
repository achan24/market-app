package ie.revalue.authenticatedbackend.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtility {

    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        //we need a model to store the key inside

        return keyPair;
    }
}
