import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/** Small helpers shared by all demos: UTF-8 console output (Arabic labels) and byte dumps. */
public final class ConsoleUtil {

    private ConsoleUtil() {}

    /** Forces System.out / System.err to UTF-8 so the Arabic labels are not garbled. */
    public static void initUtf8() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
            // UTF-8 is always supported
        }
    }

    /** Hex dump, 16 bytes per line. */
    public static String hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(i % 16 == 0 ? '\n' : ' ');
            }
            sb.append(String.format("%02X", data[i] & 0xFF));
        }
        return sb.toString();
    }

    /** Printable view: every non-printable byte is replaced by '.'. */
    public static String printable(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            int c = b & 0xFF;
            sb.append(c >= 32 && c < 127 ? (char) c : '.');
        }
        return sb.toString();
    }
}
