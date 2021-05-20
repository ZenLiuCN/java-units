package units.element;

import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static units.element.Result.Both.NOTHING;

/**
 * a Result should never null.<br>
 * Result may have only error.<br>
 * Result may have only success value.<br>
 * Result may have both error and value.<br>
 *
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Result<T> {
    @Nullable T getValue();

    @NotNull T getOrThrow();

    @Nullable Throwable getError();

    default boolean hasError() {
        return getError() != null;
    }

    default boolean isPresent() {
        return getValue() != null;
    }

    default Optional<T> toOptional(@Nullable Consumer<Throwable> errorConsumer) {
        return Optional.ofNullable(getValue(errorConsumer));
    }

    default boolean isNothing() {
        return !isPresent() && !hasError();
    }

    default @Nullable T getValue(@Nullable Consumer<Throwable> errorConsumer) {
        if (hasError() && errorConsumer != null) errorConsumer.accept(getError());
        return getValue();
    }

    /**
     * never throw error
     */

    <R> Result<R> map(Function<T, R> mapper);

    Result<T> mapError(Function<Throwable, Throwable> mapper);

    @SuppressWarnings("unchecked")
    static <T> Result<T> nothing() {
        return (Result<T>) NOTHING;
    }

    static <T> Result<T> ok(@NotNull T data) {
        return new Both.Success<>(Objects.requireNonNull(data, "a success result should not with a null value."));
    }

    static <T> Result<T> error(@NotNull Throwable ex) {
        return new Both.Failure<>(Objects.requireNonNull(ex, "an error result should not with a null exception."));
    }

    static <T> Result<T> done(@NotNull T data, @NotNull Throwable ex) {
        return new Both<>(
            Objects.requireNonNull(data, "an both result should not with a null value."),
            Objects.requireNonNull(ex, "an both result should not with a null exception.")
        );
    }

    static <T> Result<T> with(@NotNull Supplier<T> supplier) {
        try {
            return ok(supplier.get());
        } catch (Exception ex) {
            return error(ex);
        }
    }

    static <T> Result<T> withOptional(@NotNull Supplier<Optional<T>> supplier, @Nullable Supplier<Exception> notExists) {
        try {
            return ok(supplier.get().orElseThrow(notExists == null ? () -> Errors.notExists("data not exists") : notExists));
        } catch (Exception ex) {
            return error(ex);
        }
    }

    final class Both<T> implements Result<T> {
        @SuperBuilder
        final static class NothingExistsError extends Errors.CommonError {

        }

        static final Result<Void> NOTHING = new Success<>(null);

        static final class Success<T> implements Result<T> {
            final T value;

            Success(T value) {
                this.value = value;
            }


            @Override
            public @Nullable T getValue() {
                return value;
            }

            @Override
            public @NotNull T getOrThrow() {
                return value;
            }

            @Override
            public Throwable getError() {
                return null;
            }

            @Override
            public <R> Result<R> map(Function<T, R> mapper) {
                return new Success<>(Objects.requireNonNull(mapper.apply(value), "mapping result should never be null "));
            }

            @Override
            public Result<T> mapError(Function<Throwable, Throwable> mapper) {
                return this;
            }
        }

        static final class Failure<T> implements Result<T> {
            final Throwable error;

            Failure(Throwable error) {
                this.error = error;
            }

            @Override
            public @Nullable T getValue() {
                return null;
            }

            @Override
            @SneakyThrows
            public @NotNull T getOrThrow() {
                throw error;
            }

            @Override
            public @Nullable Throwable getError() {
                return error;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <R> Result<R> map(Function<T, R> mapper) {
                return (Result<R>) this;
            }

            @Override
            public Result<T> mapError(Function<Throwable, Throwable> mapper) {
                return new Failure<T>(Objects.requireNonNull(mapper.apply(error), "a failure mapping should not return null."));
            }
        }

        private final T value;
        private final Throwable error;

        public Both(T value, Throwable error) {
            this.value = value;
            this.error = error;
        }

        @Override
        public @Nullable T getValue() {
            return value;
        }

        @Override
        @SneakyThrows
        public @NotNull T getOrThrow() {
            if (error != null) throw error;
            if (value == null) throw NothingExistsError.builder().build();
            return value;
        }

        @Override
        public @Nullable Throwable getError() {
            return error;
        }

        @Override
        public <R> Result<R> map(Function<T, R> mapper) {
            return
                value != null ?
                    new Both<>(Objects.requireNonNull(mapper.apply(value), "mapping result should not return null."), error)
                    :
                    new Failure<>(error);
        }

        @Override
        public Result<T> mapError(Function<Throwable, Throwable> mapper) {
            return error == null ? this : new Both<>(value, Objects.requireNonNull(mapper.apply(error), "error mapping should not return null."));
        }
    }
}
