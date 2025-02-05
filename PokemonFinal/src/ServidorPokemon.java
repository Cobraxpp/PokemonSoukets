import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServidorPokemon {
    private ServerSocket serverSocketEquipos; // Socket for teams
    private ServerSocket serverSocketComunicacion; // Socket for normal communication

    public ServidorPokemon(int puertoEquipos, int puertoComunicacion) throws IOException {
        serverSocketEquipos = new ServerSocket(puertoEquipos);
        serverSocketComunicacion = new ServerSocket(puertoComunicacion);
    }

    public void iniciar() {
        try {
            while (true) {
                System.out.println("Esperando jugadores...");
                // First, accept connections for normal communication

                Socket cliente1 = serverSocketComunicacion.accept();
                System.out.println("Jugador 1 conectado");

                Socket cliente2 = serverSocketComunicacion.accept();
                System.out.println("Jugador 2 conectado");

                Jugador jugador1 = new Jugador(1, cliente1);
                Jugador jugador2 = new Jugador(2, cliente2);

                // Start communication in threads
                jugador1.start();
                jugador2.start();
                jugador1.join();
                jugador2.join();

                // Now accept connections for teams through serverSocketEquipos
                System.out.println("Esperando las conexiones de equipos...");
                Socket equipoCliente1 = serverSocketEquipos.accept();
                Socket equipoCliente2 = serverSocketEquipos.accept();

                // Send the teams through the new sockets for teams
                enviarEquipos(equipoCliente1, jugador1.getPokemonEquipo(), jugador2.getPokemonEquipo());
                enviarEquipos(equipoCliente2, jugador2.getPokemonEquipo(), jugador1.getPokemonEquipo());

                // Combat (with standard communication)
                Combate combate = new Combate(jugador1, jugador2);
                new Thread(() -> {
                    System.out.println("Iniciando combate entre Jugador 1 y Jugador 2");
                    try {
                        combate.iniciarCombate();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (serverSocketEquipos != null && !serverSocketEquipos.isClosed()) {
                    serverSocketEquipos.close();
                }
                if (serverSocketComunicacion != null && !serverSocketComunicacion.isClosed()) {
                    serverSocketComunicacion.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send teams through the team socket
    public void enviarEquipos(Socket socketJugador, ArrayList<Pokemon> equipo, ArrayList<Pokemon> equipoRival) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(socketJugador.getOutputStream());
        salida.writeObject(equipo); // Enviar el equipo del jugador
        salida.writeObject(equipoRival); // Enviar el equipo del rival
        salida.flush(); // Asegura que los datos se envíen de inmediato
    }

    public static void main(String[] args) {
        try {
            ServidorPokemon servidor = new ServidorPokemon(12346, 12345);
            servidor.iniciar();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
