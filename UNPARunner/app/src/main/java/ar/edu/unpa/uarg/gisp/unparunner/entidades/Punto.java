package ar.edu.unpa.uarg.gisp.unparunner.entidades;

public class Punto {

    private float latitud;
    private float longitud;

    public Punto() {
        this.latitud = Float.MIN_VALUE;
        this.longitud = Float.MIN_VALUE;
    }

    public Punto(float latitud, float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public float getLatitud() {
        return this.latitud;
    }

    public float getLongitud() {
        return this.longitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

}
