package com.example.dsuiza.modelo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Date;

public class ModeloPlanillas {
    private int idplanilla, posicion;
    private String zona;
    private String fecha, hora_desde, hora_hasta;
    private SoapObject pcliente;
    private ArrayList<ModeloCliente> clientes = new ArrayList<>();

    public ModeloPlanillas() {

    }

    public ModeloPlanillas(int idplanilla, int posicion, String zona, String fecha,
                           String hora_desde, String hora_hasta, SoapObject pcliente,
                           ArrayList<ModeloCliente> clientes ) {
        this.idplanilla = idplanilla;
        this.posicion = posicion;
        this.zona = zona;
        this.fecha = fecha;
        this.hora_desde = hora_desde;
        this.hora_hasta = hora_hasta;
        this.pcliente = pcliente;
        this.clientes = clientes;
    }

    public SoapObject getPcliente() {
        return pcliente;
    }

    public void setPcliente(SoapObject pcliente) {
        this.pcliente = pcliente;
    }

    public ArrayList<ModeloCliente> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<ModeloCliente> clientes) {
        this.clientes = clientes;
    }

    public int getIdplanilla() {
        return idplanilla;
    }

    public void setIdplanilla(int idplanilla) {
        this.idplanilla = idplanilla;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getFecha() {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
        fecha = fmt.print(dt);
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora_desde() {

        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
        hora_desde = fmt.print(dt);
        return hora_desde;
    }

    public void setHora_desde(String hora_desde) {
        this.hora_desde = hora_desde;
    }

    public String getHora_hasta() {

        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
        hora_hasta = fmt.print(dt);
        return hora_hasta;
    }

    public void setHora_hasta(String hora_hasta) {
        this.hora_hasta = hora_hasta;
    }
}
