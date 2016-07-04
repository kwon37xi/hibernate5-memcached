package kr.pe.kwonnam.hibernate5memcached.timestamper;

import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class FakeHibernateCacheTimestamper implements HibernateCacheTimestamper {

    private boolean initCalled = false;

    public boolean isInitCalled() {
        return initCalled;
    }

    @Override
    public void setSettings(SessionFactoryOptions settings) {
        
    }

    @Override
    public void setProperties(OverridableReadOnlyProperties properties) {

    }

    @Override
    public void setMemcachedAdapter(MemcachedAdapter memcachedAdapter) {

    }

    @Override
    public void init() {
        initCalled = true;
    }

    @Override
    public long next() {
        return 0;
    }
}
