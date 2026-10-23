package tareas;

import java.sql.*;

public class Ejercicio4 {
	// Propiedades
	public static final String url = "jdbc:mysql://localhost:3306/dbformacion";
	public static final String user = "test";
	public static final String password = "test";
	
	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			obtenerCursos(conn);
			int idCurso = escribirCodCurso();
			int numMatriculas = numeroMatriculados(conn, idCurso);
			System.out.println("Numero de alumnos matriculados: " + numMatriculas);
        } catch (SQLException e) {
            System.err.println("Error en la base de datos: " + e.getMessage());

        }
	}
	
    //Detalle de cursos en los que podemos matricularnos
    private static void obtenerCursos(Connection conn) throws SQLException {
        String sql = "SELECT c.id_curso, c.titulo"
        		+ "    FROM dbformacion.cursos c "
        		+ "    ORDER BY c.id_curso;";       
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
        	System.out.println("Cursos:");
        	
            while(rs.next()){
                int id_curso = rs.getInt("c.id_curso");
                String nombre = rs.getString("c.titulo");
                System.out.printf("%-5s %-25s %n", id_curso + ".", nombre);
            ;
            } 
        }                
    }
    
    //Solicitud del id_curso en el que nos queremos matricular
    private static int escribirCodCurso() {
        System.out.println("Introduce el ID del curso que quieres consultar: ");
        return Escritura.readInt();
    }
    
    //Llamada a la función de SQL para comprobar los matriculados
    private static int numeroMatriculados(Connection conn, int idCurso) {
        try (CallableStatement s = conn.prepareCall("{? = call obtener_numero_matriculados(?)}")){
        	s.setInt(2, idCurso);
        	s.registerOutParameter(1, java.sql.Types.INTEGER);
        	s.execute();
        	return s.getInt(1);
        }catch (SQLException e) {
        }
        return -1;
    }    
    
    
}
