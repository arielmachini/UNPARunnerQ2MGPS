package ar.edu.unpa.uarg.gisp.unparunner.indicadores;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.Clima;
import ar.edu.unpa.uarg.gisp.unparunner.indicadores.clima.InfoClimaAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Facilita el relevamiento de indicadores de calidad de servicio (QoS),
 * relacionados con la ubicación del dispositivo (mediante el uso del GPS) a
 * cualquier aplicación móvil para Android.
 *
 * @author Ariel Machini
 * @see #createInstance(Context, int, int)
 * @see #createInstance(Context)
 */
public class Indicadores implements LocationListener { // , GpsStatus.Listener {

    private static Indicadores instancia = null;
    private Context contextoAplicacion;
    private int actualizacionesUbicacionDeseadas; // Representa la cantidad de actualizaciones de ubicación solicitadas por el desarrollador.
    private java.util.Map<String, String> informacionUltimaUbicacion;
    private LocationManager administradorUbicacion;

    /**
     * En esta variable se almacena toda la información climática recuperada de Internet.
     * Si desea cambiar la ubicación sobre la cual se obtienen los datos climáticos, modifique el
     * valor de la variable <code>ubicacion</code> en la interfaz <code>InfoClimaAPI</code>.
     *
     * @see InfoClimaAPI
     */
    private Clima climaActual;

    /* Métricas calculadas por la librería: */
    private double latitud;
    private double longitud;
    private float precisionUbicacion; // En metros.
    private int satelitesUtilizados;
    private long latenciaUbicacion; // En milisegundos.

    private Indicadores(Context contexto, int actualizacionesUbiDeseadas, int tiempoMinActualizaciones) {
        this.actualizacionesUbicacionDeseadas = actualizacionesUbiDeseadas;
        this.contextoAplicacion = contexto;

        /* Se inicializan las variables necesarias para la obtención de datos
         * sobre la ubicación del dispositivo. */
        this.administradorUbicacion = (LocationManager) this.contextoAplicacion.getSystemService(Context.LOCATION_SERVICE);

        /* Se inicializan las métricas con valores por defecto. */
        this.latenciaUbicacion = -1;
        this.latitud = Double.MIN_VALUE;
        this.longitud = Double.MIN_VALUE;
        this.precisionUbicacion = -1;
        this.satelitesUtilizados = -1;

        if (this.contextoAplicacion.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.e("Permisos faltantes", "No se podrán obtener datos sobre la ubicación del dispositivo porque el usuario no brindó el permiso necesario (ACCESS_FINE_LOCATION).");
        } else {
            // this.administradorUbicacion.addGpsStatusListener(this);

            this.latenciaUbicacion = System.currentTimeMillis();
            this.administradorUbicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempoMinActualizaciones, 0, this);
        }

        if (this.contextoAplicacion.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.e("Permisos faltantes", "No se podrá actualizar el archivo XML en el dispositivo porque el usuario no brindó el permiso necesario (READ_EXTERNAL_STORAGE).");
        }

