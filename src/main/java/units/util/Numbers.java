package units.util;

/**
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Numbers {
    static <T extends Number> T nullThen(T v, T defaultVal) {
        return v == null ? defaultVal : v;
    }

    static <T extends Number> long nullThenLong(T v, long defaultVal) {
        return v == null ? defaultVal : v.longValue();
    }

    static <T extends Number> int nullThenInt(T v, int defaultVal) {
        return v == null ? defaultVal : v.intValue();
    }


}
