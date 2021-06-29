package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatosClima {
    @SerializedName("temp")
    @Expose
    private double temperatura;

    @SerializedName("pressure")
    @Expose
    private int presionAtmosferica; // En hPa.

    @SerializedName("humidity")
    @Expose
    private int humedad; // Porcentual.

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getPresionAtmosferica() {
        return presionAtmosferica;
    }

    public void setPresionAtmosferica(int presionAtmosferica) {
        this.presionAtmosferica = presionAtmosferica;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.humedad = humedad;
    }
}