        if (this.contextoAplicacion.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.e("Permisos faltantes", "No se podrá crear el archivo XML en el dispositivo porque el usuario no brindó el permiso necesario (WRITE_EXTERNAL_STORAGE).");
        }
    }

    /**
     * Crea y retorna una instancia de la clase <code>Indicadores</code>.
     * Si ya se había creado una instancia con anterioridad, este método
     * simplemente retorna la instancia existente.
     *
     * @param contexto El contexto de la aplicación Android que va a hacer uso
     *                 de los servicios de la clase. Es necesario para poder
     *                 acceder a diferentes funcionalidades del teléfono
     *                 requeridas para calcular el valor de la mayor parte de
     *                 las métricas.
     * @return La instancia de la clase <code>Indicadores</code> que se creó.
     * @author Ariel Machini
     * @see #destroyInstance()
     * @see #getInstanceOf()
     */
    public static Indicadores createInstance(Context contexto) {
        if (instancia == null) {
            instancia = new Indicadores(contexto, 1, 10000);
        }

        return instancia;
    }

    /**
     * Crea y retorna una instancia de la clase <code>Indicadores</code>.
     * Si ya se había creado una instancia con anterioridad, este método
     * simplemente retorna la instancia existente.
     *
     * @param contexto El contexto de la aplicación Android que va a hacer uso
     *                 de los servicios de la clase. Es necesario para poder
     *                 acceder a diferentes funcionalidades del teléfono
     *                 requeridas para calcular el valor de la mayor parte de
     *                 las métricas.
     * @param actualizacionesUbiDeseadas Número de actualizaciones de ubicación
     *                                   que se quieren registrar en el
     *                                   documento XML. Especificar un valor
     *                                   para este parámetro es opcional
     *                                   (Enviar 0 para utilizar el valor por
     *                                   defecto, 1 actualización).
     * @param tiempoMinActualizaciones Tiempo mínimo (en milisegundos) que debe
     *                                 pasar entre cada actualización de
     *                                 la ubicación (mínimo permitido: 2500 MS).
     *                                 Especificar un valor para este parámetro
     *                                 es opcional (Enviar 0 o < 2500 para
     *                                 utilizar el valor por defecto, 10000 MS).
     * @return La instancia de la clase <code>Indicadores</code> que se creó.
     * @author Ariel Machini
     * @see #destroyInstance()
     * @see #getInstanceOf()
     */
    public static Indicadores createInstance(Context contexto, int actualizacionesUbiDeseadas, int tiempoMinActualizaciones) {
        if (instancia == null) {
            if (actualizacionesUbiDeseadas <= 0) {
                actualizacionesUbiDeseadas = 1;
            }

            if (tiempoMinActualizaciones < 2500) {
                tiempoMinActualizaciones = 10000;
            }

            instancia = new Indicadores(contexto, actualizacionesUbiDeseadas, tiempoMinActualizaciones);
        }

        return instancia;
    }

    /**
     * Destruye la instancia de la clase (<code>Indicadores</code>) en el caso
     * de que exista.
     *
     * @author Ariel Machini
     * @see #createInstance(Context)
     * @see #getInstanceOf()
     */
    public static void destroyInstance() {
        /* Se deben detener los callbacks para nuevas ubicaciones y para cambios de estado del GPS. */
        instancia.administradorUbicacion.removeUpdates(instancia);
        // instancia.administradorUbicacion.removeGpsStatusListener(instancia);

        instancia = null;
    }

    /**
     * Retorna la instancia de la clase (<code>Indicadores</code>) en el caso
     * de que exista.
     *
     * @return La instancia de la clase <code>Indicadores</code> o
     * <code>null</code> en caso de que no haya sido creada.
     * @author Ariel Machini
     * @see #createInstance(Context)
     * @see #destroyInstance()
     */
    public static Indicadores getInstanceOf() {
        return instancia;
    }

    /* * * Acá comienzan los métodos heredados * * */

    /* * * MÉTODO COMENTADO * * *

    Método heredado de GpsStatus.Listener.
    Comentado porque se dejó de considerar necesario.

    @Override
    public void onGpsStatusChanged(int event) {
        if (contextoAplicacion.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS || event == GpsStatus.GPS_EVENT_FIRST_FIX) {
                GpsStatus gpsStatus = this.administradorUbicacion.getGpsStatus(null);

                if (gpsStatus != null) {
                    int satelitesDisponibles = 0;
                    int satelitesUtilizados = 0;
                    Iterator<GpsSatellite> satelites = gpsStatus.getSatellites().iterator();
                    // Agregar: gpsStatus.getTimeToFirstFix();

                    while (satelites.hasNext()) {
                        GpsSatellite satelite = satelites.next();

                        if (satelite.usedInFix()) {
                            Log.i("Satélites", "El satélite de ID " + satelite.getPrn() + " fue utilizado en la fijación.");
                            // ConstructorXML.adjuntarMetrica("UsedSatellitePRN", satelite.getPrn() + "");

                            satelitesUtilizados++;
                        }

                        satelitesDisponibles++;
                    }

                    // ConstructorXML.adjuntarMetrica("SatellitesUsed", satelitesUtilizados + "");

                    Log.v("Satélites", "Satélites utilizados para esta fijación: " + satelitesUtilizados + "/" + satelitesDisponibles + " (utilizados/disponibles).");
                }
            }
        } else {
            Log.e("Permisos faltantes", "El contenido del método «onGpsStatusChanged» no se puede ejecutar porque el usuario no brindó el permiso necesario (ACCESS_FINE_LOCATION).");
        }
    }
    * * * MÉTODO COMENTADO * * */

    /**
     * Método heredado de LocationListener.
     *
     * @author Ariel Machini
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location.getProvider().equals("gps")) {
            this.latenciaUbicacion = System.currentTimeMillis() - this.latenciaUbicacion; // Tiempo que se tardó en obtener la ubicación del dispositivo por parte del GPS.

            if (this.actualizacionesUbicacionDeseadas == 0) {
                this.administradorUbicacion.removeUpdates(this); // Al haber obtenido ya todas las actualizaciones solicitadas, se "apaga" el listener.
                Log.i("Nueva ubicación", "Ya no se obtendrán más actualizaciones sobre la ubicación del dispositivo.");
            } else {
                Log.i("Nueva ubicación", "Nueva ubicación (provista por el GPS) obtenida.");

                this.latitud = location.getLatitude();
                this.longitud = location.getLongitude();
                this.precisionUbicacion = location.getAccuracy();

                if (location.getExtras() != null) {
                    this.satelitesUtilizados = location.getExtras().getInt("satellites");
                }

                ConstructorXML.adjuntarIndicador("UbicacionLatencia", this.latitud, this.longitud, this.latenciaUbicacion + " ms");

                if (this.satelitesUtilizados > 0) {
                    ConstructorXML.adjuntarIndicador("UbicacionSatelites", this.latitud, this.longitud, this.satelitesUtilizados + "");
                }

                ConstructorXML.adjuntarIndicador("UbicacionPrecision", this.latitud, this.longitud, this.precisionUbicacion + " m");

                this.informacionUltimaUbicacion = new java.util.HashMap<String, String>();

                this.informacionUltimaUbicacion.put("Accuracy", this.precisionUbicacion + " m");
                this.informacionUltimaUbicacion.put("Latency", this.latenciaUbicacion + " ms");
                this.informacionUltimaUbicacion.put("Latitude", this.latitud + "");
                this.informacionUltimaUbicacion.put("Longitude", this.longitud + "");
                this.informacionUltimaUbicacion.put("SatellitesUsed", this.satelitesUtilizados + "");

                /* Ahora, se obtiene información sobre el clima, la cual puede
                 * asociarse con esta actualización de ubicación.
                 */
                this.fetchWeatherData(this.latitud, this.longitud);

                this.actualizacionesUbicacionDeseadas--;
                this.latenciaUbicacion = System.currentTimeMillis();
            }
        } else {
            Log.w("Nueva ubicación", "La nueva ubicación no fue provista por el GPS. Esperando a la siguiente actualización...");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    /* * * Acá terminan los métodos heredados * * */

    /**
     * Retorna un arreglo asociativo que contiene información relevante sobre
     * la última ubicación del dispositivo obtenida. EL ARREGLO CONTIENE LAS
     * SIGUIENTES CLAVES: Accuracy, Latency, LatitudeLongitude, SatellitesUsed.
     *
     * @return Un objeto de tipo <code>Map</code> (<code>HashMap</code>) con
     * información sobre la última ubicación recuperada.
     * @author Ariel Machini
     */
    public java.util.Map<String, String> getLastLocationInfo() {
        return this.informacionUltimaUbicacion;
    }

    /**
     * Retorna una variable de tipo <code>Clima</code> que contiene la última información sobre el
     * clima que se obtuvo.
     *
     * @return Un objeto de tipo <code>Clima</code> que contiene información climática detallada
     * sobre la ubicación especificada en <code>InfoClimaAPI</code>.
     * @author Ariel Machini
     * @see Clima
     * @see InfoClimaAPI#latitud
     * @see InfoClimaAPI#longitud
     */
    public Clima getLastWeatherInfo() {
        return this.climaActual;
    }

    /**
     * Recupera información sobre el clima actual desde OpenWeatherMap y la almacena en la variable
     * <code>climaActual</code>.
     *
     * @author Ariel Machini
     * @see #getLastWeatherInfo()
     */
    private void fetchWeatherData(double latitud, double longitud) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/").addConverterFactory(GsonConverterFactory.create()).build();
        InfoClimaAPI infoClimaAPI = retrofit.create(InfoClimaAPI.class);

        Call<Clima> llamadaInfoClima = infoClimaAPI.getInfoClima();

        llamadaInfoClima.enqueue(new Callback<Clima>() {
            @Override
            public void onResponse(Call<Clima> call, Response<Clima> response) {
                if (!response.isSuccessful()) {
                    Log.w("Clima", "Llamada no exitosa (código de respuesta: " + response.code() + ")");

                    return;
                }

                climaActual = (Clima) response.body();

                /* Se registran los detalles en el archivo XML. */
                ConstructorXML.adjuntarIndicador("ClimaUbicacion", latitud, longitud, climaActual.getUbicacion());
                ConstructorXML.adjuntarIndicador("ClimaDescripcion", latitud, longitud, climaActual.getDescripcionClima());
                ConstructorXML.adjuntarIndicador("ClimaPresion", latitud, longitud, climaActual.getDatosClima().getHumedad() + " hPa");
                ConstructorXML.adjuntarIndicador("ClimaNubosidad", latitud, longitud, climaActual.getNubosidad() + "%");
                ConstructorXML.adjuntarIndicador("ClimaTemperatura", latitud, longitud, climaActual.getDatosClima().getTemperatura() + "°C");

                Log.i("Clima", "Ubicación: " + climaActual.getUbicacion());
                Log.i("Clima", "Clima (tipo): " + climaActual.getTipoClima());
                Log.i("Clima", "Clima (descripción): " + climaActual.getDescripcionClima());
                Log.i("Clima", "Temperatura: " + climaActual.getDatosClima().getTemperatura() + "°C");
                Log.i("Clima", "Humedad: " + climaActual.getDatosClima().getHumedad() + "%");
                Log.i("Clima", "Presión: " + climaActual.getDatosClima().getPresionAtmosferica() + "hPa");
                Log.i("Clima", "Nubosidad: " + climaActual.getNubosidad() + "%");
                Log.i("Clima", "Viento: " + climaActual.getViento() + " m/s");
            }

            @Override
            public void onFailure(Call<Clima> call, Throwable t) {
                Log.e("Clima", "No se pudo obtener información sobre el clima (llamada fallida). Mensaje de error: \"" + t.getMessage() + "\"");
            }
        });
    }
}