package com.example.sharemybike.ui.Bike;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharemybike.MainPanelActivity;
import com.example.sharemybike.MyItemRecyclerViewAdapter;
import com.example.sharemybike.R;
import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.databinding.BikeFragmentBinding;
import com.example.sharemybike.pojos.Bike;
import com.example.sharemybike.ui.Dates.DatesFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;

public class BikeFragment extends Fragment {

    private BikeViewModel bikeViewModel;
    private BikeFragmentBinding binding;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public BikeFragment() {
    }

    public static BikeFragment newInstance(int columnCount) {
        BikeFragment fragment = new BikeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bike_fragment, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(BikesContent.ITEMS));

            //Set the adapter
            /*FirebaseRecyclerOptions<Bike> options = new FirebaseRecyclerOptions.Builder<Bike>()
                    .setQuery(query, Bike.class)
                    .build();*/
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        /*if(!DatesFragment.isDateSelected){
            //If date is not selected, go back to calendar
            Intent i = new Intent(getActivity(), MainPanelActivity.class);
            startActivity(i);
        }*/

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}