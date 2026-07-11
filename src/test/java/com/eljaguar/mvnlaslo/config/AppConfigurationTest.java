package com.eljaguar.mvnlaslo.config;

import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class AppConfigurationTest {

    @Test
    void testDefaults() {
        AppConfiguration config = AppConfiguration.defaults();
        
        assertEquals(5, config.getThreadPoolSize());
        assertEquals(37.0, config.getLowTemperature(), 0.001);
        assertEquals(70.0, config.getHighTemperature(), 0.001);
        assertEquals(5, config.getMinStemLength());
        assertEquals(20, config.getMaxLoopLength());
        assertEquals(0.8, config.getMinComplementarity(), 0.001);
        assertFalse(config.hasProxy());
        assertFalse(config.hasNcbiApiKey());
    }

    @Test
    void testBuilder() {
        AppConfiguration config = AppConfiguration.builder()
                .threadPoolSize(10)
                .lowTemperature(40.0)
                .highTemperature(75.0)
                .minStemLength(6)
                .maxLoopLength(25)
                .minComplementarity(0.9)
                .proxy("proxy.example.com", 8080)
                .ncbiApiKey("test-key")
                .build();
        
        assertEquals(10, config.getThreadPoolSize());
        assertEquals(40.0, config.getLowTemperature(), 0.001);
        assertEquals(75.0, config.getHighTemperature(), 0.001);
        assertEquals(6, config.getMinStemLength());
        assertEquals(25, config.getMaxLoopLength());
        assertEquals(0.9, config.getMinComplementarity(), 0.001);
        assertTrue(config.hasProxy());
        assertEquals("proxy.example.com", config.getProxyHost());
        assertEquals(8080, config.getProxyPort());
        assertTrue(config.hasNcbiApiKey());
        assertEquals("test-key", config.getNcbiApiKey());
    }

    @Test
    void testBuilderInvalidThreadPoolSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppConfiguration.builder().threadPoolSize(0).build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            AppConfiguration.builder().threadPoolSize(-1).build();
        });
    }

    @Test
    void testBuilderInvalidComplementarity() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppConfiguration.builder().minComplementarity(-0.1).build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            AppConfiguration.builder().minComplementarity(1.1).build();
        });
    }

    @Test
    void testToString() {
        AppConfiguration config = AppConfiguration.defaults();
        String str = config.toString();
        
        assertTrue(str.contains("AppConfiguration"));
        assertTrue(str.contains("threadPoolSize=5"));
        assertTrue(str.contains("lowTemperature=37.0"));
    }
}
