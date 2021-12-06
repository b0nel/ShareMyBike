package com.example.sharemybike;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.sharemybike.databinding.FragmentItemBinding;
import com.example.sharemybike.pojos.Bike;
import com.example.sharemybike.pojos.User;
import com.example.sharemybike.pojos.UserBooking;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Bike> mValues;
    public static User user = User.getInstance(); //singleton

    public MyItemRecyclerViewAdapter(List<Bike> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCity.setText(mValues.get(position).getCity());
        holder.mOwner.setText(mValues.get(position).getOwner());
        holder.mLocation.setText(mValues.get(position).getLocation());
        holder.mDescription.setText(mValues.get(position).getDescription());
        holder.mImg.setImageBitmap(mValues.get(position).getPhoto());
        holder.mBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //instead of mail, now we process the order in firebase
                String selectedDate = BikesContent.selectedDate.replace("Date selected: ", "");
                FirebaseDatabase database =  FirebaseDatabase.getInstance("https://sharemybike-52b38-default-rtdb.europe-west1.firebasedatabase.app");
                String userId = user.getAuth().getUid();
                UserBooking userBooking = new UserBooking(userId, user.getEmail(),
                        holder.mItem.getEmail(), holder.mItem.getCity(), selectedDate);

                DatabaseReference mRef = database.getReference().child("booking_requests");
                //mRef.setValue(userBooking);
                mRef.push().setValue(userBooking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
        //return mValues.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Bike mItem;
        public final TextView mCity;
        public final TextView mOwner;
        public final TextView mLocation;
        public final TextView mDescription;
        public final ImageButton mBtnImg;
        public final ImageView mImg;
        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mCity = binding.txtCity;
            mOwner = binding.txtOwner;
            mLocation = binding.txtLocation;
            mDescription = binding.txtDescription;
            mBtnImg = binding.btnMail;
            mImg = binding.imgPhoto;
        }

        @Override
        public String toString() {
            return super.toString();// + " '" + mDescription.getText() + "'";
        }
    }
}