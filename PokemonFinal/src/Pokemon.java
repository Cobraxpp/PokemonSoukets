import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String Nombre;
    private int vida;
    private int vivo;
    private String debilidad;
    private int vidaMaxima;
    private ArrayList<Habilidad> habilidadesPokemon;

    public Pokemon(int id, String nombre, int vida, int vivo, String debilidad) {
        this.id = id;
        Nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.vivo = vivo;
        this.debilidad = debilidad;
        this.habilidadesPokemon = new ArrayList<Habilidad>();
        meterHabilidades(this.habilidadesPokemon, this.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getVivo() {
        return vivo;
    }

    public void setVivo(int vivo) {
        this.vivo = vivo;
    }

    public void recibirDano(int dano) {
        vida -= dano;
    }

    public String getDebilidad() {
        return debilidad;
    }

    public boolean estaVivo() {
        boolean flag = true;
        if (vivo == 0) {
            flag = false;
        }
        return flag;
    }

    public void getDatos() {
        System.out.println(getId() + " " + getNombre() + " " + getVida() + " " + getVivo());
    }

    public static Pokemon meterPokemon(String nombrePokemon) {
        String sql = "select * from pokemon where NOMBRE = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, nombrePokemon);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    int id = resultado.getInt("ID");
                    String nombre = resultado.getString("Nombre");
                    int vida = resultado.getInt("VIDA");
                    int vivo = resultado.getInt("VIVO");
                    String debilidad = resultado.getString("Debilidad");

                    return new Pokemon(id, nombre, vida, vivo, debilidad);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void meterHabilidades(ArrayList<Habilidad> listapokemon, int idPokemon) {
        String sql = "select h.*\n" +
                "from habilidad h\n" +
                "join pokemon_habilidad ph on h.id = ph.habilidad_id\n" +
                "where ph.pokemon_id = ?;";

        try (Connection con = Conexion.getConexion();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, idPokemon);

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    int id = resultado.getInt("ID");
                    String tipo = resultado.getString("Tipo");
                    int danio = resultado.getInt("Ataque");
                    String nombreAtaque = resultado.getString("Nombre_ataque");

                    Habilidad habilidad = new Habilidad(id, tipo, danio, nombreAtaque);
                    listapokemon.add(habilidad);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Pokemon obtenerPokemonActivo(ArrayList<Pokemon> equipo) {
        Pokemon pokemonActual = null;

        for (Pokemon p : equipo) {
            if (p != null && p.estaVivo()) {
                pokemonActual = p;
                break;
            }
        }

        return pokemonActual;
    }

    public ArrayList<Habilidad> getHabilidades() {
        return habilidadesPokemon;
    }

    public static Pokemon darprimerPokemon(ArrayList<Pokemon> Pokemons) {
        for (Pokemon p : Pokemons) {
            if (p != null && p.estaVivo()) {
                return p;
            }
        }

        return null;
    }
}
