package com.ericsson.nms.security.aicore.integration.jee.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;
import com.ericsson.nms.security.aicore.integration.jee.archive.Dependencies;
import com.ericsson.nms.security.aicore.integration.jee.archive.DeploymentArchives;

@RunWith(Arquillian.class)
public class AICoreServiceTest {

    private static final String SERIAL_NUMBER = "AQSN1234";
    private static final String INTEGRATION_DATA = "...DATA...";

    private static final String FILESTORE_LOCATION = "/tmp/aicore/filestore/";
    private static final String FILE_LOCATION = FILESTORE_LOCATION + SERIAL_NUMBER;

    @Inject
    private AICoreServiceHolder serviceHolder;

    @Deployment(name = "pib-ear", testable = false)
    public static Archive<?> createPibArchive() {
        return DeploymentArchives.createEarArchiveFromMavenCoordinates(Dependencies.PIB_EAR);
    }

    @Deployment(name = "ai-core-ear", testable = false)
    public static Archive<?> createAiCoreArchive() {
        return DeploymentArchives.createEarArchiveFromMavenCoordinates(Dependencies.AI_CORE_EAR);
    }

    @Deployment(name = "ai-core-test-ear")
    public static Archive<?> createTestArchive() {
        return DeploymentArchives.createEarTestArchive();
    }

    @Before
    public void setup() throws ClientProtocolException, IOException {
        changeConfiguredPropertyValue("autointegration_filelocation", FILESTORE_LOCATION);
        new File(FILESTORE_LOCATION).mkdirs();
    }

    @After
    public void tearDown() {
        new File(FILE_LOCATION).delete();
        new File(FILESTORE_LOCATION).delete();
    }

    @Test
    @OperateOnDeployment("ai-core-test-ear")
    public void when_storeIntegrationData_for_new_serialnumber_then_file_is_written_to_the_filesystem_with_name_matching_the_serial_number() {
        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        final File expectedFile = new File(FILE_LOCATION);
        assertTrue(expectedFile.exists());
    }

    @Test
    @OperateOnDeployment("ai-core-test-ear")
    public void when_storeIntegrationData_for_existing_serialnumber_then_existing_file_is_overwritten_on_the_filesystem() throws IOException {
        final String LATEST_INTEGRATION_DATA = "...LATEST_DATA...";

        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, LATEST_INTEGRATION_DATA.getBytes());

        final String createdFileContent = new String(Files.readAllBytes(Paths.get(FILE_LOCATION)));

        assertEquals(LATEST_INTEGRATION_DATA, createdFileContent);
    }

    @Test(expected = AICoreServiceException.class)
    @OperateOnDeployment("ai-core-test-ear")
    public void when_storeIntegrationData_for_file_exceeding_256kb_then_AICoreException_is_thrown() {
        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, new byte[257 * 1024]);
    }

    @Test(expected = AICoreServiceException.class)
    @OperateOnDeployment("ai-core-test-ear")
    public void when_storeIntegrationData_encounters_error_writing_the_file_then_AICoreException_is_thrown() {
        new File(FILESTORE_LOCATION).delete(); //removes directory so file cannot be created
        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
    }

    @Test
    @OperateOnDeployment("ai-core-test-ear")
    public void when_deleteIntegrationData_for_existing_serialnumber_then_file_is_deleted_on_the_filesystem() {
        serviceHolder.getAIEService().storeIntegrationData(SERIAL_NUMBER, INTEGRATION_DATA.getBytes());
        serviceHolder.getAIEService().deleteIntegrationData(SERIAL_NUMBER);

        final File expectedFile = new File(FILE_LOCATION);
        assertFalse(expectedFile.exists());
    }

    @Test
    @OperateOnDeployment("ai-core-test-ear")
    public void when_deleteIntegrationData_for_nonexisting_serialnumber_then_no_exception_is_thrown() {
        try {
            serviceHolder.getAIEService().deleteIntegrationData(SERIAL_NUMBER);
        } catch (final Exception e) {
            fail("Should not throw exception when no file exists for serial number");
        }
    }

    private void changeConfiguredPropertyValue(final String paramName, final String paramValue) throws ClientProtocolException, IOException {
        final String updateUrlPrefix = "http://localhost:8080/pib/configurationService/updateConfigParameterValue?paramName=";
        final String updateUrl = updateUrlPrefix + paramName + "&paramValue=" + paramValue;

        final HttpGet httpget = new HttpGet(new URL(updateUrl).toExternalForm());
        final DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.execute(httpget);
    }

}
