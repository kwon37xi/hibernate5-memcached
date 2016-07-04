package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.regions.NaturalIdMemcachedRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
 */
public class ReadOnlyNaturalIdRegionAccessStrategy extends BaseNaturalIdMemcachedRegionAccessStrategy {
    private Logger log = LoggerFactory.getLogger(ReadOnlyNaturalIdRegionAccessStrategy.class);

    public ReadOnlyNaturalIdRegionAccessStrategy(NaturalIdMemcachedRegion naturalIdMemcachedRegion) {
        super(naturalIdMemcachedRegion);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId insert() {} {}", getInternalRegion().getCacheNamespace(), key);
        // On read-only, Hibernate never calls this method.
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId afterInsert() {} {}", getInternalRegion().getCacheNamespace(), key);
        // On read-only, Hibernate never calls this method.
        return false;
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId update() {} {}", getInternalRegion().getCacheNamespace(), key);
        // protect from users' update operation
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
        log.debug("region access strategy readonly naturalId afterUpdate() {} {}", getInternalRegion().getCacheNamespace(), key);
        // protect from users' update operation
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }
}
