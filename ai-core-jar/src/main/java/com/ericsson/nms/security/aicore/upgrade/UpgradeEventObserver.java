package com.ericsson.nms.security.aicore.upgrade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.upgrade.UpgradeEvent;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradePhase;

/**
 * Upgrade event observer.
 * 
 */
@ApplicationScoped
public class UpgradeEventObserver {

    @Inject
    private Logger logger;

    /**
     * Listens for upgrade events.
     * 
     * @param event
     *            upgrade event
     */
    public void upgradeNotificationObserver(@Observes final UpgradeEvent event) {

        final UpgradePhase phase = event.getPhase();
        switch (phase) {
        case SERVICE_INSTANCE_UPGRADE_PREPARE:
            logger.info("AI-Core Upgrade Prepare Stage");
            event.accept("OK");
            break;

        case SERVICE_CLUSTER_UPGRADE_PREPARE:
        case SERVICE_CLUSTER_UPGRADE_FAILED:
        case SERVICE_CLUSTER_UPGRADE_FINISHED_SUCCESSFULLY:
        case SERVICE_INSTANCE_UPGRADE_FAILED:
        case SERVICE_INSTANCE_UPGRADE_FINISHED_SUCCESSFULLY:
            logger.info("AI-Core Upgrade Finished Successfully");
            event.accept("OK");
            break;

        default:
            logger.info("AI-Core has rejected event", phase);
            event.reject("Unexpected UpgradePhase");
            break;

        }

    }
}
