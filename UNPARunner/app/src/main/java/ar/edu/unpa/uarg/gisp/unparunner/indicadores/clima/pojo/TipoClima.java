package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TipoClima {
    @SerializedName("main")
    @Expose
    private String tipo;

    @SerializedName("description")
    @Expose
    private String descripcion;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
