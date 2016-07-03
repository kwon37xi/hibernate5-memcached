package kr.pe.kwonnam.hibernate4memcached.timestamper;

import kr.pe.kwonnam.hibernate4memcached.Hibernate4MemcachedRegionFactory;
import kr.pe.kwonnam.hibernate4memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate4memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate4memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates increasing identifier for {@link org.hibernate.cache.spi.RegionFactory#nextTimestamp()}.
 * This implementation generates strictly increasing timestamp based on memcached incr feature.
 *
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class HibernateCacheTimestamperMemcachedImpl implements HibernateCacheTimestamper {
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final long INCREASE_BY = 1L;
    public static final int EXPIRY_SECONDS = Hibernate4MemcachedRegionFactory.MEMCACHED_MAX_EPIRY_SECONDS;

    private Logger log = LoggerFactory.getLogger(HibernateCacheTimestamperMemcachedImpl.class);

    private SessionFactoryOptions settings;

    private OverridableReadOnlyProperties properties;

    private MemcachedAdapter memcachedAdapter;

    private CacheNamespace cacheNamespace;

    @Override
    public void setSettings(SessionFactoryOptions settings) {
        this.settings = settings;
    }

    @Override
    public void setProperties(OverridableReadOnlyProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setMemcachedAdapter(MemcachedAdapter memcachedAdapter) {
        this.memcachedAdapter = memcachedAdapter;
    }

    @Override
    public void init() {
        String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
        cacheNamespace = new CacheNamespace(cacheRegionPrefix + HibernateCacheTimestamperMemcachedImpl.class
                .getSimpleName(), false);

        log.debug("hibernate cache timestamper memcached implementation linitialized. CacheNamespace : {}",
                cacheNamespace);
    }

    @Override
    public long next() {
        final long defaultNext = System.currentTimeMillis();
        long next;
        try {
            next = memcachedAdapter.increaseCounter(cacheNamespace, TIMESTAMP_KEY, INCREASE_BY,
                    defaultNext, EXPIRY_SECONDS);
        } catch (Exception ex) {
            log.warn("Failed to increase counter to get next timestamper.", ex);
            next = defaultNext;
        }

        log.debug("hibernate cache timestamper next : {}", next);
        return next;
    }

    public CacheNamespace getCacheNamespace() {
        return cacheNamespace;
    }
}
