package com.example.dsuiza.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dsuiza.MainActivity;
import com.example.dsuiza.helpers.CaptureBitmapView;
import com.example.dsuiza.R;
import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.modelo.ModeloSalida;
import com.example.dsuiza.persistencia.PersistenciaDeSalida;
import com.example.dsuiza.persistencia.PersistenciaDeTokens;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


public class FragmentFirma extends Fragment {

    Bitmap signature=null;

    private EditText aclaracion;
    private Button confirmar, tomar_foto;
    MainActivity mainActivity;

    private IFragementTransitions interfaz;
    ProgressDialog dialog;
    boolean entrega;
    DateTime joda_time;

    ArrayList<String> respuestaString = new ArrayList();
    ArrayList<ModeloSalida> salidas = new ArrayList();
    PersistenciaDeSalida persistenciaDeSalida;

    String _qr =null;
    String _token =null;
    String _idCliente=null;
    String _imagen_de_comprobacion =null;
    String _nombre=null;
    String _direccion=null;
    String _localidad=null;
    String _provincia=null;
    String _orden=null;
    String _idVenta=null;
    String _pedido=null;
    String _rubro=null;
    String _bultos=null;
    String _fecha=null;
    String _hora= null;
    String _latitud=null;
    String _longitud=null;
    String _imagen_de_firma=null;
    String _imagen_de_foto=null;
    String _aclaro=null;
    int _idplanilla;
    int _idplanilladetalle;
    String _observaciones=null;
    ArrayList<Integer> _arraydemotivos = new ArrayList();
    ArrayList<String> _idplanilladetalleSI = new ArrayList();
    ArrayList<String> _idplanilladetalleNO = new ArrayList();
    ArrayList<String> _idplanilladetalleTotal = new ArrayList();


