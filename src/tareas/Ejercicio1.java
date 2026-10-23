package tareas;

import java.sql.*;

public class Ejercicio1 {
	public static void main(String[] args) {

        // Propiedades
        String url = "jdbc:mysql://localhost:3306/dbformacion";
        String user = "test";
        String password = "test";

        // Trabajo con base de datos
        Connection conn = null;
        Statement s = null;
        ResultSet rs = null;
        String sql = "SELECT c.id_curso, c.titulo, c.fecha, COUNT(m.dni) AS matriculas, a.capacidad, a.nombre, a.edificio"
        		+ "    FROM dbformacion.cursos c INNER JOIN dbformacion.aulas a ON c.id_aula = a.id_aula"
        		+ "    LEFT JOIN dbformacion.matriculas m ON c.id_curso = m.id_curso"
        		+ "    GROUP BY c.id_curso"
        		+ "    ORDER BY fecha;";
        try {
            conn = DriverManager.getConnection(url, user, password);
            s = conn.createStatement();
            rs = s.executeQuery(sql);
            
            //Titulo del listado
        	System.out.printf("%-5s %-25s %-12s %-15s %-30s %-15s%n",
        	        "ID", "Curso", "Fecha", "Matrículas", "Aula", "Edificio");
        	
                    while(rs.next()){
                        int id_curso = rs.getInt("c.id_curso");
                        String nombre = rs.getString("c.titulo");
                        String fecha = rs.getString("c.fecha");
                        int matriculas = rs.getInt("matriculas");
                        int capacidad = rs.getInt("a.capacidad");
                        String aula = rs.getString("a.nombre");
                        String edificio = rs.getString("a.edificio");
                        String total_matriculas = matriculas + "/" + capacidad;
                        System.out.printf("%-5d %-25s %-12s %-15s %-30s %-15s%n", id_curso, nombre, fecha, total_matriculas, aula, edificio);
                    }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (rs != null) rs.close();
                if (s != null) s.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}