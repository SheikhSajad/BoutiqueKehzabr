package com.kehzabr.boutique;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterOrder extends Fragment {

    private TextView barcodeText;
    private Button btnSubmit;
    private ActivityResultLauncher<Intent> resolveLauncherBarcodeScanner;
    MainActivity mainActivity;
    private EditText cusNameEditText, cusMobileEditText;
    private DatabaseReference customerName;
    private DatabaseReference customerMobile;
    private DatabaseReference customerBarcode;
    private DatabaseReference date;
    private DatabaseReference mFirebaseDatabase1;

    private String barcode = "";
    private String strName = "";
    private String strMobile = "";
    private ConstraintLayout constraint_layout;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.register_order, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView barcodeIcon = view.findViewById(R.id.barcode_icon);
        barcodeText = view.findViewById(R.id.barcode_text);
        cusNameEditText = view.findViewById(R.id.editTextTextPersonName);
        cusMobileEditText = view.findViewById(R.id.editTextTextPersonMobile);
        btnSubmit = view.findViewById(R.id.button_submit);
        mainActivity = (MainActivity) getActivity();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFirebaseDatabase1 = database.getReference("CustomerList");
        constraint_layout = view.findViewById(R.id.constraint_layout);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strMobile = cusMobileEditText.getText().toString();
                strName = cusNameEditText.getText().toString();
                //  if (Validate())
                Submit();
            }
        });
        resolveLauncherBarcodeScanner = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 2) {
                assert result.getData() != null;
                barcode = result.getData().getStringExtra("MESSAGE");
                barcodeText.setText(barcode);

            }
        });
        barcodeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            resolveLauncherBarcodeScanner.launch(intent);
        });
    }

    private boolean Validate() {

        if (strMobile.equals("")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Enter Mobile Number", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (strName.equals("")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Enter Customer Name", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (barcode.equals("")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Scan QR Code", Snackbar.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    private void Submit() {

        customerBarcode = mFirebaseDatabase1.child(cusNameEditText.getText().toString()).child("barcode");
        customerName = mFirebaseDatabase1.child(cusNameEditText.getText().toString()).child("customerName");
        customerMobile = mFirebaseDatabase1.child(cusNameEditText.getText().toString()).child("mobile");
        date = mFirebaseDatabase1.child(cusNameEditText.getText().toString()).child("date");
        customerBarcode.setValue(barcode);
        customerName.setValue(cusNameEditText.getText().toString());
        customerMobile.setValue(cusMobileEditText.getText().toString());
        date.setValue(getCurrentDateAndTime());
        NavHostFragment.findNavController(RegisterOrder.this)
                .navigate(R.id.action_FirstFragment_to_CustomerList);
    }

    private String getCurrentDateAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}