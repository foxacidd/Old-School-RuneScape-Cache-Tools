/**
 * Copyright (c) OpenRS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package osrs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Contains {@link ByteBuffer}-related utility methods.
 *
 * @author Graham
 * @author `Discardedx2
 */
public final class ByteBufferUtils {

    /**
     * The modified set of 'extended ASCII' characters used by the client.
     */
    private static char CHARACTERS[] = { '\u20AC', '\0', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020', '\u2021',
            '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018', '\u2019', '\u201C',
            '\u201D', '\u2022', '\u2013', '\u2014', '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E',
            '\u0178' };

    /**
     * Calculates the CRC32 checksum of the specified buffer.
     *
     * @param buffer
     *            The buffer.
     * @return The CRC32 checksum.
     */
    public static int getCrcChecksum(ByteBuffer buffer) {
        Checksum crc = new CRC32();
        for (int i = 0; i < buffer.limit(); i++) {
            crc.update(buffer.get(i));
        }
        return (int) crc.getValue();
    }

    /**
     * Gets a null-terminated string from the specified buffer, using a modified
     * ISO-8859-1 character set.
     *
     * @param buf
     *            The buffer.
     * @return The decoded string.
     */
    public static String getString(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while ((b = buf.get()) != 0) {
            if (b >= 127 && b < 160) {
                char curChar = CHARACTERS[b - 128];
                if (curChar == 0) {
                    curChar = 63;
                }

                bldr.append(curChar);
            } else {
                bldr.append((char) b);
            }
        }
        return bldr.toString();
    }


    /**
     * Gets a null-terminated string from the specified buffer, using a modified
     * ISO-8859-1 character set.
     *
     * @param buf
     *            The buffer.
     * @return The decoded string.
     */
    public static String getPrefixedString(ByteBuffer buf) {
        if (buf.get() == 0)
            return getString(buf);

        return null;
    }

    /**
     * Gets a char from the specified buffer, using a modified
     * ISO-8859-1 character set.
     *
     * @param buf
     *            The buffer.
     * @return The decoded string.
     */
    public static char getJagexChar(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        int b = buf.get() & 0xFF;
        if (b >= 127 && b < 160) {
            char curChar = CHARACTERS[b - 128];
            if (curChar == 0) {
                curChar = 63;
            }

            b = curChar;
        }
        return (char) b;
    }

    public static int getUnsignedSmart(ByteBuffer buf) {
        int peek = buf.get(buf.position()) & 0xFF;
        if (peek < 128)
            return buf.get() & 0xFF;
        else
            return (buf.getShort() & 0xFFFF) - 32768;
    }

    public static int getSignedSmart(ByteBuffer buf) {
        int peek = buf.get(buf.position()) & 0xFF;
        if (peek < 128)
            return (buf.get() & 0xFF) - 64;
        else
            return (buf.getShort() & 0xFFFF) - 49152;
    }

