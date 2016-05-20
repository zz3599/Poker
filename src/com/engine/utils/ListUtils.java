package com.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtils {
    public static <E> List<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        final List<E> list = new ArrayList<E>();
        for (final E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }
}
