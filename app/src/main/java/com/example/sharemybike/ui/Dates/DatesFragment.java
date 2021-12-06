package com.example.sharemybike.ui.Dates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sharemybike.R;
import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.databinding.DatesFragmentBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class DatesFragment extends Fragment {

    private DatesViewModel datesViewModel;
    private DatesFragmentBinding binding;
    public static boolean isDateSelected = false;
    private static Calendar date;
    private static long milliTime;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = DatesFragmentBinding.inflate(inflater, container, false);
        if(isDateSelected){
            binding.textviewFirst.setText(BikesContent.selectedDate);
            binding.calendarView.setDate(milliTime, true, true);
            //mCalendarView.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(selectedDate).getTime(), true, true);
        }
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*BikesContent.selectedDate = (String) binding.textviewFirst.getText();
                NavHostFragment.findNavController(DatesFragment.this)
                        .navigate(R.id.nav_host_fragment_content_main_panel);*/
                if(isDateSelected){
                    NavHostFragment.findNavController(DatesFragment.this)
                            .navigate(R.id.action_nav_dates_to_nav_bikes);
                }
                else{
                    //popup to remember to select the date.
                    Snackbar.make(view, "Select the date before going to next step", Snackbar.LENGTH_LONG);
                }
            }
        });

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                binding.textviewFirst.setText("Date selected: " + day + "/" + (month+1) + "/" + year);
                BikesContent.selectedDate = (String) binding.textviewFirst.getText();
                isDateSelected = true;
                date = Calendar.getInstance();
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, day);

                milliTime = date.getTimeInMillis();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}