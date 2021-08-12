package com.example.dsuiza.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.R;
import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.modelo.ModeloSalida;
import com.example.dsuiza.persistencia.PersistenciaDeSalida;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

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
import java.util.ArrayList;

public class FragmentQR extends Fragment{

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private String token = "";
    private String tokenanterior = "";
    private TextView texto_qr;
    private Button button_qr;

    private String valorToken="";
    String mensaje =null;


    private IFragementTransitions interfaz;
    ProgressDialog dialog;
    ArrayList<String> respuestaString = new ArrayList();
    ArrayList<ModeloSalida> salidas = new ArrayList();
    PersistenciaDeSalida persistenciaDeSalida;
    boolean entrega;
    DateTime joda_time;

    String _qr =null;
    String _token =null;
    String _idCliente=null;
    String _latitud=null;
    String _longitud=null;
    String _fecha=null;
    String _hora=null;
    String _imagen_de_firma=null;
    String _imagen_de_comprobacion=null;
    String _nombre=null;
    String _direccion=null;
    String _localidad=null;
    String _provincia=null;
    String _orden=null;
    String _idVenta=null;
    String _pedido=null;
    String _rubro=null;
    String _bultos=null;
    int _idplanilla;
    int _idplanilladetalle;
    String _observaciones=null;
    ArrayList<Integer> _arraydemotivos = new ArrayList();
    ArrayList<String> _idplanilladetalleSI = new ArrayList();
    ArrayList<String> _idplanilladetalleNO = new ArrayList();
    ArrayList<String> _idplanilladetalleTotal = new ArrayList();


    SurfaceView cameraView;
    CameraSource cameraSource;

