package units.util;

import lombok.val;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * Util for Arrays Collections
 *
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Groups {

    //region Arrays
    static <T> boolean isNullOrEmpty(T[] original) {
        return original == null || original.length == 0;
    }

    /**
     * expend a Array
     *
     * @param origin  the source
     * @param prepend elements to prepend
     * @param append  elements to append
     * @param <T>     type
     * @return array
     */
    static <T> T[] expend(T[] origin, T[] prepend, T[] append) {
        if (isNullOrEmpty(origin)) return append(prepend, append);
        if (isNullOrEmpty(prepend)) return append(origin, append);
        if (isNullOrEmpty(append)) return prepend(origin, prepend);
        val x = java.util.Arrays.copyOf(origin, prepend.length + origin.length + append.length);
        System.arraycopy(prepend, 0, x, 0, prepend.length);
        System.arraycopy(origin, 0, x, prepend.length, origin.length);
        System.arraycopy(append, 0, x, (prepend.length + origin.length), append.length);
        return x;

    }

    /**
     * prepend a Array
     *
     * @param origin  the source
     * @param prepend the element to prepended
     * @param <T>     type
     * @return array
     */
    @SafeVarargs
    static <T> T[] prepend(T[] origin, T... prepend) {
        if (isNullOrEmpty(origin)) return prepend;
        if (isNullOrEmpty(prepend)) return origin;
        val x = java.util.Arrays.copyOf(origin, origin.length + prepend.length);
        System.arraycopy(prepend, 0, x, 0, prepend.length);
        System.arraycopy(origin, 0, x, prepend.length, origin.length);
        return x;
    }

    /**
     * append a Array
     *
     * @param origin the source
     * @param append the element to append
     * @param <T>    type
     * @return array
     */
    @SafeVarargs
    static <T> T[] append(T[] origin, T... append) {
        if (isNullOrEmpty(origin)) return append;
        if (isNullOrEmpty(append)) return origin;
        val x = java.util.Arrays.copyOf(origin, origin.length + append.length);
        System.arraycopy(append, 0, x, origin.length, append.length);
        return x;
    }

    Object[] EMPTY_OBJECT = {};

    @SuppressWarnings("unchecked")
    static <T> T[] empty() {
        return (T[]) EMPTY_OBJECT;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] create(Class<T> component, int size) {
        return (T[]) Array.newInstance(component, size);
    }

    @SuppressWarnings("unchecked")
    static <T, R> R[] createGeneric(Class<T> component, int size) {
        return (R[]) Array.newInstance(component, size);
    }

    //endregion

    //region List
    @SuppressWarnings("unchecked")
    static <T extends I, I>
    List<I> castUp(List<T> src) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        return ((List<I>) src);
    }

    /**
     * cast or convert list to a sub type list
     *
     * @param type      the target type
     * @param src       the source list
     * @param converter converter method
     * @param every     dose convert everyone or just check first
     * @param <T>       target type
     * @param <I>       source type or interface
     * @return list
     */
    @SuppressWarnings("unchecked")
    static <T extends I, I>
    List<T> castDown(Class<T> type, List<I> src, Function<I, T> converter, boolean every) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        if (type.isAssignableFrom(src.get(0).getClass()) && !every) {
            return ((List<T>) src);
        }
        val list = new ArrayList<T>();
        for (I i : src) {
            T t = converter.apply(i);
            list.add(t);
        }
        return list;
    }
    //endregion

    //region Set
    @SuppressWarnings("unchecked")
    static <T extends I, I>
    Set<I> castUp(Set<T> src) {
        if (src == null || src.isEmpty()) return Collections.emptySet();
        return ((Set<I>) src);
    }

    /**
     * cast or convert list to a sub type list
     *
     * @param type      the target type
     * @param src       the source list
     * @param converter converter method
     * @param every     dose convert everyone or just check first
     * @param <T>       target type
     * @param <I>       source type or interface
     * @return list
     */
    @SuppressWarnings("unchecked")
    static <T extends I, I>
    Set<T> castDown(Class<T> type, Set<I> src, Function<I, T> converter, boolean every) {
        if (src == null || src.isEmpty()) return Collections.emptySet();
        val first = src.iterator().next();
        if (type.isAssignableFrom(first.getClass()) && !every) {
            return ((Set<T>) src);
        }
        val list = new HashSet<T>();
        for (I i : src) {
            T t = converter.apply(i);
            list.add(t);
        }
        return list;
    }
    //endregion

    //region Map
    @SuppressWarnings("unchecked")
    static <K, T extends I, I>
    Map<K, I> castUp(Map<K, T> src) {
        if (src == null || src.isEmpty()) return Collections.emptyMap();
        return ((Map<K, I>) src);
    }

    @SuppressWarnings("unchecked")
    static <V, T extends I, I>
    Map<I, V> castUpKey(Map<T, V> src) {
        if (src == null || src.isEmpty()) return Collections.emptyMap();
        return ((Map<I, V>) src);
    }

    @SuppressWarnings("unchecked")
    static <K, T extends I, I>
    Map<K, T> castDown(Class<T> type, Map<K, I> src, Function<I, T> converter, boolean every) {
        if (src == null || src.isEmpty()) return Collections.emptyMap();
        val first = src.values().iterator().next();
        if (type.isAssignableFrom(first.getClass()) && !every) {
            return ((Map<K, T>) src);
        }
        val list = new HashMap<K, T>();
        src.forEach((k, v) -> list.put(k, converter.apply(v)));
        return list;
    }

    @SuppressWarnings("unchecked")
    static <V, T extends I, I>
    Map<T, V> castDownKey(Class<T> type, Map<I, V> src, Function<I, T> converter, boolean every) {
        if (src == null || src.isEmpty()) return Collections.emptyMap();
        val first = src.values().iterator().next();
        if (type.isAssignableFrom(first.getClass()) && !every) {
            return ((Map<T, V>) src);
        }
        val list = new HashMap<T, V>();
        src.forEach((k, v) -> list.put(converter.apply(k), v));
        return list;
    }

    @SafeVarargs
    static <K, V> Map<K, V> hashMapOf(Map.Entry<K, V>... entries) {
        val m = new HashMap<K, V>();
        for (Map.Entry<K, V> e : entries) {
            if (m.containsKey(e.getKey()))
                throw new IllegalStateException("key '" + e.getKey() + "' is already exists !");
            m.put(e.getKey(), e.getValue());
        }
        return m;
    }

    @SafeVarargs
    static <K, V> Map<K, V> unmodifiableMapOf(Map.Entry<K, V>... entries) {
        return Collections.unmodifiableMap(hashMapOf(entries));
    }
    //endregion


    static <K, V> Map.Entry<K, V> entryOf(K k, V v) {
        return new AbstractMap.SimpleEntry<>(k, v);
    }
}
