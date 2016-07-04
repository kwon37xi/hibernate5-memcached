package kr.pe.kwonnam.hibernate5memcached.regions;

import kr.pe.kwonnam.hibernate5memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.strategies.NonstrictReadWriteNaturalIdRegionAccessStrategy;
import kr.pe.kwonnam.hibernate5memcached.strategies.ReadOnlyNaturalIdRegionAccessStrategy;
import kr.pe.kwonnam.hibernate5memcached.timestamper.HibernateCacheTimestamper;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class NaturalIdMemcachedRegion extends TransactionalDataMemcachedRegion implements NaturalIdRegion {
    public NaturalIdMemcachedRegion(String regionName, OverridableReadOnlyProperties properties,
                                    CacheDataDescription metadata, SessionFactoryOptions settings,
                                    MemcachedAdapter memcachedAdapter,
                                    HibernateCacheTimestamper hibernateCacheTimestamper) {
        super(new CacheNamespace(regionName, true), properties, metadata, settings, memcachedAdapter,
              hibernateCacheTimestamper);
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        switch (accessType) {
            case READ_ONLY:
                return new ReadOnlyNaturalIdRegionAccessStrategy(this);
            case NONSTRICT_READ_WRITE:
                return new NonstrictReadWriteNaturalIdRegionAccessStrategy(this);
            default:
                throw new CacheException("Unsupported access strategy : " + accessType + ".");
        }
    }
}
