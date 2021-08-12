package com.example.dsuiza.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.dsuiza.R;
import com.example.dsuiza.modelo.ModeloComprobantes;
import com.example.dsuiza.modelo.ModeloPlanillas;

import java.util.ArrayList;

public class AdapterPlanillas extends BaseAdapter {


    Context context;
    ArrayList<ModeloPlanillas> lista_zonas;
    LayoutInflater inflater;

    public AdapterPlanillas(Context context, ArrayList<ModeloPlanillas> lista_zonas) {
        this.context = context;
        this.lista_zonas = lista_zonas;

    }

    @Override
    public int getCount() {
        return lista_zonas.size();
    }

    @Override
    public Object getItem(int position) {
        return lista_zonas.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater== null)
        {
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.modelo_planilla, parent, false);

        }

        MiHolderPlanillas holder=new MiHolderPlanillas(convertView);

        String zona = lista_zonas.get(position).getZona();
        String desde = lista_zonas.get(position).getHora_desde();
        String hasta = lista_zonas.get(position).getHora_hasta();
        String myHexColor = "#D35400";


        if(lista_zonas.get(position).getPosicion()==1){
            convertView.setBackgroundColor(Color.parseColor(myHexColor));
        } else if (lista_zonas.get(position).getPosicion()==0){
            convertView.setBackgroundColor(Color.GRAY);
        }

        holder.zonas.setText("ZONA  :"+zona +"\n"+
                             "DESDE :"+desde+"\n"+
                             "HASTA :"+hasta);

        return convertView;

    }

}
