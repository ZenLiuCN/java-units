package units.element;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Universal Identity like a BSON ObjectId
 *
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-20
 */
public interface Identity extends Comparable<Identity>, Serializable {
    Instant getInstant();

    long getTick();

    /**
     * @return 0-256
     */
    int getMachine();

    /**
     * @return 0-512
     */
    int getRegion();

    /**
     * @return 0-256
     */
    int getCounter();

    String dump();

    /**
     * @return a 12 bytes array
     */
    byte[] toBytes();

    /**
     * @return a 24 char hex string
     */
    String toHex();

    /**
     * @return a compressed 16 char Identity
     */
    String toIdentity();


    static void setMachine(short m) {
        if (m < 0 || m > 512) throw new IllegalArgumentException("machine code should between 0 to 256");
        Identifier.MACHINE.set(m);
    }

    static void setCounter(short m) {
        if (m < 0 || m > 256) throw new IllegalArgumentException("counter code should between 0 to 256");
        Identifier.COUNTER.set(m);
    }

    static void setRegion(short m) {
        if (m < 0 || m > 512) throw new IllegalArgumentException("region code should between 0 to 512");
        Identifier.REGION.set(m);
    }

    static Identity get() {
        return Identifier.newIdentity();
    }

    static Identity parse(byte[] code) {
        return Identifier.parse(code);
    }

    static Identity parse(String code) {
        return Identifier.parse(code);
    }

    @AllArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
    final class Identifier implements Identity {

        public static final AtomicInteger COUNTER = new AtomicInteger(0);
        public static final AtomicInteger MACHINE = new AtomicInteger(new SecureRandom().nextInt());
        public static final AtomicInteger REGION = new AtomicInteger(new SecureRandom().nextInt());

        private static final int IDENTITY_LENGTH = 12;
        private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        private static final long serialVersionUID = 7526908201753354140L;
        private final long tick;//8
        private final byte region;//1
        private final short machine;//2
        private final byte counter;//1

        @Override
        public Instant getInstant() {
            return Tick.of(tick).toInstant();
        }

        @Override
        public long getTick() {
            return tick;
        }

        @Override
        public int getMachine() {
            return machine & 0xFFFF;
        }

        @Override
        public int getRegion() {
            return region & 0xFF;
        }

        @Override
        public int getCounter() {
            return counter & 0xFF;
        }

        Identifier() {
            this.tick = Tick.now().toLong();
            this.machine = (short) MACHINE.get();
            this.region = (byte) REGION.get();
            this.counter = (byte) COUNTER.accumulateAndGet(127, (p, n) -> p >= n ? -127 : (p + 1));
        }

        @Override
        public byte[] toBytes() {
            final ByteBuffer buffer = ByteBuffer.allocate(IDENTITY_LENGTH);
            buffer.putLong(tick);
            buffer.put(region);
            buffer.putShort(machine);
            buffer.put(counter);
            return buffer.array();
        }

