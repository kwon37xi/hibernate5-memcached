package kr.pe.kwonnam.hibernate5memcached.regions;

import kr.pe.kwonnam.hibernate5memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.timestamper.HibernateCacheTimestamper;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kr.pe.kwonnam.hibernate5memcached.Hibernate5MemcachedRegionFactory.REGION_EXPIRY_SECONDS_PROPERTY_KEY_PREFIX;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class GeneralDataMemcachedRegion extends MemcachedRegion implements GeneralDataRegion {
    private Logger log = LoggerFactory.getLogger(GeneralDataMemcachedRegion.class);

    private int expirySeconds;

    public GeneralDataMemcachedRegion(CacheNamespace cacheNamespace, OverridableReadOnlyProperties properties, CacheDataDescription metadata,
                                      SessionFactoryOptions settings, MemcachedAdapter memcachedAdapter, HibernateCacheTimestamper hibernateCacheTimestamper) {
        super(cacheNamespace, properties, metadata, settings, memcachedAdapter, hibernateCacheTimestamper);
        populateExpirySeconds(properties);
    }

    void populateExpirySeconds(OverridableReadOnlyProperties properties) {
        String regionExpirySecondsKey = REGION_EXPIRY_SECONDS_PROPERTY_KEY_PREFIX + "." + getCacheNamespace().getName();
        String expirySecondsProperty = properties.getProperty(regionExpirySecondsKey);
        if (expirySecondsProperty == null) {
            expirySecondsProperty = properties.getProperty(REGION_EXPIRY_SECONDS_PROPERTY_KEY_PREFIX);
        }
        if (expirySecondsProperty == null) {
            throw new IllegalStateException(regionExpirySecondsKey + " or " + REGION_EXPIRY_SECONDS_PROPERTY_KEY_PREFIX
                    + "(for default expiry seconds) required!");
        }

        expirySeconds = Integer.parseInt(expirySecondsProperty);
        log.info("expirySeconds of cache region [{}] - {} seconds.", getCacheNamespace().getName(), expirySeconds);
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key) throws CacheException {
        String refinedKey = refineKey(key);

        log.debug("Cache get [{}] : key[{}]", getCacheNamespace(), refinedKey);

        Object cachedData;
        try {
            cachedData = getMemcachedAdapter().get(getCacheNamespace(), refinedKey);
        } catch (Exception ex) {
            log.warn("Failed to get from memcached.", ex);
            cachedData = null;
        }

        if (cachedData == null) {
            return null;
        }

        if (!(cachedData instanceof CacheItem)) {
            log.debug("get cachedData is not CacheItem.");
            return cachedData;
        }

        CacheItem cacheItem = (CacheItem) cachedData;
        boolean targetClassAndCurrentJvmTargetClassMatch = cacheItem.isTargetClassAndCurrentJvmTargetClassMatch();
        log.debug("cacheItem and targetClassAndCurrentJvmTargetClassMatch : {} / {}", targetClassAndCurrentJvmTargetClassMatch, cacheItem);

        if (cacheItem.isTargetClassAndCurrentJvmTargetClassMatch()) {
            return cacheItem.getCacheEntry();
        }

        return null;
    }

    @Override
    public void put(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {

        Object valueToCache = value;

        boolean classVersionApplicable = CacheItem.checkIfClassVersionApplicable(value, getSettings().isStructuredCacheEntriesEnabled());

        if (classVersionApplicable) {
            valueToCache = new CacheItem(value, getSettings().isStructuredCacheEntriesEnabled());
        }

        String refinedKey = refineKey(key);
        log.debug("Cache put [{}] : key[{}], value[{}], classVersionApplicable : {}", getCacheNamespace(), refinedKey,
                valueToCache, classVersionApplicable);
        try {
            getMemcachedAdapter().set(getCacheNamespace(), refinedKey, valueToCache, getExpiryInSeconds());
        } catch (Exception ex) {
            log.warn("Failed to set memcached value.", ex);
        }
    }

    @Override
    public void evict(Object key) throws CacheException {
        String refinedKey = refineKey(key);
        log.debug("Cache evict[{}] : key[{}]", getCacheNamespace(), refinedKey);
        try {
            getMemcachedAdapter().delete(getCacheNamespace(), refinedKey);
        } catch (Exception ex) {
            log.warn("Failed to delete memcached value.", ex);
        }
    }

    @Override
    public void evictAll() throws CacheException {
        log.debug("Cache evictAll [{}].", getCacheNamespace());
        try {
            getMemcachedAdapter().evictAll(getCacheNamespace());
        } catch (Exception ex) {
            log.warn("Failed to evictAll.", ex);
        }
    }

    /**
     * Read expiry seconds from configuration properties
     */
    protected int getExpiryInSeconds() {
        return expirySeconds;
    }

    /**
     * Memcached has limitation of key size. Shorten the key to avoid the limitation if needed.
     */
    protected String refineKey(Object key) {
        return String.valueOf(key);
    }
}
