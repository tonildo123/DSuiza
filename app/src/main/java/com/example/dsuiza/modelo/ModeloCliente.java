package com.example.dsuiza.modelo;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ModeloCliente {

    private String cliente_idCliente, cliente_nombre,cliente_direccion,
            cliente_localidad, cliente_provincia, cliente_orden,cliente_qr;
    private int cliente_posicion, cliente_idplanilla;
    private SoapObject ccomprobante;
    private ArrayList<ModeloComprobantes> comprobantes = new ArrayList<>();


    public ModeloCliente() {

    }

    public ModeloCliente(String cliente_idCliente, String cliente_nombre,
                         String cliente_direccion, String cliente_localidad,
                         String cliente_provincia, String cliente_orden, String cliente_qr,
                         int cliente_idplanilla,
                         int cliente_posicion,SoapObject ccomprobante ,ArrayList<ModeloComprobantes> comprobantes ) {
        this.cliente_idCliente = cliente_idCliente;
        this.cliente_nombre = cliente_nombre;
        this.cliente_direccion = cliente_direccion;
        this.cliente_localidad = cliente_localidad;
        this.cliente_provincia = cliente_provincia;
        this.cliente_orden = cliente_orden;
        this.cliente_qr = cliente_qr;
        this.cliente_posicion = cliente_posicion;
        this.cliente_idplanilla = cliente_idplanilla;
        this.ccomprobante = ccomprobante;
        this.comprobantes = comprobantes;
    }

    public int getCliente_idplanilla() {
        return cliente_idplanilla;
    }

    public void setCliente_idplanilla(int cliente_idplanilla) {
        this.cliente_idplanilla = cliente_idplanilla;
    }

    public SoapObject getCcomprobante() {
        return ccomprobante;
    }

    public void setCcomprobante(SoapObject ccomprobante) {
        this.ccomprobante = ccomprobante;
    }

    public ArrayList<ModeloComprobantes> getComprobantes() {
        return comprobantes;
    }

    public void setComprobantes(ArrayList<ModeloComprobantes> comprobantes) {
        this.comprobantes = comprobantes;
    }

    public String getCliente_idCliente() {
        return cliente_idCliente;
    }

    public void setCliente_idCliente(String cliente_idCliente) {
        this.cliente_idCliente = cliente_idCliente;
    }

    public String getCliente_nombre() {
        return cliente_nombre;
    }

    public void setCliente_nombre(String cliente_nombre) {
        this.cliente_nombre = cliente_nombre;
    }

    public String getCliente_direccion() {
        return cliente_direccion;
    }

    public void setCliente_direccion(String cliente_direccion) {
        this.cliente_direccion = cliente_direccion;
    }

    public String getCliente_localidad() {
        return cliente_localidad;
    }

    public void setCliente_localidad(String cliente_localidad) {
        this.cliente_localidad = cliente_localidad;
    }

    public String getCliente_provincia() {
        return cliente_provincia;
    }

    public void setCliente_provincia(String cliente_provincia) {
        this.cliente_provincia = cliente_provincia;
    }

    public String getCliente_orden() {
        return cliente_orden;
    }

    public void setCliente_orden(String cliente_orden) {
        this.cliente_orden = cliente_orden;
    }

    public String getCliente_qr() {
        return cliente_qr;
    }

    public void setCliente_qr(String cliente_qr) {
        this.cliente_qr = cliente_qr;
    }

    public int getCliente_posicion() {
        return cliente_posicion;
    }

    public void setCliente_posicion(int cliente_posicion) {
        this.cliente_posicion = cliente_posicion;
    }
}
