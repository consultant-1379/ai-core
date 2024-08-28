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
package com.ericsson.nms.security.aicore.integration.jee.archive;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import com.ericsson.nms.security.aicore.integration.jee.test.AICoreServiceTest;

public class DeploymentArchives {

    private static final File MANIFEST_MF_FILE = new File("src/test/resources/META-INF/MANIFEST.MF");

    private static final File BEANS_XML_FILE = new File("src/test/resources/META-INF/beans.xml");

    /**
     * Create deployment from given maven coordinates
     * 
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static EnterpriseArchive createEarArchiveFromMavenCoordinates(final String mavenCoordinates) {
        final File archiveFile = resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, archiveFile);

        return ear;
    }

    /**
     * Create deployment from given maven coordinates
     * 
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static JavaArchive createJarArchiveFromMavenCoordinates(final String mavenCoordinates) {
        final File archiveFile = resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final JavaArchive jar = ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);

        return jar;
    }

    public static Archive<?> createEarTestArchive() {
        final MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "service-bean-test-lib.jar")
                .addAsResource("ServiceFrameworkConfiguration.properties", "ServiceFrameworkConfiguration.properties")
                .addAsResource("META-INF/beans.xml", "META-INF/beans.xml").addAsResource("META-INF/MANIFEST.MF", "META-INF/MANIFEST.MF")
                .addPackage(AICoreServiceTest.class.getPackage());

        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, AICoreServiceTest.class.getSimpleName() + ".ear")
                .addAsApplicationResource(BEANS_XML_FILE).setManifest(MANIFEST_MF_FILE).addAsModule(testJar)
                .addAsLibraries(resolver.artifact(Dependencies.SDK_DIST).resolveAsFiles())
                .addAsLibraries(resolver.artifact(Dependencies.REST_EASY).resolveAsFiles())
                .addAsLibraries(resolver.artifact(Dependencies.AI_CORE_API).resolveAsFiles());

        return ear;
    }

    private static File resolveArtifactWithoutDependencies(final String artifactCoordinates) {
        final File[] artifacts = getMavenResolver().artifact(artifactCoordinates).exclusion("*").resolveAsFiles();
        if (artifacts == null) {
            throw new IllegalStateException("Artifact with coordinates " + artifactCoordinates + " was not resolved");
        }
        if (artifacts.length != 1) {
            throw new IllegalStateException("Resolved more then one artifact with coordinates " + artifactCoordinates);
        }
        return artifacts[0];
    }

    private static MavenDependencyResolver getMavenResolver() {
        return DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
    }

}
