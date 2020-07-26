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
import android.widget.Toast;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import java.security.MessageDigest;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.UnsupportedEncodingException;


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
    public StringBuilder md5text() throws Exception {
        MessageDigest initp = MessageDigest.getInstance("MD5");
        ch_array  = initp.digest(text.toString().getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b :ch_array) {
            sb.append(String.format("%02x",b));
        }
        return  sb;
    }
    public StringBuilder md5pass() throws Exception {
        MessageDigest initp = MessageDigest.getInstance("MD5");
        ch_array  = initp.digest(password.toString().getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b :ch_array) {
            sb.append(String.format("%02x",b));
        }
        return  sb;
    }
    public String sha256text() throws Exception {
        MessageDigest initp = MessageDigest.getInstance("SHA-256");
        ch_array  = initp.digest(text.toString().getBytes("UTF-8"));
        return Base64.encodeToString(ch_array, Base64.DEFAULT);
    }
    public String sha256pass() throws Exception {
        MessageDigest initp = MessageDigest.getInstance("SHA-256");
        ch_array  = initp.digest(password.toString().getBytes("UTF-8"));
        return Base64.encodeToString(ch_array, Base64.DEFAULT);
    }
    public byte[] aes(String password) throws Exception {
        Cipher xuy1 = Cipher.getInstance("AES");
        SecretKeySpec xuy123 = new SecretKeySpec(password.substring(0,16).getBytes("UTF-8"), "AES");
        xuy1.init(Cipher.ENCRYPT_MODE, xuy123);
        return xuy1.doFinal(text.toString().getBytes("UTF-8"));
    }
    public byte[] UnAesCipher(String password) throws Exception {
        Cipher xuy1 = Cipher.getInstance("AES");
        SecretKeySpec xuy123 = new SecretKeySpec(password.substring(0,16).getBytes("UTF-8"), "AES");
        xuy1.init(Cipher.DECRYPT_MODE, xuy123);
        return xuy1.doFinal(Base64.decode(text.toString(), Base64.DEFAULT));
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
                        butt.setEnabled(true);
                        pass.setEnabled(true);
                        break;
                    case 2:
                        butt.setEnabled(false);
                        pass.setEnabled(false);
                        break;
                    case 3:
                        pass.setEnabled(false);
                        butt.setEnabled(true);
                        break;
                    case 4:
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
                switch (spin.getSelectedItemPosition()) {
                    case 0:
                        try {
                            //aes + sha256
                            outp.setText(new String(UnAesCipher(sha256pass())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1: {
                        try {
                            // aes + md5
                            outp.setText(new String(UnAesCipher(md5pass().toString())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                        case 3: {
                            try {
                                outp.setText(new String(Base64.decode(text.toString().getBytes("UTF-8"), Base64.DEFAULT)));
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                            } catch (IllegalArgumentException i) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

        });

        view.findViewById(R.id.outp).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (outp.getText().toString().trim().length() > 0) {
                    cpm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    Cd1 = ClipData.newPlainText("text", outp.getText().toString());
                    cpm.setPrimaryClip(Cd1);
                    Toast.makeText(getActivity(),"Output text has been copied", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(),"Output is empty", Toast.LENGTH_LONG).show();
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
                            // aes + sha256
                            outp.setText(Base64.encodeToString(aes(sha256pass()), Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1: // aes + md5
                        try {
                            outp.setText(Base64.encodeToString(aes(md5pass().toString()), Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            // sha256
                            outp.setText(sha256text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            outp.setText(Base64.encodeToString(text.toString().getBytes("UTF-8"), Base64.DEFAULT));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            // md5
                            outp.setText(md5text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                }
        });
    }
}