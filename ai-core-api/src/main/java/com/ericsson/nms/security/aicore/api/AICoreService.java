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
package com.ericsson.nms.security.aicore.api;

import javax.ejb.Remote;

import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * Centralized management of auto integration files to be made available for
 * download via AutoIntegration Web Service.
 */
@EService
@Remote
public interface AICoreService {

    int MAX_ALLOWED_DATA = 256 * 1024; // 256 kBytes 

    /**
     * Stores the integration data for the specified serialNumber. If there is
     * existing data for the specified serialNumber it overwrites it.
     * 
     * @param serinalNumber
     *            the serial number of the node
     * @param integrationFile
     *            the integration data of the node to be stored
     * 
     * @throws AICoreServiceException
     *             if the data cannot be stored or the data is too large
     *             (>256KB)
     */
    void storeIntegrationData(final String serialNumber, final byte[] integrationData) throws AICoreServiceException;

    /**
     * Deletes the integration data if exists for the specified serialNumber.
     * 
     * @param serialNumber
     *            the serial number of the node
     * 
     * @throws AICoreServiceException
     *             if the data cannot be deleted
     */
    void deleteIntegrationData(final String serialNumber) throws AICoreServiceException;
}
