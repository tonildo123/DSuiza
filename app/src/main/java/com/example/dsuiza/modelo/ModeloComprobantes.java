package com.example.dsuiza.modelo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class ModeloComprobantes {

    private String comprobante_idVenta, comprobante_pedido, comprobante_rubro,
                   comprobante_bultos, idplanilladetalle, fecha;
    private int idplanilla, idcliente;
    private int spinner=0;
    private boolean check=false;

    
    public ModeloComprobantes() {
    }

    public ModeloComprobantes(int idplanilla, int idcliente, String comprobante_idVenta, String comprobante_pedido,
                              String comprobante_rubro, String comprobante_bultos,String fecha,int spinner,
                              String idplanilladetalle, boolean check) {
        this.idplanilla = idplanilla;
        this.idcliente = idcliente;
        this.comprobante_idVenta = comprobante_idVenta;
        this.comprobante_pedido = comprobante_pedido;
        this.comprobante_rubro = comprobante_rubro;
        this.comprobante_bultos = comprobante_bultos;
        this.fecha = fecha;
        this.spinner = spinner;
        this.idplanilladetalle = idplanilladetalle;
        this.check = check;


    }


    public int getSpinner() {
        return spinner;
    }

    public void setSpinner(int spinner) {
        this.spinner = spinner;
    }

    public String getFecha() {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
        fecha = fmt.print(dt);
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdplanilla() {
        return idplanilla;
    }

    public void setIdplanilla(int idplanilla) {
        this.idplanilla = idplanilla;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public String getIdplanilladetalle() {
        return idplanilladetalle;
    }

    public void setIdplanilladetalle(String idplanilladetalle) {
        this.idplanilladetalle = idplanilladetalle;
    }


    public String getComprobante_idVenta() {
        return comprobante_idVenta;
    }

    public void setComprobante_idVenta(String comprobante_idVenta) {
        this.comprobante_idVenta = comprobante_idVenta;
    }

    public String getComprobante_pedido() {
        return comprobante_pedido;
    }

    public void setComprobante_pedido(String comprobante_pedido) {
        this.comprobante_pedido = comprobante_pedido;
    }

    public String getComprobante_rubro() {
        return comprobante_rubro;
    }

    public void setComprobante_rubro(String comprobante_rubro) {
        this.comprobante_rubro = comprobante_rubro;
    }

    public String getComprobante_bultos() {
        return comprobante_bultos;
    }

    public void setComprobante_bultos(String comprobante_bultos) {
        this.comprobante_bultos = comprobante_bultos;
    }



}
