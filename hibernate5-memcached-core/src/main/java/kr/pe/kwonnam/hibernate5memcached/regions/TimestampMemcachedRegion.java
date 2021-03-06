package kr.pe.kwonnam.hibernate5memcached.regions;

import kr.pe.kwonnam.hibernate5memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.timestamper.HibernateCacheTimestamper;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.TimestampsRegion;

/**
 * {@link org.hibernate.cache.spi.TimestampsRegion} has no concurrency strategy.
 * It deals <code>[cache-region-prefix.]org.hibernate.cache.spi.UpdateTimestampsCache</code>.
 * <p/>
 *
 * This region should have very long expiry seconds.
 *
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class TimestampMemcachedRegion extends GeneralDataMemcachedRegion implements TimestampsRegion {
    public TimestampMemcachedRegion(String regionName, OverridableReadOnlyProperties properties, SessionFactoryOptions settings,
                                    MemcachedAdapter memcachedAdapter,
                                    HibernateCacheTimestamper hibernateCacheTimestamper) {
        super(new CacheNamespace(regionName, false), properties, null, settings, memcachedAdapter,
              hibernateCacheTimestamper);
    }
}
