import java.io.Serializable;

public class Habilidad implements Serializable {

    private int id;
    private String tipo;
    private int danio;
    private String nombreAtaque;

    public Habilidad(int id, String tipo, int danio, String nombreAtaque) {
        this.id = id;
        this.tipo = tipo;
        this.danio = danio;
        this.nombreAtaque = nombreAtaque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDanio() {
        return danio;
    }

    public void setDanio(int danio) {
        this.danio = danio;
    }

    public String getNombreAtaque() {
        return nombreAtaque;
    }

    public void setNombreAtaque(String nombreAtaque) {
        this.nombreAtaque = nombreAtaque;
    }

    public void getDatos() {
        System.out.println(getId() + " " + getTipo() + " " + getDanio() + " " + getNombreAtaque());
    }
}


