package org.apache.geode.demo.fastfootshoes.application.listeners;

import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;

public class CQListener {
    private static final Logger logger = LogService.getLogger(CQListener.class);

    public void handleEvent(CqEvent e) {
        logger.info("Received event: " + e);
    }
}
