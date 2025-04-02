package org.example.configs;

import org.example.services.BookService;

import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private Map<Class<?>, Object> singletons = new HashMap<>();

    public <T> void registerSingleton(Class<T> clazz, T obj) {
        singletons.put(clazz, obj);
    }

    public <T> T getSingleton(Class<T> clazz) {
        return clazz.cast(singletons.get(clazz));
    }
}
