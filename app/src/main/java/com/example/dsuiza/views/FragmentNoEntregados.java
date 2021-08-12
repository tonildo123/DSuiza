package com.example.dsuiza.views;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsuiza.R;
import com.example.dsuiza.adapters.AdapterPlanillas;
import com.example.dsuiza.modelo.ModeloPlanillas;
import com.example.dsuiza.persistencia.PersistenciaDeTokens;
import com.example.dsuiza.persistencia.PersistenciaDeZonasYplanillas;

import java.util.ArrayList;


public class FragmentNoEntregados extends Fragment {

    private ListView listadelete;
    private TextView textoDelete;
    PersistenciaDeZonasYplanillas persistencia_x_zonas;
    AdapterPlanillas clase_adapatador_planillas;
    ArrayList<ModeloPlanillas> lista_planillas = new ArrayList();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_no_entregados, container, false);
        listadelete = (ListView) vista.findViewById(R.id.ListViewPlanillasDelete);
        textoDelete = (TextView) vista.findViewById(R.id.tvPlanillasDelete);
        textoDelete.setText("ELIJA LA PLANILLA A ELIMINAR");
        CrearVista();


        listadelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModeloPlanillas modelo = (ModeloPlanillas) parent.getItemAtPosition(position);
                int idplanillam = modelo.getIdplanilla();
                String zona = modelo.getZona();

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getLayoutInflater();
                View view2 = inflater.inflate(R.layout.mensajedelete,null);
                alertDialog.setView(view2);
                alertDialog.show();
                Button buton_continuar = (Button)view2.findViewById(R.id.btn_mensaje_delete);
                TextView txt = view2.findViewById(R.id.txt_mensaje_delete);
                txt.setText("ZONA : "+zona+
                        "\n"+"¿ ES CORRECTO ?" +"\n"+
                        "PRESIONE - ELIMINAR - ");
                buton_continuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarPlanilla(idplanillam);
                        alertDialog.dismiss();
                    }
                });


            }
        });
        return vista;
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

                    lista_planillas.add(modelo);

                }while(fila.moveToNext());

                clase_adapatador_planillas = new AdapterPlanillas(getContext(),lista_planillas);
                listadelete.setAdapter(clase_adapatador_planillas);

                fila.close();
                bd.close();

            } else {
                Toast.makeText(getContext(),"Fallo1",Toast.LENGTH_SHORT).show();
                }

        } else {
            Toast.makeText(getContext(),"Fallo2",Toast.LENGTH_SHORT).show();
        }


    }
    public void eliminarPlanilla(int idplanilla){

        persistencia_x_zonas=new PersistenciaDeZonasYplanillas(getContext(), "zonas.db", null, 1);
        SQLiteDatabase bd = persistencia_x_zonas.getReadableDatabase();
        bd.execSQL("DELETE FROM table_zonas WHERE idplanilla="+idplanilla);
        bd.close();
        Toast.makeText(getContext(),"Se elimino con exito!!!",Toast.LENGTH_SHORT).show();
        String soy = "comprobantes";
        FragmentPlanillas planillas = new FragmentPlanillas();
        Bundle datos = new Bundle();
        datos.putString("soy", soy);
        planillas.setArguments(datos);
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.idContenedor, planillas).commit();

    }

}