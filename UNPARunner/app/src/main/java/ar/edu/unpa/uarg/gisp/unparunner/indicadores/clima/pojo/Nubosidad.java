package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nubosidad {
    @SerializedName("all")
    @Expose
    private int porcentaje;

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }
}