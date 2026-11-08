package pe.edu.upeu.PharmaBackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger LOG =
            LoggerFactory.getLogger(CacheConfig.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {

            @Override
            public void handleCacheGetError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                LOG.warn(
                        "Error al leer cache {} | tipo={}",
                        cache.getName(),
                        exception.getClass().getSimpleName()
                );
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception,
                    Cache cache,
                    Object key,
                    Object value) {

                LOG.warn(
                        "Error al guardar en cache {} | tipo={}",
                        cache.getName(),
                        exception.getClass().getSimpleName()
                );
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                LOG.warn(
                        "Error al invalidar entrada de cache {} | tipo={}",
                        cache.getName(),
                        exception.getClass().getSimpleName()
                );
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception,
                    Cache cache) {

                LOG.warn(
                        "Error al limpiar cache {} | tipo={}",
                        cache.getName(),
                        exception.getClass().getSimpleName()
                );
            }
        };
    }
}