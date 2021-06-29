package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viento {
    @SerializedName("speed")
    @Expose
    private double velocidad; // En M/S.

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }
}
