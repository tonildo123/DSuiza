package com.example.dsuiza.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.dsuiza.R;
import com.example.dsuiza.modelo.ModeloCliente;
import com.example.dsuiza.modelo.ModeloComprobantes;

import java.util.ArrayList;

public class AdapterFarmacias extends BaseAdapter {

    Context context;
    ArrayList<ModeloCliente> lista_clientes;
    LayoutInflater inflater;

    public AdapterFarmacias(Context context, ArrayList<ModeloCliente> lista_clientes) {
        this.context = context;
        this.lista_clientes = lista_clientes;

    }

    @Override
    public int getCount() {
        return lista_clientes.size();
    }

    @Override
    public Object getItem(int position) {
        return lista_clientes.get(position);
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
            convertView = inflater.inflate(R.layout.modeloclientes, parent, false);

        }

        MiholderClientes holder=new MiholderClientes(convertView);
        String myHexColor = "#D35400";
        if(lista_clientes.get(position).getCliente_posicion()==1){
            convertView.setBackgroundColor(Color.parseColor(myHexColor));
        } else if (lista_clientes.get(position).getCliente_posicion()==0){
            convertView.setBackgroundColor(Color.GRAY);
        }

        holder.clientes.setText("CLIENTE N° "+lista_clientes.get(position).getCliente_idCliente() +"\n"+
                                "LUGAR    : "+lista_clientes.get(position).getCliente_nombre() +"\n"+
                                "DOMICILIO: "+lista_clientes.get(position).getCliente_direccion());

        return convertView;

    }

}
