package com.example.dsuiza.views;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsuiza.persistencia.PersistenciaDS;
import com.example.dsuiza.R;


public class   FragmentRegister extends Fragment {


    private EditText input_user1, input_user2, input_pass1, input_pass2;
    private Button registro, volver;
    PersistenciaDS persistencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_register, container, false);
        input_user1 = (EditText)vista.findViewById(R.id.etUser1);
        input_user2 = (EditText)vista. findViewById(R.id.etUser2);
        input_pass1 = (EditText) vista.findViewById(R.id.etPass1);
        input_pass2 = (EditText) vista.findViewById(R.id.etPass2);
        registro = (Button)vista.findViewById(R.id.buttonRegister);
        volver = (Button)vista.findViewById(R.id.buttonVolverALogin);

        String myHexColor = "#D35400";
        registro.setBackgroundColor(Color.parseColor(myHexColor));
        volver.setBackgroundColor(Color.parseColor(myHexColor));

        persistencia = new PersistenciaDS(getContext(), "",null, 1);
        //metodo para dar el alta
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }    });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.idContenedor,
                        new LoginFragment()).addToBackStack(null).commit();
            }
        });

        return  vista;
    }

    public void validarCampos(){
        String usuario = input_user1.getText().toString();
        String contrase = input_pass1.getText().toString();
        String usuario_dos = input_user1.getText().toString();
        String contrase_dos = input_pass1.getText().toString();


        if(usuario.isEmpty() || contrase.isEmpty() || usuario.length() < 2 || contrase.length() < 4){

            Toast.makeText(getContext(), "Verifique que: " + "\n " +
                            " - Se Escriba una Contraseña con al menos 4 caracteres "+ "\n " +
                            " - Se Escriban contraseñas iguales "+ "\n " +
                            " - Un usuario con al menos 4 caracteres "+ "\n " +
                            " - Usuarios sean iguales o escriba un nombre usuario",
                    Toast.LENGTH_SHORT).show();
        } else if(usuario.matches(usuario_dos) && contrase.matches(contrase_dos)){

            Toast.makeText(getContext(), "Sus datos son :  " + usuario + "\n" +
                            "Contraseña     : "  + contrase  ,
                    Toast.LENGTH_SHORT).show();
            registrar(usuario, contrase);
        } else Toast.makeText(getContext(), "Verifique que los campos se hayan ingresado correctamente!",
                Toast.LENGTH_SHORT).show();



    }


    public void registrar(String usuario, String contrase){

        try {
            persistencia.insertar_usuarios(usuario,contrase);
            Toast.makeText(getContext(), "Datos de Usuario"
                            +"\n"
                            + usuario +"\n"
                            + contrase +"\n"
                            +"\n cargados", Toast.LENGTH_SHORT).show();
            input_user1.setText(""); input_pass1.setText(""); input_user2.setText(""); input_pass2.setText("");
        }catch(SQLiteException e){
            Toast.makeText(getContext(), "usuario ya existe" +e, Toast.LENGTH_SHORT).show();
        }

    }


}