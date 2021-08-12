package com.example.dsuiza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dsuiza.helpers.IFragementTransitions;
import com.example.dsuiza.views.FragmentListaDeEntregas;
import com.example.dsuiza.views.LoginFragment;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements IFragementTransitions {

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        // verifico si el usuario dio los permisos para la camara
        checkPermission();
        //////////////////////////////
    }

    public void primerLlamado(){

        LoginFragment f = new LoginFragment();
        fragmentTransicion(f);
    }


    @Override
    public void fragmentTransicion() {

    }

    @Override
    public void fragmentTransicion(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.idContenedor,
                fragment).addToBackStack(null).commit();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private static final int INTERVALO = 1000; //1 segundos para salir
    private long tiempoPrimerClick;


    @Override
    public void onBackPressed() {

        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            //super.onBackPressed();
            System.exit(0);
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }



    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);

                Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();

            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){

              //  Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
                primerLlamado();

            }

        }

        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // Toast.makeText(this, "OK Permissions granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                primerLlamado();
            } else {
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}