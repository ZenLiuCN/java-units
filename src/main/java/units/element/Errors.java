package units.element;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.slf4j.helpers.MessageFormatter;
import units.util.Numbers;
import units.util.Strings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Errors {
    AtomicInteger DEFAULT_CODE = new AtomicInteger(500);
    AtomicInteger INVALID_CODE = new AtomicInteger(400);
    AtomicInteger CONFLICT_CODE = new AtomicInteger(409);
    AtomicInteger ALREADY_EXISTS_CODE = new AtomicInteger(500);
    AtomicInteger NOT_EXISTS_CODE = new AtomicInteger(404);
    AtomicInteger FORBIDDEN_CODE = new AtomicInteger(403);
    AtomicInteger NOT_ALLOWED_CODE = new AtomicInteger(405);
    AtomicInteger GONE_CODE = new AtomicInteger(410);
    AtomicInteger TIMEOUT_CODE = new AtomicInteger(408);
    AtomicInteger LEGAL_REASON_CODE = new AtomicInteger(451);
    AtomicInteger DEFAULT_NOTIFY_TYPE = new AtomicInteger(0);

    @Accessors(fluent = false)
    abstract class CommonError extends RuntimeException {
        @Getter final int code;
        @Getter final int notify;
        @Getter final String notification;

        protected CommonError(String message,
                              Throwable cause,
                              boolean enableSuppression,
                              boolean writableStackTrace,
                              Integer code,
                              Integer notify,
                              String notification
        ) {
            super(message, cause, enableSuppression, writableStackTrace);
            this.code = Numbers.nullThenInt(code, DEFAULT_CODE.get());
            this.notify = Numbers.nullThenInt(notify, DEFAULT_NOTIFY_TYPE.get());
            this.notification = Strings.nullThenEmpty(notification);
        }

        public static abstract class CommonErrorBuilder<C extends CommonError, B extends CommonErrorBuilder<C, B>> {
            String message;
            Throwable cause;
            boolean enableSuppression;
            boolean writableStackTrace;
            Integer code;
            Integer notify;
            String notification;

            public B cause(Throwable cause) {
                this.cause = cause;
                return self();
            }

            public B writableStackTrace(boolean writableStackTrace) {
                this.writableStackTrace = writableStackTrace;
                return self();
            }

            public B enableSuppression(boolean enableSuppression) {
                this.enableSuppression = enableSuppression;
                return self();
            }

            public B notification(Integer type, String message) {
                this.notification = message;
                this.notify = type;
                return self();
            }

            public B notification(String message) {
                this.notification = message;
                return self();
            }

            public B message(String message) {
                this.message = message;
                return self();
            }


            public B messageAndNotify(Integer type, String message) {
                this.message = message;
                this.notification = message;
                this.notify = type;
                return self();
            }

            public B messageAndNotify(String message) {
                return messageAndNotify(null, message);
            }

            public B messageAndNotify(String template, Object... args) {
                return messageAndNotify(null, template, args);
            }

            public B messageAndNotify(Integer type, String template, Object... args) {
                val msg = MessageFormatter.arrayFormat(template, args);
                this.notification = msg.getMessage();
                this.message = msg.getMessage();
                this.notify = type;
                if (msg.getThrowable() != null) {
                    this.cause = msg.getThrowable();
                }
                return self();
            }

            public B notify(Integer type, String template, Object... args) {
                val msg = MessageFormatter.arrayFormat(template, args);
                this.notification = msg.getMessage();
                this.notify = type;
                if (msg.getThrowable() != null) {
                    this.cause = msg.getThrowable();
                }
                return self();
            }

            public B notify(String template, Object... args) {
                return notify(null, template, args);
            }

            public B message(String template, Object... args) {
                val msg = MessageFormatter.arrayFormat(template, args);
                this.message = msg.getMessage();
                if (msg.getThrowable() != null) {
                    this.cause = msg.getThrowable();
                }
                return self();
            }

            public B code(int code) {
                this.code = code;
                return self();
            }

            protected abstract B self();

            public abstract C build();

            public String toString() {
                return "CommonError.CommonExceptionBuilder(super=" +
                    super.toString() +
                    ", message=" + this.message +
                    ", code=" + this.code +
                    ", alertType=" + this.notify +
                    ", alert=" + this.notification + ")";
            }
        }


        static <E extends CommonError, B extends CommonErrorBuilder<E, ?>>
        E buildMessage(B builder, String message, Object... args) {
            return builder.message(message, args).build();
        }

        static <E extends CommonError, B extends CommonErrorBuilder<E, ?>>
        E buildNotify(B builder, Integer type, String message, Object... args) {
            return builder.messageAndNotify(type, message, args).build();
        }
    }

    @SuperBuilder
    final class InternalError extends CommonError {
        public static InternalError message(String pattern, Object... args) {
            return buildMessage(builder().code(DEFAULT_CODE.get()), pattern, args);
        }

        public static InternalError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(DEFAULT_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class AlreadyExistsError extends CommonError {
        public static AlreadyExistsError message(String pattern, Object... args) {
            return buildMessage(builder().code(ALREADY_EXISTS_CODE.get()), pattern, args);
        }

        public static AlreadyExistsError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(ALREADY_EXISTS_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class NotExistsError extends CommonError {
        public static NotExistsError message(String pattern, Object... args) {
            return buildMessage(builder().code(NOT_EXISTS_CODE.get()), pattern, args);
        }

        public static NotExistsError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(NOT_EXISTS_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class ConflictError extends CommonError {
        public static ConflictError message(String pattern, Object... args) {
            return buildMessage(builder().code(CONFLICT_CODE.get()), pattern, args);
        }

        public static ConflictError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(CONFLICT_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class InvalidError extends CommonError {
        public static InvalidError message(String pattern, Object... args) {
            return buildMessage(builder().code(INVALID_CODE.get()), pattern, args);
        }

        public static InvalidError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(INVALID_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class GoneError extends CommonError {
        public static GoneError message(String pattern, Object... args) {
            return buildMessage(builder().code(GONE_CODE.get()), pattern, args);
        }

        public static GoneError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(GONE_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class TimeoutError extends CommonError {
        public static TimeoutError message(String pattern, Object... args) {
            return buildMessage(builder().code(TIMEOUT_CODE.get()), pattern, args);
        }

        public static TimeoutError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(TIMEOUT_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class IllegalError extends CommonError {
        public static IllegalError message(String pattern, Object... args) {
            return buildMessage(builder().code(LEGAL_REASON_CODE.get()), pattern, args);
        }

        public static IllegalError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(LEGAL_REASON_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class NotAllowedError extends CommonError {
        public static NotAllowedError message(String pattern, Object... args) {
            return buildMessage(builder().code(NOT_ALLOWED_CODE.get()), pattern, args);
        }

        public static NotAllowedError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(NOT_ALLOWED_CODE.get()), type, pattern, args);
        }
    }

    @SuperBuilder
    final class ForbiddenError extends CommonError {
        public static ForbiddenError message(String pattern, Object... args) {
            return buildMessage(builder().code(FORBIDDEN_CODE.get()), pattern, args);
        }

        public static ForbiddenError notify(Integer type, String pattern, Object... args) {
            return buildNotify(builder().code(FORBIDDEN_CODE.get()), type, pattern, args);
        }
    }


    static AlreadyExistsError alreadyExists(String pattern, Object... args) {
        return AlreadyExistsError.message(pattern, args);
    }

    static NotExistsError notExists(String pattern, Object... args) {
        return NotExistsError.message(pattern, args);
    }

    static ConflictError conflict(String pattern, Object... args) {
        return ConflictError.message(pattern, args);
    }

    static InvalidError invalid(String pattern, Object... args) {
        return InvalidError.message(pattern, args);
    }

    static AlreadyExistsError alreadyExistsNotify(String pattern, Object... args) {
        return AlreadyExistsError.notify(null, pattern, args);
    }

    static NotExistsError notExistsNotify(String pattern, Object... args) {
        return NotExistsError.notify(null, pattern, args);
    }

    static ConflictError conflictNotify(String pattern, Object... args) {
        return ConflictError.notify(null, pattern, args);
    }

    static InvalidError invalidNotify(String pattern, Object... args) {
        return InvalidError.notify(null, pattern, args);
    }

    static AlreadyExistsError alreadyExistsNotify(Integer type, String pattern, Object... args) {
        return AlreadyExistsError.notify(type, pattern, args);
    }

    static NotExistsError notExistsNotify(Integer type, String pattern, Object... args) {
        return NotExistsError.notify(type, pattern, args);
    }

    static ConflictError conflictNotify(Integer type, String pattern, Object... args) {
        return ConflictError.notify(type, pattern, args);
    }

    static InvalidError invalidNotify(Integer type, String pattern, Object... args) {
        return InvalidError.notify(type, pattern, args);
    }

    static GoneError gone(String pattern, Object... args) {
        return GoneError.message(pattern, args);
    }

    static GoneError goneNotify(Integer type, String pattern, Object... args) {
        return GoneError.notify(type, pattern, args);
    }

    static GoneError goneNotify(String pattern, Object... args) {
        return GoneError.notify(null, pattern, args);
    }

    static TimeoutError timeout(String pattern, Object... args) {
        return TimeoutError.message(pattern, args);
    }

    static TimeoutError timeoutNotify(Integer type, String pattern, Object... args) {
        return TimeoutError.notify(type, pattern, args);
    }

    static TimeoutError timeoutNotify(String pattern, Object... args) {
        return TimeoutError.notify(null, pattern, args);
    }

    static IllegalError illegal(String pattern, Object... args) {
        return IllegalError.message(pattern, args);
    }

    static IllegalError illegalNotify(Integer type, String pattern, Object... args) {
        return IllegalError.notify(type, pattern, args);
    }

    static IllegalError illegalNotify(String pattern, Object... args) {
        return IllegalError.notify(null, pattern, args);
    }

    static NotAllowedError notAllowed(String pattern, Object... args) {
        return NotAllowedError.message(pattern, args);
    }

    static NotAllowedError notAllowedNotify(Integer type, String pattern, Object... args) {
        return NotAllowedError.notify(type, pattern, args);
    }

    static NotAllowedError notAllowedNotify(String pattern, Object... args) {
        return NotAllowedError.notify(null, pattern, args);
    }

    static ForbiddenError forbidden(String pattern, Object... args) {
        return ForbiddenError.message(pattern, args);
    }

    static ForbiddenError forbiddenNotify(Integer type, String pattern, Object... args) {
        return ForbiddenError.notify(type, pattern, args);
    }

    static ForbiddenError forbiddenNotify(String pattern, Object... args) {
        return ForbiddenError.notify(null, pattern, args);
    }

}
