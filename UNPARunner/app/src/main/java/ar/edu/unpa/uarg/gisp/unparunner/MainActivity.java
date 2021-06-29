package ar.edu.unpa.uarg.gisp.unparunner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ar.edu.unpa.uarg.gisp.unparunner.sqlite.ConexionSQLite;
import ar.edu.unpa.uarg.gisp.unparunner.sqlite.ConstantesSQLite;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_SOLICITUD_ALMACENAMIENTO = 25081996;

    ConexionSQLite conexionSQLite;
    TextView textViewNumeroRecorridos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.conexionSQLite = new ConexionSQLite(this, null, 1);

        Button buttonVerHistorial = findViewById(R.id.mainBotorVerHistorial);
        Button buttonNuevoRecorrido = findViewById(R.id.mainBotonNuevoRecorrido);
        this.textViewNumeroRecorridos = findViewById(R.id.mainNumeroRecorridos);

        buttonVerHistorial.setOnClickListener(view -> {
            startActivity(new Intent(this, HistorialRecorridosActivity.class));
        });

        buttonNuevoRecorrido.setOnClickListener(view -> {
            startActivity(new Intent(this, NuevoRecorridoActivity.class));
            finish();
        });

        this.obtenerNumeroRecorridos();

        this.solicitarPermisosAlmacenamiento();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.obtenerNumeroRecorridos();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Aviso: Para que la librería funcione correctamente, se requiere del permiso para leer/escribir archivos en el dispositivo", Toast.LENGTH_LONG).show();
        }
    }

    private void obtenerNumeroRecorridos() {
        SQLiteDatabase bd = this.conexionSQLite.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT COUNT(`" + ConstantesSQLite.RECORRIDO_PRIMARY_KEY + "`) FROM `" + ConstantesSQLite.NOMBRE_TABLA_RECORRIDO + "`", new String[0]);

        cursor.moveToFirst();
        this.textViewNumeroRecorridos.setText(cursor.getString(0));

        bd.close();
    }

    private void solicitarPermisosAlmacenamiento() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.CODIGO_SOLICITUD_ALMACENAMIENTO);
        }
    }

}
