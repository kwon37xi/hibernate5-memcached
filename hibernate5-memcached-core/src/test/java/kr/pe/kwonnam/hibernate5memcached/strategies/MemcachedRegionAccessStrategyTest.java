package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.memcached.CacheNamespace;
import kr.pe.kwonnam.hibernate5memcached.regions.GeneralDataMemcachedRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.boot.spi.TestingSettingsBuilder;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MemcachedRegionAccessStrategyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private GeneralDataMemcachedRegion generalDataMemcachedRegion;

    @Mock
    private SessionImplementor session;

    private MemcachedRegionAccessStrategy memcachedRegionAccessStrategy;

    @Before
    public void setUp() throws Exception {
        memcachedRegionAccessStrategy = spy(new MemcachedRegionAccessStrategy(generalDataMemcachedRegion));
        when(generalDataMemcachedRegion.getCacheNamespace()).thenReturn(new CacheNamespace("memcachedTest", true));
    }


    @Test
    public void get() throws Exception {
        String expected = "Hello!";
        String key = "greeting";

        when(generalDataMemcachedRegion.get(session, key)).thenReturn(expected);

        assertThat(memcachedRegionAccessStrategy.get(session, key, 1L)).isEqualTo(expected);
    }

    @Test
    public void putFromLoad_without_minimalPutOverride() throws Exception {
        SessionFactoryOptions settings = new TestingSettingsBuilder().setField("minimalPutsEnabled", true).build();
        when(generalDataMemcachedRegion.getSettings()).thenReturn(settings);

        memcachedRegionAccessStrategy.putFromLoad(session, "books#1", "book 1", 1L, "version object");

        verify(memcachedRegionAccessStrategy).putFromLoad(session, "books#1", "book 1", 1L, "version object", true);
    }

    @Test
    public void putFromLoad_key_value_null() throws Exception {
        for (String key : new String[] {null, "books#1"}) {
            for (String value : new String [] {null, "cache value"}) {
                if (key == null || value == null) {
                    assertThat(memcachedRegionAccessStrategy.putFromLoad(session, key, value, 1L, "version object", false)).isFalse();
                } else {
                    assertThat(memcachedRegionAccessStrategy.putFromLoad(session, key, value, 1L, "version object", false)).isTrue();
                }
            }
        }
    }

    @Test
    public void putFromLoad() throws Exception {
        boolean actual = memcachedRegionAccessStrategy.putFromLoad(session, "greeting key", "hello value", 1L, "version object", true);

        assertThat(actual).isTrue();

        verify(generalDataMemcachedRegion).put(session, "greeting key", "hello value");
    }

    @Test
    public void lockItem() throws Exception {
        assertThat(memcachedRegionAccessStrategy.lockItem(session, "key", "version object")).isNull();
    }

    @Test
    public void lockRegion() throws Exception {
        assertThat(memcachedRegionAccessStrategy.lockRegion()).isNull();
    }

    @Test
    public void unlockItem() throws Exception {
        memcachedRegionAccessStrategy.unlockItem(session, "key", null);
        verify(generalDataMemcachedRegion, never()).evict("key");
    }

    @Test
    public void unlockRegion() throws Exception {
        memcachedRegionAccessStrategy.unlockRegion(null);

        verify(memcachedRegionAccessStrategy).evictAll();
    }

    @Test
    public void remove() throws Exception {
        memcachedRegionAccessStrategy.remove(session, "mykey");

        verify(memcachedRegionAccessStrategy).evict("mykey");
    }

    @Test
    public void removeAll() throws Exception {
        memcachedRegionAccessStrategy.removeAll();

        verify(memcachedRegionAccessStrategy, never()).evictAll();
    }

    @Test
    public void evict() throws Exception {
        memcachedRegionAccessStrategy.evict("books#1");

        verify(generalDataMemcachedRegion).evict("books#1");
    }

    @Test
    public void evictAll() throws Exception {
        memcachedRegionAccessStrategy.evictAll();

        verify(generalDataMemcachedRegion).evictAll();
    }

    @Test
    public void isMinimalPutsEnabled_true() throws Exception {
        SessionFactoryOptions settings = new TestingSettingsBuilder().setField("minimalPutsEnabled", true).build();
        when(generalDataMemcachedRegion.getSettings()).thenReturn(settings);

        assertThat(memcachedRegionAccessStrategy.isMinimalPutsEnabled()).isTrue();
    }

    @Test
    public void isMinimalPutsEnabled_false() throws Exception {
        SessionFactoryOptions settings = new TestingSettingsBuilder().setField("minimalPutsEnabled", false).build();
        when(generalDataMemcachedRegion.getSettings()).thenReturn(settings);

        assertThat(memcachedRegionAccessStrategy.isMinimalPutsEnabled()).isFalse();
    }

}