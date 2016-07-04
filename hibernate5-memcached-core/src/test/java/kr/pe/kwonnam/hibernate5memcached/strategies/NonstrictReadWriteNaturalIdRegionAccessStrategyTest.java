package kr.pe.kwonnam.hibernate5memcached.strategies;

import kr.pe.kwonnam.hibernate5memcached.regions.NaturalIdMemcachedRegion;
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
public class NonstrictReadWriteNaturalIdRegionAccessStrategyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SessionImplementor session;

    @Mock
    private NaturalIdMemcachedRegion naturalIdMemcachedRegion;

    private NonstrictReadWriteNaturalIdRegionAccessStrategy nonstrictReadWriteNaturalIdRegionAccessStrategy;

    @Before
    public void setUp() throws Exception {
        nonstrictReadWriteNaturalIdRegionAccessStrategy = new NonstrictReadWriteNaturalIdRegionAccessStrategy(naturalIdMemcachedRegion);
    }


    @Test
    public void insert() throws Exception {
        assertThat(nonstrictReadWriteNaturalIdRegionAccessStrategy.insert(session, "key", "value")).isFalse();
    }

    @Test
    public void afterInsert() throws Exception {
        assertThat(nonstrictReadWriteNaturalIdRegionAccessStrategy.afterInsert(session, "key", "value")).isFalse();
    }

    @Test
    public void update() throws Exception {
        assertThat(nonstrictReadWriteNaturalIdRegionAccessStrategy.update(session, "key", "value")).isFalse();
    }

    @Test
    public void afterUpdate() throws Exception {
        assertThat(nonstrictReadWriteNaturalIdRegionAccessStrategy.afterUpdate(session, "key", "value", null)).isFalse();

        verify(naturalIdMemcachedRegion).evict("key");
    }
}