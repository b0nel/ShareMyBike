package com.example.sharemybike.bikes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.sharemybike.R;
import com.example.sharemybike.placeholder.PlaceholderContent;
import com.example.sharemybike.pojos.Bike;
import com.example.sharemybike.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageReference;

public class BikesContent extends ArrayAdapter<Bike>{

    private static final String TAG = "BikesContent";
    //List of all the bikes to be listed in the RecyclerView
    public static List<Bike> ITEMS = new ArrayList<Bike>();
    public static String selectedDate;
    public static DatabaseReference mDatabase;
    public static StorageReference mStorageReference;

    public BikesContent(Context c){
        super(c, R.layout.fragment_item_list);
        loadBikesList();
    }

    public static void loadBikesFromJSON(Context c) {

        String json = null;
        try {
            InputStream is = c.getAssets().open("bikeList.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray couchList = jsonObject.getJSONArray("bike_list");
            for (int i = 0; i < couchList.length(); i++) {
                JSONObject jsonCouch = couchList.getJSONObject(i);
                String owner = jsonCouch.getString("owner");
                String description = jsonCouch.getString("description");
                String city=jsonCouch.getString("city");
                String location=jsonCouch.getString("location");
                String email=jsonCouch.getString("email");
                Bitmap photo=null;
                try {
                    photo= BitmapFactory.decodeStream(
                            c.getAssets().open("images/"+
                                    jsonCouch.getString("image")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ITEMS.add(new Bike(photo,owner,description,city,location,email));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private void loadBikesList() {
        if (ITEMS.isEmpty()) {
            Log.d(TAG, "loadBikesList empty");
            mDatabase = FirebaseDatabase.getInstance("https://sharemybike-52b38-default-rtdb.europe-west1.firebasedatabase.app").getReference();

            mDatabase.child("bikes_list").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "loadBikesList onDataChange");
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Bike bike = productSnapshot.getValue(Bike.class);
                        downloadPhoto(bike);
                        ITEMS.add(bike);
                    }
                    notifyDataSetChanged();
                    //listener.notifyDataSetChanged();
                    //mValues.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled error:" + error);
                }
            });
        }
    }
    private void downloadPhoto(Bike c) {

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
                            //notifyDataSetChanged();
                            Log.d(TAG, "Loaded pic " + c.getImage() + ";" + url + localFile.getAbsolutePath());
                        }
                    }
                }

            });
        } catch (IOException e) {
            Log.e("downloadPhoto", "error:" + e);
        }
    }
}
