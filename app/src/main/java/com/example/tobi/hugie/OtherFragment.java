package com.example.tobi.hugie;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by tobi on 28.12.16.
 */

public class OtherFragment extends android.app.Fragment{

    private View otherFragmentView;
    private Chronometer totalChr;
    final long ALMOST_YEAR = 25920000000L; //1000*60*60*24*300;
    final long ALMOST_MONTH = 2332800000L; //1000*60*60*24*27;
    private TextView daysAni;
    private TextView hoursAni;
    private TextView minutesAni;
    private TextView secondsAni;
    private TextView daysMen;
    private TextView hoursMen;
    private TextView minutesMen;
    private TextView secondsMen;
    private TextView daysSince;
    private TextView hoursSince;
    private TextView minutesSince;
    private TextView secondsSince;
    private int year;
    private int month;
    private int day;
    Calendar dateSelectedCal = Calendar.getInstance();
    long currentTimeMillis;
    long dateSelectedMillis;
    long diffMillis;
    long diffMonthMillis;
    long diffYearMillis;
    CountDownTimer monthCdT;
    CountDownTimer yearCdT;


    DatePicker picker;
    Button displayDate;
    TextView currentDateLbl;
    boolean calendarOpen = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        totalChr = (Chronometer) otherFragmentView.findViewById(R.id.ch_count_since_display);
        daysAni = (TextView) otherFragmentView.findViewById(R.id.days_ani);
        hoursAni = (TextView) otherFragmentView.findViewById(R.id.hours_ani);
        minutesAni = (TextView) otherFragmentView.findViewById(R.id.minutes_ani);
        secondsAni = (TextView) otherFragmentView.findViewById(R.id.seconds_ani);
        daysMen = (TextView) otherFragmentView.findViewById(R.id.days_men);
        hoursMen = (TextView) otherFragmentView.findViewById(R.id.hours_men);
        minutesMen = (TextView) otherFragmentView.findViewById(R.id.minutes_men);
        secondsMen = (TextView) otherFragmentView.findViewById(R.id.seconds_men);
        daysSince = (TextView) otherFragmentView.findViewById(R.id.days_since);
        hoursSince = (TextView) otherFragmentView.findViewById(R.id.hours_since);
        minutesSince = (TextView) otherFragmentView.findViewById(R.id.minutes_since);
        secondsSince = (TextView) otherFragmentView.findViewById(R.id.seconds_since);

        year = 2015;
        month = 1;
        day = 7;
        dateSelectedCal.set(year, month, day, 0, 0, 0);

        currentTimeMillis = System.currentTimeMillis(); //get the start time in milliseconds
        dateSelectedMillis = dateSelectedCal.getTimeInMillis(); //get the end time in milliseconds
        diffMillis = (dateSelectedMillis - currentTimeMillis); //total time in milliseconds
        diffYearMillis = getMissingMillisAni(diffMillis);
        diffMonthMillis = getMissingMillisMens(diffYearMillis);

        currentDateLbl = (TextView) otherFragmentView.findViewById(R.id.lblDate);
        picker = (DatePicker) otherFragmentView.findViewById(R.id.dpResult);
        displayDate = (Button) otherFragmentView.findViewById(R.id.btnChangeDate);

        picker.updateDate(year, month, day);
        currentDateLbl.setText(getCurrentDate());
        totalChr.setBase(dateSelectedMillis);

        totalChr.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {

                long t = System.currentTimeMillis() - totalChr.getBase();
                long days = TimeUnit.MILLISECONDS.toDays(t);
                t -= TimeUnit.DAYS.toMillis(days);
                daysSince.setText(String.valueOf(days));
                
                long hours = TimeUnit.MILLISECONDS.toHours(t);
                t -= TimeUnit.HOURS.toMillis(hours);
                hoursSince.setText(String.valueOf(hours));

                long minutes = TimeUnit.MILLISECONDS.toMinutes(t);
                t -= TimeUnit.MINUTES.toMillis(minutes);
                minutesSince.setText(String.valueOf(minutes));

                long seconds = TimeUnit.MILLISECONDS.toSeconds(t);
                secondsSince.setText(String.valueOf(seconds));
            }
        });

        yearCdT = new CountDownTimer(diffYearMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);
                daysAni.setText(String.valueOf(days));

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                hoursAni.setText(String.valueOf(hours));

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);
                minutesAni.setText(String.valueOf(minutes));

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                secondsAni.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
            }
        };

        monthCdT = new CountDownTimer(diffMonthMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);
                daysMen.setText(String.valueOf(days));

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                hoursMen.setText(String.valueOf(hours));

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);
                minutesMen.setText(String.valueOf(minutes));

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                secondsMen.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
            }
        };

        monthCdT.start();
        yearCdT.start();
        totalChr.start();

        displayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (calendarOpen){
                    picker.setVisibility(View.GONE);
                    currentDateLbl.setText(getCurrentDate());
                    dateSelectedCal.set(year, month, day, 0, 0, 0);
                    dateSelectedMillis = dateSelectedCal.getTimeInMillis();
                    diffMillis = (dateSelectedMillis - currentTimeMillis);
                    totalChr.setBase(dateSelectedMillis);
                    diffYearMillis = getMissingMillisAni(diffMillis);
                    diffMonthMillis = getMissingMillisMens(diffYearMillis);
                    yearCdT.start();
                    monthCdT.start();
                    calendarOpen = false;
                }
                else {
                    picker.setVisibility(View.VISIBLE);
                    calendarOpen = true;
                }
            }
        });

        return otherFragmentView;
    }

    public String getCurrentDate(){
        StringBuilder builder = new StringBuilder();
        builder.append("Current Date: ");
        builder.append(picker.getDayOfMonth() + "/");
        builder.append((picker.getMonth() + 1) + "/");//month is 0 based
        builder.append(picker.getYear());
        day = picker.getDayOfMonth();
        month = picker.getMonth();
        year = picker.getYear();
        return builder.toString();
    }

    public long getMissingMillisMens(long totalMillisTemp){
        long diffToMonthMillis;
        int monthTemp = month;
        Calendar dateMonthCal = Calendar.getInstance();

        for (long i = totalMillisTemp; i < 0 || i > ALMOST_MONTH; i-=ALMOST_MONTH) {
            if (totalMillisTemp > ALMOST_MONTH){
                dateMonthCal.set(year, --monthTemp, day);
                diffToMonthMillis = dateMonthCal.getTimeInMillis();
                totalMillisTemp = (diffToMonthMillis - currentTimeMillis);
            }
            else
                break;
            if (totalMillisTemp < 0){
                dateMonthCal.set(year, ++monthTemp, day);
                diffToMonthMillis = dateMonthCal.getTimeInMillis();
                totalMillisTemp = (diffToMonthMillis - currentTimeMillis);
                break;
            }
        }
        return  totalMillisTemp;
    }

    public long getMissingMillisAni(long diffToYearMillis){
        Calendar dateYearCal = Calendar.getInstance();
        int yearTemp = year;

        for (long i = diffToYearMillis; i < 0; i+=ALMOST_YEAR) {
            if (diffToYearMillis < 0){
                dateYearCal.set(++yearTemp, month, day);
                long dateYearMillis = dateYearCal.getTimeInMillis();
                diffToYearMillis = (dateYearMillis - currentTimeMillis);
            }
            else
                break;
        }
        return diffToYearMillis;
    }
}




