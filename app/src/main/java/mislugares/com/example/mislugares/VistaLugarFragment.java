package mislugares.com.example.mislugares;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import android.view.View.OnClickListener;


/**
 * Created by Victor on 01/03/2018.
 */

public class VistaLugarFragment extends Fragment {
    private long id_eleccion;
    private Lugar lugar;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;
    private Uri uriFoto;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.vista_lugar,contenedor,false);
        setHasOptionsMenu(true);
        LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.url);
        pUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pgWeb(null);
            }
        });
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            id_eleccion = extras.getLong("id", -1);
            if (id_eleccion != -1) {
                actualizarVistas(id_eleccion);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vista_lugar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void borrarLugar(final int id_eleccion){
        new AlertDialog.Builder(getActivity())
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainActivity.lugares.borrar(id_eleccion);
                        SelectorFragment.adaptador.setCursor(
                                MainActivity.lugares.extraeCursor());
                        SelectorFragment.adaptador.notifyDataSetChanged();
                        getActivity().finish();
                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                System.out.println("Has elegido la opcion editar");
                Intent i = new Intent (getActivity(), EdicionLugarActivity.class);
                i.putExtra("id", id_eleccion);
                //startActivity(i);
                startActivityForResult(i, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                int _id = SelectorFragment.adaptador.idPosicion((int) id_eleccion);
                borrarLugar(_id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(id_eleccion);
            v.findViewById(R.id.scrollView1).invalidate();
        }else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                lugar.setFoto(data.getDataString());
                ponerFoto((ImageView)v.findViewById(R.id.foto), lugar.getFoto());
            } else {
                Toast.makeText(getActivity(), "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK
                    && lugar != null && uriFoto != null) {
                lugar.setFoto(uriFoto.toString());
                ponerFoto((ImageView)v.findViewById(R.id.foto), lugar.getFoto());
            } else {
                Toast.makeText(getActivity(), "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void actualizarVistas(final long id) {
        this.id_eleccion = id;
        //lugar = MainActivity.lugares.elemento((int) id_eleccion);
        lugar = SelectorFragment.adaptador.lugarPosicion((int) id);
        if (lugar != null) {
            TextView nombre = (TextView) v.findViewById(R.id.nombre);
            nombre.setText(lugar.getNombre());
            ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
            logo_tipo.setImageResource(lugar.getTipo().getRecurso());
            TextView tipo = (TextView) v.findViewById(R.id.tipo);
            tipo.setText(lugar.getTipo().getTexto());

            if (lugar.getDireccion().isEmpty()) {
                v.findViewById(R.id.direccion).setVisibility(View.GONE);
                System.out.println("SE oculta el campo dirección");
            } else {
                TextView direccion = (TextView) v.findViewById(R.id.direccion);
                direccion.setText(lugar.getDireccion());
            }
            if (lugar.getTelefono() == 0) {
                v.findViewById(R.id.telefono).setVisibility(View.GONE);
            } else {
                TextView telefono = (TextView) v.findViewById(R.id.telefono);
                telefono.setText(Integer.toString(lugar.getTelefono()));
            }
            if (lugar.getUrl().isEmpty()) {
                v.findViewById(R.id.url).setVisibility((View.GONE));
            } else {
                TextView url = (TextView) v.findViewById(R.id.url);
                url.setText(lugar.getUrl());
            }
            if (lugar.getComentario().isEmpty()) {
                v.findViewById(R.id.comentario).setVisibility(View.GONE);
            } else {
                TextView comentario = (TextView) v.findViewById(R.id.comentario);
                comentario.setText(lugar.getComentario());
            }
            if (lugar.getFecha() == 0) {
                v.findViewById(R.id.fecha).setVisibility(View.GONE);
                v.findViewById(R.id.hora).setVisibility((View.GONE));
            } else {
                TextView fecha = (TextView) v.findViewById(R.id.fecha);
                fecha.setText(DateFormat.getDateInstance().format(
                        new Date(lugar.getFecha())));
                TextView hora = (TextView) v.findViewById(R.id.hora);
                hora.setText(DateFormat.getTimeInstance().format(
                        new Date(lugar.getFecha())));
            }
            RatingBar valoracion = (RatingBar) v.findViewById(R.id.valoracion);
            valoracion.setOnRatingBarChangeListener(null);
            valoracion.setRating(lugar.getValoracion());
            valoracion.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar,
                                                    float valor, boolean fromUser) {
                            lugar.setValoracion(valor);
                            actualizaLugar();
                        }
                    });
            actualizaLugar();
            ponerFoto((ImageView)v.findViewById(R.id.foto), lugar.getFoto());
            actualizaLugar();
        }
    }

    void actualizaLugar(){
        int _id = SelectorFragment.adaptador.idPosicion((int) id_eleccion);
        MainActivity.lugares.actualiza(_id, lugar);
        SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
        SelectorFragment.adaptador.notifyItemChanged((int) id_eleccion);
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageURI(Uri.parse(uri));
        } else{
            imageView.setImageBitmap(null);
        }
    }

    /*public void tomarFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
        if (intent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            uriFoto = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, RESULTADO_FOTO);
        } else {
            Toast.makeText(getActivity(), "Error en captura", Toast.LENGTH_LONG).show();
        }
    }*/

    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto((ImageView)v.findViewById(R.id.foto), null);
    }
}
