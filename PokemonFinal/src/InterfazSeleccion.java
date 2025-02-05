

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class InterfazSeleccion extends JFrame {

    static JLabel textoDatos = new JLabel("Selecciona 3 Pokémons para comenzar el combate!", JLabel.LEFT);

    public InterfazSeleccion(ClientePokemon clientePokemon) {
        setTitle("Eleccion de pokemon");
        setSize(1000, 835);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Panel principal (Pantalla Superior)
        JPanel PantallaS = new JPanel(null);
        PantallaS.setBounds(0, 0, 1000, 500);
        PantallaS.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.black));
        add(PantallaS);

        // Fondo del panel superior
        ImageIcon fondoImagen = new ImageIcon("src\\img\\Fondo7.png"); // Ruta de la imagen de fondo
        Image fondoEscalado = fondoImagen.getImage().getScaledInstance(1000, 500, Image.SCALE_SMOOTH);
        JLabel fondo = new JLabel(new ImageIcon(fondoEscalado));
        fondo.setBounds(0, 0, 1000, 500);
        PantallaS.add(fondo);

        // Panel para mostrar las imágenes de los Pokémon
        JPanel panelImagenes = new JPanel();
        panelImagenes.setLayout(new GridLayout(3, 6)); // Ajusta las filas y columnas
        panelImagenes.setBounds(50, 40, 900, 400); // Posición y tamaño ajustable
        panelImagenes.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente

        // Lista de Pokémon e imágenes
        String[] nombresPokemons = {
                "Aggron", "Alakazam", "Articuno", "Blastoise", "Butterfree", "Charizard",
                "Dragonite", "Garchomp", "Gengar", "Machamp", "Nidoking", "Onix",
                "Pidgeot", "Raichu", "Snorlax", "Umbreon", "Venusaur"
        };
        String[] imagenesPokemons = {
                "src\\img\\Aggron.png", "src\\img\\Alakazam.png", "src\\img\\Articuno.png",
                "src\\img\\Blastoise.png", "src\\img\\Butterfree.png", "src\\img\\Charizard.png",
                "src\\img\\Dragonite.png", "src\\img\\Garchomp.png", "src\\img\\Gengar.png",
                "src\\img\\Machamp.png", "src\\img\\Nidoking.png", "src\\img\\Onix.png",
                "src\\img\\Pidgeot.png", "src\\img\\Raichu.png", "src\\img\\Snorlax.png",
                "src\\img\\Umbreon.png", "src\\img\\Venusaur.png"
        };

        // Añadir imágenes y nombres al panel
        for (int i = 0; i < nombresPokemons.length; i++) {
            // Panel individual para cada Pokémon
            JPanel panelPokemon = new JPanel(null);
            panelPokemon.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente

            // Imagen del Pokémon
            JLabel labelImagen = new JLabel();
            ImageIcon imageIcon = new ImageIcon(imagenesPokemons[i]);
            ImageIcon iconoEscalado = new ImageIcon(imageIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
            labelImagen.setIcon(iconoEscalado);
            labelImagen.setBounds(5, 5, 90, 90); // Posición dentro del panel

            // Nombre del Pokémon
            JLabel labelNombre = new JLabel(nombresPokemons[i], JLabel.CENTER);
            labelNombre.setFont(new Font("Monospaced", Font.BOLD, 12)); // Fuente estilo retro
            labelNombre.setBounds(0, 100, 100, 20); // Justo debajo de la imagen

            // Añadir imagen y nombre al panel individual
            panelPokemon.add(labelImagen);
            panelPokemon.add(labelNombre);

            // Añadir el panel del Pokémon al panel de imágenes
            panelImagenes.add(panelPokemon);
        }

        // Añadir el panel de imágenes al panel superior (PantallaS)
        PantallaS.add(panelImagenes);

        // Asegurar que el fondo esté detrás de todo
        PantallaS.setComponentZOrder(fondo, PantallaS.getComponentCount() - 1);

        // Panel de Datos (Cuadro de texto estilo Pokémon)
        JPanel Datos = new JPanel(null);
        Datos.setBounds(0, 500, 1000, 100);
        Datos.setBackground(Color.WHITE);
        Datos.setBorder(new LineBorder(Color.BLACK, 8, true)); // Borde grueso negro con esquinas redondeadas

        // Etiqueta de texto dentro del panel de Datos
        textoDatos.setFont(new Font("Monospaced", Font.BOLD, 18));
        textoDatos.setForeground(Color.BLACK);
        textoDatos.setBounds(30, 30, 940, 40);
        Datos.add(textoDatos);

        add(Datos);

        JPanel MeterDatos = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        MeterDatos.setBounds(0, 600, 1000, 100);
        MeterDatos.setBackground(new Color(0, 0, 0, 0));

        add(MeterDatos);


        // Botón de enviar
        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.setBounds(850, 30, 100, 30);
        botonEnviar.setFont(new Font("Monospaced", Font.BOLD, 14));
        botonEnviar.setBackground(Color.LIGHT_GRAY);
        botonEnviar.setBorder(new LineBorder(Color.BLACK, 2));
        MeterDatos.add(botonEnviar);

        // Configurar el área de texto
        JTextArea textArea = new JTextArea();
        textArea.setBounds(30, 30, 800, 30);
        textArea.setBorder(new LineBorder(Color.BLACK, 2));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    e.consume(); // Evita que se cree una nueva línea
                    botonEnviar.doClick(); // Simula el clic del botón "Enviar"
                }
            }
        });
        MeterDatos.add(textArea);

        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoIngresado = textArea.getText();
                clientePokemon.enviarPokemon(textoIngresado); // Enviar el mensaje al cliente
                textArea.setText("");
            }
        });
    }

    public static void updateTextoDatos(String message){
        SwingUtilities.invokeLater(() -> {
            textoDatos.setText(message);
        });
    }
}

