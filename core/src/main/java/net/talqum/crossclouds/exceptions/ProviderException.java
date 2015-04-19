package net.talqum.crossclouds.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.04.19.
 * Time: 10:24
 */
public class ProviderException extends ClientException {

    public ProviderException(ClientErrorCodes errorCode) {
        super(errorCode);
    }

    public ProviderException(String message, ClientErrorCodes errorCode) {
        super(message, errorCode);
    }
}
