package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.regions.CollectionMemcachedRegion;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class NonstrictReadWriteCollectionRegionAccessStrategy extends BaseCollectionMemcachedRegionAccessStrategy {
    public NonstrictReadWriteCollectionRegionAccessStrategy(CollectionMemcachedRegion collectionMemcachedRegion) {
        super(collectionMemcachedRegion);
    }
}