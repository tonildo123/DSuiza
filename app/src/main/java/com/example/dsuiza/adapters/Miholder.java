package com.example.dsuiza.adapters;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dsuiza.R;

public class Miholder {
    TextView comprobantes, factura, bultos, fecha;
    CheckBox checkBox;
    Spinner spinner;

    public  Miholder(View itemView)
    {
        comprobantes  = itemView.findViewById(R.id.tvRubro);
        factura  = itemView.findViewById(R.id.tvFactura);
        bultos  = itemView.findViewById(R.id.tvBultos);
        fecha  = itemView.findViewById(R.id.tvFecha);
        checkBox  = itemView.findViewById(R.id.checkBox);
        spinner  = itemView.findViewById(R.id.spinner_holder);


    }
}
