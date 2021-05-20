package units.util;

import java.util.function.*;

/**
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-21
 */
public interface Functions {
    @FunctionalInterface
    interface Triple<A, B, C, R> {
        R apply(A a, B b, C c);

        default BiFunction<B, C, R> closure(A a) {
            return (b, c) -> apply(a, b, c);
        }

        default Function<C, R> closure(A a, B b) {
            return c -> apply(a, b, c);
        }
    }

    @FunctionalInterface
    interface TripleConsumer<A, B, C> {
        void accept(A a, B b, C c);

        default BiConsumer<B, C> closure(A a) {
            return (b, c) -> accept(a, b, c);
        }

        default Consumer<C> closure(A a, B b) {
            return c -> accept(a, b, c);
        }
    }

    @FunctionalInterface
    interface ToIntTriple<A, B, C> {
        int applyAsInt(A a, B b, C c);

        default ToIntBiFunction<B, C> closure(A a) {
            return (b, c) -> applyAsInt(a, b, c);
        }

        default ToIntFunction<C> closure(A a, B b) {
            return c -> applyAsInt(a, b, c);
        }
    }

    @FunctionalInterface
    interface ToLongTriple<A, B, C> {
        long applyAsLong(A a, B b, C c);

        default ToLongBiFunction<B, C> closure(A a) {
            return (b, c) -> applyAsLong(a, b, c);
        }

        default ToLongFunction<C> closure(A a, B b) {
            return c -> applyAsLong(a, b, c);
        }
    }

    @FunctionalInterface
    interface IntTripleOperator {
        int applyAsInt(int a, int b, int c);

        default IntBinaryOperator closure(int a) {
            return (b, c) -> applyAsInt(a, b, c);
        }

        default IntUnaryOperator closure(int a, int b) {
            return c -> applyAsInt(a, b, c);
        }
    }

    @FunctionalInterface
    interface LongTripleOperator {
        long applyAsLong(long a, long b, long c);

        default LongBinaryOperator closure(long a) {
            return (b, c) -> applyAsLong(a, b, c);
        }

        default LongUnaryOperator closure(long a, long b) {
            return c -> applyAsLong(a, b, c);
        }
    }

    @FunctionalInterface
    interface TriplePredicate<A, B, C> {
        boolean test(A a, B b, C c);

        default BiPredicate<B, C> closure(A a) {
            return (b, c) -> test(a, b, c);
        }

        default Predicate<C> closure(A a, B b) {
            return c -> test(a, b, c);
        }
    }

}
