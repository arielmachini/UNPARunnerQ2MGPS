package ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Detalles sobre la API utilizada:
 * https://openweathermap.org/current
 *
 * @author Ariel Machini
 */
public interface InfoClimaAPI {
    String apiKey = "d459a571ba2de7d1771262a052ba2b1b";

    /* Coordenadas de Comandante Luis Piedrabuena: */
    // double latitud = -49.983, longitud = -68.91;

    /* Coordenadas de Río Gallegos: */
    double latitud = -51.633333, longitud = -69.233333;

    String parametros = "?lat=" + latitud + "&lon=" + longitud + "&appid=" + apiKey + "&units=metric&lang=es";

    @GET("weather" + parametros)
    Call<Clima> getInfoClima();
}
