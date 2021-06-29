package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo.DatosClima;
import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo.Nubosidad;
import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo.TipoClima;
import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo.Viento;

public class Clima {
    @SerializedName("name")
    @Expose
    private String ubicacion;

    @SerializedName("weather")
    @Expose
    private List<TipoClima> tipoClima = null;

    @SerializedName("main")
    @Expose
    private DatosClima datosClima;

    @SerializedName("clouds")
    @Expose
    private Nubosidad nubosidad;

    @SerializedName("wind")
    @Expose
    private Viento viento;

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoClima() {
        return tipoClima.get(0).getTipo();
    }

    public void setTipoClima(String tipoClima) {
        this.tipoClima.get(0).setTipo(tipoClima);
    }

    public String getDescripcionClima() {
        return tipoClima.get(0).getDescripcion();
    }

    public void setDescripcionClima(String descripcionClima) {
        this.tipoClima.get(0).setTipo(descripcionClima);
    }

    public DatosClima getDatosClima() {
        return datosClima;
    }

    public void setDatosClima(DatosClima datosClima) {
        this.datosClima = datosClima;
    }

    public int getNubosidad() {
        return nubosidad.getPorcentaje();
    }

    public void setNubosidad(Nubosidad nubosidad) {
        this.nubosidad = nubosidad;
    }

    public double getViento() {
        return viento.getVelocidad();
    }

    public void setViento(Viento viento) {
        this.viento = viento;
    }
}
