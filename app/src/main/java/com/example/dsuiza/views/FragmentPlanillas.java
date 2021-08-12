
   package com.example.dsuiza.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Helper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.R;
import com.example.dsuiza.adapters.AdapterFarmacias;
import com.example.dsuiza.adapters.AdapterPlanillas;
import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.helpers.OrderComprobantes;
import com.example.dsuiza.modelo.ModeloCliente;
import com.example.dsuiza.modelo.ModeloComprobantes;
import com.example.dsuiza.modelo.ModeloPlanillas;
import com.example.dsuiza.modelo.ModeloSalida;
import com.example.dsuiza.persistencia.PersistenciaDS;
import com.example.dsuiza.persistencia.PersistenciaDeComprobantes;
import com.example.dsuiza.persistencia.PersistenciaDeSalida;
import com.example.dsuiza.persistencia.PersistenciaDeTokens;
import com.example.dsuiza.persistencia.PersistenciaDeZonasYplanillas;
import com.example.dsuiza.persistencia.PersistenciaPlanillas;

import org.jetbrains.annotations.NotNull;
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


public class FragmentPlanillas extends Fragment {

    //// implementando interfaz
    private IFragementTransitions interfaz;

    private String usu=null;
    private String pass=null;
    int contador_de_clientres;

    //// recursos de la vista
    private ListView lista_de_zonas;
    private TextView tvResultado;

    ProgressDialog dialog, dialog2;
    boolean error4=false;
    boolean se_guardo=false;
    private String nuevo_token=null;

      // recursos adapter y persistencia
    ArrayList<ModeloSalida> salidas = new ArrayList();
    ArrayList<ModeloSalida> salidas2 = new ArrayList();

    PersistenciaDeSalida persistenciaDeSalida;

    ArrayList<String> respuestaString = new ArrayList();


    ArrayList<ModeloPlanillas> lista_planillas = new ArrayList();
    ArrayList<ModeloPlanillas> lista_planillas_del_db = new ArrayList();


    ArrayList<ModeloCliente> lista_de_clientes = new ArrayList();
    ArrayList<ModeloComprobantes> lista_comprobantes = new ArrayList();

    AdapterPlanillas clase_adapatador_planillas;



    // otros recursos
    String token = null;
    String _soy =null;

    ///////// recuros de persistencia
    PersistenciaPlanillas persistencia;
    PersistenciaDeZonasYplanillas persistencia_x_zonas;
    PersistenciaDeComprobantes persistencia_x_comprobantes;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_planillas, container, false);
        tvResultado = (TextView) vista.findViewById(R.id.tvResutplanillas);

        lista_de_zonas = (ListView) vista.findViewById(R.id.ListView_planillas);


        if(getArguments() != null){

            _soy = getArguments().getString("soy");

            if(_soy.matches("login")){
                token = getArguments().getString("token");
                ComunicacionSoap com_soap = new ComunicacionSoap();
                com_soap.execute();
            } else if (_soy.matches("comprobantes")){
                        CrearVista();
            } else MostrarError("manejar mejor el error");


        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////// TODO con respecto al click en el item  ///////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        lista_de_zonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModeloPlanillas modelo = (ModeloPlanillas) parent.getItemAtPosition(position);
                int idplanillam = modelo.getIdplanilla();
                String zona    = modelo.getZona();

                if(modelo.getPosicion()==0){

                    modificarposicion(idplanillam);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getLayoutInflater();
                View view2 = inflater.inflate(R.layout.mensaje_planilla,null);
                alertDialog.setView(view2);
                alertDialog.show();
                Button buton_continuar = (Button)view2.findViewById(R.id.btn_mensaje_plnialla);
                String myHexColor = "#D35400";
                buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));
                TextView txt = view2.findViewById(R.id.txt_mensaje_planilla);
                txt.setText("ZONA : "+zona+"\n"+""+
                            "\n"+"¿ ES CORRECTO ?" );
                buton_continuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentListaDeEntregas f1 = new FragmentListaDeEntregas();
                        Bundle args = new Bundle();
                        args.putInt("idplanilla", idplanillam);
                        args.putString("token", token);
                        f1.setArguments(args);
                        interfaz.fragmentTransicion(f1);
                        alertDialog.dismiss();
                    }
                });


            }
        });



        return vista;
    }
