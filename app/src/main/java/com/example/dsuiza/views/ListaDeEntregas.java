package com.example.dsuiza.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.MainActivity;
import com.example.dsuiza.R;
import com.example.dsuiza.adapters.AdapterComprobantes;
import com.example.dsuiza.adapters.AdapterFarmacias;
import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.helpers.OrderComprobantes;
import com.example.dsuiza.modelo.ModeloCliente;
import com.example.dsuiza.modelo.ModeloComprobantes;
import com.example.dsuiza.modelo.ModeloSalida;
import com.example.dsuiza.persistencia.PersistenciaDeComprobantes;
import com.example.dsuiza.persistencia.PersistenciaDeSalida;
import com.example.dsuiza.persistencia.PersistenciaPlanillas;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


public class ListaDeEntregas extends Fragment {

    ListView list;
    private IFragementTransitions interfaz;

    ArrayList<String> _idplanilladetalleSI=new ArrayList();
    ArrayList<String> _idplanilladetalleNO=new ArrayList();
    ArrayList<String> _idplanilladetalleTotal=new ArrayList();
    ArrayList<Integer> _arraydemotivos=new ArrayList();
    int cantidad_de_bultos_si_entregados=0;
    int cantidad_de_bultos_no_entregados=0;
    boolean bandera=false;


    ProgressDialog dialog;
    boolean entrega;
    boolean error4 = false;
    DateTime joda_time;

    ArrayList<String> respuestaString = new ArrayList();
    ArrayList<ModeloSalida> salidas = new ArrayList();

    PersistenciaDeSalida persistenciaDeSalida;



    String _observaciones=null;
    String _qr =null;
    String _idCliente=null;
    String _nombre=null;
    String _direccion=null;
    String _localidad=null;
    String _provincia=null;
    String _orden=null;
    String _idVenta=null;
    String _pedido=null;
    String _rubro=null;
    String _bultos=null;
    String _latitud=null;
    String _longitud=null;
    String _fecha=null;
    String _hora=null;
    String _token=null;

    int _idplanilla;
    int _idplanilladetalle;

    //// recursos adaptarse y array
    ArrayList<ModeloComprobantes> lista_Ccomprobantes = new ArrayList();
    ModeloComprobantes comprobantes;
    AdapterComprobantes clase_adapatador_comprobantes;