        @Override
        public String toString() {
            return toIdentity();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Identifier)) return false;
            Identifier identity = (Identifier) o;
            return tick == identity.tick
                && getMachine() == identity.getMachine()
                && getCounter() == identity.getRegion()
                && getRegion() == identity.getRegion();
        }

        @Override
        public int hashCode() {
            return Objects.hash(tick, region, machine, counter);
        }

        @Override
        public String dump() {
            return "Identity{" +
                "tick=" + getInstant() + "|" + tick +
                ", machine=" + getMachine() +
                ", center=" + getRegion() +
                ", counter=" + getCounter() +
                '}';
        }

        public String toHex() {
            char[] chars = new char[IDENTITY_LENGTH * 2];
            int i = 0;
            for (byte b : toBytes()) {
                chars[i++] = HEX_CHARS[b >> 4 & 0xF];
                chars[i++] = HEX_CHARS[b & 0xF];
            }
            return new String(chars);
        }

        /**
         * @return 16 byte Identity String
         */

        public String toIdentity() {
            return compress(toHex());
        }

        @Override
        public int compareTo(@NotNull Identity o) {
            if (tick != o.getTick()) return Long.compare(tick, o.getTick());
            if (region != o.getRegion()) return Integer.compare(getRegion(), o.getRegion());
            if (machine != o.getMachine()) return Integer.compare(getMachine(), o.getMachine());
            return Integer.compare(getCounter(), o.getCounter());
        }

        //region Compress
        private static final char[] COMPRESS_HEX_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();

        private static String compress(String hex) {
            if (hex == null || hex.length() != 24) throw new IllegalArgumentException("not a hex identity");
            final char[] chars = hex.toCharArray();
            final StringBuilder sb = new StringBuilder(16);
            for (int i = 0; i < chars.length; i += 3) {
                int pre = charToInt(chars[i]),
                    mid = charToInt(chars[i + 1]),
                    end = charToInt(chars[i + 2]);
                sb.append(intToChar((pre << 2) + (mid >> 2)));
                sb.append(intToChar(((mid & 3) << 4) + end));
            }
            return sb.toString();
        }

        private static String decompress(String hex) {
            if (hex == null || hex.length() != 16) throw new IllegalArgumentException("not a short identity");
            final char[] chars = hex.toCharArray();
            final StringBuilder sb = new StringBuilder(24);
            for (int i = 0; i < chars.length; i += 2) {
                int pre = charToInt(chars[i]),
                    end = charToInt(chars[i + 1]);
                sb.append(intToChar((pre >> 2)));
                sb.append(intToChar(((pre & 3) << 2) + (end >> 4)));
                sb.append(intToChar(end & 15));
            }
            return sb.toString();
        }

        private static int charToInt(char c) {
            int found = -1;
            for (int i = 0; i < COMPRESS_HEX_CHARS.length; ++i) {
                if (COMPRESS_HEX_CHARS[i] == c) {
                    found = i;
                    break;
                }
            }
            if (found < 0) throw new IllegalArgumentException("not valid code : " + c);
            return found;
        }

        private static char intToChar(int c) {
            if (c < 0 || c > 63) throw new IllegalArgumentException("not valid index : " + c);
            return COMPRESS_HEX_CHARS[c];
        }
        //endregion

        public static Identity newIdentity() {
            return new Identifier();
        }

        public static boolean isValid(final String hexString) {
            if (hexString == null) {
                throw new IllegalArgumentException();
            }
            int len = hexString.length();
            if (len == 24) {
                for (int i = 0; i < len; i++) {
                    char c = hexString.charAt(i);
                    if (c >= '0' && c <= '9') {
                        continue;
                    }
                    if (c >= 'a' && c <= 'f') {
                        continue;
                    }
                    if (c >= 'A' && c <= 'F') {
                        continue;
                    }
                    return false;
                }
            } else if (len == 16) {
                for (int i = 0; i < len; i++) {
                    char c = hexString.charAt(i);
                    if (c >= '0' && c <= '9') {
                        continue;
                    }
                    if (c >= 'a' && c <= 'z') {
                        continue;
                    }
                    if (c >= 'A' && c <= 'Z') {
                        continue;
                    }
                    if (c == '-' || c == '_') {
                        continue;
                    }
                    return false;
                }
            } else {
                return false;
            }

            return true;
        }

        public static @Nullable Identity parse(String identity) {
            if (identity == null || (identity.length() != 16 && identity.length() != 24)) return null;
            final String hex;
            if (identity.length() == 16) {
                hex = decompress(identity);
            } else {
                hex = identity;
            }
            ByteBuffer buffer = ByteBuffer.wrap(parseAsByte(hex));
            return Identifier.of(buffer.getLong(), buffer.get(), buffer.getShort(), buffer.get());
        }

        public static @Nullable Identity parse(byte[] identity) {
            if (identity == null || (identity.length != 12)) return null;
            ByteBuffer buffer = ByteBuffer.wrap(identity);
            return Identifier.of(buffer.getLong(), buffer.get(), buffer.getShort(), buffer.get());
        }

        private static byte[] parseAsByte(String code) {
            byte[] b = new byte[IDENTITY_LENGTH];
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) Integer.parseInt(code.substring(i * 2, i * 2 + 2), 16);
            }
            return b;
        }


    }

}
