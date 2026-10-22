package tareas;

import java.io.*;

public class Escritura {
	private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static byte readByte() {
        try {
            return Byte.parseByte(in.readLine());
        } catch (IOException e) {
            System.err.println("Error al leer byte: " + e.getMessage());
            return 0;
        }
    }

    public static short readShort() {
        try {
            return Short.parseShort(in.readLine());
        } catch (IOException e) {
            System.err.println("Error al leer short: " + e.getMessage());
            return 0;
        }
    }

    public static int readInt() {
        try {
            return Integer.parseInt(in.readLine());
        } catch (IOException e) {
            System.err.println("Error al leer int: " + e.getMessage());
            return 0;
        }
    }

    public static char readChar() {
        try {
            return (char) in.read();
        } catch (IOException e) {
            System.err.println("Error al leer char: " + e.getMessage());
            return '\0';
        }
    }

    public static String readString() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Error al leer String: " + e.getMessage());
            return "";
	    }
	}
}
