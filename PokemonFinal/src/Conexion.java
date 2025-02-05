import java.sql.*;
import java.util.ArrayList;

public class Conexion {
    public static final String url = "jdbc:mysql://localhost:3306/pokemondb";
    public static final String nUsuario = "root";
    public static final String contraseña = "root";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, nUsuario, contraseña);
    }

    public static void obtenerHabilidad(ArrayList<Habilidad> habilidades) {

        String sql = "select * from habilidad";

        try(Connection con = Conexion.getConexion();
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet resultado = statement.executeQuery()){

            while (resultado.next()){
                int id = resultado.getInt("ID");
                String tipo= resultado.getString("Tipo");
                int ataque = resultado.getInt("Ataque");
                String nombre_ataque = resultado.getString("Nombre_ataque");

                Habilidad habilidad = new Habilidad(id,tipo,ataque,nombre_ataque);
                habilidades.add(habilidad);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
