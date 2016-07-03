package org.hibernate.boot.spi;

import org.hibernate.boot.internal.SessionFactoryOptionsImpl;
import org.hibernate.boot.internal.SessionFactoryOptionsState;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

/**
 * {@link SessionFactoryOptions} builder for testing
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class TestingSettingsBuilder {

    private SessionFactoryOptions settings;

    public TestingSettingsBuilder() {
        settings = new SessionFactoryOptionsImpl(mock(SessionFactoryOptionsState.class));
    }

    public TestingSettingsBuilder setField(String fieldName, Object value) {
        try {
            Field field = SessionFactoryOptionsImpl.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(settings, value);

            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return this;
    }

    public SessionFactoryOptions build() {
        return settings;
    }
}
