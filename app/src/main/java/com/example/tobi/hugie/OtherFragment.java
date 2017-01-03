package com.example.tobi.hugie;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

/**
 * Created by tobi on 28.12.16.
 */

public class OtherFragment extends android.app.Fragment{

    private View otherFragmentView;
    private TextView tvCountdownYear;
    private TextView tvCountdownMonth;
    //private Chronometer chCountdownSince;
    TextView tvCountdownSince;
    private Handler handler;
    private Runnable runnable;
    int year = 2015;
    int month = 1;
    int day = 7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        final TextView tvCountdownYear = (TextView) otherFragmentView.findViewById(R.id.tv_count_year_display);
        final TextView tvCountdownMonth = (TextView) otherFragmentView.findViewById(R.id.tv_count_month_display);
        final Chronometer chCountdownSince = (Chronometer) otherFragmentView.findViewById(R.id.ch_count_since_display);
        tvCountdownSince = (TextView) otherFragmentView.findViewById(R.id.tv_count_since_display);
        int year = 2015;
        int month = 1;
        int day = 7;

        //countDownStart();
        final long ALMOST_YEAR= 1000*60*60*24*300;
        final long ALMOST_MONTH= 1000*60*60*24*27;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month, day, 0, 0, 0);

        long startMillis = System.currentTimeMillis(); //get the start time in milliseconds
        long endMillis = endCalendar.getTimeInMillis(); //get the end time in milliseconds
        long totalMillis = (endMillis - startMillis); //total time in milliseconds


        chCountdownSince.setBase(endMillis);

        chCountdownSince.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {

                long t = System.currentTimeMillis() - chCountdownSince.getBase();
                long days = TimeUnit.MILLISECONDS.toDays(t);
                t -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(t);
                t -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(t);
                t -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(t);
                final String leftYearString = String.format(getString(R.string.left_year_string), days, hours, minutes, seconds);
                Log.d("Entered in tick", leftYearString);
                tvCountdownSince.setText(leftYearString);
            }
        });
        chCountdownSince.start();

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
/*
    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run(){
                handler.postDelayed(this, 1000);
                try {
                    FestCountdownTimer timer = new FestCountdownTimer(0, 0, 0, day, month, year);
                    new CountDownTimer(-timer.getIntervalMillis(), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished){
                            int days = (int) ((millisUntilFinished / 1000) / 86400);
                            int hours = (int) (((millisUntilFinished / 1000)
                                    - (days * 86400)) / 3600);
                            int minutes = (int) (((millisUntilFinished / 1000)
                                    - (days * 86400) - (hours * 3600)) / 60);
                            int seconds = (int) ((millisUntilFinished / 1000) % 60);
                            String daysPast = String.format("Days past: %d", days);
                            tvCountdownSince.setText(daysPast);
                        }
                        @Override
                        public void onFinish() {

                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);

    }*/
}

