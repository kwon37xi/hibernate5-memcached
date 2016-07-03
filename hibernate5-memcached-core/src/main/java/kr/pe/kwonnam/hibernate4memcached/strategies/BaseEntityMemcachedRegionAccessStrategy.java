package kr.pe.kwonnam.hibernate4memcached.strategies;

import kr.pe.kwonnam.hibernate4memcached.regions.EntityMemcachedRegion;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
 */
public abstract class BaseEntityMemcachedRegionAccessStrategy extends MemcachedRegionAccessStrategy implements EntityRegionAccessStrategy {
    private EntityMemcachedRegion entityMemcachedRegion;

    public BaseEntityMemcachedRegionAccessStrategy(EntityMemcachedRegion entityMemcachedRegion) {
        super(entityMemcachedRegion);
        this.entityMemcachedRegion = entityMemcachedRegion;
    }

    @Override
    public EntityRegion getRegion() {
        return entityMemcachedRegion;
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.createEntityKey(id, persister, factory, tenantIdentifier);
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.getEntityId(cacheKey);
    }
}
