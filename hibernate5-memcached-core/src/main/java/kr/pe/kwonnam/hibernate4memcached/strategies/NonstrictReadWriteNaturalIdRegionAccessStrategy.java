package kr.pe.kwonnam.hibernate4memcached.strategies;

import kr.pe.kwonnam.hibernate4memcached.regions.NaturalIdMemcachedRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class NonstrictReadWriteNaturalIdRegionAccessStrategy extends BaseNaturalIdMemcachedRegionAccessStrategy {
    private Logger log = LoggerFactory.getLogger(NonstrictReadWriteNaturalIdRegionAccessStrategy.class);

    public NonstrictReadWriteNaturalIdRegionAccessStrategy(NaturalIdMemcachedRegion naturalIdMemcachedRegion) {
        super(naturalIdMemcachedRegion);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId insert() {} {}", getInternalRegion().getCacheNamespace(), key);
        // On nonstrict-read-write, Hibernate never calls this method.
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId afterInsert() {} {}", getInternalRegion().getCacheNamespace(), key);
        // On nonstrict-read-write, Hibernate never calls this method.
        return false;
    }

    /**
     * not necessary in nostrict-read-write
     *
     * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
     */
    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId update() {} {}", getInternalRegion().getCacheNamespace(), key);
        return false;
    }

    /**
     * need evict the key, after update.
     *
     * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId afterUpdate() {} {}", getInternalRegion().getCacheNamespace(), key);
        getInternalRegion().evict(key);
        return false;
    }

}