    private boolean bandera=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View vista =  inflater.inflate(R.layout.fragment_q_r, container, false);
        texto_qr = (TextView)vista.findViewById(R.id.textoQR);
        cameraView = (SurfaceView) vista.findViewById(R.id.camera_view);
        button_qr = (Button) vista.findViewById(R.id.buttonQR);
        String myHexColor = "#D35400";
        button_qr.setBackgroundColor(Color.parseColor(myHexColor));

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null) {
            valorToken =getArguments().getString("qr");
            _token =getArguments().getString("token");
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
            _fecha =getArguments().getString("fecha");
            _hora =getArguments().getString("hora");
            _latitud =getArguments().getString("latitud");
            _longitud =getArguments().getString("longitud");
            _idplanilla =getArguments().getInt("idplanilla");
            _idplanilladetalle=getArguments().getInt("idplanilladetalle");
            _observaciones=getArguments().getString("observaciones");
            _idplanilladetalleSI=getArguments().getStringArrayList("lista_de_id_si");
            _idplanilladetalleNO=getArguments().getStringArrayList("lista_de_id_no");
            _idplanilladetalleTotal=getArguments().getStringArrayList("lista_de_id");
            _arraydemotivos=getArguments().getIntegerArrayList("list_de_motivos");

            initQR();
        }
        texto_qr.setText("POR FAVOR"+"\n"+
                "PONGA LA CAMARA"+"\n"+
                "SOBRE EL CODIGO QR");
        String data_time=_fecha+" "+_hora;
        DateTimeFormatter datetimeformat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        joda_time = datetimeformat.parseDateTime(data_time);

        button_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarEstadoQR();
            }
        });


        return vista;
    }

    private void verificarEstadoQR() {


        if(mensaje==null){
            Toast.makeText(getContext(),"Escanear QR por favor!",Toast.LENGTH_SHORT).show();
        } else if(mensaje=="OK"){
            llamarADialog();
        } else if (mensaje=="NO") {
            llamarADialog2();
        }

    }

    public void initQR() {

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add getActivity() feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    String myHexColor = "#F7DC6F";
                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    if(token.matches(valorToken)){

                        /////////////////////////////////////0
                        new Thread(new Runnable() {
                            public void run() {
                                mensaje ="OK";
                                texto_qr.setText("FIN DE ESCANEO"+"\n"+
                                        "RESULTADO :"+"EXITOSO");
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        texto_qr.setTextColor(Color.BLACK);
                                        texto_qr.setBackgroundColor(Color.parseColor(myHexColor));

                                    }
                                });

                            }
                        }).start();

                    /////////////////////////////////////1

                    } else {
                        /////////////////////////////////////0
                        new Thread(new Runnable() {
                            public void run() {
                                mensaje ="NO";
                                texto_qr.setText("FIN DE ESCANEO"+"\n"+
                                        "RESULTADO :"+"\n"+
                                        "OCURRIO UN ERROR");
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        texto_qr.setTextColor(Color.BLACK);
                                        texto_qr.setBackgroundColor(Color.parseColor(myHexColor));

                                    }
                                });


                            }
                        }).start();

                        /////////////////////////////////////1

                    }


                }
            }
        });


    }

    public void llamarADialog2(){


        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.mensajedialogdos,null);
        alertDialog.setView(view2);
        alertDialog.show();
        Button buton_continuar = (Button)view2.findViewById(R.id.btndialog2);
        String myHexColor = "#D35400";
        buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));
        TextView txt = view2.findViewById(R.id.txtdialog2);
        txt.setText("ERROR");
        alertDialog.show();
        buton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                _imagen_de_comprobacion ="";
                _imagen_de_firma="";
                String _aclaracion = "NO HAY FIRMA";
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////

                FragmentFotoComprobacion foto = new FragmentFotoComprobacion();
                Bundle datosAEnviar = new Bundle();
                datosAEnviar.putString("fecha", _fecha);
                datosAEnviar.putString("hora", _hora);
                datosAEnviar.putString("latitud", _latitud);
                datosAEnviar.putString("longitud", _longitud);
                datosAEnviar.putString("aclaracion", _aclaracion);
                datosAEnviar.putString("imagen_de_firma", _imagen_de_firma);
                datosAEnviar.putString("imagen_de_comprobacion", _imagen_de_comprobacion);
                datosAEnviar.putString("idCliente", _idCliente);
                datosAEnviar.putString("nombre", _nombre);
                datosAEnviar.putString("direccion", _direccion);
                datosAEnviar.putString("localidad", _localidad);
                datosAEnviar.putString("provincia", _provincia);
                datosAEnviar.putString("qr", _qr);
                datosAEnviar.putString("token", _token);
                datosAEnviar.putString("orden", _orden);
                datosAEnviar.putString("idVenta", _idVenta);
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

                foto.setArguments(datosAEnviar);
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.idContenedor, foto).
                        commit();


            }
        });


}

    public void llamarADialog(){


        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.mensajedialoguno,null);
        alertDialog.setView(view2);
        Button buton_continuar = (Button)view2.findViewById(R.id.btndialog1);


        String myHexColor = "#D35400";
        buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));



        TextView txt = view2.findViewById(R.id.txtdialog1);
        txt.setText("ENTREGA REALIZADA!");
        alertDialog.show();


        buton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                _imagen_de_comprobacion ="";
                _imagen_de_firma="";
                String _aclaracion = "NO HAY FIRMA";
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////

                FragmentFirma firma = new FragmentFirma();
                Bundle datosAEnviar = new Bundle();
                datosAEnviar.putString("fecha", _fecha);
                datosAEnviar.putString("hora", _hora);
                datosAEnviar.putString("latitud", _latitud);
                datosAEnviar.putString("longitud", _longitud);
                datosAEnviar.putString("aclaracion", _aclaracion);
                datosAEnviar.putString("imagen_de_firma", _imagen_de_firma);
                datosAEnviar.putString("imagen_de_comprobacion", _imagen_de_comprobacion);
                datosAEnviar.putString("idCliente", _idCliente);
                datosAEnviar.putString("nombre", _nombre);
                datosAEnviar.putString("direccion", _direccion);
                datosAEnviar.putString("localidad", _localidad);
                datosAEnviar.putString("provincia", _provincia);
                datosAEnviar.putString("qr", _qr);
                datosAEnviar.putString("token", _token);
                datosAEnviar.putString("orden", _orden);
                datosAEnviar.putString("idVenta", _idVenta);
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

                firma.setArguments(datosAEnviar);
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.idContenedor, firma).
                        commit();

            }
        });




    }

    private void guardarTodo() {


        ComunicacionSoapGuardarentrega guardarSoap = new ComunicacionSoapGuardarentrega();
        guardarSoap.execute();
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
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

            }

            return respuestaString;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);
            if(respuestaString != null && !respuestaString.isEmpty() && respuestaString.size()>0){
                dialog.dismiss();
                //Toast.makeText(getActivity(), "respuesta" + s.toString(), Toast.LENGTH_SHORT).show();
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
                    String valor_si = _idplanilladetalleNO.get(y);

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

            } catch (SQLiteException e) {

            }
        }

    }

}