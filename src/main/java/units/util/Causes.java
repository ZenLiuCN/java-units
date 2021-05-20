package units.util;

import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * a Exception Unit
 *
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Causes {
    /**
     * format a Exception Message
     *
     * @param ctor    Exception constructor , eg: RuntimeException::new
     * @param pattern String patter, with '{}' as place holder
     * @param args    args
     * @param <R>     the Type of result
     * @return a formatted Exception
     */
    static <R extends Throwable> R format(BiFunction<String, Throwable, R> ctor, String pattern, Object... args) {
        val f = MessageFormatter.format(pattern, args);
        return ctor.apply(f.getMessage(), f.getThrowable());
    }

    /**
     * format and throw a Exception.
     *
     * @see #format(BiFunction, String, Object...)
     */
    @SneakyThrows
    static <R extends Throwable> void sneakyThrow(BiFunction<String, Throwable, R> ctor, String pattern, Object... args) {
        val f = MessageFormatter.format(pattern, args);
        throw ctor.apply(f.getMessage(), f.getThrowable());
    }


    /**
     * convert a Throwable into a list of Throwable
     *
     * @param throwable source
     * @return a List
     */
    static List<Throwable> flatten(Throwable throwable) {
        val list = new ArrayList<Throwable>();
        var cause = throwable;
        while (cause != null && !list.contains(cause)) {
            list.add(cause);
            cause = cause.getCause();
        }
        return list;
    }
}
