package net.talqum.crossclouds.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.04.19.
 * Time: 10:29
 */
public class ClientException extends Exception {
    protected ClientErrorCodes errorCode;

    public ClientException(ClientErrorCodes errorCode){
        this.errorCode = errorCode;
    }

    public ClientException(Exception e, ClientErrorCodes errorCode){
        super(e);
        this.errorCode = errorCode;
    }

    public ClientErrorCodes getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ClientErrorCodes errorCode) {
        this.errorCode = errorCode;
    }
}
