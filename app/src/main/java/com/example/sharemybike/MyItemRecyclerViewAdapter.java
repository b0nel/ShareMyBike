package com.example.sharemybike;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.sharemybike.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<BikesContent.Bike> mValues;

    public MyItemRecyclerViewAdapter(List<BikesContent.Bike> items) {
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
                //Código para enviar el email
                Intent i= new Intent();
                i.createChooser(i,"Choose the app to send the email with your order");
                i.setAction(Intent.ACTION_SEND);
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[] {holder.mItem.getEmail()});
                String subjectText = "Couch App: I'd like to book your bike";
                i.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                String emailText = "Dear Mr/Mrs " + holder.mItem.getOwner() + ":\n" +
                        " I'd like to use your bike at " + holder.mItem.getLocation() + " (" +
                        holder.mItem.getCity() + ")\n for the following date: " +
                        BikesContent.selectedDate.replace("Date selected: ", "")
                        + "\nCan you confirm its availability?\nKindest regards";
                i.putExtra(Intent.EXTRA_TEXT,emailText);
                i.setType("message/rfc822");
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public BikesContent.Bike mItem;
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