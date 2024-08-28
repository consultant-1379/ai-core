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
package com.ericsson.nms.security.aicore.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.aicore.api.AICoreService;
import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;
import com.ericsson.nms.security.aicore.configuration.ConfigurationBean;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
// TODO: Check with the BSIM team most probably need to change this to
// REQUIRED
public class AICoreServiceBean implements AICoreService {

    @Inject
    private Logger log;

    @Inject
    private ConfigurationBean configurationBean;

    @Override
    public void storeIntegrationData(final String serialNumber, final byte[] integrationData) throws AICoreServiceException {

        if (integrationData.length > MAX_ALLOWED_DATA) {
            throw new AICoreServiceException("Max data size of 256kb exceeded");
        }

        final String directoryPath = configurationBean.getFilestoreLocation();
        final String filePath = directoryPath + serialNumber;

        log.info("Creating file " + filePath);

        try {
            final Resource fileResource = Resources.getFileSystemResource(filePath);
            fileResource.write(integrationData, false);
        } catch (final Exception e) {
            log.error("Error writing file " + filePath, e);
            throw new AICoreServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteIntegrationData(final String serialNumber) throws AICoreServiceException {

        final String directoryPath = configurationBean.getFilestoreLocation();
        final String filePath = directoryPath + serialNumber;

        log.info("Deleting file " + filePath);

        try {
            final Resource fileResource = Resources.getFileSystemResource(filePath);
            fileResource.delete();
        } catch (final Exception e) {
            log.error("Error deleting file " + filePath, e);
            throw new AICoreServiceException(e.getMessage());
        }
    }
}
