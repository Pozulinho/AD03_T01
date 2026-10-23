package tareas;

import java.sql.*;
import java.time.LocalDate;

public class Ejercicio3 {
	// Propiedades
	public static final String url = "jdbc:mysql://localhost:3306/dbformacion";
	public static final String user = "test";
	public static final String password = "test";
	
	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
           String dni = obtenerDni();
            String currentName = obtenerNombre(conn, dni);
            String nombreNuevo = "";
            if (currentName != null) {
                System.out.println(currentName);
                nombreNuevo = currentName;
            } else {
                nombreNuevo = escribirNombre();
			}
            obtenerCursos(conn);
            int idCurso = escribirCodCurso();
            int existeCurso = existeCurso(conn,idCurso);
            String yaMatriculado = yaMatriculado(conn, dni, idCurso);
            
            if (disponibilidad(conn, idCurso)>0 && yaMatriculado == null && existeCurso != 0) {
            	conn.setAutoCommit(false);
            	try {
            	    // Si el alumno es nuevo, lo damos de alta
            	    if (currentName == null) {
            	        nuevoAlumno(conn, dni, nombreNuevo);
            	    }
            	    // Registramos la matrícula
            	    matricular(conn, dni, idCurso);
            	    // Confirmamos ambas operaciones
            	    System.out.println("Matrícula registrada para " + nombreNuevo + " en " + nombreCurso(conn, idCurso) + " correctamente.");
            	    conn.commit();

            	} catch (SQLException e) {
            	    // Si falla el alta o la matrícula, deshacemos la transacción
            	    try {
            	        conn.rollback();
            	    } catch (SQLException ex) {
            	        e.addSuppressed(ex);
            	    }

            	    throw e;
            	}
            }else if (existeCurso == 0) {
            	System.out.println("Este curso no existe.");
            }else if (yaMatriculado != null) {
            	System.out.println("Ya estás matriculado en este curso.");
            }else {
            	System.out.println("No hay plazas libres en este curso.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la base de datos: " + e.getMessage());

        }
    }

	//Solicitud del dni por consola y comprobación de que tiene el formato correcto
    private static String obtenerDni() {
        String dni;
        do {
            System.out.println("Escribe el DNI del alumno que deseas matricular:");
            dni = Escritura.readString();

            if (!dni.matches("[0-9]{8}[A-Za-z]|[A-Za-z][0-9]{8}")) {
                System.out.println("Formato del DNI incorrecto. Debe tener 8 dígitos y una letra. Prueba de nuevo: ");
            }
        } while (!dni.matches("[0-9]{8}[A-Za-z]|[A-Za-z][0-9]{8}"));
        return dni;
    }

    //Nombre del alumno en caso de que sí exista el DNI en la base de datos
    private static String obtenerNombre(Connection conn, String dni) throws SQLException {
        String sql = "SELECT nombre FROM dbformacion.alumnos WHERE dni= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }               
            }
        }
        return null;
    }
    
    //Detalle de cursos en los que podemos matricularnos
    private static void obtenerCursos(Connection conn) throws SQLException {
        String sql = "SELECT c.id_curso, c.titulo, a.capacidad - COUNT(m.dni) AS plazas_libres"
        		+ "    FROM dbformacion.cursos c INNER JOIN dbformacion.aulas a ON c.id_aula = a.id_aula"
        		+ "    LEFT JOIN dbformacion.matriculas m ON c.id_curso = m.id_curso"
        		+ "    GROUP BY c.id_curso"
        		+ "    ORDER BY c.id_curso;";
        
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
        	System.out.printf("%-5s %-25s %-20s %n",
        	        "ID", "Curso", "Plazas Libres");
        	
                    while(rs.next()){
                        int id_curso = rs.getInt("c.id_curso");
                        String nombre = rs.getString("c.titulo");
                        int plazasLibres = rs.getInt("plazas_libres");
                        System.out.printf("%-5s %-25s %-20s %n", id_curso, nombre, "plazas libres: " + plazasLibres);
                    ;
                } 
                }                
            }
    //Solicitud del nombre del alumno nuevo en caso de que no exista el DNI en la base de datos
    private static String escribirNombre() {
        System.out.println("Escribe el nombre del nuevo alumno: ");
        return Escritura.readString();
    }
    
    //Solicitud del id_curso en el que nos queremos matricular
    private static int escribirCodCurso() {
        System.out.println("Introduce el ID del curso en el que te quieres matricular: ");
        return Escritura.readInt();
    }
    
    //Comprobar disponibilidad de los cursos
    private static int disponibilidad(Connection conn, int idCurso) throws SQLException {
        String sql = "SELECT c.id_curso, a.capacidad - COUNT(m.dni) AS plazas_libres"
        		+ "    FROM dbformacion.cursos c INNER JOIN dbformacion.aulas a ON c.id_aula = a.id_aula"
        		+ "    LEFT JOIN dbformacion.matriculas m ON c.id_curso = m.id_curso"
        		+ "		WHERE c.id_curso = ?"
        		+ "    GROUP BY c.id_curso"
        		+ "    ORDER BY c.id_curso;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("plazas_libres");
                }                
            }
        }
        return 0;
    }
    
    //Comprobación de si el curso existe
    private static int existeCurso(Connection conn, int idCurso) throws SQLException {
        String sql = "SELECT c.id_curso"
        		+ "    FROM dbformacion.cursos c"
        		+ "		WHERE c.id_curso = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c.id_Curso");
                }                
            }
        }
        return 0;
    }
    
    //Comprobación de si el alumno ya está matriculado en el curso
    private static String yaMatriculado(Connection conn, String dni, int idCurso) throws SQLException {
        String sql = "SELECT m.id_curso, m.dni"
        		+ "    FROM dbformacion.matriculas m"
        		+ "		WHERE m.dni = ? AND m.id_curso = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            stmt.setInt(2, idCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("m.dni");
                }                
            }
        }
        return null;
    }

    //Inclusión del nuevo alumno en la tabla alumnos
    private static void nuevoAlumno(Connection conn, String dni, String nuevoNombre) throws SQLException {
    	String sql = "INSERT INTO dbformacion.alumnos (dni, nombre) VALUES (?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            stmt.setString(2, nuevoNombre);
            stmt.executeUpdate();
        }
    }
    
    //Inclusión de la matricula en la tabla matrículas
    private static void matricular(Connection conn, String dni, int idCurso) throws SQLException {   
    	String sql = "INSERT INTO dbformacion.matriculas (dni, id_curso, fecha_matricula) VALUES (?, ?, ?);";
    	String fecha = LocalDate.now().toString();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            stmt.setInt(2, idCurso);
            stmt.setString(3, fecha);
            stmt.executeUpdate();
        }
    }
    
    //Obtención del nombre del curso matriculado
    private static String nombreCurso(Connection conn, int idCurso) throws SQLException {
        String sql = "SELECT c.titulo"
        		+ "    FROM dbformacion.cursos c"
        		+ "		WHERE c.id_Curso = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("c.titulo");
                }                
            }
        }
        return null;
     }                

}