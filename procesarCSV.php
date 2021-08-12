<?php
/* Primero, averiguo la cantidad de documentos CSV que hay en el directorio: */
$dirDocumentosCSV = scandir("./csv");
$documentosCSV = array();

foreach ($dirDocumentosCSV as $clave => $valor) {
    if (!is_dir($valor)) {
        $documentosCSV[] = $valor;
    }
}

/* Indicadores separados por presión atmosférica: */
$iBajaPresion = array(); // Presión (hPa) < 1013 hPa
$iAltaPresion = array(); // Presión (hPa) ≥ 1013 hPa

/* Indicadores separados por nubosidad: */
$iNubesSoleado = array(); // 0% ≤ Nubes < 25%
$iNubesParcial = array(); // 25% ≤ Nubes < 50%
$iNubesNublado = array(); // 50% ≤ Nubes ≤ 100%

for ($i = 0; $i < count($documentosCSV); $i++) {
    $rutaDocCSV = "./csv/" . $documentosCSV[$i];
    $documentoCSV = fopen($rutaDocCSV, "r");

    do {
        $indicador = fgetcsv($documentoCSV, ",");

        $fechaActual = substr($indicador[2], 0, 10); // Se usa a modo de "clave", razón por la que se quita la hora.
        $fecha = $fechaActual;

        /* TODO VER SI LA INFO EN EL ARRAY SE DESTRUYE TRAS EL BUCLE */
        $indicadores = array(); // Arreglo que contendrá todos los indicadores de ese día.
        echo(sizeof($indicadores));
        
        while ($indicador !== false && $fecha === $fechaActual) {
            /* Estructura de los datos en el archivo CSV de entrada:
             * [Valor, Indicador, Fecha y hora, Latitud, Longitud] */
            
            $nombreIndicador = $indicador[1];

            switch ($nombreIndicador) {
                case "UbicacionLatencia":
                    $indicadores["UbicacionLatencia"][] = $indicador[0] / 1000; // Convierto el valor a segundos, para que se vea bien en el gráfico junto con otros indicadores.

                    break;
                case "UbicacionPrecision":
                    $indicadores["UbicacionPrecision"][] = $indicador[0];
    
                    break;
                case "UbicacionSatelites":
                    $indicadores["UbicacionSatelites"][] = $indicador[0];

                    break;
                case "ClimaPresion":
                    $indicadores["ClimaPresion"][] = $indicador[0];
                    
                    break;
                case "ClimaNubosidad":
                    $indicadores["ClimaNubosidad"][] = $indicador[0];

                    break;
            }

            $indicador = fgetcsv($documentoCSV, ","); // Se obtiene la siguiente línea del documento CSV.

            if ($indicador !== false) $fecha = substr($indicador[2], 0, 10); // Se extrae la fecha de la nueva línea del documento.
        }

        if (!array_key_exists("UbicacionLatencia", $indicadores) || !array_key_exists("UbicacionPrecision", $indicadores)
            || !array_key_exists("ClimaPresion", $indicadores) || !array_key_exists("ClimaNubosidad", $indicadores)) {
            continue; // Si no se calculó Latencia/Precisión/Nubosidad/Presión, se omite el día.
        }

        /* Se calculan los valores promedio: */
        $indicadores["UbicacionLatencia"] = array_sum($indicadores["UbicacionLatencia"]) / count($indicadores["UbicacionLatencia"]);
        $indicadores["UbicacionPrecision"] = array_sum($indicadores["UbicacionPrecision"]) / count($indicadores["UbicacionPrecision"]);
        
        if (array_key_exists("UbicacionSatelites", $indicadores)) {
            $ubicacionSatelites = intval(array_sum($indicadores["UbicacionSatelites"]) / count($indicadores["UbicacionSatelites"]));
        } else {
            $ubicacionSatelites = 0;
        }
        
        $indicadores["ClimaPresion"] = intval(array_sum($indicadores["ClimaPresion"]) / count($indicadores["ClimaPresion"]));
        $indicadores["ClimaNubosidad"] = intval(array_sum($indicadores["ClimaNubosidad"]) / count($indicadores["ClimaNubosidad"]));

        /* Se separan los indicadores del día por presión atmosférica: */
        if ($indicadores["ClimaPresion"] < 1013) {
            $iBajaPresion["UbicacionLatencia"][] = $indicadores["UbicacionLatencia"];
            $iBajaPresion["UbicacionPrecision"][] = $indicadores["UbicacionPrecision"];
            $iBajaPresion["UbicacionSatelites"][] = $ubicacionSatelites;
        } else { // Presión (hPa) ≥ 1013 hPa
            $iAltaPresion["UbicacionLatencia"][] = $indicadores["UbicacionLatencia"];
            $iAltaPresion["UbicacionPrecision"][] = $indicadores["UbicacionPrecision"];
            $iAltaPresion["UbicacionSatelites"][] = $ubicacionSatelites;
        }

        /* Se separan los indicadores del día por nubosidad: */
        if ($indicadores["ClimaNubosidad"] < 25) { // 0% ≤ Nubes < 25%
            $iNubesSoleado["UbicacionLatencia"][] = $indicadores["UbicacionLatencia"];
            $iNubesSoleado["UbicacionPrecision"][] = $indicadores["UbicacionPrecision"];
            $iNubesSoleado["UbicacionSatelites"][] = $ubicacionSatelites;
        } else if (25 <= $indicadores["ClimaNubosidad"] && $indicadores["ClimaNubosidad"] < 50) { // 25% ≤ Nubes < 50%
            $iNubesParcial["UbicacionLatencia"][] = $indicadores["UbicacionLatencia"];
            $iNubesParcial["UbicacionPrecision"][] = $indicadores["UbicacionPrecision"];
            $iNubesParcial["UbicacionSatelites"][] = $ubicacionSatelites;
        } else { // 50% ≤ Nubes ≤ 100%
            $iNubesNublado["UbicacionLatencia"][] = $indicadores["UbicacionLatencia"];
            $iNubesNublado["UbicacionPrecision"][] = $indicadores["UbicacionPrecision"];
            $iNubesNublado["UbicacionSatelites"][] = $ubicacionSatelites;
        }
    } while ($indicador !== false);

    fclose($documentoCSV);

    /* Generación de los documentos CSV de salida: */

    if (!empty($iBajaPresion)) { // ¿Hay indicadores registrados con presión atmosférica baja?
        $csvBajaPresion = fopen("./csv-out/1_Presión_atmosférica_baja.csv", "w");
        fwrite($csvBajaPresion, "Latencia,Precisión,Satélites\n");

        for ($j = 0; $j < count($iBajaPresion["UbicacionLatencia"]); $j++) {
            $lineaCSV = "\"" . str_replace(".", ",", $iBajaPresion["UbicacionLatencia"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iBajaPresion["UbicacionPrecision"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iBajaPresion["UbicacionSatelites"][$j]). "\"\n";
            
            fwrite($csvBajaPresion, $lineaCSV);
        }

        fclose($csvBajaPresion);
    }

    if (!empty($iAltaPresion)) { // ¿Hay indicadores registrados con presión atmosférica alta?
        $csvAltaPresion = fopen("./csv-out/2_Presión_atmosférica_alta.csv", "w");
        fwrite($csvAltaPresion, "Latencia,Precisión,Satélites\n");

        for ($j = 0; $j < count($iAltaPresion["UbicacionLatencia"]); $j++) {
            $lineaCSV = "\"" . str_replace(".", ",", $iAltaPresion["UbicacionLatencia"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iAltaPresion["UbicacionPrecision"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iAltaPresion["UbicacionSatelites"][$j]). "\"\n";
            
            fwrite($csvAltaPresion, $lineaCSV);
        }

        fclose($csvAltaPresion);
    }

    if (!empty($iNubesSoleado)) { // ¿Hay indicadores registrados con cielo despejado?
        $csvNubesSoleado = fopen("./csv-out/3_Cielo_soleado.csv", "w");
        fwrite($csvNubesSoleado, "Latencia,Precisión,Satélites\n");

        for ($j = 0; $j < count($iNubesSoleado["UbicacionLatencia"]); $j++) {
            $lineaCSV = "\"" . str_replace(".", ",", $iNubesSoleado["UbicacionLatencia"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesSoleado["UbicacionPrecision"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesSoleado["UbicacionSatelites"][$j]). "\"\n";
            
            fwrite($csvNubesSoleado, $lineaCSV);
        }

        fclose($csvNubesSoleado);
    }

    if (!empty($iNubesParcial)) { // ¿Hay indicadores registrados con cielo parcialmente nublado?
        $csvNubesParcial = fopen("./csv-out/4_Cielo_parcialmente_nublado.csv", "w");
        fwrite($csvNubesParcial, "Latencia,Precisión,Satélites\n");

        for ($j = 0; $j < count($iNubesParcial["UbicacionLatencia"]); $j++) {
            $lineaCSV = "\"" . str_replace(".", ",", $iNubesParcial["UbicacionLatencia"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesParcial["UbicacionPrecision"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesParcial["UbicacionSatelites"][$j]). "\"\n";
            
            fwrite($csvNubesParcial, $lineaCSV);
        }

        fclose($csvNubesParcial);
    }

    if (!empty($iNubesNublado)) { // ¿Hay indicadores registrados con cielo nublado?
        $csvNubesNublado = fopen("./csv-out/5_Cielo_nublado.csv", "w");
        fwrite($csvNubesNublado, "Latencia,Precisión,Satélites\n");

        for ($j = 0; $j < count($iNubesNublado["UbicacionLatencia"]); $j++) {
            $lineaCSV = "\"" . str_replace(".", ",", $iNubesNublado["UbicacionLatencia"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesNublado["UbicacionPrecision"][$j]). "\"," .
                "\"" . str_replace(".", ",", $iNubesNublado["UbicacionSatelites"][$j]). "\"\n";
            
            fwrite($csvNubesNublado, $lineaCSV);
        }

        fclose($csvNubesNublado);
    }
}
