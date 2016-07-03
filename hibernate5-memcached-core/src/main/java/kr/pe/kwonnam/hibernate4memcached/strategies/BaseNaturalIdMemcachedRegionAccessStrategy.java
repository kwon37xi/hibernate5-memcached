package kr.pe.kwonnam.hibernate4memcached.strategies;

import kr.pe.kwonnam.hibernate4memcached.regions.NaturalIdMemcachedRegion;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
 */
public abstract class BaseNaturalIdMemcachedRegionAccessStrategy extends MemcachedRegionAccessStrategy implements NaturalIdRegionAccessStrategy {
    private NaturalIdMemcachedRegion naturalIdMemcachedRegion;

    public BaseNaturalIdMemcachedRegionAccessStrategy(NaturalIdMemcachedRegion naturalIdMemcachedRegion) {
        super(naturalIdMemcachedRegion);
        this.naturalIdMemcachedRegion = naturalIdMemcachedRegion;
    }

    @Override
    public NaturalIdRegion getRegion() {
        return naturalIdMemcachedRegion;
    }

    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
    }
}