    /**
     * Gets a smart integer from the buffer.
     *
     * @param buffer
     *            The buffer.
     * @return The value.
     */
    public static int getSmartInt(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) < 0)
            return buffer.getInt() & 0x7fffffff;
        return buffer.getShort() & 0xFFFF;
    }

    /**
     * Gets a small smart integer from the buffer.
     *
     * @param buffer
     *            The buffer.
     * @return The value.
     */
    public static int getSmallSmartInt(ByteBuffer buffer) {
        if ((buffer.get(buffer.position()) & 0xff) < 128) {
            return (buffer.get() & 0xff) - 1;
        }
        int shortValue = buffer.getShort() & 0xFFFF;
        return shortValue - 32769;
    }

    /**
     * Reads a 'tri-byte' from the specified buffer.
     *
     * @param buf
     *            The buffer.
     * @return The value.
     */
    public static int getMedium(ByteBuffer buf) {
        return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
    }


    /**
     * Writes a 'tri-byte' to the specified buffer.
     *
     * @param buf
     *            The buffer.
     * @param value
     *            The value.
     */
    public static void putMedium(ByteBuffer buf, int value) {
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
    }

    /**
     * Converts the contents of the specified byte buffer to a string, which is
     * formatted similarly to the output of the {@link Arrays#toString()}
     * method.
     *
     * @param buffer
     *            The buffer.
     * @return The string.
     */
    public static String toString(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < buffer.limit(); i++) {
            String hex = Integer.toHexString(buffer.get(i) & 0xFF).toUpperCase();
            if (hex.length() == 1)
                hex = "0" + hex;

            builder.append("0x").append(hex);
            if (i != buffer.limit() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static void putString317(ByteBuffer buffer, String val) {
        buffer.put(val.getBytes());
        buffer.put((byte) 10);
    }

    public static ByteBuffer clone(final ByteBuffer original) {
        // Create clone with same capacity as original.
        final ByteBuffer clone = (original.isDirect()) ?
                ByteBuffer.allocateDirect(original.capacity()) :
                ByteBuffer.allocate(original.capacity());

        // Create a read-only copy of the original.
        // This allows reading from the original without modifying it.
        final ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();

        // Read from the original.
        clone.put(readOnlyCopy);

        return clone;
    }

    public static void putVarInt(ByteBuffer buffer, int val) {
        if ((val & 0xFFFFFF80) != 0) {
            if ((val & 0xFFFFC000) != 0) {
                if ((val & 0xFFE00000) != 0) {
                    if ((val & 0xF0000000) != 0) {
                        buffer.put((byte) (val >>> 28 | 0x80));
                    }

                    buffer.put((byte) (val >>> 21 | 0x80));
                }

                buffer.put((byte) (val >>> 14 | 0x80));
            }

            buffer.put((byte) (val >>> 7 | 0x80));
        }

        buffer.put((byte) (val & 0x7F));
    }

    public static int getVarInt(ByteBuffer buffer) {
        byte val = buffer.get();

        int val_;
        for (val_ = 0; val < 0; val = buffer.get()) {
            val_ = (val_ | val & 0x7F) << 7;
        }

        return val_ | val;
    }

    public static void putLengthFromMark(ByteBuffer buffer, int length) {
        int pos = buffer.position();
        buffer.position(pos - length - 4);
        buffer.putInt(length);
        buffer.position(pos);
    }

    public static void skip(ByteBuffer buffer, int skip) {
        buffer.position(buffer.position() + skip);
    }

    /**
     * Default private constructor to prevent instantiation.
     */
    private ByteBufferUtils() {

    }

    public static void clearIntArray(int[] var0, int var1, int var2) {
        for (var2 = var2 + var1 - 7; var1 < var2; var0[var1++] = 0) { // L: 364 365 373
            var0[var1++] = 0; // L: 366
            var0[var1++] = 0; // L: 367
            var0[var1++] = 0; // L: 368
            var0[var1++] = 0; // L: 369
            var0[var1++] = 0; // L: 370
            var0[var1++] = 0; // L: 371
            var0[var1++] = 0; // L: 372
        }

        for (var2 += 7; var1 < var2; var0[var1++] = 0) { // L: 375 376
        }

    }

    public static int iLog(int var0) {
        int var1 = 0; // L: 61
        if (var0 < 0 || var0 >= 65536) { // L: 62
            var0 >>>= 16; // L: 63
            var1 += 16; // L: 64
        }

        if (var0 >= 256) { // L: 66
            var0 >>>= 8; // L: 67
            var1 += 8; // L: 68
        }

        if (var0 >= 16) { // L: 70
            var0 >>>= 4; // L: 71
            var1 += 4; // L: 72
        }

        if (var0 >= 4) { // L: 74
            var0 >>>= 2; // L: 75
            var1 += 2; // L: 76
        }

        if (var0 >= 1) { // L: 78
            var0 >>>= 1; // L: 79
            ++var1; // L: 80
        }

        return var0 + var1; // L: 82
    }

    public static int method5852(int var0, int var1) {
        int var2;
        for (var2 = 1; var1 > 1; var1 >>= 1) { // L: 24 25 28
            if ((var1 & 1) != 0) {
                var2 = var0 * var2; // L: 26
            }

            var0 *= var0; // L: 27
        }

        if (var1 == 1) { // L: 30
            return var0 * var2;
        } else {
            return var2; // L: 31
        }
    }
    public static void putVarIntDos(DataOutputStream dos, int value) throws IOException {
        if ((value & ~0x7f) != 0) {
            if ((value & ~0x3fff) != 0) {
                if ((~0x1fffff & value) != 0) {
                    if ((~0xfffffff & value) != 0) {
                        dos.write(value >>> 28 | 0x80);
                    }
                    dos.write(value >>> 21 | 0x80);
                }
                dos.write(value >>> 14 | 0x80);
            }
            dos.write(value >>> 7 | 0x80);
        }
        dos.write(0x7f & value);
    }

    public static void writeUnsignedSmart(int value, DataOutputStream dataOutputStream) throws IOException {
        if (value < 64 && value >= -64) {
            dataOutputStream.writeByte(value + 64);
            return;
        }
        if (value < 16384 && value >= -16384) {
            dataOutputStream.writeShort(value + 49152);
            return;
        } else {
            System.out.println("Error psmart out of range: " + value);
            return;
        }
    }

    public static void writeSmart(int value, DataOutputStream dataOutputStream) {
        try {
            if (value >= 128) {
                dataOutputStream.writeShort(value + 32768);
            } else {
                dataOutputStream.writeByte(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTriByte(int value, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.write(value >> 16);
        dataOutputStream.write(value >> 8);
        dataOutputStream.write(value);
    }
}