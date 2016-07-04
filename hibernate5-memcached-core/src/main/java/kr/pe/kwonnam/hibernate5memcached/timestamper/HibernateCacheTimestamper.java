package kr.pe.kwonnam.hibernate5memcached.timestamper;

import kr.pe.kwonnam.hibernate5memcached.memcached.MemcachedAdapter;
import kr.pe.kwonnam.hibernate5memcached.util.OverridableReadOnlyProperties;
import org.hibernate.boot.spi.SessionFactoryOptions;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public interface HibernateCacheTimestamper {

    void setSettings(SessionFactoryOptions settings);

    void setProperties(OverridableReadOnlyProperties properties);

    void setMemcachedAdapter(MemcachedAdapter memcachedAdapter);

    /** initialize timestamp object */
    void init();

    /** get next timestamp */
    long next();
}
