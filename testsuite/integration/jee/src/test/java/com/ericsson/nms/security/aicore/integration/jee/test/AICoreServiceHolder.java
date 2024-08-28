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
package com.ericsson.nms.security.aicore.integration.jee.test;

import javax.ejb.Stateless;

import com.ericsson.nms.security.aicore.api.AICoreService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

@Stateless
public class AICoreServiceHolder {

    @EServiceRef
    AICoreService eserviceRef;

    public AICoreService getAIEService() {
        return eserviceRef;
    }
}
