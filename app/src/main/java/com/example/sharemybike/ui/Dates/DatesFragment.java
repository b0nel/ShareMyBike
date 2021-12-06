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

import com.example.sharemybike.FirstFragment;
import com.example.sharemybike.R;
import com.example.sharemybike.bikes.BikesContent;
import com.example.sharemybike.databinding.DatesFragmentBinding;
import com.example.sharemybike.databinding.FragmentFirstBinding;

public class DatesFragment extends Fragment {

    private DatesViewModel datesViewModel;
    private DatesFragmentBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = DatesFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BikesContent.selectedDate = (String) binding.textviewFirst.getText();
                NavHostFragment.findNavController(DatesFragment.this)
                        .navigate(R.id.nav_host_fragment_content_main_panel);
            }
        });*/

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                binding.textviewFirst.setText("Date selected: " + day + "/" + (month+1) + "/" + year);
                BikesContent.selectedDate = (String) binding.textviewFirst.getText();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}