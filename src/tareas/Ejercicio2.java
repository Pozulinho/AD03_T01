package tareas;

import java.sql.*;

public class Ejercicio2 {
	// Propiedades
	public static final String url = "jdbc:mysql://localhost:3306/dbformacion";
	public static final String user = "test";
	public static final String password = "test";
	
	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String dni = obtenerDni();
            String nombreRevisado = obtenerNombre(conn, dni);

            if (nombreRevisado != null) {
                System.out.println(nombreRevisado);
                String nombreNuevo = escribirNombre();

                if (!nombreNuevo.isEmpty()) {
                    updateStudentName(conn, dni, nombreNuevo);
                    System.out.println("Se ha modificado la información correctamente.");
                } else {
                    System.out.println("No ha introducido un nuevo nombre.");
                }
            }
            System.out.println("Fin del programa.");
        } catch (SQLException e) {
            System.err.println("Error en la base de datos: " + e.getMessage());
        }
    }

    private static String obtenerDni() {
        System.out.println("Escribe el DNI del alumno que deseas modificar: ");
        return Escritura.readString();
    }

    private static String obtenerNombre(Connection conn, String dni) throws SQLException {
        String sql = "SELECT nombre FROM dbformacion.alumnos WHERE dni= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                } else {
                	System.out.println("El dni del alumno no existe.");
                }                
            }
        }
        return null;
    }

    private static String escribirNombre() {
        System.out.println("Escribe el nuevo nombre para el alumno (deja en blanco para no modificarlo):");
        return Escritura.readString();
    }

    private static void updateStudentName(Connection conn, String dni, String nuevoNombre) throws SQLException {
        String sql = "UPDATE dbformacion.alumnos SET nombre = ? WHERE dni= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoNombre);
            stmt.setString(2, dni);
            stmt.executeUpdate();
        }
    }
}