package org.apache.geode.demo.fastfootshoes.clusterside.listeners;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.demo.fastfootshoes.clusterside.util.ReferenceHelper;
import org.apache.geode.demo.fastfootshoes.model.Alert;
import org.apache.geode.demo.fastfootshoes.model.Transaction;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertCacheListener extends CacheListenerAdapter<String, Object> implements Declarable {
    private static final Logger logger = LogService.getLogger();

    @Override
    public void init(Properties props) {
    }

    @Override
    public void afterCreate(EntryEvent<String, Object> entryEvent) {
        Alert alert = null;
        if (entryEvent.getNewValue() instanceof PdxInstanceImpl) {
            alert = ReferenceHelper.toObject(entryEvent.getNewValue(), Alert.class);
        } else {
            alert = (Alert) entryEvent.getNewValue();
        }
        logger.info(String.format("Got a new alert: %s", alert.toString()));
    }
}