/////////////////////////////////////////////////////
/// TODO SOBRE actualizar el valor de posicion //////
/////////////////////////////////////////////////////
    public void modificarposicion(int idp) {

        PersistenciaDeZonasYplanillas admin = new PersistenciaDeZonasYplanillas(getContext(),
                "zonas.db", null, 1);

        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("posicion",1);

        int cantidad = bd.update("table_zonas", valores, "idplanilla" + "=?",
                new String[]{String.valueOf(idp)});
        if(cantidad!=0){
            Toast.makeText(getContext(),"Se ha modificado",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Fallo",Toast.LENGTH_SHORT).show();
        }
        //Cerramos la BD
        bd.close();


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////// TODO EL PROCESO DE OBTENCINO DE PLANILLAS VA AQUI /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    private class ComunicacionSoap extends AsyncTask<String, ArrayList, ArrayList> {

        String NAMESPACE = "http://dsuizaapireparto.com.ar/"; // Espacio de nombres utilizado en nuestro servicio web.
        String URL = "http://181.111.175.138:8093/DsuizaApiReparto.asmx"; // Dirección URL para realizar la conexión con el servicio web.
        String METODO_NAME = "get_planilla_reparto"; // Nombre del método web concreto que vamos a ejecutar.
        String SOAP_ACTION = "http://dsuizaapireparto.com.ar/get_planilla_reparto"; //Equivalente al anterior, pero en la notación definida por SOAP.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setIndeterminate(false);
            dialog.setMessage("Cargando...");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected ArrayList doInBackground(String... strings) {

            persistencia_x_comprobantes = new PersistenciaDeComprobantes(getContext(), "", null, 1);
            persistencia = new PersistenciaPlanillas(getContext(), "",null, 1);
            persistencia_x_zonas = new PersistenciaDeZonasYplanillas(getContext(), "",null, 1);

            SoapObject request = new SoapObject(NAMESPACE, METODO_NAME);
            SoapObject resultado=null;
            SoapPrimitive resultado_resultado=null;
            SoapObject planillas_new=null;
            SoapObject planillas = null;
            SoapPrimitive obejeto_de_planillas = null;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            // Enviando un parámetro al web service
            request.addProperty("token_tmp", token);


            try {
                httpTransport.call(SOAP_ACTION, envelope);

                SoapObject response = (SoapObject) envelope.getResponse();
                resultado = (SoapObject) response.getProperty("resultado");
                resultado_resultado = (SoapPrimitive) resultado.getProperty("resultado");
                SoapPrimitive resultado_mensaje = (SoapPrimitive) resultado.getProperty("mensaje");

                if(resultado_resultado.toString().matches("true") ||
                        resultado_mensaje.toString().matches("OK")){

                    planillas = (SoapObject) response.getProperty("planillas");



                for(int i=0; i<planillas.getPropertyCount(); i++){

                        ModeloPlanillas modeloPlanillas = new ModeloPlanillas();
                        SoapObject objeto = (SoapObject) planillas.getProperty(i);

                        modeloPlanillas.setIdplanilla(Integer.parseInt(objeto.getProperty(0).toString()));
                        modeloPlanillas.setZona(objeto.getProperty(1).toString());
                        modeloPlanillas.setFecha(objeto.getProperty(2).toString());
                        modeloPlanillas.setHora_desde(objeto.getProperty(3).toString());
                        modeloPlanillas.setHora_hasta(objeto.getProperty(4).toString());
                        modeloPlanillas.setPosicion(0);
                        modeloPlanillas.setPcliente((SoapObject) objeto.getProperty(5));
                        modeloPlanillas.setClientes(lista_de_clientes);
                        lista_planillas.add(modeloPlanillas);


                    }


                    for(int i=0; i<lista_planillas.size(); i++){
                        for (int j=0; j<lista_planillas.get(i).getPcliente().getPropertyCount();j++){

                            ModeloCliente modelocliente = new ModeloCliente();
                            SoapObject cliente = (SoapObject)lista_planillas.get(i).getPcliente().getProperty(j);

                            modelocliente.setCliente_idplanilla(lista_planillas.get(i).getIdplanilla());
                            modelocliente.setCliente_idCliente(cliente.getProperty(0).toString());
                            modelocliente.setCliente_nombre(cliente.getProperty(1).toString());
                            modelocliente.setCliente_direccion(cliente.getProperty(2).toString());
                            modelocliente.setCliente_localidad(cliente.getProperty(3).toString());
                            modelocliente.setCliente_provincia(cliente.getProperty(4).toString());
                            modelocliente.setCliente_orden(cliente.getProperty(5).toString());
                            modelocliente.setCliente_qr(cliente.getProperty(6).toString());
                            modelocliente.setCliente_posicion(0);
                            modelocliente.setCcomprobante((SoapObject) cliente.getProperty(7));
                            lista_de_clientes.add(modelocliente);

                        }

                    }

                    for(int i=0;i< lista_de_clientes.size();i++){

                        for(int j=0;j<lista_de_clientes.get(i).getCcomprobante().getPropertyCount(); j++){

                            ModeloComprobantes modeloComprobantes = new ModeloComprobantes();
                            SoapObject comprobante = (SoapObject)lista_de_clientes.get(i).getCcomprobante().getProperty(j);

                            modeloComprobantes.setIdcliente(Integer.parseInt(lista_de_clientes.get(i).getCliente_idCliente()));
                            modeloComprobantes.setComprobante_idVenta(comprobante.getProperty("idventa").toString());
                            modeloComprobantes.setComprobante_pedido(comprobante.getProperty("pedido").toString());
                            modeloComprobantes.setComprobante_rubro(comprobante.getProperty("rubro").toString());
                            modeloComprobantes.setComprobante_bultos(comprobante.getProperty("bultos").toString());
                            modeloComprobantes.setIdplanilladetalle(comprobante.getProperty("idplanilladetalle").toString());
                            lista_comprobantes.add(modeloComprobantes);
                        }

                    }


                } else MostrarError("error doInBackground");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return lista_planillas;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);


            if(s.size() != 0 && !s.isEmpty()){

                if(isExistPlanilla(lista_planillas.get(0).getIdplanilla())){
                    dialog.dismiss();
                    CrearVista();
                } else {
                    guardarDatos();
                    ComunicacioPlanillaDescargada descargada = new ComunicacioPlanillaDescargada();
                    descargada.execute();

                }


            } else {
                verificarRegistros();
                dialog.dismiss();

            }


        }

    }

    public void verificarRegistros() {

        PersistenciaDeZonasYplanillas admin = new PersistenciaDeZonasYplanillas(getContext(),
                "zonas.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_zonas" , null);

        if (fila != null && fila.getCount()>0) {
            CrearVista();
        } else { MostrarError("La cagaste");
            tvResultado.setText("sin planillas " + String.valueOf(lista_planillas));
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// TODO PARA la persistencia planillas         ///////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void guardarDatos() {

        persistencia_x_comprobantes = new PersistenciaDeComprobantes(getContext(), "", null, 1);
        persistencia = new PersistenciaPlanillas(getContext(), "",null, 1);
        persistencia_x_zonas = new PersistenciaDeZonasYplanillas(getContext(), "",null, 1);

        // aqui se guarda cada planilla

        for(int n =0; n< lista_planillas.size(); n++){

            int idplanilla = lista_planillas.get(n).getIdplanilla();
            int posicion   = lista_planillas.get(n).getPosicion();
            String zona    = lista_planillas.get(n).getZona();
            String fecha   = lista_planillas.get(n).getFecha();
            String desde   = lista_planillas.get(n).getHora_desde();
            String hasta   = lista_planillas.get(n).getHora_hasta();

            try {
                    persistencia_x_zonas.guardarPlanillaXZona(idplanilla, posicion
                            , zona, fecha, desde, hasta);
                    Toast.makeText(getContext(), "Guardado Planillas" +idplanilla, Toast.LENGTH_SHORT).show();

            }catch(SQLiteException e){
                    Toast.makeText(getContext(), "planilla  ya existe" +e, Toast.LENGTH_SHORT).show();

            }

        }

        ///////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////
        /////// insercion de clientes por cada planillas  /////////
        ///////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////
            int rechazo =0;
        for(int x=0; x<lista_planillas.size(); x++){
            for(int y = 0; y<lista_de_clientes.size(); y++){

                if(lista_de_clientes.get(y).getCliente_idplanilla() == lista_planillas.get(x).getIdplanilla()){

                    int idplanillac  = lista_de_clientes.get(y).getCliente_idplanilla();
                    String idCliente = lista_de_clientes.get(y).getCliente_idCliente();
                    String nombre    = lista_de_clientes.get(y).getCliente_nombre();
                    String direccion = lista_de_clientes.get(y).getCliente_direccion();
                    String localidad = lista_de_clientes.get(y).getCliente_localidad();
                    String provincia = lista_de_clientes.get(y).getCliente_provincia();
                    String orden     = lista_de_clientes.get(y).getCliente_orden();
                    String qr        = lista_de_clientes.get(y).getCliente_qr();
                    int posicionm    = lista_de_clientes.get(y).getCliente_posicion();


                    try {
                        persistencia.guardarPlanilla(idplanillac, Integer.parseInt(idCliente), nombre, direccion, localidad, provincia,
                                token, Integer.parseInt(orden), qr, posicionm);
                        Toast.makeText(getContext(), "Cliente  guardado" +idCliente, Toast.LENGTH_SHORT).show();
                    }catch(SQLiteException e){
                     //   Toast.makeText(getContext(), "Cliente  ya existe" +e, Toast.LENGTH_SHORT).show();
                    }

                }else rechazo++;

            }
        }

        int rechazados_comprobantes=0;
        int rechazados_clientes=0;

        for(int m=0; m<lista_planillas.size(); m++){
            for(int n=0; n<lista_de_clientes.size(); n++){

                if(lista_planillas.get(m).getIdplanilla() == lista_de_clientes.get(n).getCliente_idplanilla()){


                    for (int o=0; o<lista_comprobantes.size(); o++){


                        if (Integer.parseInt(lista_de_clientes.get(n).getCliente_idCliente()) == lista_comprobantes.get(o).getIdcliente()
                        ){
                            int idplanilla = lista_de_clientes.get(n).getCliente_idplanilla();
                            int idclientec = lista_comprobantes.get(o).getIdcliente();
                            String idVenta = lista_comprobantes.get(o).getComprobante_idVenta();
                            String pedido  = lista_comprobantes.get(o).getComprobante_pedido();
                            String rubro   = lista_comprobantes.get(o).getComprobante_rubro();
                            String bultos  = lista_comprobantes.get(o).getComprobante_bultos();
                            String fecha   = lista_planillas.get(m).getFecha();
                            String detalle = lista_comprobantes.get(o).getIdplanilladetalle();

                            try {
                                persistencia_x_comprobantes.guardarcomprobantes(idplanilla, idclientec,
                                        Integer.parseInt(idVenta), pedido, rubro, bultos, fecha, Integer.parseInt(detalle));
                            }catch(SQLiteException e){
                               // Toast.makeText(getContext(), "comprobante ya existe" +e, Toast.LENGTH_SHORT).show();
                            }

                        } else{rechazados_comprobantes++;}

                    }

                }else{rechazados_clientes++;}

         }

        }

        dialog.dismiss();
        CrearVista();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// TODO PARA La consulta de planillas         ///////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    private void CrearVista() {

        PersistenciaDeZonasYplanillas admin = new PersistenciaDeZonasYplanillas(getContext(),
                "zonas.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_zonas" , null);

        if (fila != null && fila.getCount()>0) {
            if(fila.moveToFirst()){

                do{

                    ModeloPlanillas modelo = new ModeloPlanillas();

                    modelo.setIdplanilla(fila.getInt(0));
                    modelo.setPosicion(fila.getInt(1));
                    modelo.setZona(fila.getString(2));
                    modelo.setFecha(fila.getString(3));
                    modelo.setHora_desde(fila.getString(4));
                    modelo.setHora_hasta(fila.getString(5));

                    lista_planillas_del_db.add(modelo);

                }while(fila.moveToNext());

                clase_adapatador_planillas = new AdapterPlanillas(getContext(),lista_planillas_del_db);
                lista_de_zonas.setAdapter(clase_adapatador_planillas);
                tvResultado.setText("LISTA DE ENTREGAS!");
                fila.close();
                bd.close();

            } else {
                MostrarError("Error en el envio de parametros");     }

        } else {MostrarError("YA HICISTE TODAS LAS PLANILLAS"); // filas vacias o nulas
        }


    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el servicio SOAP    /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private class ComunicacioPlanillaDescargada extends AsyncTask<String, Void, String> {
        String NAMESPACE = "http://dsuizaapireparto.com.ar/";
        String URL = "http://181.111.175.138:8093/DsuizaApiReparto.asmx";
        String METODO_NAME = "set_planilla_reparto_bajada";
        String SOAP_ACTION = "http://dsuizaapireparto.com.ar/set_planilla_reparto_bajada";


        @Override
        protected String doInBackground(String... strings) {

            for (int k=0; k<lista_planillas.size(); k++){
                int idplanilla_par = lista_planillas.get(k).getIdplanilla();

                SoapObject request = new SoapObject(NAMESPACE, METODO_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransport = new HttpTransportSE(URL);

                // Enviando un parámetro al web service
                request.addProperty("token_tmp", token);
                request.addProperty("idplanillareparto_par",idplanilla_par );


                try {
                    httpTransport.call(SOAP_ACTION, envelope);
                    SoapObject response = (SoapObject) envelope.getResponse();

                    SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                    SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                } catch (SoapFault soapFault) {
                    soapFault.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// TODO PARA EL MANEJO DE ERRORES /           ///////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    private void MostrarError(String msg) {
        tvResultado.setText(msg);
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

////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
/////////       TODO EL IMPLEMENTO de consulta de planillas  ///////////
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////


    public boolean isExistPlanilla(int planilla) {
        boolean estado;

        PersistenciaDeZonasYplanillas admin = new PersistenciaDeZonasYplanillas(getContext(),
                "zonas.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_zonas WHERE idplanilla = '"+planilla+"'" , null);

        if (fila != null ) {
            if(fila.moveToFirst()){
                estado = true;
                fila.close();
                bd.close();

            } else { estado = false; }
        } else estado = false;

        return estado;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.planillasmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemguardaplanilla:
                consultarUyC();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO EL MANEJO DE Guardar planillas pendientes  ////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

private void consultarUyC() {

        if(!tvResultado.getText().toString().matches("LISTA DE ENTREGAS!")){
            consultar();
        } else if (tvResultado.getText().toString().matches("LISTA DE ENTREGAS!")){
            Toast.makeText(getActivity(), "Tiene pedidos pendientes!!!", Toast.LENGTH_SHORT).show();
        }

}

    private void consultar() {
        PersistenciaDS admin = new PersistenciaDS(getContext(),
                "dbusuarios.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_usuarios" , null);

        if (fila != null) {
            if(fila.moveToFirst()){
                usu = fila.getString(1);
                pass = fila.getString(2);

                ComunicacionSoapGetToken com_get = new ComunicacionSoapGetToken();
                com_get.execute();

                fila.close();
                bd.close();

            } else MostrarError("Sin datos de usuario");

        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO EL MANEJO DE MENU TOOLBAR           ///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

    public void guardarDatosWS(){


        PersistenciaDeSalida admin = new PersistenciaDeSalida(getContext(),
                "salidas.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_salidas" , null);

        if (fila != null && fila.getCount()>0) {
            if(fila.moveToFirst()){

                do{

                ModeloSalida salida = new ModeloSalida();

                salida.setToken_tmp(fila.getString(0));
                salida.setIdplanilladetalle_par(fila.getInt(1));
                boolean entregasqlite = fila.getInt(2) > 0;
                salida.setEntregada_par(entregasqlite);
                salida.setFecha_par(fila.getString(3)); // fecha
                salida.setObservaciones_par(fila.getString(4)); // observacion
                salida.setFirma_par(fila.getString(5)); // firma
                salida.setFoto_par(fila.getString(6)); // foto
                salida.setLatitud_par(fila.getString(7)); // latitud
                salida.setLongitud_par(fila.getString(8)); // longitud
                salida.setIdmotivo_par(fila.getInt(9)); // idmotivo
                salidas.add(salida);
                }while(fila.moveToNext());

                ComunicacionSoapSalidas comsalida = new ComunicacionSoapSalidas();
                comsalida.execute();

                fila.close();
                bd.close();

            } else MostrarError("Sin salidas que guardar");

        }

    }


    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el servicio SOAP    /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private class ComunicacionSoapSalidas extends AsyncTask<String, ArrayList, ArrayList> {

        String NAMESPACE = "http://dsuizaapireparto.com.ar/"; // Espacio de nombres utilizado en nuestro servicio web.
        String URL = "http://181.111.175.138:8093/DsuizaApiReparto.asmx"; // Dirección URL para realizar la conexión con el servicio web.
        String METODO_NAME = "set_estado_entrega"; // Nombre del método web concreto que vamos a ejecutar.
        String SOAP_ACTION = "http://dsuizaapireparto.com.ar/set_estado_entrega"; //Equivalente al anterior, pero en la notación definida por SOAP.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog2=new ProgressDialog(getActivity());
            dialog2.setIndeterminate(false);
            dialog2.setMessage("Guardando informacion...");
            dialog2.setCancelable(false);
            dialog2.show();

        }

        @Override
        protected ArrayList doInBackground(String... strings) {


            for(int i=0; i<salidas.size(); i++){

                SoapObject request = new SoapObject(NAMESPACE, METODO_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransport = new HttpTransportSE(URL);


                request.addProperty("token_tmp", nuevo_token);
                request.addProperty("idplanilladetalle_par",salidas.get(i).getIdplanilladetalle_par());
                request.addProperty("entregada_par", salidas.get(i).isEntregada_par());
                request.addProperty("fecha_par",salidas.get(i).getFecha_par());
                request.addProperty("observaciones_par", salidas.get(i).getObservaciones_par());
                request.addProperty("firma_par", salidas.get(i).getFirma_par());
                request.addProperty("foto_par", salidas.get(i).getFoto_par());
                request.addProperty("latitud_par", salidas.get(i).getLatitud_par());
                request.addProperty("longitud_par", salidas.get(i).getLongitud_par());
                request.addProperty("idmotivo_par", salidas.get(i).getIdmotivo_par());

                        try {
                            httpTransport.call(SOAP_ACTION, envelope);
                            SoapObject response = (SoapObject) envelope.getResponse();
                            SoapPrimitive resultado = (SoapPrimitive) response.getProperty("resultado");
                            SoapPrimitive mensaje = (SoapPrimitive) response.getProperty("mensaje"); // pero esta es mejor
                            String mensajeString = mensaje.toString();

                            if(mensajeString.matches("OK")){
                                respuestaString.add(resultado + " - "+mensaje);
                              ///  se_guardo=true;
                            } else if (mensajeString.matches(
                                    "El valor informado para idplanilladetalle no existe.")){
                                String mensajep="no hag nada";
                            }
                            else
                            {
                                ModeloSalida salida = new ModeloSalida();
                                salida.setToken_tmp(salidas.get(i).getToken_tmp());
                                salida.setIdplanilladetalle_par(salidas.get(i).getIdplanilladetalle_par());
                                salida.setEntregada_par(salidas.get(i).isEntregada_par());
                                salida.setFecha_par(salidas.get(i).getFecha_par());
                                salida.setObservaciones_par(salidas.get(i).getObservaciones_par());
                                salida.setFirma_par(salidas.get(i).getFirma_par());
                                salida.setFoto_par(salidas.get(i).getFoto_par());
                                salida.setLatitud_par(salidas.get(i).getLatitud_par());
                                salida.setLongitud_par(salidas.get(i).getLongitud_par());
                                salida.setIdmotivo_par(salidas.get(i).getIdmotivo_par());
                                salidas2.add(salida);

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
                            salida.setToken_tmp(salidas.get(i).getToken_tmp());
                            salida.setIdplanilladetalle_par(salidas.get(i).getIdplanilladetalle_par());
                            salida.setEntregada_par(salidas.get(i).isEntregada_par());
                            salida.setFecha_par(salidas.get(i).getFecha_par());
                            salida.setObservaciones_par(salidas.get(i).getObservaciones_par());
                            salida.setFirma_par(salidas.get(i).getFirma_par());
                            salida.setFoto_par(salidas.get(i).getFoto_par());
                            salida.setLatitud_par(salidas.get(i).getLatitud_par());
                            salida.setLongitud_par(salidas.get(i).getLongitud_par());
                            salida.setIdmotivo_par(salidas.get(i).getIdmotivo_par());
                            salidas2.add(salida);
                        }


            }

            return respuestaString;
        }

        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);

            if(respuestaString != null && !respuestaString.isEmpty() && respuestaString.size()>0){
                dialog2.dismiss();
                Toast.makeText(getActivity(), "Se guardo exitosamente!", Toast.LENGTH_SHORT).show();
                borrarSalidas();
                if(salidas2.size()!=0){
                    guardarSalida();
                }


            } else {
                dialog2.dismiss();
                guardarSalidaSinInternet();

            }

        }
    }
//////////////////////////////////////////// hasta aqui asyntask

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Todo con respecto a la persistencia  /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    private void guardarSalidaSinInternet() {

        for(int i=0; i<salidas.size(); i++){

            ModeloSalida salida = new ModeloSalida();
            salida.setToken_tmp(salidas.get(i).getToken_tmp());
            salida.setIdplanilladetalle_par(salidas.get(i).getIdplanilladetalle_par());
            salida.setEntregada_par(salidas.get(i).isEntregada_par());
            salida.setFecha_par(salidas.get(i).getFecha_par());
            salida.setObservaciones_par(salidas.get(i).getObservaciones_par());
            salida.setFirma_par(salidas.get(i).getFirma_par());
            salida.setFoto_par(salidas.get(i).getFoto_par());
            salida.setLatitud_par(salidas.get(i).getLatitud_par());
            salida.setLongitud_par(salidas.get(i).getLongitud_par());
            salida.setIdmotivo_par(salidas.get(i).getIdmotivo_par());
            salidas2.add(salida);

        }

        guardarSalida();

    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Todo con respecto a la persistencia  /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    private void guardarSalida() {
        // primero borro lo anterior
        borrarClientes();
        borrarComprobantes();
        borrarSalidas();

        for(int a =0; a<salidas2.size();a++){

            String token_tmp = salidas2.get(a).getToken_tmp();
            int idplanilladetalle_par = salidas2.get(a).getIdplanilladetalle_par();
            boolean entregada_par = salidas2.get(a).isEntregada_par();
            String fecha_par = salidas2.get(a).getFecha_par();
            String observaciones_par = salidas2.get(a).getObservaciones_par();
            String firma_par = salidas2.get(a).getFirma_par();
            String foto_par = salidas2.get(a).getFoto_par();
            String latitud_par = salidas2.get(a).getLatitud_par();
            String longitud_par = salidas2.get(a).getLongitud_par();
            int idmotivo_par = salidas2.get(a).getIdmotivo_par();

            persistenciaDeSalida = new PersistenciaDeSalida(getContext(), "", null, 1);

            try {
                persistenciaDeSalida.guardarSalidas(token_tmp, idplanilladetalle_par, entregada_par,
                        fecha_par, observaciones_par, firma_par, foto_par, latitud_par, longitud_par,
                        idmotivo_par);

            } catch (SQLiteException e) {

            }
        }

    }

    private void borrarClientes() {

        //// de la db clientes
        persistencia=new PersistenciaPlanillas(getContext(), "planillas.db", null, 1);
        SQLiteDatabase bd3 = persistencia.getReadableDatabase();
        bd3.execSQL("DELETE FROM table_planillas");
        bd3.close();
    }
    private void borrarComprobantes() {
        //// de la db comprobantes
        persistencia_x_comprobantes=new PersistenciaDeComprobantes(getContext(), "comprobantes.db", null, 1);
        SQLiteDatabase bd2 = persistencia_x_comprobantes.getReadableDatabase();
        bd2.execSQL("DELETE FROM table_comprobantes");
        bd2.close();
    }
    private void borrarSalidas() {

        //// de la db salida
        persistenciaDeSalida=new PersistenciaDeSalida(getContext(), "salidas.db", null, 1);
        SQLiteDatabase bd = persistenciaDeSalida.getReadableDatabase();
        bd.execSQL("DELETE FROM table_salidas");
        bd.close();

    }
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el servicio SOAP    /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private class ComunicacionSoapGetToken extends AsyncTask<String, Void, String> {

        String NAMESPACE = "http://dsuizaapitoken.com.ar/";
        String URL = "http://181.111.175.138:8901/DsuizaApiToken.asmx";
        String METODO_NAME = "get_token";
        String SOAP_ACTION = "http://dsuizaapitoken.com.ar/get_token";


        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METODO_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            // Enviando un parámetro al web service
            request.addProperty("api_par", "DsuizaApiReparto");
            request.addProperty("usuario_par", usu);
            request.addProperty("contraseña_par", pass);

            try {
                httpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();

                SoapPrimitive respuestaresultado = (SoapPrimitive) response.getProperty("resultado");
                SoapPrimitive respuestatoken = (SoapPrimitive) response.getProperty("valor"); // pero esta es mejor


                if( respuestaresultado.toString().matches("true")){
                    nuevo_token = respuestatoken.toString();
                } else {nuevo_token = "RESULTADO: FALSE";}


            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return nuevo_token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && !s.isEmpty() && s.length()>30){
                //
                Toast.makeText(getActivity(), "se recogio token!", Toast.LENGTH_SHORT).show();
                guardarDatosWS();

            } else MostrarError("Error de token");


        }
    }


}