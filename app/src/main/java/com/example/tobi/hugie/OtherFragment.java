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
    private TextView tvCountdownMonth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        final TextView tvCountdownYear = (TextView) otherFragmentView.findViewById(R.id.tv_count_year_display);
        final TextView tvCountdownMonth = (TextView) otherFragmentView.findViewById(R.id.tv_count_month_display);
        int year = 2015;
        int month = 1;
        int day = 7;

        final long ALMOST_YEAR= 1000*60*60*24*300;
        final long ALMOST_MONTH= 1000*60*60*24*27;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month, day, 0, 0, 0);

        long startMillis = System.currentTimeMillis(); //get the start time in milliseconds
        long endMillis = endCalendar.getTimeInMillis(); //get the end time in milliseconds
        long totalMillis = (endMillis - startMillis); //total time in milliseconds

        for (long i = totalMillis; i < 0; i+=ALMOST_YEAR) {
            if (totalMillis < 0){
                endCalendar.set(++year, month, day);
                endMillis = endCalendar.getTimeInMillis();
                totalMillis = (endMillis - startMillis);
            }
            else
                break;
        }

        long endMillisMonth = endMillis;
        long totalMillisMonth = totalMillis;

        for (long i = totalMillisMonth; i < 0 || i > ALMOST_MONTH; i-=ALMOST_MONTH) {
            if (totalMillisMonth > ALMOST_MONTH){
                endCalendar.set(year, --month, day);
                endMillisMonth = endCalendar.getTimeInMillis();
                totalMillisMonth = (endMillisMonth - startMillis);
            }
            else
                break;
            if (totalMillisMonth < 0){
                endCalendar.set(year, ++month, day);
                endMillisMonth = endCalendar.getTimeInMillis();
                totalMillisMonth = (endMillisMonth - startMillis);
                break;
            }
        }


        CountDownTimer cdt = new CountDownTimer(totalMillis, 1000) {
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

        CountDownTimer cdtm = new CountDownTimer(totalMillisMonth, 1000) {
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
                tvCountdownMonth.setText(leftYearString);
            }

            @Override
            public void onFinish() {
            }
        };
        cdtm.start();

        return otherFragmentView;
    }
}
