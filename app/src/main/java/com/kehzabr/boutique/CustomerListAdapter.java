package com.kehzabr.boutique;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class CustomerListAdapter extends FirebaseRecyclerAdapter<Customer, CustomerListAdapter.customerViewholder> {

    public CustomerListAdapter(
            @NonNull FirebaseRecyclerOptions<Customer> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerListAdapter.customerViewholder holder, int position, @NonNull Customer model) {

        holder.bind(model);
    }

    @NonNull
    @Override
    public customerViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_row, parent, false);


        return new CustomerListAdapter.customerViewholder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class customerViewholder
            extends RecyclerView.ViewHolder {
        TextView customerName, mobile, date, barcode;

        public customerViewholder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customer_name);
            mobile = itemView.findViewById(R.id.mobile);
            date = itemView.findViewById(R.id.date);
            barcode = itemView.findViewById(R.id.barcode);

        }

        void bind(@NonNull Customer item) {
            customerName.setText(item.getCustomerName());
            mobile.setText(item.getMobile());
            date.setText(item.getDate());
            barcode.setText(item.getBarcode());
        }


    }
}
