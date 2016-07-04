package kr.pe.kwonnam.hibernate5memcached;

import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.regions.*;
import kr.pe.kwonnam.hibernate5memcached.timestamper.HibernateCacheTimestamper;
import kr.pe.kwonnam.hibernate5memcached.timestamper.HibernateCacheTimestamperJvmImpl;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyPropertiesImpl;
import kr.pe.kwonnam.hibernate5memcached.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Hibernate 5 Memcached Cache Region Factory
 *
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class Hibernate5MemcachedRegionFactory implements RegionFactory {

    private final Logger log = LoggerFactory.getLogger(Hibernate5MemcachedRegionFactory.class);

    /**
     * Memcached Max expiry seconds is 30 days
     */
    public static final int MEMCACHED_MAX_EPIRY_SECONDS = 60 * 60 * 24 * 30;
    public static final AccessType DEFAULT_ACCESS_TYPE = AccessType.NONSTRICT_READ_WRITE;
    public static final String REGION_EXPIRY_SECONDS_PROPERTY_KEY_PREFIX = "h5m.expiry.seconds";
    public static final String MEMCACHED_ADAPTER_CLASS_PROPERTY_KEY = "h5m.adapter.class";
    public static final String TIMESTAMPER_PROPERTY_KEY = "h5m.timestamper.class";
    public static final Class<?> DEFAULT_TIMESTAMPER_CLASS = HibernateCacheTimestamperJvmImpl.class;

    private SessionFactoryOptions settings;

    private Properties cacheProviderConfigProperties;

    private MemcachedAdapter memcachedAdapter;

    private HibernateCacheTimestamper hibernateCacheTimestamper;

    public Hibernate5MemcachedRegionFactory() {
        // no op
    }

    public Hibernate5MemcachedRegionFactory(Properties properties) {
    }

    @Override
    public void start(SessionFactoryOptions settings, Properties properties) throws CacheException {
        log.debug("# start Hibernate5MemcachedRegionFactory.");

        this.settings = settings;
        cacheProviderConfigProperties = populateCacheProviderConfigProperties(properties);
        OverridableReadOnlyProperties mergedConfigProperties = new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties);
        memcachedAdapter = populateMemcachedProvider(mergedConfigProperties);
        hibernateCacheTimestamper = populateTimestamper(settings, mergedConfigProperties);
    }

    Properties populateCacheProviderConfigProperties(Properties properties) {
        String configPath = properties.getProperty(AvailableSettings.CACHE_PROVIDER_CONFIG);
        if (StringUtils.isEmpty(configPath)) {
            return new Properties();
        }

        return PropertiesUtils.loadFromClasspath(configPath);
    }

    MemcachedAdapter populateMemcachedProvider(OverridableReadOnlyProperties properties) {
        String adapterClass = properties.getRequiredProperty(MEMCACHED_ADAPTER_CLASS_PROPERTY_KEY);

        MemcachedAdapter adapter;
        try {
            Class<?> clazz = Class.forName(adapterClass);
            adapter = (MemcachedAdapter) clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        adapter.init(properties);
        return adapter;
    }

    HibernateCacheTimestamper populateTimestamper(SessionFactoryOptions settings, OverridableReadOnlyProperties properties) {
        String timestamperClazzName = properties.getProperty(TIMESTAMPER_PROPERTY_KEY,
                DEFAULT_TIMESTAMPER_CLASS.getName());

        HibernateCacheTimestamper timestamper;
        try {
            Class<?> clazz = Class.forName(timestamperClazzName);
            timestamper = (HibernateCacheTimestamper) clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        timestamper.setSettings(settings);
        timestamper.setProperties(properties);
        timestamper.setMemcachedAdapter(memcachedAdapter);
        timestamper.init();
        return timestamper;
    }

    @Override
    public void stop() {
        log.debug("# stop Hibernate5MemcachedRegionFactory.");
        memcachedAdapter.destroy();
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.NONSTRICT_READ_WRITE;
    }

    @Override
    public long nextTimestamp() {
        return hibernateCacheTimestamper.next();
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties,
                                          CacheDataDescription metadata) throws CacheException {
        return new EntityMemcachedRegion(regionName, new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties), metadata, settings, memcachedAdapter, hibernateCacheTimestamper);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
        return new NaturalIdMemcachedRegion(regionName, new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties), metadata, settings, memcachedAdapter, hibernateCacheTimestamper);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties,
                                                  CacheDataDescription metadata) throws CacheException {
        return new CollectionMemcachedRegion(regionName, new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties), metadata, settings, memcachedAdapter, hibernateCacheTimestamper);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new QueryResultsMemcachedRegion(regionName, new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties), settings, memcachedAdapter, hibernateCacheTimestamper);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new TimestampMemcachedRegion(regionName, new OverridableReadOnlyPropertiesImpl(properties,
                cacheProviderConfigProperties), settings, memcachedAdapter, hibernateCacheTimestamper);
    }
}