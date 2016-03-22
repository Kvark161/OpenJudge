package ru.openjudge.server.dao.mapImpl;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    public static void generateObjectId(Object object, AtomicLong maxId) {
        Field field;
        long newId = maxId.get();
        maxId.set(maxId.get() + 1);
        try {
            field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.setLong(object, newId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
