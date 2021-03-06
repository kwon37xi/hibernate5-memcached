package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.regions.CollectionMemcachedRegion;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class ReadOnlyCollectionRegionAccessStrategy extends BaseCollectionMemcachedRegionAccessStrategy {
    public ReadOnlyCollectionRegionAccessStrategy(CollectionMemcachedRegion collectionMemcachedRegion) {
        super(collectionMemcachedRegion);
    }
}