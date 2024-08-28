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

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;
import com.ericsson.nms.security.aicore.configuration.ConfigurationBean;

@RunWith(MockitoJUnitRunner.class)
public class AICoreServiceBeanTest extends TestCase {

    private static final String SERIAL_NUMBER = "AQSN1234";
    private static final String INTEGRATION_DATA = "...DATA...";

    private static final String FILESTORE_LOCATION = System.getProperty("java.io.tmpdir") + "/aicore/filestore/";
    private static final String FILE_LOCATION = FILESTORE_LOCATION + SERIAL_NUMBER;

    @Mock
    private ConfigurationBean configurationBean;

    @Mock
    private Logger logger; //NOPMD

    @InjectMocks
    private AICoreServiceBean aicoreService;

    @Before
    public void setup() {
        new File(FILESTORE_LOCATION).mkdirs();

        when(configurationBean.getFilestoreLocation()).thenReturn(FILESTORE_LOCATION);
    }

    @Override
    @After
    public void tearDown() {
        new File(FILE_LOCATION).delete();
        new File(FILESTORE_LOCATION).delete();
    }

    @Test
    public void when_storeIntegrationData_for_new_serialnumber_then_file_is_written_to_the_filesystem_with_name_matching_the_serial_number() {
        aicoreService.storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        final File expectedFile = new File(FILE_LOCATION);
        assertTrue(expectedFile.exists());
    }

    @Test
    public void when_storeIntegrationData_for_existing_serialnumber_then_existing_file_is_overwritten_on_the_filesystem() throws IOException {
        final String LATEST_INTEGRATION_DATA = "...LATEST_DATA...";

        aicoreService.storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        aicoreService.storeIntegrationData(SERIAL_NUMBER, LATEST_INTEGRATION_DATA.getBytes());

        final String createdFileContent = new String(Files.readAllBytes(Paths.get(FILE_LOCATION)));

        assertEquals(LATEST_INTEGRATION_DATA, createdFileContent);
    }

    @Test(expected = AICoreServiceException.class)
    public void when_storeIntegrationData_for_file_exceeding_256kb_then_AICoreException_is_thrown() {
        aicoreService.storeIntegrationData(SERIAL_NUMBER, new byte[257 * 1024]);
    }

    @Test(expected = AICoreServiceException.class)
    public void when_storeIntegrationData_encounters_error_writing_the_file_then_AICoreException_is_thrown() {
        new File(FILESTORE_LOCATION).delete(); //removes directory so file cannot be created
        aicoreService.storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
    }

    @Test
    public void when_deleteIntegrationData_for_existing_serialnumber_then_file_is_deleted_on_the_filesystem() {
        aicoreService.storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        aicoreService.deleteIntegrationData(SERIAL_NUMBER);

        final File expectedFile = new File(FILE_LOCATION);
        assertFalse(expectedFile.exists());
    }

    @Test
    public void when_deleteIntegrationData_for_nonexisting_serialnumber_then_no_exception_is_thrown() {
        try {
            aicoreService.deleteIntegrationData(SERIAL_NUMBER);
        } catch (final Exception e) {
            fail("Should not throw exception when no file exists for serial number");
        }

    }

}