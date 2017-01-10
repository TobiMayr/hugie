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
    private Chronometer chCountdownSince;
    final long ALMOST_YEAR= 1000*60*60*24*300;
    final long ALMOST_MONTH= 1000*60*60*24*27;
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
    Calendar endCalendar = Calendar.getInstance();
    long startMillis;
    long endMillis;
    long totalMillis;
    long totalMillisMonth;
    long totalMillisYear;
    CountDownTimer cdtm;
    CountDownTimer cdt;


    DatePicker picker;
    Button displayDate;
    TextView currentDateLbl;
    boolean calendarOpen = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        chCountdownSince = (Chronometer) otherFragmentView.findViewById(R.id.ch_count_since_display);
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
        endCalendar.set(year, month, day, 0, 0, 0);

        startMillis = System.currentTimeMillis(); //get the start time in milliseconds
        endMillis = endCalendar.getTimeInMillis(); //get the end time in milliseconds
        totalMillis = (endMillis - startMillis); //total time in milliseconds
        totalMillisYear = getMissingMilliesAni();
        totalMillisMonth = getMissingMilliesMens();

        currentDateLbl =(TextView) otherFragmentView.findViewById(R.id.lblDate);
        picker=(DatePicker)otherFragmentView.findViewById(R.id.dpResult);
        displayDate=(Button)otherFragmentView.findViewById(R.id.btnChangeDate);

        picker.updateDate(year, month, day);
        currentDateLbl.setText(getCurrentDate());
        chCountdownSince.setBase(endMillis);

        chCountdownSince.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {

                long t = System.currentTimeMillis() - chCountdownSince.getBase();
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
        chCountdownSince.start();


        cdt = new CountDownTimer(totalMillisYear, 1000) {
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
        cdt.start();

        cdtm = new CountDownTimer(totalMillisMonth, 1000) {
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
        cdtm.start();

        displayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (calendarOpen){
                    picker.setVisibility(View.GONE);
                    currentDateLbl.setText(getCurrentDate());
                    endCalendar.set(year, month, day, 0, 0, 0);
                    endMillis = endCalendar.getTimeInMillis();
                    totalMillis = (endMillis - startMillis);
                    chCountdownSince.setBase(endMillis);
                    totalMillisYear = getMissingMilliesAni();
                    totalMillisMonth = getMissingMilliesMens();
                    cdt.start();
                    cdtm.start();
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

    public long getMissingMilliesMens(){
        long endMillisMonth;
        long totalMillisTemp = totalMillis;
        int monthTemp = month;
        Calendar endCalendarTemp = Calendar.getInstance();

        for (long i = totalMillisTemp; i < 0 || i > ALMOST_MONTH; i-=ALMOST_MONTH) {
            if (totalMillisTemp > ALMOST_MONTH){
                endCalendarTemp.set(year, --monthTemp, day);
                endMillisMonth = endCalendarTemp.getTimeInMillis();
                totalMillisTemp = (endMillisMonth - startMillis);
            }
            else
                break;
            if (totalMillisTemp < 0){
                endCalendarTemp.set(year, ++monthTemp, day);
                endMillisMonth = endCalendarTemp.getTimeInMillis();
                totalMillisTemp = (endMillisMonth - startMillis);
                break;
            }
        }
        return  totalMillisTemp;
    }

    public long getMissingMilliesAni(){
        long totalMillisTemp = totalMillis;
        Calendar endCalendarTemp = Calendar.getInstance();
        int yearTemp = year;

        for (long i = totalMillisTemp; i < 0; i+=ALMOST_YEAR) {
            if (totalMillisTemp < 0){
                endCalendarTemp.set(++yearTemp, month, day);
                long endMillisTemp = endCalendarTemp.getTimeInMillis();
                totalMillisTemp = (endMillisTemp - startMillis);
            }
            else
                break;
        }
        return totalMillisTemp;
    }
}




