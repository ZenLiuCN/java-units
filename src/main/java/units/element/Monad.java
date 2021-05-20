package units.element;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Monad<S extends Monad<S, T>, T> extends Supplier<T> {
    <R> Monad<?, R> with(R value);

    S self();

    default S apply(Consumer<T> act) {
        act.accept(get());
        return self();
    }

    default Monad<?, T> when(Predicate<T> condition, Function<T, T> act) {
        if (condition.test(get())) {
            return with(act.apply(get()));
        }
        return self();
    }

    default Optional<S> take(Predicate<T> act) {
        if (!act.test(get())) return Optional.empty();
        return Optional.of(self());
    }

    default Optional<S> takeNot(Predicate<T> act) {
        if (act.test(get())) return Optional.empty();
        return Optional.of(self());
    }

    default <R> Monad<?, R> let(Function<T, R> act) {
        return with(act.apply(get()));
    }

    default <R> Monad<?, R> let(Predicate<T> condition, Function<T, R> act, Function<T, R> elseAct) {
        return with(condition.test(get()) ? act.apply(get()) : elseAct.apply(get()));
    }


    default S loop(int n, Consumer<T> act) {
        for (int i = 0; i < n; i++) {
            act.accept(get());
        }
        return self();
    }

    default S loop(int n, int step, Consumer<T> act) {
        for (int i = 0; i < n; i += step) {
            act.accept(get());
        }
        return self();
    }

    default S when(Predicate<T> condition, Consumer<T> act) {
        while (condition.test(get())) {
            act.accept(get());
        }
        return self();
    }


    default void run(Consumer<T> act) {
        act.accept(get());
    }

    default <R> R exec(Function<T, R> act) {
        return act.apply(get());
    }

    interface Sequence<S extends Sequence<S, T>, T> extends Monad<S, Collection<T>> {

        <R> Sequence<?, R> withSeq(Collection<R> value);

        default S every(Consumer<T> act) {
            get().forEach(act);
            return self();
        }

        default <R> Sequence<?, R> map(Function<T, R> mapping) {
            List<R> list = new ArrayList<>();
            for (T t : get()) {
                R r = mapping.apply(t);
                list.add(r);
            }
            return withSeq(list);
        }

        default <R> Sequence<?, R> mapSet(Function<T, R> mapping) {
            Set<R> set = new HashSet<>();
            for (T t : get()) {
                R r = mapping.apply(t);
                set.add(r);
            }
            return withSeq(set);
        }
    }

    static <T> Monad<?, T> of(T src) {
        return new MonadSingle<>(src);
    }

    static <R> Sequence<?, R> seq(Collection<R> src) {
        return new MonadSequence<>(src);
    }

    @SafeVarargs
    static <R> Sequence<?, R> seqOf(R... src) {
        return new MonadSequence<>(Arrays.asList(src));
    }

    @AllArgsConstructor(staticName = "of")
    class MonadSequence<T> implements Sequence<MonadSequence<T>, T> {
        protected final Collection<T> value;

        @Override
        public Collection<T> get() {
            return value;
        }


        @Override
        public <R> Monad<?, R> with(R value) {
            return new MonadSingle<>(value);
        }

        @Override
        public MonadSequence<T> self() {
            return this;
        }


        @Override
        public <X> Sequence<?, X> withSeq(Collection<X> value) {
            return new MonadSequence<>(value);
        }
    }

    @AllArgsConstructor(staticName = "of")
    class MonadSingle<T> implements Monad<MonadSingle<T>, T> {
        protected final T value;

        @Override
        public T get() {
            return value;
        }


        @Override
        public <R> Monad<?, R> with(R value) {
            return new MonadSingle<>(value);
        }

        @Override
        public MonadSingle<T> self() {
            return this;
        }
    }
}