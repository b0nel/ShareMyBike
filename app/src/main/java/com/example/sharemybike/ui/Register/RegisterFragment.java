package com.example.sharemybike.ui.Register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sharemybike.R;
import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.databinding.ActivityMainPanelBinding;
import com.example.sharemybike.databinding.RegisterFragmentBinding;
import com.example.sharemybike.pojos.Bike;
import com.example.sharemybike.pojos.User;
import com.example.sharemybike.ui.Dates.DatesFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    public static FusedLocationProviderClient fusedLocationClient;
    private RegisterFragmentBinding binding;
    public static Location fragment_location;
    public static User user = User.getInstance(); //singleton
    ActivityResultLauncher<Intent> mTakePicture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.w(TAG, "onActivityResult");
                    Bitmap bmp = (Bitmap) result.getData().getExtras().get("data");
                    binding.imgSofa.setImageBitmap(bmp);
                }

            });

    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                Log.w(TAG, "fineLocationGranted");
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Log.w(TAG, "coarseLocationGranted");
                            } else {
                                // No location access granted.
                                Log.w(TAG, "No location granted");
                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        Log.w(TAG, "permission request");
    }

    @SuppressLint("MissingPermission") //permissions already checked in onCreate method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient((Context)getActivity());
        binding = RegisterFragmentBinding.inflate(getLayoutInflater());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //location available
                            binding.txtLatitude.setText(String.valueOf(location.getLatitude()));
                            binding.txtLongitude.setText(String.valueOf(location.getLongitude()));
                            fragment_location = location;

                        }
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error trying to get last grabbed location" + e);
                }
            });
        //return;
        binding.txtDetails.setText("Bike of " + user.getName() + "[" + user.getEmail() + "]");
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                mTakePicture.launch(intent);

            }
        });
        binding.btnAddMyBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String image, String owner, String description, String city, Double longitude, Double latitude, String location, Bitmap photo, String email

                Bike bike = new Bike(
                        "image",
                        user.getName(),
                        binding.txtDescription.getText().toString(),
                        binding.txtCity.getText().toString(),
                        fragment_location.getLongitude(),
                        fragment_location.getLatitude(),
                        binding.txtLocation.getText().toString(),
                        ((BitmapDrawable)binding.imgSofa.getDrawable()).getBitmap(),
                        user.getEmail()
                );
                BikesContent.ITEMS.add(bike);
            }
        });

    }

}