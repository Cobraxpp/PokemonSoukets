import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Jugador extends Thread {
    private int id;
    private Socket socket;
    BufferedReader entrada;
    PrintWriter salida;
    private ArrayList<Pokemon> pokemonEquipo;
    private ArrayList<String> pokemon = new ArrayList<>();

    public Jugador(int id, Socket socket) throws IOException {
        this.id = id;
        this.socket = socket;
        this.pokemonEquipo = new ArrayList<Pokemon>();
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            salida.println("BIENVENIDO_JUGADOR_" + id);
            salida.println("INSTRUCCION: Selecciona 3 Pokémon para tu equipo:");
            meterListaPokemon(pokemon);

            salida.println("Escribe el nombre del Pokémon:");
            while (pokemonEquipo.size() < 3) {
                String nombrePokemon = entrada.readLine();
                if (pokemon.contains(nombrePokemon) && !esPokemonRepetido(nombrePokemon)) {
                    Pokemon p = Pokemon.meterPokemon(nombrePokemon);
                    pokemonEquipo.add(p);
                    salida.println("OK: " + nombrePokemon + " añadido a tu equipo.");
                    if (pokemonEquipo.size() < 3) {
                        salida.println("Escribe el nombre del Pokémon:");
                    }
                } else {
                    salida.println("Pokémon no encontrado. Inténtalo de nuevo.");
                    salida.println("Escribe el nombre del Pokémon:");
                }
            }
            salida.println("SELECCION_COMPLETA");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Pokemon> getPokemonEquipo() {
        return pokemonEquipo;
    }

    public synchronized void enviarMensaje(String mensaje) {
        salida.println(mensaje);
        salida.flush();
    }

    public synchronized String esperarRespuesta() throws IOException {
        String respuesta = entrada.readLine();
        if (respuesta == null) {
            throw new IOException("Conexión cerrada.");
        }
        return respuesta;
    }

    public void meterListaPokemon(ArrayList<String> listapokemon) {
        String sql = "select nombre from pokemon";

        try (Connection con = Conexion.getConexion();
             PreparedStatement statement = con.prepareStatement(sql)) {

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    String nombre = resultado.getString("Nombre");

                    listapokemon.add(nombre);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean esPokemonRepetido(String nombrePokemon) {
        for (Pokemon p : pokemonEquipo) {
            if (p.getNombre().equalsIgnoreCase(nombrePokemon)) {
                return true;
            }
        }
        return false;
    }
}
