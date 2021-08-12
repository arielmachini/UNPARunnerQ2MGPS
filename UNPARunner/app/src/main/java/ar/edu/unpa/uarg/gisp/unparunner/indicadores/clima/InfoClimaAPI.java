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
    // double latitud = -49.98300, longitud = -68.91000;

    /* Coordenadas de Río Gallegos: */
    double latitud = -51.63333, longitud = -69.23333;

    /* Coordenadas de CABA: */
    // double latitud = -34.61315, longitud = -58.37723;

    /* Coordenadas de San Isidro: */
    // double latitud = -34.4721, longitud = -58.52708;

    /* Coordenadas de Punta Alta: */
    // double latitud = -38.88000, longitud = -62.07500;

    String parametros = "?lat=" + latitud + "&lon=" + longitud + "&appid=" + apiKey + "&units=metric&lang=es";

    @GET("weather" + parametros)
    Call<Clima> getInfoClima();
}
