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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

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

import com.example.dsuiza.MainActivity;
import com.example.dsuiza.R;
import com.example.dsuiza.adapters.AdapterFarmacias;
import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.helpers.OrderComprobantes;
import com.example.dsuiza.modelo.ModeloCliente;
import com.example.dsuiza.modelo.ModeloComprobantes;
import com.example.dsuiza.persistencia.PersistenciaDS;
import com.example.dsuiza.persistencia.PersistenciaDeTokens;
import com.example.dsuiza.persistencia.PersistenciaDeZonasYplanillas;
import com.example.dsuiza.persistencia.PersistenciaPlanillas;

import org.jetbrains.annotations.NotNull;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class
FragmentListaDeEntregas extends Fragment {

    TextView texto_de_respuesta_soap;
    ProgressDialog dialog;
    ListView list;

    private IFragementTransitions interfaz;
    PersistenciaDeZonasYplanillas persistencia_x_zonas;

    private final String mCurrentPosition = "idplanilla";
    private final String mCurrentToken = "token";
    int idplanilla;
    String token=null;
    String idCliente=null;
    String nombre=null;
    String direccion=null;
    String localidad=null;
    String provincia=null;
    String orden=null;
    String qr=null;

    int cantidad_de_marcado=0;



    ArrayList<ModeloCliente> lista_Clientes = new ArrayList();
    ModeloCliente modeloCliente;
    AdapterFarmacias clase_adapatador_farmacias;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_lista_de_entregas2, container, false);
        texto_de_respuesta_soap = (TextView) vista.findViewById(R.id.txt_response_soap);
        list = (ListView) vista.findViewById(R.id.ListView_listado);


            if (getArguments() != null) {
                idplanilla = getArguments().getInt(mCurrentPosition);
                token = getArguments().getString(mCurrentToken);
                cargarVista(idplanilla);

            }else if (savedInstanceState != null){
                idplanilla =savedInstanceState.getInt(mCurrentPosition);
                token = getArguments().getString(mCurrentToken);
                cargarVista(idplanilla);

            }else MostrarError("No se recibio parametro");


        /// TODO CON RESPECTO AL EVENTO ClickLiet
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modeloCliente = (ModeloCliente) parent.getItemAtPosition(position);

                idCliente = modeloCliente.getCliente_idCliente();
                nombre    = modeloCliente.getCliente_nombre();
                direccion = modeloCliente.getCliente_direccion();
                localidad = modeloCliente.getCliente_localidad();
                provincia = modeloCliente.getCliente_provincia();
                orden     = modeloCliente.getCliente_orden();
                qr        = modeloCliente.getCliente_qr();

                if(modeloCliente.getCliente_posicion()==0){
                    modificarItem(idCliente, String.valueOf(idplanilla));
                }

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getLayoutInflater();
                View view2 = inflater.inflate(R.layout.mensaje_cliente,null);
                alertDialog.setView(view2);
                Button buton_continuar = (Button)view2.findViewById(R.id.btn_mensaje_cliente);
                String myHexColor = "#D35400";
                buton_continuar.setBackgroundColor(Color.parseColor(myHexColor));
                TextView txt = view2.findViewById(R.id.txt_mensaje_cliente);
                alertDialog.show();
                txt.setText("FARMACIA:\n" +
                        nombre+ "\n"+
                        direccion + "\n"+""+"\n"+
                        "¿CORRECTO?");
                buton_continuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListaDeEntregas f1 = new ListaDeEntregas();
                        Bundle args = new Bundle();
                        args.putString("idCliente", idCliente);
                        args.putString("nombre", nombre);
                        args.putString("direccion", direccion);
                        args.putString("localidad", localidad);
                        args.putString("provincia", provincia);
                        args.putString("orden", orden);
                        args.putString("qr",qr);
                        args.putString("token",token);
                        args.putInt("idplanilla",idplanilla);
                        f1.setArguments(args);

                        interfaz.fragmentTransicion(f1);
                        alertDialog.dismiss();
                    }
                });


            }
        });


        if(lista_Clientes.size()!=0){

            for(int x=0; x<lista_Clientes.size(); x++){
                if(lista_Clientes.get(x).getCliente_posicion()==1){
                    cantidad_de_marcado++;
                }

            }
            if(lista_Clientes.size()==cantidad_de_marcado){
                eliminarBaseDeDatos();

            }

        }

        return vista;
    }
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/////////       TODO EL DELETE DE LA PLANILLA //////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
    private void eliminarBaseDeDatos() {
        persistencia_x_zonas=new PersistenciaDeZonasYplanillas(getContext(), "zonas.db", null, 1);
        SQLiteDatabase bd = persistencia_x_zonas.getReadableDatabase();
        bd.execSQL("DELETE FROM table_zonas WHERE idplanilla="+idplanilla);
        bd.close();
        Toast.makeText(getContext(),"Ultima entrega de esta planilla!!!",Toast.LENGTH_SHORT).show();
        //String soy = "comprobantes";
        //FragmentPlanillas planillas = new FragmentPlanillas();
        //Bundle datos = new Bundle();
        //datos.putString("soy", soy);
        //planillas.setArguments(datos);
        //interfaz.fragmentTransicion(planillas);
    }
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/////////       TODO EL UPDATE DEL ITEM   //////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////


    private void modificarItem(String sidcliente,String sidplanilla) {
        PersistenciaPlanillas admin = new PersistenciaPlanillas(getContext(),
                "planillas.db", null, 1);
        
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("seleccion",1);

        int cantidad = bd.update("table_planillas", valores, "idcliente"+"=? AND"+
                " idplanilla"+"=?",
                new String[]{sidcliente, sidplanilla});
        if(cantidad!=0){
            Toast.makeText(getContext(),"Se marco item!",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Fallo",Toast.LENGTH_SHORT).show();
        }
        //Cerramos la BD
        bd.close();

    }

////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/////////       TODO EL contenido de la planilla  //////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////

    public void cargarVista(int id_planilla) {


        PersistenciaPlanillas admin = new PersistenciaPlanillas(getContext(),
                "planillas.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM table_planillas WHERE idplanilla = '"+id_planilla+"'" , null);

        if (fila != null && fila.getCount()>0) {
            if(fila.moveToFirst()){

                do{

                    modeloCliente = new ModeloCliente();

                    modeloCliente.setCliente_idCliente(fila.getString(1));
                    modeloCliente.setCliente_nombre(fila.getString(2));
                    modeloCliente.setCliente_direccion(fila.getString(3));
                    modeloCliente.setCliente_localidad(fila.getString(4));
                    modeloCliente.setCliente_provincia(fila.getString(5));
                    modeloCliente.setCliente_orden(String.valueOf(fila.getInt(7)));
                    modeloCliente.setCliente_qr(fila.getString(8));
                    modeloCliente.setCliente_posicion(fila.getInt(9));

                    lista_Clientes.add(modeloCliente);


                }while(fila.moveToNext());


                OrderComprobantes o = new OrderComprobantes();
                o.compare(lista_Clientes);
                clase_adapatador_farmacias = new AdapterFarmacias(getContext(),lista_Clientes);
                list.setAdapter(clase_adapatador_farmacias);
                texto_de_respuesta_soap.setText("LISTA DE ENTREGAS!");
                fila.close();
                bd.close();

            } else {
                        MostrarError("Error en el envio de parametros");     }

        } else {MostrarError(String.valueOf(fila));
        }

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
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
/////////       MENSAJES DE ERROR         //////////////
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
  public void MostrarError(String error) {
        texto_de_respuesta_soap.setText("Error " +error);

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////// TODO CONSERVAR EL ESTADO DE ESTE FRAGMNT ///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Override
    public void onSaveInstanceState(Bundle outState) {
           super.onSaveInstanceState(outState);
           outState.putInt("idplanilla", idplanilla);
        outState.putString("token", token);
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
                volver_a_planillas();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void volver_a_planillas(){
        String soy = "comprobantes";
        FragmentPlanillas planillas = new FragmentPlanillas();
        Bundle datos = new Bundle();
        datos.putString("soy", soy);
        planillas.setArguments(datos);
        interfaz.fragmentTransicion(planillas);

    }

   public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
