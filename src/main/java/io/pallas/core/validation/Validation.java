package io.pallas.core.validation;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Validation {

    /**
     * @return default validator
     */
    public javax.validation.Validator validator() {
        return javax.validation.Validation.buildDefaultValidatorFactory().getValidator();
    }

}
