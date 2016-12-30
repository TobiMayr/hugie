package com.example.tobi.hugie;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by tobi on 28.12.16.
 */

public class OtherFragment extends android.app.Fragment{

    private View otherFragmentView;
    private TextView tvCountdownYear;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        int year = 2015;
        int month = 1;
        int day = 7;
        final TextView tvCountdownYear = (TextView) otherFragmentView.findViewById(R.id.tv_Count_Year_display);
        final long ALMOST_YEAR= 1000*60*60*24*300;
        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calendar = Calendar.getInstance();
        end_calendar.set(year, month, day);

        long start_millis = start_calendar.getTimeInMillis(); //get the start time in milliseconds
        long end_millis = end_calendar.getTimeInMillis(); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        for (long i = total_millis; i < 0; i+=ALMOST_YEAR) {
            if (total_millis < 0){
                end_calendar.set(++year, month, day);
                end_millis = end_calendar.getTimeInMillis();
                total_millis = (end_millis - start_millis);

            }
            else
                break;
        }


        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                String leftYearString = getString(R.string.left_year_string);
                leftYearString = String.format(leftYearString, days, hours, minutes, seconds);
                tvCountdownYear.setText(leftYearString);
            }

            @Override
            public void onFinish() {
            }
        };
        cdt.start();

        return otherFragmentView;
    }
}
