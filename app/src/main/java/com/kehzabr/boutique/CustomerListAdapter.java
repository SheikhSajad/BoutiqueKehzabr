package com.kehzabr.boutique;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class CustomerListAdapter extends FirebaseRecyclerAdapter<Customer, CustomerListAdapter.customerViewholder> {

    private Context mContext;
    private ProgressBar progressBar;

    public CustomerListAdapter(Context context, ProgressBar progress,
                               @NonNull FirebaseRecyclerOptions<Customer> options) {
        super(options);
        this.mContext = context;
        this.progressBar = progress;

    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerListAdapter.customerViewholder holder, int position, @NonNull Customer model) {

        holder.bind(model);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        progressBar.setVisibility(View.GONE);
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
        ConstraintLayout constraint_layout;

        public customerViewholder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customer_name);
            mobile = itemView.findViewById(R.id.mobile);
            date = itemView.findViewById(R.id.date);
            barcode = itemView.findViewById(R.id.barcode);
            constraint_layout = itemView.findViewById(R.id.constraint_layout);

        }

        void bind(@NonNull Customer item) {
            customerName.setText(item.getCustomerName());
            mobile.setText(item.getMobile());
            date.setText(item.getDate());
            barcode.setText(item.getBarcode());
            constraint_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  }
            });
        }


    }
}
