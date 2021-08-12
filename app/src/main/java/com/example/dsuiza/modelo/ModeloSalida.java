package com.example.dsuiza.modelo;

public class ModeloSalida {

   private String token_tmp;
   private int idplanilladetalle_par;
   private boolean entregada_par;
   private String fecha_par;
   private String observaciones_par;
   private String firma_par ;
   private String foto_par;
   private String latitud_par;
   private String longitud_par;
   private int idmotivo_par;

    public ModeloSalida() {

    }

    public ModeloSalida(String token_tmp, int idplanilladetalle_par, boolean entregada_par,
                        String fecha_par, String observaciones_par, String firma_par, String foto_par,
                        String latitud_par, String longitud_par, int idmotivo_par) {
        this.token_tmp = token_tmp;
        this.idplanilladetalle_par = idplanilladetalle_par;
        this.entregada_par = entregada_par;
        this.fecha_par = fecha_par;
        this.observaciones_par = observaciones_par;
        this.firma_par = firma_par;
        this.foto_par = foto_par;
        this.latitud_par = latitud_par;
        this.longitud_par = longitud_par;
        this.idmotivo_par = idmotivo_par;
    }

    public String getToken_tmp() {
        return token_tmp;
    }

    public void setToken_tmp(String token_tmp) {
        this.token_tmp = token_tmp;
    }

    public int getIdplanilladetalle_par() {
        return idplanilladetalle_par;
    }

    public void setIdplanilladetalle_par(int idplanilladetalle_par) {
        this.idplanilladetalle_par = idplanilladetalle_par;
    }

    public boolean isEntregada_par() {
        return entregada_par;
    }

    public void setEntregada_par(boolean entregada_par) {
        this.entregada_par = entregada_par;
    }

    public String getFecha_par() {
        return fecha_par;
    }

    public void setFecha_par(String fecha_par) {
        this.fecha_par = fecha_par;
    }

    public String getObservaciones_par() {
        return observaciones_par;
    }

    public void setObservaciones_par(String observaciones_par) {
        this.observaciones_par = observaciones_par;
    }

    public String getFirma_par() {
        return firma_par;
    }

    public void setFirma_par(String firma_par) {
        this.firma_par = firma_par;
    }

    public String getFoto_par() {
        return foto_par;
    }

    public void setFoto_par(String foto_par) {
        this.foto_par = foto_par;
    }

    public String getLatitud_par() {
        return latitud_par;
    }

    public void setLatitud_par(String latitud_par) {
        this.latitud_par = latitud_par;
    }

    public String getLongitud_par() {
        return longitud_par;
    }

    public void setLongitud_par(String longitud_par) {
        this.longitud_par = longitud_par;
    }

    public int getIdmotivo_par() {
        return idmotivo_par;
    }

    public void setIdmotivo_par(int idmotivo_par) {
        this.idmotivo_par = idmotivo_par;
    }
}
