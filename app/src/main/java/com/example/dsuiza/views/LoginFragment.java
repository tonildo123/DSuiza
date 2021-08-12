package com.example.dsuiza.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.R;
import com.example.dsuiza.persistencia.PersistenciaDS;
import com.example.dsuiza.persistencia.PersistenciaDeComprobantes;
import com.example.dsuiza.persistencia.PersistenciaDeTokens;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {
    private EditText usuario, password;
    private Button login, register, reset;
    private TextView texto_eventos;
    String usu = null;
    String pass = null;
    String _expiracion=null;
    private String _fecha, _hora, _fechaDB, _horaDB, _tokensDB;

    PersistenciaDeTokens persistenciaDeToken;

    float sizeButton = 30;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VerificarLogin();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_login, container, false);
        usuario = (EditText) vista.findViewById(R.id.etUsers2);
        password = (EditText) vista.findViewById(R.id.etPassword2);
        login = (Button) vista.findViewById(R.id.bLogin2);
        register = (Button) vista.findViewById(R.id.bRegister2);
        reset = (Button) vista.findViewById(R.id.buttonReset);

        String myHexColor = "#D35400";
        login.setBackgroundColor(Color.parseColor(myHexColor));
        register.setBackgroundColor(Color.parseColor(myHexColor));
        reset.setBackgroundColor(Color.parseColor(myHexColor));


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamar_a_ventana_registro();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usu = usuario.getText().toString();
                pass = password.getText().toString();
                llamar_a_validar_campos(usu, pass);

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetearSistema();
            }
        });


        return vista;
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //// Validacion de campos y consulta a Base de Datos  /////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    public void llamar_a_validar_campos(String user, String pass) {

        PersistenciaDS admin = new PersistenciaDS(getContext(),
                "dbusuarios.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT id FROM table_usuarios WHERE user = '"+user+"' AND password = '"+pass+"'" , null);

        if (fila != null) {
            if(fila.moveToFirst()){

                Toast.makeText(getContext(), "Bien hecho!" ,
                        Toast.LENGTH_SHORT).show();
                fila.close();
                bd.close();
                ComunicacionSoapLogin com_soap= new ComunicacionSoapLogin();
                com_soap.execute();
                //verifyToken(user,pass);
            } else MostrarError("Ingrese bien usuario y contraseña");

        }

    }
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /////////// Llamado al fragment Registro    ///////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    public void llamar_a_ventana_registro(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.idContenedor, new FragmentRegister()).addToBackStack(null).commit();
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el servicio SOAP    /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private class ComunicacionSoapLogin extends AsyncTask<String, Void, String> {
        String tokens;

        String NAMESPACE = "http://dsuizaapitoken.com.ar/";
        // Espacio de nombres utilizado en nuestro servicio web.
        String URL = "http://181.111.175.138:8901/DsuizaApiToken.asmx";
        // Dirección URL para realizar la conexión con el servicio web.
        String METODO_NAME = "get_token";
        // Nombre del método web concreto que vamos a ejecutar.
        String SOAP_ACTION = "http://dsuizaapitoken.com.ar/get_token";
        //Equivalente al anterior, pero en la notación definida por SOAP.

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


    // YES WORKING //PropertyInfo respuestatoken = (PropertyInfo) response.getPropertyInfo(1);
                SoapPrimitive respuestaresultado = (SoapPrimitive) response.getProperty("resultado");
                SoapPrimitive respuestatoken = (SoapPrimitive) response.getProperty("valor"); // pero esta es mejor
                SoapPrimitive respuestaexpiro = (SoapPrimitive) response.getProperty("expiracion");
                _expiracion = respuestaexpiro.toString();

                if( respuestaresultado.toString().matches("true")){
                    tokens = respuestatoken.toString();
                } else {tokens = "RESULTADO: FALSE";}


            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tokens;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && !s.isEmpty() && s.length()>30){
                guardartoken(s);

            } else MostrarError("Es necesario WIFI ó DATOS MOVILES!!");


        }
    }
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el Fragment Planillas ///////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    public void llamadoAlFragmentPlanilla(String s){

        String soy = "login";
        FragmentPlanillas f = new FragmentPlanillas();
        Bundle enviar = new Bundle();
        enviar.putString("token", s);
        enviar.putString("soy", soy);
        f.setArguments(enviar);
        getActivity().getSupportFragmentManager().
                beginTransaction().replace(R.id.idContenedor, f).
                addToBackStack(null).commit();
    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el Mensaje de error /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private void guardartoken(String token) {

        BorrarTokens();

        if(token==null){
            Toast.makeText(getContext(), "se detuvo el proceso", Toast.LENGTH_SHORT).show();

        } else {

            String _fechaSubstring = _expiracion.substring(0, 10);
            String _horaSubstring = _expiracion.substring(11, 19);
            persistenciaDeToken = new PersistenciaDeTokens(getContext(), "", null, 1);


            try {
                persistenciaDeToken.insertarToken(token, _fechaSubstring, _horaSubstring);
                Toast.makeText(getContext(), "Guardado token", Toast.LENGTH_SHORT).show();

                ConsultarSitokenEstaVencido();

            } catch (SQLiteException e) {
                Toast.makeText(getContext(), "token ya existe" + e, Toast.LENGTH_SHORT).show();
            }
        }


    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el Mensaje de error /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////

    private void ConsultarSitokenEstaVencido() {

        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        _hora = hourFormat.format(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        _fecha = dateFormat.format(date);


        PersistenciaDeTokens admin = new PersistenciaDeTokens(getContext(),
                "dbusuarios.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_tokens" , null);

        if (fila != null) {
            if(fila.moveToFirst()){
                _tokensDB = fila.getString(1);
                _fechaDB = fila.getString(2);
                _horaDB = fila.getString(3);

                if(_fecha.compareTo(_fechaDB) == 0){

                    if(_hora.compareTo(_horaDB)<0){
                        ///  aqui envio el token
                        llamadoAlFragmentPlanilla(_tokensDB);

                    } else {

                        ComunicacionSoapLogin com_soap= new ComunicacionSoapLogin();
                        com_soap.execute();
                    }
                    
                } else {

                    ComunicacionSoapLogin com_soap= new ComunicacionSoapLogin();
                    com_soap.execute();
                }


                fila.close();
                bd.close();



            } else MostrarError("Ingrese bien usuario y contraseña");

        }


    }

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////// Comunicacion con el Mensaje de error /////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    public void MostrarError(String msg){
        Toast.makeText(getContext(), "Error " + msg,
                Toast.LENGTH_SHORT).show();
    }


    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    ////////// TODO si se realizo un login enviar token /////////7////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    public  void VerificarLogin() {

        PersistenciaDS admin = new PersistenciaDS(getContext(),
                "dbusuarios.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_usuarios" , null);

        if (fila != null) {
            if(fila.moveToFirst()){
                usu = fila.getString(1);
                pass = fila.getString(2);

                ConsultarSitokenEstaVencido();

                fila.close();
                bd.close();

            } else MostrarError("Ingrese bien usuario y contraseña");

        }


    }

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////// TOdO UN BLOCK DE BACK       /////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

    public void callParentMethod(){
        getActivity().onBackPressed();
    }


