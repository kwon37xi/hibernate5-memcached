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

@RunWith(MockitoJUnitRunner.class)
public class ReadOnlyNaturalIdRegionAccessStrategyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SessionImplementor session;

    @Mock
    private NaturalIdMemcachedRegion naturalIdMemcachedRegion;

    private ReadOnlyNaturalIdRegionAccessStrategy readOnlyNaturalIdRegionAccessStrategy;

    @Before
    public void setUp() throws Exception {
        readOnlyNaturalIdRegionAccessStrategy = new ReadOnlyNaturalIdRegionAccessStrategy(naturalIdMemcachedRegion);
    }

    @Test
    public void insert() throws Exception {
        assertThat(readOnlyNaturalIdRegionAccessStrategy.insert(session, "key", "value")).isFalse();
    }

    @Test
    public void afterInsert() throws Exception {
        assertThat(readOnlyNaturalIdRegionAccessStrategy.afterInsert(session, "key", "value")).isFalse();
    }

    @Test
    public void update() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        readOnlyNaturalIdRegionAccessStrategy.update(session, "key", "value");
    }

    @Test
    public void afterUpdate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        readOnlyNaturalIdRegionAccessStrategy.afterUpdate(session, "key", "value", null);
    }
}