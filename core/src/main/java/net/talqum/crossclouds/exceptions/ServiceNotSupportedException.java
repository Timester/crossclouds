package net.talqum.crossclouds.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Imre
 * Date: 2015.05.18.
 * Time: 14:52
 */
public class ServiceNotSupportedException extends RuntimeException {
    public ServiceNotSupportedException(Throwable cause) {
        super(cause);
    }

    public ServiceNotSupportedException(String message) {
        super(message);
    }
}