////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////// TOdO UN RESETEO DEL SISTEMA /////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
    public  void ResetearSistema() {

        persistenciaDeToken=new PersistenciaDeTokens(getContext(), "dbtokens.db", null, 1);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View view2 = inflater.inflate(R.layout.button_personalizados,null);
        alertDialog.setView(view2);
        alertDialog.show();
        Button ok_reset = (Button)view2.findViewById(R.id.btnReset);
        TextView txt = view2.findViewById(R.id.text_dialog);
        txt.setText("ATENCION"+"\n"+"¿SEGURO QUE DESEA REINICIAR EL SISTEMA?"+"\n"+"PRESIONE REINICIAR");
        ok_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Reiniciando...",Toast.LENGTH_SHORT).show();
                SQLiteDatabase bd = persistenciaDeToken.getReadableDatabase();
                bd.execSQL("DELETE FROM table_tokens");
                bd.close();
                System.exit(0);
                VerificarLogin();
            }
        });



    }

    public void BorrarTokens(){

         persistenciaDeToken=new PersistenciaDeTokens(getContext(), "dbtokens.db", null, 1);
         SQLiteDatabase bd = persistenciaDeToken.getReadableDatabase();
         bd.execSQL("DELETE FROM table_tokens");
         bd.close();

    }

}