    private boolean bandera=false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_firma, container, false);
        aclaracion = (EditText) vista.findViewById(R.id.etAclaracion);
        confirmar = (Button) vista.findViewById(R.id.buttonFirmayAclaracion);
        tomar_foto = (Button) vista.findViewById(R.id.buttonPasarAFoto);

        String myHexColor = "#D35400";
        confirmar.setBackgroundColor(Color.parseColor(myHexColor));
        tomar_foto.setBackgroundColor(Color.parseColor(myHexColor));


        LinearLayout mContent = (LinearLayout) vista.findViewById(R.id.signLayout);
        CaptureBitmapView mSig = new CaptureBitmapView(getActivity(), null);
        mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);



        if (getArguments() != null) {
            _nombre = getArguments().getString("nombre");
            _idCliente =getArguments().getString("idCliente");
            _direccion =getArguments().getString("direccion");
            _localidad =getArguments().getString("localidad");
            _provincia =getArguments().getString("provincia");
            _orden =getArguments().getString("orden");
            _idVenta =getArguments().getString("idVenta");
            _pedido =getArguments().getString("pedido");
            _rubro =getArguments().getString("rubro");
            _bultos =getArguments().getString("bultos");
            _qr =getArguments().getString("qr");
            _token =getArguments().getString("token");
            _fecha =getArguments().getString("fecha");
            _hora =getArguments().getString("hora");
            _latitud =getArguments().getString("latitud");
            _longitud =getArguments().getString("longitud");
            _idplanilla=getArguments().getInt("idplanilla");
            _idplanilladetalle=getArguments().getInt("idplanilladetalle");
            _observaciones=getArguments().getString("observaciones");
            _idplanilladetalleSI=getArguments().getStringArrayList("lista_de_id_si");
            _idplanilladetalleNO=getArguments().getStringArrayList("lista_de_id_no");
            _idplanilladetalleTotal=getArguments().getStringArrayList("lista_de_id");
            _arraydemotivos=getArguments().getIntegerArrayList("list_de_motivos");

        }
        String data_time=_fecha+" "+_hora;
        DateTimeFormatter datetimeformat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        joda_time = datetimeformat.parseDateTime(data_time);

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _aclaro = aclaracion.getText().toString();
                _imagen_de_foto ="";
                signature = mSig.getBitmap();
                _imagen_de_firma = convertir_bitmap_a_string();
                _imagen_de_comprobacion ="";

                if(!_aclaro.isEmpty() && _aclaro != null && _imagen_de_firma!= null && !_imagen_de_firma.isEmpty()) {
                    FragmentGuardarEntrega guardarEntrega = new FragmentGuardarEntrega();
                    Bundle datosAEnviar = new Bundle();

                    datosAEnviar.putString("imagen_de_firma", _imagen_de_firma);
                    datosAEnviar.putString("imagen_de_comprobacion", _imagen_de_comprobacion);
                    datosAEnviar.putString("hora", _hora);
                    datosAEnviar.putString("fecha", _fecha);
                    datosAEnviar.putString("latitud", _latitud);
                    datosAEnviar.putString("longitud", _longitud);
                    datosAEnviar.putString("aclaracion", _aclaro);
                    datosAEnviar.putString("idCliente", _idCliente);
                    datosAEnviar.putString("nombre", _nombre);
                    datosAEnviar.putString("direccion", _direccion);
                    datosAEnviar.putString("localidad", _localidad);
                    datosAEnviar.putString("provincia", _provincia);
                    datosAEnviar.putString("orden", _orden);
                    datosAEnviar.putString("idVenta", _idVenta);
                    datosAEnviar.putString("qr", _qr);
                    datosAEnviar.putString("token", _token);
                    datosAEnviar.putString("pedido", _pedido);
                    datosAEnviar.putString("rubro", _rubro);
                    datosAEnviar.putString("bultos", _bultos);
                    datosAEnviar.putInt("idplanilla", _idplanilla);
                    datosAEnviar.putInt("idplanilladetalle", _idplanilladetalle);
                    datosAEnviar.putString("observaciones", _observaciones);
                    datosAEnviar.putStringArrayList("lista_de_id_si", _idplanilladetalleSI);
                    datosAEnviar.putStringArrayList("lista_de_id_no", _idplanilladetalleNO);
                    datosAEnviar.putStringArrayList("lista_de_id", _idplanilladetalleTotal);
                    datosAEnviar.putIntegerArrayList("list_de_motivos", _arraydemotivos);

                    guardarTodo();
                }

                aclaracion.setText("");
                mSig.ClearCanvas();
            }
        });

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////7 PARA TOMAR LA FOTO ////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
        tomar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _aclaro = aclaracion.getText().toString();
                signature = mSig.getBitmap();
                _imagen_de_firma = convertir_bitmap_a_string();

                if(!_aclaro.isEmpty() && _aclaro != null && _imagen_de_firma!= null && !_imagen_de_firma.isEmpty()) {
                    FragmentFotoComprobacion tomarFoto = new FragmentFotoComprobacion();
                    Bundle datosAEnviar = new Bundle();

                    datosAEnviar.putString("imagen_de_firma", _imagen_de_firma);
                    datosAEnviar.putString("aclaracion", _aclaro);
                    datosAEnviar.putString("idCliente", _idCliente);
                    datosAEnviar.putString("nombre", _nombre);
                    datosAEnviar.putString("direccion", _direccion);
                    datosAEnviar.putString("localidad", _localidad);
                    datosAEnviar.putString("provincia", _provincia);
                    datosAEnviar.putString("orden", _orden);
                    datosAEnviar.putString("idVenta", _idVenta);
                    datosAEnviar.putString("qr", _qr);
                    datosAEnviar.putString("token", _token);
                    datosAEnviar.putString("pedido", _pedido);
                    datosAEnviar.putString("rubro", _rubro);
                    datosAEnviar.putString("bultos", _bultos);
                    datosAEnviar.putString("hora", _hora);
                    datosAEnviar.putString("fecha", _fecha);
                    datosAEnviar.putString("latitud", _latitud);
                    datosAEnviar.putString("longitud", _longitud);
                    datosAEnviar.putInt("idplanilla", _idplanilla);
                    datosAEnviar.putInt("idplanilladetalle", _idplanilladetalle);
                    datosAEnviar.putString("observaciones", _observaciones);
                    datosAEnviar.putStringArrayList("lista_de_id_si", _idplanilladetalleSI);
                    datosAEnviar.putStringArrayList("lista_de_id_no", _idplanilladetalleNO);
                    datosAEnviar.putStringArrayList("lista_de_id", _idplanilladetalleTotal);
                    datosAEnviar.putIntegerArrayList("list_de_motivos", _arraydemotivos);

                    tomarFoto.setArguments(datosAEnviar);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.idContenedor,
                            tomarFoto).addToBackStack(null).commit();
                }

                aclaracion.setText("");
                mSig.ClearCanvas();
            }
        });

        return vista;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////  METODOS PARA PASAR DE BITMAP A STRING ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String convertir_bitmap_a_string(){


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signature.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] imagen = stream.toByteArray();
        return  Base64.encodeToString(imagen, Base64.DEFAULT);

    }


    public void callParentMethod(){
        getActivity().onBackPressed();
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
                        Log.i("VALOR : ", valor);
                        request.addProperty("entregada_par", entrega);
                        request.addProperty("fecha_par",String.valueOf(joda_time));
                        request.addProperty("observaciones_par", _observaciones);
                        request.addProperty("firma_par", _imagen_de_firma);
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
                                salida.setFirma_par(_imagen_de_firma);
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
                            ModeloSalida salida = new ModeloSalida();
                            salida.setToken_tmp(_token);
                            salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                            salida.setEntregada_par(entrega);
                            salida.setFecha_par(String.valueOf(joda_time));
                            salida.setObservaciones_par(_observaciones);
                            salida.setFirma_par(_imagen_de_firma);
                            salida.setFoto_par("");
                            salida.setLatitud_par(_latitud);
                            salida.setLongitud_par(_longitud);
                            salida.setIdmotivo_par(id_motivo);
                            salidas.add(salida);
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
                        request.addProperty("firma_par", _imagen_de_firma);
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
                            else {
                                ModeloSalida salida = new ModeloSalida();
                                salida.setToken_tmp(_token);
                                salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                                salida.setEntregada_par(entrega);
                                salida.setFecha_par(String.valueOf(joda_time));
                                salida.setObservaciones_par(_observaciones);
                                salida.setFirma_par(_imagen_de_firma);
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

                            ModeloSalida salida = new ModeloSalida();
                            salida.setToken_tmp(_token);
                            salida.setIdplanilladetalle_par(Integer.parseInt(valor));
                            salida.setEntregada_par(entrega);
                            salida.setFecha_par(String.valueOf(joda_time));
                            salida.setObservaciones_par(_observaciones);
                            salida.setFirma_par(_imagen_de_firma);
                            salida.setFoto_par("");
                            salida.setLatitud_par(_latitud);
                            salida.setLongitud_par(_longitud);
                            salida.setIdmotivo_par(id_motivo);
                            salidas.add(salida);
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

                FragmentListaDeEntregas lista_de_entregas= new FragmentListaDeEntregas();
                Bundle enviar= new Bundle();
                enviar.putInt("idplanilla", _idplanilla);
                enviar.putString("token", _token);
                lista_de_entregas.setArguments(enviar);
                interfaz.fragmentTransicion(lista_de_entregas);

            } else {
                dialog.dismiss();
                guardarSalidaSinInternet();
               // Toast.makeText(getActivity(), "error" + s.toString(), Toast.LENGTH_SHORT).show();
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
                        salida.setFirma_par(_imagen_de_firma);
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
                        salida.setFirma_par(_imagen_de_firma);
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
                   // Toast.makeText(getActivity(), "SAVE "+salidas.get(a).getIdplanilladetalle_par() +"\n" , Toast.LENGTH_SHORT).show();

                } catch (SQLiteException e) {

                }
        }

    }
//////////////////////////////////////////// hasta aqui asyntask

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            interfaz = (IFragementTransitions) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */

        }

    }
}

