package kr.pe.kwonnam.hibernate5memcached.timestamper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class HibernateCacheTimestamperJvmImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HibernateCacheTimestamperJvmImpl hibernateCacheTimestamperJvm;

    @Before
    public void setUp() throws Exception {
        hibernateCacheTimestamperJvm = new HibernateCacheTimestamperJvmImpl();
    }

    @Test
    public void next() throws Exception {
        long start = System.currentTimeMillis();

        Thread.sleep(1);
        long next = hibernateCacheTimestamperJvm.next();

        assertThat(next).isGreaterThanOrEqualTo(start).isLessThanOrEqualTo(System.currentTimeMillis());
    }
}