    MainActivity mainActivity;
/// TODO LOS ELEMENTOS DE LA VISTA
    private TextView nombre;
    private Button button_entregados;
    private Switch aSwitch;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ObtenerLatitudYLongitud();

    }

    private void ObtenerLatitudYLongitud() {

        ////////////////////////////////////////////////////////////////////////////////////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationManager locationManager = (LocationManager)getActivity().
                    getSystemService(LOCATION_SERVICE);
            Location location = null;
            LocationListener mlocListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            if (locationManager != null) {
                //Existe GPS_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if(location == null){ //Trata con NETWORK_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                if (locationManager != null) {
                    //Existe NETWORK_PROVIDER obtiene ubicación
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if(location != null) {
                _latitud = String.valueOf(location.getLatitude());
                _longitud = String.valueOf(location.getLongitude());


            }else {
                Toast.makeText(getContext(), "No se pudo obtener geolocalización", Toast.LENGTH_LONG).show();
            }

        }

        ////////////////////////////////////////////////////////////////////////////////////
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        _hora = hourFormat.format(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        _fecha = dateFormat.format(date);
        //////////////////////////////////////////////////////////////////////////////////////////

        String data_time=_fecha+" "+_hora;
        DateTimeFormatter datetimeformat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        joda_time = datetimeformat.parseDateTime(data_time);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_lista_de_entregas, container, false);
        list = (ListView) vista.findViewById(R.id.listView_comprobantes);
        aSwitch = (Switch) vista.findViewById(R.id.switch1);
        aSwitch.setTextOn("SI"); // displayed text of the Switch whenever it is in checked or on state
        aSwitch.setTextOff("NO");
        button_entregados = (Button) vista.findViewById(R.id.button_entregado);
        String myHexColor = "#D35400";
        button_entregados.setBackgroundColor(Color.parseColor(myHexColor));


        if (getArguments() != null) {
            _nombre = getArguments().getString("nombre");
            _idCliente =getArguments().getString("idCliente");
            _direccion =getArguments().getString("direccion");
            _localidad =getArguments().getString("localidad");
            _provincia =getArguments().getString("provincia");
            _orden =getArguments().getString("orden");
            _qr =getArguments().getString("qr");
            _token=getArguments().getString("token");
            _idplanilla =getArguments().getInt("idplanilla");

            consulta_de_Planilla_Y_Cliente(_idplanilla, Integer.parseInt(_idCliente));


        } else nombre.setText("Error");


        button_entregados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEntregados();
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoDeSwitch();
            }
        });

        return vista;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////TODO sobre manejo de el switch               //////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    private void estadoDeSwitch() {
        if (!aSwitch.isChecked()) {
            Toast.makeText(getActivity(), "Desactivado", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Activado", Toast.LENGTH_SHORT).show();
            for(int h =0; h<lista_Ccomprobantes.size(); h++) {
                lista_Ccomprobantes.get(h).setCheck(true);

            }
            // actulizarVista();
            clase_adapatador_comprobantes.notifyDataSetChanged();

        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////TODO sobre consultas sqlite                  //////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public  void consulta_de_Planilla_Y_Cliente(int idplanilla, int idcliente) {

        if(_latitud==null && _longitud==null){

            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("NO SE PUDO OBTENER LA UBICACION");
            alertDialog.setMessage("ACTIVE LA UBICACION Y PRESIONE - OK -");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "||| OK |||",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        FragmentListaDeEntregas f = new FragmentListaDeEntregas();
                        Bundle enviar = new Bundle();
                        enviar.putInt("idplanilla", idplanilla);
                        f.setArguments(enviar);
                        getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.idContenedor, f).
                        addToBackStack(null).commit();

                        }
                    });
            alertDialog.show();

        } else {

            PersistenciaDeComprobantes admin = new PersistenciaDeComprobantes(getContext(),
                    "comprobantes.db", null, 1);
            SQLiteDatabase bd = admin.getReadableDatabase();


            Cursor fila = bd.rawQuery("SELECT * FROM table_comprobantes " +
                    "WHERE idplanilla = '"+idplanilla+"' AND idcliente = '"+idcliente+"'" , null);


            if (fila != null && fila.getCount()>0) {
                if(fila.moveToFirst()){

                    do{
                        comprobantes = new ModeloComprobantes();

                        comprobantes.setIdplanilladetalle(String.valueOf(fila.getInt(7)));
                        comprobantes.setComprobante_bultos(fila.getString(6));
                        comprobantes.setComprobante_bultos(fila.getString(5));
                        comprobantes.setComprobante_rubro(fila.getString(4));
                        comprobantes.setComprobante_pedido(fila.getString(3));// esta es factura

                        lista_Ccomprobantes.add(comprobantes);


                    }while(fila.moveToNext());


                    clase_adapatador_comprobantes = new AdapterComprobantes(getContext(),lista_Ccomprobantes);
                    list.setAdapter(clase_adapatador_comprobantes);
                    MostrarError("LISTA DE comprobantes!");
                    fila.close();
                    bd.close();

                } else {
                    MostrarError("Error en el envio de parametros");     }

            } else {MostrarError(String.valueOf(fila));
            }

        }
        int contador=0;
        for(int x=0; x<lista_Ccomprobantes.size();x++){
            contador=contador + Integer.parseInt(lista_Ccomprobantes.get(x).getComprobante_bultos());
        }
        aSwitch.setText("CANTIDAD DE BULTOS : "+contador);

    }

    private void MostrarError(String s) {
    }

    public void clickEntregados() {

        cantidad_de_bultos_no_entregados =0;
        cantidad_de_bultos_si_entregados =0;

        for (int i = 0; i < lista_Ccomprobantes.size(); i++) {

            if (lista_Ccomprobantes.get(i).isCheck()) {
                cantidad_de_bultos_si_entregados = cantidad_de_bultos_si_entregados + Integer.parseInt(lista_Ccomprobantes.get(i).getComprobante_bultos());
                _idplanilladetalleSI.add(lista_Ccomprobantes.get(i).getIdplanilladetalle());
                _idplanilladetalleTotal.add(lista_Ccomprobantes.get(i).getIdplanilladetalle());
                _arraydemotivos.add(lista_Ccomprobantes.get(i).getSpinner());
            } else {
                if (lista_Ccomprobantes.get(i).getSpinner() == 0) {
                    bandera = true;
                    Toast.makeText(getActivity(), "complete los motivos restantes",
                            Toast.LENGTH_SHORT).show();

                } else if (lista_Ccomprobantes.get(i).getSpinner() != 0) {
                    bandera = false;
                    cantidad_de_bultos_no_entregados = cantidad_de_bultos_no_entregados + Integer.parseInt(lista_Ccomprobantes.get(i).getComprobante_bultos());
                    _idplanilladetalleNO.add(lista_Ccomprobantes.get(i).getIdplanilladetalle());
                    _idplanilladetalleTotal.add(lista_Ccomprobantes.get(i).getIdplanilladetalle());
                    _arraydemotivos.add(lista_Ccomprobantes.get(i).getSpinner());

                }

            }

        }


        if (cantidad_de_bultos_no_entregados == 0) {
            _observaciones ="";
        } else {
            _observaciones ="";
        }

        _bultos = String.valueOf(cantidad_de_bultos_si_entregados);

        FragmentQR f_qr = new FragmentQR();
        FragmentFirma f_firma = new FragmentFirma();

        Bundle args = new Bundle();

        args.putString("idCliente", _idCliente);
        args.putString("nombre", _nombre);
        args.putString("direccion", _direccion);
        args.putString("localidad", _localidad);
        args.putString("provincia", _provincia);
        args.putString("orden", _orden);
        args.putString("idVenta", _idVenta);
        args.putString("pedido", _pedido);
        args.putString("rubro", _rubro);
        args.putString("bultos", _bultos);
        args.putString("qr", _qr);
        args.putString("token", _token);
        args.putString("fecha", _fecha);
        args.putString("hora", _hora);
        args.putString("latitud", _latitud);
        args.putString("longitud", _longitud);
        args.putInt("idplanilla", _idplanilla);
        args.putInt("idplanilladetalle", _idplanilladetalle);
        args.putString("observaciones", _observaciones);
        args.putStringArrayList("lista_de_id_si", _idplanilladetalleSI);
        args.putStringArrayList("lista_de_id_no", _idplanilladetalleNO);
        args.putStringArrayList("lista_de_id", _idplanilladetalleTotal);
        args.putIntegerArrayList("list_de_motivos", _arraydemotivos);


        ///////////////////////////////////////////////////////////////////

        if (bandera) {
            Toast.makeText(getActivity(), "Elija un motivo para continuar", Toast.LENGTH_SHORT).show();
        } else {

        if (cantidad_de_bultos_si_entregados == 0) {
            // esto porque esta cerrado u otro motivo
            llamar_a_vista_foto_comprobacion();

        } else if (cantidad_de_bultos_si_entregados != 0) {

            if (cantidad_de_bultos_no_entregados != 0) {
                llamar_a_verificar_bultos();
            } else if (cantidad_de_bultos_no_entregados == 0) {
                if (_qr.matches("NO")) {

                    f_firma.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.idContenedor,
                            f_firma).addToBackStack(null).commit();

                } else {
                    f_qr.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.idContenedor,
                            f_qr).addToBackStack(null).commit();

                }

            }

        }
    }// de la bandera

        ////////////////////////

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO EL MANEJO entrega vacia             ///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void llamar_a_verificar_bultos() {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.mensaje_sin_bultos,null);
        alertDialog.setView(view2);
        Button buton_continuar = (Button)view2.findViewById(R.id.btn_mensaje_sin_bultos);
        String myHexColor = "#D35400";
        buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));
        TextView txt = view2.findViewById(R.id.txt_mensaje_sin_bultos);

        alertDialog.show();
        if(cantidad_de_bultos_no_entregados==1){
            txt.setText( cantidad_de_bultos_no_entregados +" BULTO NO ENTREGADO");
        } else {txt.setText( cantidad_de_bultos_no_entregados +" BULTOS NO ENTREGADOS");}

        FragmentQR f_qr = new FragmentQR();
        FragmentFirma f_firma = new FragmentFirma();
        Bundle args = new Bundle();
        args.putString("idCliente", _idCliente);
        args.putString("nombre", _nombre);
        args.putString("direccion", _direccion);
        args.putString("localidad", _localidad);
        args.putString("provincia", _provincia);
        args.putString("orden", _orden);
        args.putString("idVenta", _idVenta);
        args.putString("pedido", _pedido);
        args.putString("rubro", _rubro);
        args.putString("bultos", _bultos);
        args.putString("qr", _qr);
        args.putString("token", _token);
        args.putString("fecha", _fecha);
        args.putString("hora", _hora);
        args.putString("latitud", _latitud);
        args.putString("longitud", _longitud);
        args.putInt("idplanilla", _idplanilla);
        args.putInt("idplanilladetalle", _idplanilladetalle);
        args.putString("observaciones", _observaciones);
        args.putStringArrayList("lista_de_id_si", _idplanilladetalleSI);
        args.putStringArrayList("lista_de_id_no", _idplanilladetalleNO);
        args.putStringArrayList("lista_de_id", _idplanilladetalleTotal);
        args.putIntegerArrayList("list_de_motivos", _arraydemotivos);

        buton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(_qr.matches("NO")){

                    f_firma.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.idContenedor,
                            f_firma).addToBackStack(null).commit();

                } else {
                    f_qr.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.idContenedor,
                            f_qr).addToBackStack(null).commit();
                }

                alertDialog.dismiss();
            }
        });


    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO EL MANEJO entrega vacia             ///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void llamar_a_vista_foto_comprobacion() {
