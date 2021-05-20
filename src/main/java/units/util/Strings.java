package units.util;

import lombok.val;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Strings {
    String[] EMPTY_ARRAY = {};
    String EMPTY = "";

    static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    static String nullOrEmptyThen(String value, String val) {
        return value == null || value.isEmpty() ? val : value;
    }

    static <T> String nullOrEmptyThen(T target, Function<T, String> supply, String val) {
        if (target == null) return val;
        val value = supply.apply(target);
        return value == null || value.isEmpty() ? val : value;
    }

    static String nullOrEmptyThen(String value, Supplier<String> val) {
        return value == null || value.isEmpty() ? val.get() : value;
    }

    static String nullThenEmpty(String val) {
        return val == null ? EMPTY : val;
    }
}
