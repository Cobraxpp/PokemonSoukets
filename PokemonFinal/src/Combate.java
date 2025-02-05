import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Combate {
    private Jugador jugador1;
    private Jugador jugador2;
    private boolean isCombateActivo;
    private int turnoActual;
    private static ArrayList<Habilidad> ListaHabilidades;
    private static Habilidad habilidadElegida;

    public static Habilidad getListaHabilidades(int index) {
        return Combate.ListaHabilidades.get(index);
    }

    public static void setHabilidadElegida(Habilidad habilidad) {
        Combate.habilidadElegida = habilidad;
    }

    public Combate(Jugador jugador1, Jugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.isCombateActivo = true;
        this.turnoActual = (Math.random() < 0.5) ? 1 : 2;
        this.ListaHabilidades = new ArrayList<>();
        Conexion.obtenerHabilidad(this.ListaHabilidades);
        this.habilidadElegida = null;
    }

    public void iniciarCombate() throws IOException {
        jugador1.enviarMensaje("¡El combate ha comenzado!");
        jugador2.enviarMensaje("¡El combate ha comenzado!");

        while (isCombateActivo) {
            Jugador jugadorActual = (turnoActual == 1) ? jugador1 : jugador2;
            Jugador jugadorOponente = (turnoActual == 1) ? jugador2 : jugador1;

            mostrarEstadoPokemon(jugadorActual, jugadorOponente);
            mostrarHabilidadesDisponibles(jugadorActual);
            procesarTurno(jugadorActual, jugadorOponente);

            if (!hayPokemonVivos(jugadorActual.getPokemonEquipo())) {
                finalizarCombate(jugadorOponente, jugadorActual);
                return;
            }

            turnoActual = (turnoActual == 1) ? 2 : 1;
        }
    }

    private void mostrarEstadoPokemon(Jugador jugadorActual, Jugador jugadorOponente) {
        StringBuilder estado = new StringBuilder("Estado actual:\n");
        estado.append("Tu Pokémon: \n");
        for (Pokemon p : jugadorActual.getPokemonEquipo()) {
            if (p != null && p.estaVivo()) {
                estado.append(p.getNombre()).append(" - Vida: ").append((int)(p.getVida() * 100.0 / p.getVidaMaxima())).append("%\n");
            }
        }
        estado.append("\nPokémon rival: \n");
        for (Pokemon p : jugadorOponente.getPokemonEquipo()) {
            if (p != null && p.estaVivo()) {
                estado.append(p.getNombre()).append(" - Vida: ").append((int)(p.getVida() * 100.0 / p.getVidaMaxima())).append("%\n");
            }
        }
        jugadorActual.enviarMensaje(estado.toString());

        StringBuilder estado2 = new StringBuilder("Estado actual:\n");
        estado2.append("Tu Pokémon: \n");
        for (Pokemon p : jugadorOponente.getPokemonEquipo()) {
            if (p != null && p.estaVivo()) {
                estado2.append(p.getNombre()).append(" - Vida: ").append((int)(p.getVida() * 100.0 / p.getVidaMaxima())).append("%\n");
            }
        }
        estado2.append("\nPokémon rival: \n");
        for (Pokemon p : jugadorActual.getPokemonEquipo()) {
            if (p != null && p.estaVivo()) {
                estado2.append(p.getNombre()).append(" - Vida: ").append((int)(p.getVida() * 100.0 / p.getVidaMaxima())).append("%\n");
            }
        }

        jugadorOponente.enviarMensaje(estado2.toString());

        if (!hayPokemonVivos(jugadorActual.getPokemonEquipo())) {
            finalizarCombate(jugadorOponente, jugadorActual);
        }
    }

    public void mostrarHabilidadesDisponibles(Jugador jugador) throws IOException {
        Pokemon pokemonActual = null;

        for (Pokemon p : jugador.getPokemonEquipo()) {
            if (p != null && p.estaVivo()) {
                pokemonActual = p;
                break;
            }
        }

        if (pokemonActual != null) {
            StringBuilder mensaje = new StringBuilder("Habilidades disponibles para " +
                    pokemonActual.getNombre() + ":\n");

            for (Habilidad h : pokemonActual.getHabilidades()) {
                mensaje.append(h.getId()).append(". ")
                        .append(h.getNombreAtaque())
                        .append(" (Daño: ").append(h.getDanio())
                        .append(", Tipo: ").append(h.getTipo()).append(")\n");
            }

            jugador.enviarMensaje(String.valueOf(mensaje));
        }

        jugador.enviarMensaje("Selecciona el número de la habilidad");
        int seleccion = Integer.parseInt(jugador.esperarRespuesta());

        if (seleccion > 0 && seleccion <= ListaHabilidades.size()) {
            jugador.enviarMensaje("Habilidad elegida");
            habilidadElegida = ListaHabilidades.get(seleccion - 1);
        } else {
            jugador.enviarMensaje(" Selección inválida, intenta de nuevo.");
            mostrarHabilidadesDisponibles(jugador);
        }
    }

    private void procesarTurno(Jugador jugadorActual, Jugador jugadorOponente) {
        try {
            Pokemon pokemonAtacante = null;
            for (Pokemon p : jugadorActual.getPokemonEquipo()) {
                if (p != null && p.estaVivo()) {
                    pokemonAtacante = p;
                    break;
                }
            }

            if (pokemonAtacante != null) {

                if (this.habilidadElegida != null) {
                    for (Pokemon pokemonDefensor : jugadorOponente.getPokemonEquipo()) {
                        if (pokemonDefensor != null && pokemonDefensor.estaVivo()) {
                            aplicarDaño(pokemonDefensor, habilidadElegida);

                            String mensaje = "¡" + pokemonAtacante.getNombre() +
                                    " usó " + habilidadElegida.getNombreAtaque() +
                                    " contra " + pokemonDefensor.getNombre() + "!" + "\n";

                            jugadorActual.enviarMensaje(mensaje);
                            jugadorOponente.enviarMensaje("Habilidad:" + habilidadElegida.getId() + "\n");
                            Thread.sleep(10);
                            jugadorOponente.enviarMensaje(mensaje);
                            break;
                        }
                    }
                } else {
                    jugadorActual.enviarMensaje("Habilidad no válida para este Pokémon");
                }
            }
        } catch (Exception e) {
            jugadorActual.enviarMensaje("Error al procesar el turno");
        }
    }

    private boolean hayPokemonVivos(ArrayList<Pokemon> equipo) {
        for (Pokemon pokemon : equipo) {
            if (pokemon != null && pokemon.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    private void finalizarCombate(Jugador ganador, Jugador perdedor) {
        isCombateActivo = false;
        ganador.enviarMensaje("FIN_COMBATE\n¡Felicidades! Has ganado el combate.");
        perdedor.enviarMensaje("FIN_COMBATE\nHas perdido el combate.");
    }

    private void aplicarDaño(Pokemon defensor, Habilidad habilidad) {
        int daño = calcularDaño(habilidad, defensor);
        defensor.recibirDano(daño);

        if (defensor.getVida() <= 0) {
            defensor.setVivo(0);
            String mensaje = defensor.getNombre() + " ha sido derrotado!";
            jugador1.enviarMensaje(mensaje);
            jugador2.enviarMensaje(mensaje);
        }
    }

    private int calcularDaño(Habilidad habilidad, Pokemon defensor) {
        int daño = habilidad.getDanio();
        if (habilidad.getTipo().equals(defensor.getDebilidad())) {
            daño *= 2;
        }
        return daño;
    }
}
