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
package com.ericsson.nms.security.aicore.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.modeling.annotation.constraints.NotNull;
import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;

/**
 * Provides centralized access to ai-core configuration properties.
 */
@ApplicationScoped
public class ConfigurationBean {

    private static final String FILESTORE_LOCATION_PROPERTY = "autointegration_filelocation";

    @Inject
    private Logger logger;

    @Inject
    @NotNull
    @Configured(propertyName = FILESTORE_LOCATION_PROPERTY)
    private String filestoreLocation;

    void listenForChanges(@Observes @ConfigurationChangeNotification(propertyName = FILESTORE_LOCATION_PROPERTY) final String value) {
        this.logger.info("Received notification that value changed to {}", value);
        this.filestoreLocation = value;
    }

    /**
     * @return the absolute path to the directory where integration files are
     *         stored
     */
    public String getFilestoreLocation() {
        return this.filestoreLocation;
    }
}