//// en el caso de no entregar ningun comprobante en l entrega N
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.mensaje_sin_bultos,null);
        alertDialog.setView(view2);
        Button buton_continuar = (Button)view2.findViewById(R.id.btn_mensaje_sin_bultos);
        String myHexColor = "#D35400";
        buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));
        TextView txt = view2.findViewById(R.id.txt_mensaje_sin_bultos);

        alertDialog.show();
        if(cantidad_de_bultos_no_entregados==1){
            txt.setText( cantidad_de_bultos_no_entregados +" BULTO NO ENTREGADO");
        } else {txt.setText( cantidad_de_bultos_no_entregados +" BULTOS NO ENTREGADOS");}

        buton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentFotoComprobacion f1 = new FragmentFotoComprobacion();
                Bundle args = new Bundle();
                args.putString("imagen_de_comprobacion", "");
                args.putString("imagen_de_firma", "");
                args.putString("aclaracion", "No hay firma");
                args.putString("idCliente", _idCliente);
                args.putString("nombre", _nombre);
                args.putString("direccion", _direccion);
                args.putString("localidad", _localidad);
                args.putString("provincia", _provincia);
                args.putString("orden", _orden);
                args.putString("idVenta", _idVenta);
                args.putString("pedido", _pedido);
                args.putString("rubro", _rubro);
                args.putString("bultos", _bultos);
                args.putString("qr", _qr);
                args.putString("token", _token);
                args.putString("fecha", _fecha);
                args.putString("hora", _hora);
                args.putString("latitud", _latitud);
                args.putString("longitud", _longitud);
                args.putInt("idplanilla", _idplanilla);
                args.putInt("idplanilladetalle", _idplanilladetalle);
                args.putString("observaciones", _observaciones); // todavia nada
                args.putStringArrayList("lista_de_id_si", _idplanilladetalleSI);
                args.putStringArrayList("lista_de_id_no", _idplanilladetalleNO);
                args.putStringArrayList("lista_de_id", _idplanilladetalleTotal);
                args.putIntegerArrayList("list_de_motivos", _arraydemotivos);

                guardarTodo();
                alertDialog.dismiss();
            }
        });


    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO EL MANEJO DE MENU TOOLBAR           ///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.comprobantesmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemvolveraplanilla:
                volver_a_lista_de_clientes();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void volver_a_lista_de_clientes(){

        FragmentListaDeEntregas entregas = new FragmentListaDeEntregas();
        Bundle datos = new Bundle();
        datos.putInt("idplanilla", _idplanilla);
        datos.putString("token", _token);
        entregas.setArguments(datos);
        interfaz.fragmentTransicion(entregas);

    }
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/////////       TODO EL IMPLEMENTO ONATTACH  ///////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            interfaz = (IFragementTransitions) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */

        }
    }


    private void guardarTodo() {

        ComunicacionSoapGuardarentrega guardarSoap = new ComunicacionSoapGuardarentrega();
        guardarSoap.execute();
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el servicio SOAP    /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private class ComunicacionSoapGuardarentrega extends AsyncTask<String, ArrayList, ArrayList> {

        String NAMESPACE = "http://dsuizaapireparto.com.ar/"; // Espacio de nombres utilizado en nuestro servicio web.
        String URL = "http://181.111.175.138:8093/DsuizaApiReparto.asmx"; // Dirección URL para realizar la conexión con el servicio web.
        String METODO_NAME = "set_estado_entrega"; // Nombre del método web concreto que vamos a ejecutar.
        String SOAP_ACTION = "http://dsuizaapireparto.com.ar/set_estado_entrega"; //Equivalente al anterior, pero en la notación definida por SOAP.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setIndeterminate(false);
            dialog.setMessage("Guardando informacion...");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected ArrayList doInBackground(String... strings) {


            for(int i=0; i<_idplanilladetalleTotal.size(); i++){

                SoapObject request = new SoapObject(NAMESPACE, METODO_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                String valor = _idplanilladetalleTotal.get(i);
                int id_motivo = _arraydemotivos.get(i);

                for(int m =0; m<_idplanilladetalleSI.size();m++){
                    String valor_si =_idplanilladetalleSI.get(m);

                    if (valor == valor_si){
                        entrega = true;
                        request.addProperty("token_tmp", _token);
                        request.addProperty("idplanilladetalle_par",valor);
                        request.addProperty("entregada_par", entrega);
                        request.addProperty("fecha_par",String.valueOf(joda_time));
                        request.addProperty("observaciones_par", _observaciones);
                        request.addProperty("firma_par", "");
                        request.addProperty("foto_par", "");
                        request.addProperty("latitud_par", _latitud);
                        request.addProperty("longitud_par", _longitud);
                        request.addProperty("idmotivo_par", id_motivo);

                        try {
                            httpTransport.call(SOAP_ACTION, envelope);
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                            SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor
                            respuestaString.add(resultado + " - "+mensaje);
                            String mensajeString = mensaje.toString();

                            if(mensajeString.matches("OK")){
                                respuestaString.add(resultado + " - "+mensaje);
                            }
                            else if (mensajeString.matches(
                                    "El valor informado para idplanilladetalle no existe.")){
                                String mensajep="no hag nada";
                            }
                            else
                            {
                                ModeloSalida salida = new ModeloSalida();
                                salida.setToken_tmp(_token);
                                salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                                salida.setEntregada_par(entrega);
                                salida.setFecha_par(String.valueOf(joda_time));
                                salida.setObservaciones_par(_observaciones);
                                salida.setFirma_par("");
                                salida.setFoto_par("");
                                salida.setLatitud_par(_latitud);
                                salida.setLongitud_par(_longitud);
                                salida.setIdmotivo_par(id_motivo);
                                salidas.add(salida);

                            }

                        } catch (HttpResponseException e) {
                            e.printStackTrace();
                            respuestaString.add("error 1  " +String.valueOf(e));
                        } catch (SoapFault soapFault) {
                            soapFault.printStackTrace();
                            respuestaString.add("error 2  " +String.valueOf(soapFault));
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                            respuestaString.add("error 3  " +String.valueOf(e));
                        } catch (IOException e) {
                            e.printStackTrace();
                            respuestaString.add("error 4  " +String.valueOf(e));
                            error4=true;
                        }
                    }
                }
                for(int n =0; n<_idplanilladetalleNO.size();n++){
                    String valor_no = _idplanilladetalleNO.get(n);

                    if (valor==valor_no){
                        entrega = false;
                        // Enviando un parámetro al web service
                        request.addProperty("token_tmp", _token);
                        request.addProperty("idplanilladetalle_par",valor);
                        request.addProperty("entregada_par", entrega);
                        request.addProperty("fecha_par",String.valueOf(joda_time));
                        request.addProperty("observaciones_par", _observaciones);
                        request.addProperty("firma_par", "");
                        request.addProperty("foto_par", "");
                        request.addProperty("latitud_par", _latitud);
                        request.addProperty("longitud_par", _longitud);
                        request.addProperty("idmotivo_par", id_motivo);

                        try {
                            httpTransport.call(SOAP_ACTION, envelope);
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                            SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor
                            respuestaString.add(resultado + " - "+mensaje);
                            String mensajeString = mensaje.toString();
                            if(mensajeString.matches("OK")){
                                respuestaString.add(resultado + " - "+mensaje);
                            }else if (mensajeString.matches(
                                    "El valor informado para idplanilladetalle no existe.")){
                                String mensajep="no hag nada";
                            }
                            else {
                                ModeloSalida salida = new ModeloSalida();
                                salida.setToken_tmp(_token);
                                salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                                salida.setEntregada_par(entrega);
                                salida.setFecha_par(String.valueOf(joda_time));
                                salida.setObservaciones_par(_observaciones);
                                salida.setFirma_par("");
                                salida.setFoto_par("");
                                salida.setLatitud_par(_latitud);
                                salida.setLongitud_par(_longitud);
                                salida.setIdmotivo_par(id_motivo);
                                salidas.add(salida);

                            }


                        } catch (HttpResponseException e) {
                            e.printStackTrace();
                            respuestaString.add("error 1  " +String.valueOf(e));
                        } catch (SoapFault soapFault) {
                            soapFault.printStackTrace();
                            respuestaString.add("error 2  " +String.valueOf(soapFault));
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                            respuestaString.add("error 3  " +String.valueOf(e));
                        } catch (IOException e) {
                            e.printStackTrace();
                            respuestaString.add("error 4  " +String.valueOf(e));
                            error4=true;
                        }
                    }
                }

            }

            return respuestaString;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);

            if(respuestaString != null && !respuestaString.isEmpty() && respuestaString.size()>0){
                dialog.dismiss();
                // Toast.makeText(getActivity(), "respuesta" + s.toString(), Toast.LENGTH_SHORT).show();
                if(salidas.size()!=0){
                    guardarSalida();
                }
                if(error4){
                    guardarSalidaSinInternet();
                }
                FragmentListaDeEntregas lista_de_entregas= new FragmentListaDeEntregas();
                Bundle enviar= new Bundle();
                enviar.putInt("idplanilla", _idplanilla);
                enviar.putString("token", _token);
                lista_de_entregas.setArguments(enviar);
                interfaz.fragmentTransicion(lista_de_entregas);

            } else {
                dialog.dismiss();
                guardarSalidaSinInternet();

                FragmentListaDeEntregas lista_de_entregas= new FragmentListaDeEntregas();
                Bundle enviar= new Bundle();
                enviar.putInt("idplanilla", _idplanilla);
                enviar.putString("token", _token);
                lista_de_entregas.setArguments(enviar);
                interfaz.fragmentTransicion(lista_de_entregas);

            }

        }
    }
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Todo con respecto a la persistencia  /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    private void guardarSalidaSinInternet() {

        for(int x=0; x<_idplanilladetalleTotal.size();x++){
            int id_motivo = _arraydemotivos.get(x);
            String valor = _idplanilladetalleTotal.get(x);


            if(_idplanilladetalleSI.size()!=0){
                for(int y=0;y<_idplanilladetalleSI.size(); y++){
                    String valor_si = _idplanilladetalleSI.get(y);

                    if (valor==valor_si){
                        entrega = true;

                        ModeloSalida salida = new ModeloSalida();
                        salida.setToken_tmp(_token);
                        salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                        salida.setEntregada_par(entrega);
                        salida.setFecha_par(String.valueOf(joda_time));
                        salida.setObservaciones_par(_observaciones);
                        salida.setFirma_par("");
                        salida.setFoto_par("");
                        salida.setLatitud_par(_latitud);
                        salida.setLongitud_par(_longitud);
                        salida.setIdmotivo_par(id_motivo);
                        salidas.add(salida);
                    }

                }
            }
            if(_idplanilladetalleNO.size()!=0){
                for(int z=0; z<_idplanilladetalleNO.size();z++){
                    String valor_no = _idplanilladetalleNO.get(z);

                    if (valor==valor_no){
                        entrega = false;
                        ModeloSalida salida = new ModeloSalida();
                        salida.setToken_tmp(_token);
                        salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                        salida.setEntregada_par(entrega);
                        salida.setFecha_par(String.valueOf(joda_time));
                        salida.setObservaciones_par(_observaciones);
                        salida.setFirma_par("");
                        salida.setFoto_par("");
                        salida.setLatitud_par(_latitud);
                        salida.setLongitud_par(_longitud);
                        salida.setIdmotivo_par(id_motivo);
                        salidas.add(salida);
                    }

                }

            }

        }

        guardarSalida();

    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Todo con respecto a la persistencia  /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    private void guardarSalida() {

        for(int a =0; a<salidas.size();a++){

            String token_tmp = salidas.get(a).getToken_tmp();
            int idplanilladetalle_par = salidas.get(a).getIdplanilladetalle_par();
            boolean entregada_par = salidas.get(a).isEntregada_par();
            String fecha_par = salidas.get(a).getFecha_par();
            String observaciones_par = salidas.get(a).getObservaciones_par();
            String firma_par = salidas.get(a).getFirma_par();
            String foto_par = salidas.get(a).getFoto_par();
            String latitud_par = salidas.get(a).getLatitud_par();
            String longitud_par = salidas.get(a).getLongitud_par();
            int idmotivo_par = salidas.get(a).getIdmotivo_par();

            persistenciaDeSalida = new PersistenciaDeSalida(getContext(), "", null, 1);

            try {
                persistenciaDeSalida.guardarSalidas(token_tmp, idplanilladetalle_par, entregada_par,
                        fecha_par, observaciones_par, firma_par, foto_par, latitud_par, longitud_par,
                        idmotivo_par);

            } catch (SQLiteException e) {

            }
        }

    }
//////////////////////////////////////////// hasta aqui asyntask






}
