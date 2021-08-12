package com.example.dsuiza.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.dsuiza.R;
import com.example.dsuiza.modelo.ModeloComprobantes;
import com.example.dsuiza.modelo.ModeloPlanillas;

import java.util.ArrayList;

public class AdapterComprobantes extends BaseAdapter {


    Context context;
    ArrayList<ModeloComprobantes> lista_comprobantes;
    LayoutInflater inflater;

    public AdapterComprobantes(Context context, ArrayList<ModeloComprobantes> lista_comprobantes) {
        this.context = context;
        this.lista_comprobantes = lista_comprobantes;

    }

    @Override
    public int getCount() {
        return lista_comprobantes.size();
    }

    @Override
    public Object getItem(int position) {
        return lista_comprobantes.get(position);
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
            convertView = inflater.inflate(R.layout.nombre_de_farmacia, parent, false);

        }

        Miholder holder=new Miholder(convertView);

        ArrayAdapter<CharSequence> adapter_motivos = ArrayAdapter.createFromResource(convertView.getContext(),
                R.array.spinnerMotivos, R.layout.formato_item_spinner);
        adapter_motivos.setDropDownViewResource(R.layout.formato_item_spinner);
        holder.spinner.setAdapter(adapter_motivos);

        String rubro    = lista_comprobantes.get(position).getComprobante_rubro();
        String factura  = lista_comprobantes.get(position).getComprobante_pedido();
        String bultos   = lista_comprobantes.get(position).getComprobante_bultos();
        String fecha    = lista_comprobantes.get(position).getFecha();
        int spinner     = lista_comprobantes.get(position).getSpinner();
        boolean check   = lista_comprobantes.get(position).isCheck();

        holder.comprobantes.setText("RUBRO :"+rubro);
        holder.factura.setText("FAC N°"+factura);
        holder.bultos.setText("BULTOS :"+bultos);
        holder.fecha.setText("FECHA :"+fecha);
        holder.checkBox.setChecked(check);

        if(lista_comprobantes.get(position).isCheck()){
            holder.spinner.setEnabled(false);
        }


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lista_comprobantes.get(position).isCheck()){
                    lista_comprobantes.get(position).setCheck(false);
                    holder.spinner.setEnabled(true);

                }else {
                    lista_comprobantes.get(position).setCheck(true);
                    holder.spinner.setEnabled(false);

                }

            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            lista_comprobantes.get(position).setSpinner(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                lista_comprobantes.get(position).setSpinner(0);
            }

        });

        return convertView;

    }
}
