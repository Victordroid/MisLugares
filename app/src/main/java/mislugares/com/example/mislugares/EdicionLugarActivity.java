package mislugares.com.example.mislugares;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Victor on 02/03/2018.
 */

public class EdicionLugarActivity extends AppCompatActivity {

    private long id_eleccion;
    private Lugar lugar;

    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;
    private long _id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);
        Bundle extras = getIntent().getExtras();
        id_eleccion = extras.getLong("id", -1);
        _id = extras.getLong("_id", -1);
        if (_id!=-1) {
            lugar = MainActivity.lugares.elemento((int) _id);
        } else {
            //System.out.println("VALOR DE ID_ELECCION: "+id_eleccion);
            //lugar = MainActivity.lugares.elemento((int) id_eleccion);
            lugar = SelectorFragment.adaptador.lugarPosicion((int) id_eleccion);
        }

        nombre = (EditText) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());

        tipo = (Spinner) findViewById(R.id.tipo_lugar);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());

        direccion = (EditText) findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());

        telefono = (EditText) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));

        url = (EditText) findViewById(R.id.url);
        url.setText(lugar.getUrl());

        comentario = (EditText) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_guardar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        int opcion = menu.getItemId();
        switch (opcion) {
            case R.id.guardar:
                if (_id==-1) {
                    _id = SelectorFragment.adaptador.idPosicion((int) id_eleccion);
                    /*cosas de guardar*/
                }
                MainActivity.lugares.actualiza((int) _id, lugar);
                SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
                if (id_eleccion!=-1) {
                    SelectorFragment.adaptador.notifyItemChanged((int) id_eleccion);
                } else {
                    SelectorFragment.adaptador.notifyDataSetChanged();
                }
                Guardar();
                return true;
            case R.id.cancelar:
                /*cosas de cancelar*/
                if (_id!=-1) {
                    MainActivity.lugares.borrar((int) _id);
                }
                finish();
                return true;
        }
        return true;

    }

    private void Guardar() {
        lugar.setNombre(nombre.getText().toString());
        lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
        lugar.setDireccion(direccion.getText().toString());
        lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
        lugar.setUrl(url.getText().toString());
        lugar.setComentario(comentario.getText().toString());
        //MainActivity.lugares.actualiza((int) id_eleccion,lugar);
        int _id = SelectorFragment.adaptador.idPosicion((int) id_eleccion);
        MainActivity.lugares.actualiza(_id, lugar);
        SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
        finish();
    }
}

