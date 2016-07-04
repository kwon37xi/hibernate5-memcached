package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.regions.EntityMemcachedRegion;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NonstrictReadWriteEntityRegionAccessStrategyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SessionImplementor session;

    @Mock
    private EntityMemcachedRegion entityMemcachedRegion;

    private NonstrictReadWriteEntityRegionAccessStrategy nonstrictReadWriteEntityRegionAccessStrategy;

    @Before
    public void setUp() throws Exception {
        nonstrictReadWriteEntityRegionAccessStrategy = new NonstrictReadWriteEntityRegionAccessStrategy(entityMemcachedRegion);
    }

    @Test
    public void insert() throws Exception {
        assertThat(nonstrictReadWriteEntityRegionAccessStrategy.insert(session, "key", "value", "version")).isFalse();
    }

    @Test
    public void afterInsert() throws Exception {
        assertThat(nonstrictReadWriteEntityRegionAccessStrategy.afterInsert(session, "key", "value", "version")).isFalse();
    }

    @Test
    public void update() throws Exception {
        assertThat(nonstrictReadWriteEntityRegionAccessStrategy.update(session, "key", "value", "currentVersion", "previousVersion")).isFalse();
    }

    @Test
    public void afterUpdate() throws Exception {
        assertThat(nonstrictReadWriteEntityRegionAccessStrategy.afterUpdate(session, "key", "value", "currentVersion", "previousVersion", null)).isFalse();

        verify(entityMemcachedRegion).evict("key");
    }
}