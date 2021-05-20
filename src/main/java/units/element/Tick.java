package units.element;

import units.util.Groups;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static units.element.Tick.Ticks.fromUTC;

/**
 * this is compact format of C# ticks
 *
 * @author Zen.Liu
 * @apiNote
 * @since 2021-01-17
 */
public interface Tick {
    long toLong();

    Instant toInstant();

    static Tick now() {
        return new Ticks(Ticks.fromNowUTC());
    }

    static Tick from(Instant instant) {
        return new Ticks(fromUTC(instant));
    }

    static Tick of(long tick) {
        return new Ticks(tick);
    }

    final class Ticks implements Tick {
        static final long KindUtc = 0x4000000000000000L;//1
        static final long KindLocal = 0x8000000000000000L;//2
        static final long TicksMask = 0x3FFFFFFFFFFFFFFFL;
        static final int KindShift = 62;
        static final int tickPerSec = 10_000_000;
        static final long base = -62135596800L;

        /**
         * convert ticks to Instant
         *
         * @param ticks long from C# DateTime.toBinary()
         * @return (timestamp, isLocalTime)
         */
        static Map.Entry<Instant, Boolean> from(long ticks) {
            final long flag = ticks >>> KindShift;
            final long tick = ticks & TicksMask;
            final long sec = tick / tickPerSec;
            final long nano = (tick % tickPerSec) * 100;
            return Groups.entryOf(Instant.ofEpochSecond(sec + base, nano), flag == 2L);
        }

        /**
         * create ticks from instant
         *
         * @param instant source instant
         * @param isLocal dose use local flag
         * @return ticks
         */
        static long from(Instant instant, boolean isLocal) {
            long nano = instant.getNano() / 100;
            long sec = (instant.getEpochSecond() - base) * tickPerSec;
            long tick = sec + nano;
            return tick | (isLocal ? KindLocal : KindUtc);
        }

        static long fromUTC(Instant instant) {
            long nano = instant.getNano() / 100;
            long sec = (instant.getEpochSecond() - base) * tickPerSec;
            long tick = sec + nano;
            return tick | KindUtc;
        }

        static long fromNowUTC() {
            return from(Instant.now(), false);
        }

        static Duration between(long firstUTC, long secondUTC) {
            final Instant f = from(firstUTC).getKey();
            final Instant s = from(secondUTC).getKey();
            return Duration.between(f, s);
        }

        static Duration betweenNow(Long firstUTC) {
            final Instant f = from(firstUTC).getKey();
            return Duration.between(f, Instant.now());
        }

        final long tick;

        Ticks(long tick) {
            this.tick = tick;
        }

        @Override
        public long toLong() {
            return tick;
        }

        @Override
        public Instant toInstant() {
            return from(tick).getKey();
        }
    }


}
