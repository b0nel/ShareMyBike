package com.example.sharemybike.ui.Bike;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharemybike.MainPanelActivity;
import com.example.sharemybike.MyItemRecyclerViewAdapter;
import com.example.sharemybike.R;
import com.example.sharemybike.databinding.BikeFragmentBinding;
import com.example.sharemybike.pojos.Bike;
import com.example.sharemybike.ui.Dates.DatesFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BikeFragment extends Fragment {

    private BikeViewModel bikeViewModel;
    private BikeFragmentBinding binding;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private static final String TAG = "BikeFragment";
    public static List<Bike> ITEMS = new ArrayList<Bike>();
    public static String selectedDate;
    public static DatabaseReference mDatabase;
    public static StorageReference mStorageReference;
    public static boolean load_finished = false;
    public static ArrayAdapter<Bike> arrayAdapter;

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
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(this.ITEMS));

        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void loadBikesList() {
        ITEMS.clear();
        load_finished = false;
        if (ITEMS.isEmpty())
        {
            //Log.d(TAG, "loadBikesList empty");
            mDatabase = FirebaseDatabase.getInstance("https://sharemybike-52b38-default-rtdb.europe-west1.firebasedatabase.app").getReference();

            mDatabase.child("bikes_list").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "loadBikesList onDataChange");
                    if(!load_finished) {
                        // without this condition, database is read twice when writing new bikes
                        // in firebase. Probably there are better ways to do this
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Bike bike = productSnapshot.getValue(Bike.class);
                            downloadPhoto(bike);
                            ITEMS.add(bike);
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                    load_finished = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled error:" + error);
                }
            });
        }
    }

    public static void downloadPhoto(Bike c) {

        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(c.getImage());
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final File localFile = File.createTempFile("PNG_" + timeStamp, ".png");
            mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Insert the downloaded image in its right position at the ArrayList

                    String url = "gs://" + taskSnapshot.getStorage().getBucket() + "/" + taskSnapshot.getStorage().getName();
                    ;
                    Log.d(TAG, "Loaded " + url);
                    for (Bike c : ITEMS) {
                        if (c.getImage().equals(url)) {
                            c.setPhoto(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            arrayAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded pic " + c.getImage() + ";" + url + localFile.getAbsolutePath());
                        }
                    }
                }

            });
        } catch (IOException e) {
            Log.e("downloadPhoto", "error:" + e);
        }
    }

    public static void initArrayAdapter(Context context){
        arrayAdapter = new ArrayAdapter<Bike>(context, R.layout.bike_fragment, ITEMS);
    }

    public static void addToDatabase(Bike bike){
        //first, upload image to storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();//storage.getReference();
        StorageReference imageRef = storageRef.child("bike" + (ITEMS.size() + 1) + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bike.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "unsuccessful upload:" + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "successful upload");
            }
        });

        //now, add to database
        DatabaseReference database= FirebaseDatabase.getInstance("https://sharemybike-52b38-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        bike.setImage(storageRef.getStorage().getReference().toString() + "bike" + (ITEMS.size() + 1) + ".jpg");
        bike.setPhoto(null);// photo needs to be null when adding it to database, because doesn't accepts serializing arrays
        database.child("bikes_list/"+(ITEMS.size())).setValue(bike);

        //clear and read ITEMS
        BikeFragment.loadBikesList();
    }
}