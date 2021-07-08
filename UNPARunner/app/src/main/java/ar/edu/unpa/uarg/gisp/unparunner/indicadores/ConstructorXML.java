package ar.edu.unpa.uarg.gisp.unparunner.indicadores;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Construye un documento XML a partir de las indicadores que va recibiendo
 * mediante el método «adjuntarIndicador».
 * Esta clase fue desarrollada en base al trabajo de Juan Enriquez.
 * <p>
 * NOTA PARA EL DESARROLLADOR: El archivo XML en el que se almacenan los
 * indicadores se guarda en el directorio raíz de la memoria del teléfono.
 * Lo puede ubicar con el nombre «indicadores.xml».
 *
 * @author Ariel Machini
 */
public class ConstructorXML {

    /**
     * El nombre del archivo XML que se va a guardar (/‹NOMBRE AQUÍ›.xml).
     * El valor por defecto es «indicadores».
     */
    public static String NOMBRE_ARCHIVO = "indicadores";

    private static SimpleDateFormat formateadorFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);

    /**
     * Adjunta el indicador especificado por parámetros al archivo XML que
     * almacena los indicadores.
     *
     * @author Ariel Machini
     */
    public static void adjuntarIndicador(String indicador, double latitud, double longitud, String valor) {
        File archivoXML = new File(Environment.getExternalStorageDirectory() + "/" + NOMBRE_ARCHIVO + ".xml");

        try {
            if (!archivoXML.exists()) {
                archivoXML.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(archivoXML, true));

            long fechaSistema = System.currentTimeMillis();
            String fechaFormateada = formateadorFecha.format(fechaSistema);
            indicador = "<indicador nombre=\"" + indicador + "\" fecha=\"" + fechaFormateada + "\" lat=\"" + latitud + "\" lon=\"" + longitud + "\">" + valor + "</indicador>";

            bufferedWriter.append(indicador);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            Log.e("ConstructorXML", "Se produjo un error de E/S durante la ejecución del método «adjuntarMetrica» de la clase ConstructorXML.");
        }
    }

}
