package com.example.dsuiza.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.MainActivity;
import com.example.dsuiza.R;
import com.example.dsuiza.helpers.IFragementTransitions;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;



public class FragmentGuardarEntrega extends Fragment {

    private TextView idCliente, nombre, direccion,localidad, provincia,idVenta, pedido, rubro,bultos, hora, fecha, latitud, longitud, aclaracion;
    private Button button_guardar_entrega;
    private ImageView imagen_de_firma, imagen_de_comprobacion;

    private IFragementTransitions interfaz;
    ProgressDialog dialog;
    ArrayList<String> respuestaString = new ArrayList();

    String mas_observaciones=null;


    String _idCliente=null;
    String _nombre=null;
    String _direccion=null;
    String _localidad=null;
    String _provincia=null;
    String _qr=null;
    String _token=null;
    String _orden=null;
    String _idVenta=null;
    String _pedido=null;
    String _rubro=null;
    String _bultos=null;
    String _fecha=null;
    String _hora = null;
    String _latitud=null;
    String _longitud=null;
    String _aclaracion=null;
    String _imagenString=null;
    String _imagen_de_firma=null;
    int _idplanilla;
    int _idplanilladetalle;
    String _observaciones=null;
    ArrayList<String> _idplanilladetalleSI = new ArrayList();
    ArrayList<String> _idplanilladetalleNO = new ArrayList();
    ArrayList<String> _idplanilladetalleTotal = new ArrayList();
    boolean entrega;
    DateTime joda_time;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_guardar_entrega, container, false);
        idCliente = (TextView) vista.findViewById(R.id.tvGuardarIdCLiente);
        nombre = (TextView) vista.findViewById(R.id.tvGuardarNombre);
        direccion = (TextView) vista.findViewById(R.id.tvGuardarDireccion);
        localidad = (TextView) vista.findViewById(R.id.tvGuardarLocalidad);
        provincia = (TextView) vista.findViewById(R.id.tvGuardarProvincia);
        bultos = (TextView) vista.findViewById(R.id.tvGuardarBultos);
        rubro = (TextView) vista.findViewById(R.id.tvGuardarBultosNo); // es obervaciones ahora
        fecha = (TextView) vista.findViewById(R.id.tvGuardarFecha);
        hora = (TextView) vista.findViewById(R.id.tvGuardarHora);
        aclaracion = (TextView) vista.findViewById(R.id.tvGuardarAclaracion);
        imagen_de_firma = (ImageView) vista.findViewById(R.id.imagenGurdarAclaracion);
        imagen_de_comprobacion = (ImageView) vista.findViewById(R.id.imagenGurdarComprobacion);
        button_guardar_entrega = (Button) vista.findViewById(R.id.button_guardar_entregado);

        if (getArguments() != null) {
            _nombre = getArguments().getString("nombre");
            _idCliente =getArguments().getString("idCliente");
            _direccion =getArguments().getString("direccion");
            _localidad =getArguments().getString("localidad");
            _provincia =getArguments().getString("provincia");
            _qr=getArguments().getString("qr");
            _token=getArguments().getString("token");
            _orden =getArguments().getString("orden");
            _idVenta =getArguments().getString("idVenta");
            _pedido =getArguments().getString("pedido");
            _rubro =getArguments().getString("rubro");
            _bultos =getArguments().getString("bultos");
            _fecha=getArguments().getString("fecha");
            _hora= getArguments().getString("hora");
            _idplanilla= getArguments().getInt("idplanilla");
            _latitud=String.valueOf(getArguments().getString("latitud"));
            _longitud=String.valueOf(getArguments().getString("longitud"));
            _imagenString=getArguments().getString("imagen_de_comprobacion");
            _imagen_de_firma=getArguments().getString("imagen_de_firma");
            _aclaracion=getArguments().getString("aclaracion");
            _idplanilladetalle=getArguments().getInt("idplanilladetalle");
            _observaciones=getArguments().getString("observaciones");
            _idplanilladetalleSI=getArguments().getStringArrayList("lista_de_id_si");
            _idplanilladetalleNO=getArguments().getStringArrayList("lista_de_id_no");
            _idplanilladetalleTotal=getArguments().getStringArrayList("lista_de_id");

            int bultos_no_entregado = _idplanilladetalleNO.size();
            idCliente.setText("CLIENTE N° " + _idCliente);
            nombre.setText("LUGAR : "+ _nombre);
            direccion.setText("DIRECCION : "+_direccion);
            localidad.setText("LOCALIDAD : "+_localidad);
            provincia.setText("PROVINCIA : "+_provincia);
            bultos.setText("BULTOS ENTREGADOS   : "+_bultos);
            rubro.setText("BULTOS SIN ENTREGAR  : "+bultos_no_entregado);
            aclaracion.setText("ACLARACION : " + _aclaracion);

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String _fecha_para_mostrar = dateFormat.format(date);
            fecha.setText("FECHA : " + _fecha_para_mostrar);
            hora.setText("HORA   : " + _hora);

            StringToBitMap(_imagenString, imagen_de_comprobacion);
            String data_time=_fecha+" "+_hora;

            DateTimeFormatter datetimeformat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
            joda_time = datetimeformat.parseDateTime(data_time);

        } else nombre.setText("Error");

        button_guardar_entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ComunicacionSoapGuardarentrega guardarSoap = new ComunicacionSoapGuardarentrega();
            guardarSoap.execute();

            }
        });

        return vista;
    }

    // TODO CONVIERTO STRING A IMAGEN

    public void StringToBitMap(String encodedString, ImageView confirmo_contexto) {
        // convierto el string a bitmap y lo seteo en mi imagView
        String imageDataBytes = encodedString.substring(encodedString.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        confirmo_contexto.setImageBitmap(bitmap);
        StringToBitMap2(_imagen_de_firma, imagen_de_firma);
    }
    public void StringToBitMap2(String encodedString, ImageView confirmo_contexto) {
        // convierto el string a bitmap y lo seteo en mi imagView
        String imageDataBytes = encodedString.substring(encodedString.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        confirmo_contexto.setImageBitmap(bitmap);
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            interfaz = (IFragementTransitions) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */

        }

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

                for(int m =0; m<_idplanilladetalleSI.size();m++){
                    String valor_si =_idplanilladetalleSI.get(m);

                    if (valor == valor_si){
                        entrega = true;

                        request.addProperty("token_tmp", _token);
                        request.addProperty("idplanilladetalle_par",valor);
                        request.addProperty("entregada_par", entrega);
                        request.addProperty("fecha_par",String.valueOf(joda_time));
                        request.addProperty("observaciones_par", _observaciones);
                        request.addProperty("firma_par", _imagen_de_firma);
                        request.addProperty("foto_par", _imagenString);
                        request.addProperty("latitud_par", _latitud);
                        request.addProperty("longitud_par", _longitud);

                        try {
                            httpTransport.call(SOAP_ACTION, envelope);
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                            SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor
                            respuestaString.add(resultado + " - "+mensaje);


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
                    request.addProperty("foto_par", _imagenString);
                    request.addProperty("latitud_par", _latitud);
                    request.addProperty("longitud_par", _longitud);

                    try {
                        httpTransport.call(SOAP_ACTION, envelope);
                        SoapObject response = (SoapObject) envelope.getResponse();
                        SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                        SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor
                        respuestaString.add(resultado + " - "+mensaje);


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
                    }
                }
                }

          }

            return respuestaString;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);
            if(s != null && !s.isEmpty() && s.size()>0){
                dialog.dismiss();
                
                FragmentListaDeEntregas lista_de_entregas= new FragmentListaDeEntregas();
                Bundle enviar= new Bundle();
                enviar.putInt("idplanilla", _idplanilla);
                enviar.putString("token", _token);
                lista_de_entregas.setArguments(enviar);
                interfaz.fragmentTransicion(lista_de_entregas);



            } else {
                dialog.dismiss();
                // este va con errores
                            FragmentListaDeEntregas lista_de_entregas= new FragmentListaDeEntregas();
                            Bundle enviar= new Bundle();
                            enviar.putInt("idplanilla", _idplanilla);
                            enviar.putString("token", _token);
                            lista_de_entregas.setArguments(enviar);
                            interfaz.fragmentTransicion(lista_de_entregas);

            }

        }
    }
//////////////////////////////////////////// hasta aqui asyntask


}