import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientePokemon {
    private final Socket socketEquipos;  // Socket para los equipos
    private final Socket socketComunicacion; // Socket para la comunicación normal
    private BufferedReader entrada;
    private PrintWriter salida;
    private InterfazCombate interfazCombate;
    private InterfazSeleccion interfazSeleccion;
    private boolean interfazCreada = false;
    private ArrayList<Pokemon> equipoJugador;
    private ArrayList<Pokemon> equipoRival;
    private ArrayList<Habilidad> ListaHabilidades;


    public ClientePokemon(String host, int puertoEquipos, int puertoComunicacion) throws IOException {
        // Creamos dos sockets: uno para los equipos y otro para la comunicación normal
        socketEquipos = new Socket(host, puertoEquipos); 
        socketComunicacion = new Socket(host, puertoComunicacion);

        this.ListaHabilidades = new ArrayList<>();
        Conexion.obtenerHabilidad(this.ListaHabilidades);

        // Flujos para el socket de comunicación normal
        entrada = new BufferedReader(new InputStreamReader(socketComunicacion.getInputStream()));
        salida = new PrintWriter(socketComunicacion.getOutputStream(), true);

        interfazSeleccion = new InterfazSeleccion(this);
    }

    public void iniciar() {
        try {
            System.out.println("Conectado al servidor. Esperando instrucciones...");
            String mensajeServidor;
            while ((mensajeServidor = entrada.readLine()) != null) {
                procesarMensaje(mensajeServidor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    private void procesarMensaje(String mensaje) throws IOException {
    	//: Método para interpretar y actuar según los mensajes recibidos del servidor.
    	
        System.out.println(mensaje);

        if (mensaje.contains("Escribe el nombre del Pokémon")) {
            SwingUtilities.invokeLater(() -> {
                interfazSeleccion.setVisible(true);
            });
        } else if (mensaje.equals("SELECCION_COMPLETA")) {
            SwingUtilities.invokeLater(() -> {
                interfazSeleccion.updateTextoDatos("Equipo Pokemon completado");

                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        interfazSeleccion.dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            });

            try {
            	//Método para recibir los equipos del jugador y del rival enviados por el servidor.
            	recibirEquipos();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (mensaje.contains("Selecciona el número de la habilidad")) {
            if (!interfazCreada) {
                interfazCombate = new InterfazCombate(this);
                interfazCombate.meterEquipos(equipoJugador, equipoRival);
                interfazCombate.componentesInterfaz(); // Crear la interfaz solo una vez
                SwingUtilities.invokeLater(() -> {
                    interfazCombate.setVisible(true); // Mostrar la interfaz de combate
                });
            }

            // Actualizar los datos de la interfaz
            SwingUtilities.invokeLater(() -> {
                Pokemon pokemonActivo = null;

                // Encuentra el primer Pokémon vivo del equipo jugador
                for (Pokemon p : equipoJugador) {
                    if (p != null && p.estaVivo()) {
                        pokemonActivo = p;
                        break;
                    }
                }

                Pokemon pokemonActivoRival = null;

                // Encuentra el primer Pokémon vivo del equipo jugador
                for (Pokemon p : equipoRival) {
                    if (p != null && p.estaVivo()) {
                        pokemonActivoRival = p;
                        break;
                    }
                }

                System.out.println("Pokemons CLiente");
                System.out.println(pokemonActivo.getNombre()+pokemonActivo.getVida());
                System.out.println(pokemonActivoRival.getNombre()+pokemonActivoRival.getVida());

                System.out.println("Pokemons Interfaz");
                interfazCombate.darPokemons();

                interfazCombate.cambiarPokemonActual();

                interfazCombate.revalidate();
                interfazCombate.repaint();
            });

            interfazCreada = true;
        } else if (mensaje.contains("Habilidad:")) {
            if (!interfazCreada) {
                interfazCombate = new InterfazCombate(this);
                interfazCombate.meterEquipos(equipoJugador, equipoRival);
                interfazCombate.componentesInterfaz(); // Crear la interfaz solo una vez
                SwingUtilities.invokeLater(() -> {
                    interfazCombate.setterPokemons();
                    interfazCombate.setVisible(true); // Mostrar la interfaz de combate
                });
            }

            String contenido = mensaje.substring(mensaje.indexOf(":") + 1).trim();

            try {
                Pokemon pokemonActivo = null;

                // Encuentra el primer Pokémon vivo del equipo jugador
                for (Pokemon p : equipoJugador) {
                    if (p != null && p.estaVivo()) {
                        pokemonActivo = p;
                        break;
                    }
                }

                int habilidad = Integer.parseInt(contenido);
                System.out.println(habilidad);
                interfazCombate.aplicarDaño(pokemonActivo, ListaHabilidades.get(habilidad - 1));

                interfazCreada = true;

            } catch (NumberFormatException e) {
                System.out.println("El valor después de 'Habilidad:' no es un número válido.");
            }
        }
    }

    // Recibir equipos usando ObjectInputStream
    public void recibirEquipos() throws IOException, ClassNotFoundException {
        ObjectInputStream entradaObjeto = new ObjectInputStream(socketEquipos.getInputStream());
        
        equipoJugador = (ArrayList<Pokemon>) entradaObjeto.readObject();
        
        System.out.println("Equipo del jugador recibido");

        equipoRival = (ArrayList<Pokemon>) entradaObjeto.readObject();
        System.out.println("Equipo del rival recibido");
    }

    public void enviarPokemon(String pokemon) {
        salida.println(pokemon);
    }

    public void enviarHabilidad(int habilidad) {
        salida.println(habilidad);
    }

    private void cerrarConexion() {
        try {
            socketComunicacion.close();  // Cerramos el socket de comunicación
            entrada.close();
            salida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ClientePokemon cliente = new ClientePokemon("192.168.1.88", 12346, 12345);
            cliente.iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
