package com.kehzabr.boutique;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CustomerList extends Fragment {

    private RecyclerView customerListRecyclerView;
    CustomerListAdapter adapter;
    DatabaseReference mbase;
    Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_list, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mbase
                = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("CustomerList")
                .limitToLast(50);
        customerListRecyclerView = view.findViewById(R.id.customerListRecyclerView);
        customerListRecyclerView.setLayoutManager(
                new LinearLayoutManager(mContext));


        FirebaseRecyclerOptions<Customer> options
                = new FirebaseRecyclerOptions.Builder<Customer>()
                .setQuery(query, Customer.class)
                .build();

        adapter = new CustomerListAdapter(options);
        customerListRecyclerView.setAdapter(adapter);

    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }


}