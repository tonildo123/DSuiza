package com.example.dsuiza.helpers;

import com.example.dsuiza.modelo.ModeloCliente;
import com.example.dsuiza.modelo.ModeloComprobantes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class OrderComprobantes implements Comparable<ModeloCliente> {

    public void compare(ArrayList<ModeloCliente> el_array) {
        Collections.sort(el_array, new Comparator<ModeloCliente>() {
            @Override
            public int compare(ModeloCliente o1, ModeloCliente o2) {

                return o1.getCliente_orden().compareTo(o2.getCliente_orden());

            }
        });

    }


    @Override
    public int compareTo(ModeloCliente o) {
        return 0;
    }
}
