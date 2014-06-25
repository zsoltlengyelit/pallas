package io.pallas.core.cdi;

public class DeploymentException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4762332875334326551L;

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);

    }

    public DeploymentException(String message) {
        super(message);

    }

}
