import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class InterfazCombate extends JFrame {

    private ClientePokemon cliente;
    private ArrayList<Pokemon> equipoJugador;
    private ArrayList<Pokemon> equipoRival;

    private Pokemon pokemonActivo;
    private Pokemon pokemonActivoRival;

    private JLabel textoVidaJugador, textoVidaEnemigo;
    private JPanel hudJugador, hudEnemigo;
    private JProgressBar barraVidaJugador, barraVidaEnemigo;
    private JPanel campoPanel, habilidadesPanel, pokemonsPanel;
    private JButton[] habilidad;
    private ArrayList<Habilidad> ListaHabilidades;

    public InterfazCombate(ClientePokemon cliente) {
        this.habilidad = new JButton[4];
        this.cliente = cliente;

        this.ListaHabilidades = new ArrayList<>();
        Conexion.obtenerHabilidad(this.ListaHabilidades);

        configurarVentanaPrincipal();

    }

    public void setterPokemons() {
        this.pokemonActivo = Pokemon.darprimerPokemon(equipoJugador);
        this.pokemonActivoRival = Pokemon.darprimerPokemon(equipoRival);
    }

    public void componentesInterfaz() {
        inicializarPaneles();
        configurarCampo();
        configurarPokemons();
        configurarHabilidades();

        // Añadir paneles principales a la ventana
        add(campoPanel);
        add(pokemonsPanel);
        add(habilidadesPanel);
    }

    private void configurarVentanaPrincipal() {
        setTitle("Combate Pokémon");
        setSize(1016, 835);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Diseño absoluto para mayor control
    }

    private void inicializarPaneles() {
        // Crear y configurar paneles principales
        campoPanel = new JPanel(null);
        pokemonsPanel = new JPanel(null);
        habilidadesPanel = new JPanel(null);

        campoPanel.setBounds(0, 0, 1000, 500);
        pokemonsPanel.setBounds(0, 500, 1000, 100);
        habilidadesPanel.setBounds(0, 600, 1000, 200);

        pokemonsPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, Color.BLACK));
    }

    private void configurarCampo() {
        // Añadir fondo al campo
        ImageIcon fondoImagen = new ImageIcon("src/img/Fondo6.jpg");
        Image fondoEscalado = fondoImagen.getImage().getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        JLabel fondo = new JLabel(new ImageIcon(fondoEscalado));
        fondo.setBounds(0, 0, 1000, 500);
        campoPanel.add(fondo);

        // Crear HUDs una sola vez
        hudJugador = crearHUD(pokemonActivo.getNombre(), pokemonActivo.getVida(), pokemonActivo.getVidaMaxima(), true);
        hudJugador.setBounds(360, 350, 300, 100);
        campoPanel.add(hudJugador);

        // Imagen del Pokémon del jugador (sin borde)
        JLabel imagenJugador = cargarImagen("src/img/"+ pokemonActivo.getNombre() +".png", 200, 200, false);
        imagenJugador.setBounds(160, 326, 250, 190);
        campoPanel.add(imagenJugador);

        // Crear HUD enemigo una sola vez
        hudEnemigo = crearHUD(pokemonActivoRival.getNombre(), pokemonActivoRival.getVida(), 
            pokemonActivoRival.getVidaMaxima(), false);
        hudEnemigo.setBounds(350, 50, 300, 100);
        campoPanel.add(hudEnemigo);

        // Imagen del Pokémon enemigo (sin borde)
        JLabel imagenEnemigo = cargarImagen("src/img/"+ pokemonActivoRival.getNombre() +".png", 200, 200, false);
        imagenEnemigo.setBounds(650, 140, 250, 190);
        campoPanel.add(imagenEnemigo);

        // Asegurar que el fondo se muestre detrás de todo
        campoPanel.setComponentZOrder(fondo, campoPanel.getComponentCount() - 1);
    }

    private JPanel crearHUD(String nombre, int vida, int vidaMaxima, boolean esJugador) {
        // Crear panel de HUD persistente
        JPanel hud = new JPanel(null);
        hud.setOpaque(true);
        hud.setBackground(Color.WHITE);

        JLabel nombrePokemon = new JLabel(nombre);
        nombrePokemon.setBounds(10, 10, 200, 20);
        nombrePokemon.setFont(new Font("Arial", Font.BOLD, 18));
        nombrePokemon.setForeground(Color.BLACK);

        JProgressBar barraVida = new JProgressBar(0, vidaMaxima);
        barraVida.setValue(vida);
        barraVida.setBounds(10, 40, 200, 15);
        barraVida.setForeground(Color.GREEN);
        barraVida.setBackground(Color.LIGHT_GRAY);

        JLabel textoVida = new JLabel();
        actualizarTextoVida(textoVida, vida, vidaMaxima);
        textoVida.setBounds(10, 60, 200, 20);
        textoVida.setFont(new Font("Arial", Font.BOLD, 15));
        textoVida.setForeground(Color.BLACK);

        if (esJugador) {
            barraVidaJugador = barraVida;
            textoVidaJugador = textoVida;
        } else {
            barraVidaEnemigo = barraVida;
            textoVidaEnemigo = textoVida;
        }

        hud.setBorder(BorderFactory.createMatteBorder(4, 2, 4, 2, Color.BLACK));
        hud.add(nombrePokemon);
        hud.add(barraVida);
        hud.add(textoVida);

        return hud;
    }

    private void configurarPokemons() {
        // Configurar el panel de Pokémon del jugador
        pokemonsPanel.setLayout(new GridLayout(1, 3, 5, 0)); // 1 fila, 3 columnas

        ArrayList<Pokemon> listaJugador = equipoJugador;

        // Crear etiquetas para los Pokémon y añadirlas al panel con bordes
        for (Pokemon p : listaJugador) {
            pokemonsPanel.add(cargarImagen("src/img/"+ p.getNombre() +".png", 100, 100, true));
        }
    }


    private void configurarHabilidades() {
        // Configurar el panel de habilidades del jugador
        habilidadesPanel.setLayout(new GridLayout(2, 2, 5, 3)); // 1 fila, 4 columnas

        for (int i = 0; i < 4; i++) {
            habilidad[i] = new JButton("Habilidad " + (i + 1));
            habilidad[i].setFont(new Font("Arial", Font.BOLD, 23));
            habilidad[i].setVisible(true);

            habilidad[i].addActionListener(e -> {
                JButton botonPresionado = (JButton) e.getSource();
                Integer idHabilidad = (Integer) botonPresionado.getClientProperty("idHabilidad");
                if (idHabilidad != null) {
                    cliente.enviarHabilidad(idHabilidad);
                    aplicarDaño(pokemonActivoRival, ListaHabilidades.get(idHabilidad - 1));
                }
            });

            habilidadesPanel.add(habilidad[i]);
        }
    }

    private JLabel cargarImagen(String ruta, int ancho, int alto, boolean conBorde) {
        ImageIcon icono = new ImageIcon(new ImageIcon(ruta).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
        JLabel etiqueta = new JLabel(icono);
        etiqueta.setHorizontalAlignment(JLabel.CENTER);

        if (conBorde) {
            etiqueta.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 4, Color.BLACK));
        }

        return etiqueta;
    }

    public void cambiarPokemonActual() {
        // Cambiar el Pokémon activo
        this.pokemonActivo = Pokemon.darprimerPokemon(equipoJugador);
        this.pokemonActivoRival = Pokemon.darprimerPokemon(equipoRival);

        // Configurar el campo con el nuevo Pokémon
        configurarCampo();

        // Verificar que las barras de vida no sean null
        if (barraVidaJugador != null && barraVidaEnemigo != null) {
            // Actualizar las barras de vida
            actualizarHUDVida(pokemonActivo, barraVidaJugador);  // Actualiza la barra de vida del jugador
            actualizarHUDVida(pokemonActivoRival, barraVidaEnemigo);  // Actualiza la barra de vida del enemigo
        } else {
            System.out.println("Error: Las barras de vida no están inicializadas correctamente.");
        }

        // Actualizar habilidades del Pokémon en los botones
        for (int i = 0; i < 4; i++) {
            if (i < pokemonActivo.getHabilidades().size()) {
                Habilidad habilidadPokemon = pokemonActivo.getHabilidades().get(i);
                habilidad[i].setText(habilidadPokemon.getId() + habilidadPokemon.getNombreAtaque());
                habilidad[i].putClientProperty("idHabilidad", habilidadPokemon.getId());
                habilidad[i].setVisible(true);
            } else {
                habilidad[i].setVisible(false);  // Ocultar botones vacíos si el Pokémon tiene menos de 4 habilidades
            }
        }

        // Revalidar y repintar para asegurarnos de que los cambios se reflejan
        campoPanel.revalidate();
        campoPanel.repaint();
        habilidadesPanel.revalidate();
        habilidadesPanel.repaint();
    }


    public void meterEquipos (ArrayList<Pokemon> equipoJugador, ArrayList<Pokemon> equipoRival) {
        this.equipoJugador = equipoJugador;
        this.equipoRival = equipoRival;

        this.pokemonActivo = Pokemon.obtenerPokemonActivo(equipoJugador);
        this.pokemonActivoRival = Pokemon.obtenerPokemonActivo(equipoRival);
    }



    private void actualizarHUDVida(Pokemon pokemon, JProgressBar barraVida) {
        int vidaActual = pokemon.getVida();
        int vidaMaxima = pokemon.getVidaMaxima();

        // Actualizar barra de vida
        barraVida.setValue(vidaActual);
        barraVida.setMaximum(vidaMaxima);

        // Actualizar texto correspondiente
        JLabel textoVida = (barraVida == barraVidaJugador) ? textoVidaJugador : textoVidaEnemigo;
        actualizarTextoVida(textoVida, vidaActual, vidaMaxima);

        // Revalidar y repintar para asegurar los cambios
        barraVida.revalidate();
        barraVida.repaint();
    }

    private void actualizarTextoVida(JLabel textoVida, int vidaActual, int vidaMaxima) {
        textoVida.setText(vidaActual + " / " + vidaMaxima + " ps");
    }

    protected void aplicarDaño(Pokemon defensor, Habilidad habilidad) {
        if (defensor.equals(pokemonActivoRival)) {
            int daño = calcularDaño(habilidad, defensor);
            pokemonActivoRival.recibirDano(daño);

            if (pokemonActivoRival.getVida() <= 0) {
                pokemonActivoRival.setVivo(0);

                cambiarPokemonActual();
            } else {
                actualizarHUDVida(pokemonActivoRival, barraVidaEnemigo);
            }
        } else if (defensor.equals(pokemonActivo)) {
            int daño = calcularDaño(habilidad, defensor);
            pokemonActivo.recibirDano(daño);

            if (pokemonActivo.getVida() <= 0) {
                pokemonActivo.setVivo(0);

                cambiarPokemonActual();
            } else {
                actualizarHUDVida(pokemonActivo, barraVidaJugador);
            }
        }
    }

    public void darPokemons() {
        System.out.println(pokemonActivo.getNombre() + pokemonActivo.getVida());
        System.out.println(pokemonActivoRival.getNombre() + pokemonActivoRival.getVida());
    }

    private int calcularDaño(Habilidad habilidad, Pokemon defensor) {
        int daño = habilidad.getDanio();
        if (habilidad.getTipo().equals(defensor.getDebilidad())) {
            daño *= 2;
        }
        return daño;
    }
}
