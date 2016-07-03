package kr.pe.kwonnam.hibernate4memcached.regions;

import kr.pe.kwonnam.hibernate4memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate4memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate4memcached.strategies.NonstrictReadWriteEntityRegionAccessStrategy;
import kr.pe.kwonnam.hibernate4memcached.strategies.ReadOnlyEntityRegionAccessStrategy;
import kr.pe.kwonnam.hibernate4memcached.timestamper.HibernateCacheTimestamper;
import kr.pe.kwonnam.hibernate4memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class EntityMemcachedRegion extends TransactionalDataMemcachedRegion implements EntityRegion {
    public EntityMemcachedRegion(String regionName, OverridableReadOnlyProperties properties, CacheDataDescription metadata, SessionFactoryOptions settings, MemcachedAdapter memcachedAdapter, HibernateCacheTimestamper hibernateCacheTimestamper) {
        super(new CacheNamespace(regionName, true), properties, metadata, settings, memcachedAdapter, hibernateCacheTimestamper);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        switch (accessType) {
            case READ_ONLY:
                return new ReadOnlyEntityRegionAccessStrategy(this);
            case NONSTRICT_READ_WRITE:
                return new NonstrictReadWriteEntityRegionAccessStrategy(this);
            default:
                throw new CacheException("Unsupported access strategy : " + accessType + ".");
        }
    }
}
