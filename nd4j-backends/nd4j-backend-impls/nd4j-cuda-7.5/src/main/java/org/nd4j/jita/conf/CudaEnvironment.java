package org.nd4j.jita.conf;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author raver119@gmail.com
 */
public class CudaEnvironment {
    private static final CudaEnvironment INSTANCE = new CudaEnvironment();
    private static volatile Configuration configuration;
    private static Logger logger = LoggerFactory.getLogger(CudaEnvironment.class);

    private CudaEnvironment() {
        configuration = new Configuration();
        configuration.enableDebug(configuration.isDebug());
        configuration.setVerbose(configuration.isVerbose());
    }

    public static CudaEnvironment getInstance() {
        return INSTANCE;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
