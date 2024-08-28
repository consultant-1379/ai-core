/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.aicore.api.exception;

import javax.ejb.ApplicationException;

/**
 * Indicates an error occurred during storing or deletion of auto integration
 * data.
 * 
 */
@ApplicationException
public class AICoreServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AICoreServiceException(final String message) {
        super(message);
    }
}
