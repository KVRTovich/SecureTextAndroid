package net.kyrtovich.myapplicationx;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.util.Base64;
import android.widget.Spinner;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import java.security.MessageDigest;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


public class FirstFragment extends Fragment {
    public Editable text;
    public String ob_string;
    public byte [] ch_array;
    public Editable password;
    public ClipboardManager cpm;
    public ClipData Cd1;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }
    public void md5() throws Exception {
        MessageDigest initp = MessageDigest.getInstance("MD5");
        ch_array  = initp.digest(text.toString().getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b :ch_array) {
            sb.append(String.format("%02x",b));
        }
        ob_string = sb.toString();
    }
    public void hashingpassword(Boolean tru) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (tru == true) {
            MessageDigest initp = MessageDigest.getInstance("SHA-256");
            ch_array  = initp.digest(password.toString().getBytes("UTF-8"));
            ob_string = Base64.encodeToString(ch_array, Base64.DEFAULT);
        }
        else {
            MessageDigest initp = MessageDigest.getInstance("SHA-256");
            ch_array  = initp.digest(text.toString().getBytes("UTF-8"));
            ob_string = Base64.encodeToString(ch_array, Base64.DEFAULT);
        }

    }
    public void aes(Boolean tru) throws Exception {
        Cipher xuy1 = Cipher.getInstance("AES");
        SecretKeySpec xuy123 = new SecretKeySpec(ob_string.substring(0,16).getBytes("UTF-8"), "AES");
        if (tru == true) {
            xuy1.init(Cipher.ENCRYPT_MODE, xuy123);
            ch_array = xuy1.doFinal(text.toString().getBytes("UTF-8"));
        }
        else {
            xuy1.init(Cipher.DECRYPT_MODE, xuy123);
            ch_array = xuy1.doFinal(Base64.decode(text.toString(), Base64.DEFAULT));
            ob_string = new String(ch_array, "UTF-8");

        }
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)  {
        super.onViewCreated(view, savedInstanceState);
        final EditText inp = view.findViewById(R.id.inp);
        final EditText outp = view.findViewById(R.id.outp);
        final EditText pass = view.findViewById(R.id.pass);
        final Spinner spin = view.findViewById(R.id.spinner);
        final Button butt = view.findViewById(R.id.button);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        butt.setEnabled(true);
                        pass.setEnabled(true);
                        break;
                    case 1:
                        butt.setEnabled(false);
                        pass.setEnabled(false);
                        break;
                    case 2:
                        pass.setEnabled(false);
                        butt.setEnabled(true);
                        break;
                    case 3:
                        butt.setEnabled(false);
                        pass.setEnabled(false);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Error")
                        .setTitle("Nothing Selected");
                AlertDialog dialog = builder.create();

            }
        });



        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) { // too bad
                text = inp.getText();
                password = pass.getText();
                if (spin.getSelectedItemPosition() == 0) {
                    try {
                        hashingpassword(true);
                        aes(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        ob_string = new String(Base64.decode(text.toString().getBytes("UTF-8"), Base64.DEFAULT));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                outp.setText(ob_string);
            }

        });

        view.findViewById(R.id.outp).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (outp.getText().toString().trim().length() > 0) {
                    cpm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    Cd1 = ClipData.newPlainText("text", outp.getText().toString());
                    cpm.setPrimaryClip(Cd1);
                    Snackbar.make(view, "Output text has been copied", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(view, "Output Text empty", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener()  {

            @Override

            public void onClick(View view) { // too bad
                text = inp.getText();
                password = pass.getText();
                switch (spin.getSelectedItemPosition()) {
                    case 0:
                        try {
                            hashingpassword(true);
                            aes(true);
                            outp.setText(Base64.encodeToString(ch_array, Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            hashingpassword(false);
                            outp.setText(Base64.encodeToString(ch_array, Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            outp.setText(Base64.encodeToString(text.toString().getBytes("UTF-8"), Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            md5();
                            outp.setText(ob_string);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                }
        });
    }
}