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

@RunWith(MockitoJUnitRunner.class)
public class ReadOnlyEntityRegionAccessStrategyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SessionImplementor session;

    @Mock
    private EntityMemcachedRegion entityMemcachedRegion;

    private ReadOnlyEntityRegionAccessStrategy readOnlyEntityRegionAccessStrategy;

    @Before
    public void setUp() throws Exception {
        readOnlyEntityRegionAccessStrategy = new ReadOnlyEntityRegionAccessStrategy(entityMemcachedRegion);
    }

    @Test
    public void insert() throws Exception {
        assertThat(readOnlyEntityRegionAccessStrategy.insert(session, "key", "value", "version")).isFalse();
    }

    @Test
    public void afterInsert() throws Exception {
        assertThat(readOnlyEntityRegionAccessStrategy.afterInsert(session, "key", "value", "version")).isFalse();
    }

    @Test
    public void update() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        readOnlyEntityRegionAccessStrategy.update(session, "key", "value", "currentVersion", "previousVersion");
    }

    @Test
    public void afterUpdate() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        readOnlyEntityRegionAccessStrategy.afterUpdate(session, "key", "value", "currentVersion", "previousVersion", null);
    }
}