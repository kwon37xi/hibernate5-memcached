package kr.pe.kwonnam.hibernate4memcached.regions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Map;

/**
 * Entity Cache에 데이터를 저장할 때
 *
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class CacheItem implements Serializable {
    private Logger log = LoggerFactory.getLogger(CacheItem.class);

    public static final String STRUCTURED_CACHE_ENTRY_SUBCLASS_KEY = "_subclass";

    private Object cacheEntry;

    /**
     * Entity class FQCN
     */
    private String targetClassName;

    /**
     * Entity class serialVersionUID
     */
    private long targetClassSerialVersionUID;

    private boolean useStructuredCache;

    /**
     * constructor for test
     */
    CacheItem() {
        // no op
    }

    public CacheItem(Object cacheEntry, boolean useStructuredCache) {

        if (!checkIfClassVersionApplicable(cacheEntry, useStructuredCache)) {
            throw new IllegalArgumentException(cacheEntry + "is not class version applicable.");
        }

        this.useStructuredCache = useStructuredCache;

        parseTargetClass(cacheEntry, useStructuredCache);
    }

    private void parseTargetClass(Object cacheEntry, boolean useStructuredCache) {
        Class<?> subclass = getSubclassName(cacheEntry, useStructuredCache);
        if (!Serializable.class.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException(subclass + " class is not Serializable.");
        }
        targetClassName = subclass.getName();

        ObjectStreamClass osc = ObjectStreamClass.lookup(subclass);
        targetClassSerialVersionUID = osc.getSerialVersionUID();

        this.cacheEntry = cacheEntry;
    }

    private Class<?> getSubclassName(Object cacheEntry, boolean useStructuredCache) {
        String subclassName = null;
        if (useStructuredCache) {
            @SuppressWarnings("unchecked")
            Map structuredCacheEntry = (Map) cacheEntry;
            subclassName = (String) structuredCacheEntry.get(STRUCTURED_CACHE_ENTRY_SUBCLASS_KEY);
        } else {
            subclassName = ((CacheEntry) cacheEntry).getSubclass();
        }

        Class<?> clazz;
        try {
            clazz = Class.forName(subclassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(subclassName + " class is not found.", e);
        }
        return clazz;
    }

    public Object getCacheEntry() {
        return cacheEntry;
    }

    void setCacheEntry(Object cacheEntry) {
        this.cacheEntry = cacheEntry;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public long getTargetClassSerialVersionUID() {
        return targetClassSerialVersionUID;
    }

    void setTargetClassSerialVersionUID(long targetClassSerialVersionUID) {
        this.targetClassSerialVersionUID = targetClassSerialVersionUID;
    }

    public boolean isUseStructuredCache() {
        return useStructuredCache;
    }

    void setUseStructuredCache(boolean useStructuredCache) {
        this.useStructuredCache = useStructuredCache;
    }

    /**
     * Compare targetClassSerialVersionUID and current JVM's targetClass serialVersionUID.
     * If they are same return true else return false.
     */
    public boolean isTargetClassAndCurrentJvmTargetClassMatch() {
        Class<?> targetClassOnThisJVM;

        // JVM에 Class가 없는 상황
        try {
            targetClassOnThisJVM = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            log.error("target class " + targetClassName + " does not exist on this JVM.");
            return false;
        }

        ObjectStreamClass osc = ObjectStreamClass.lookup(targetClassOnThisJVM);

        // JVM Class가 Not serializable
        if (osc == null) {
            return false;
        }
        return targetClassSerialVersionUID == osc.getSerialVersionUID();
    }


    /**
     * Check if class version comparable.
     */
    public static boolean checkIfClassVersionApplicable(Object cacheEntry, boolean useStructuredCache) {
        if (!useStructuredCache && cacheEntry instanceof CacheEntry) {
            return true;
        }

        if (useStructuredCache && cacheEntry instanceof Map) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        CacheItem otherCacheItem = (CacheItem) o;

        return new EqualsBuilder().append(targetClassSerialVersionUID, otherCacheItem.targetClassSerialVersionUID)
                .append(useStructuredCache, otherCacheItem.useStructuredCache)
                .append(cacheEntry, otherCacheItem.cacheEntry)
                .append(targetClassName, otherCacheItem.targetClassName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cacheEntry)
                .append(targetClassName)
                .append(targetClassSerialVersionUID)
                .append(useStructuredCache)
                .toHashCode();
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this).append("cacheEntry", cacheEntry).append("targetClassName", targetClassName)
                .append("targetClassSerialVersionUID", targetClassSerialVersionUID)
                .append("useStrucuturedCache", useStructuredCache)
                .toString();
    }
}
