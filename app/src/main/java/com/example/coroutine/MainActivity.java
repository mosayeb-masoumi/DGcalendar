package com.example.coroutine;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import calendar.util.Constants;
import calendar.util.Util;

import com.devadvance.circularseekbar.CircularSeekBar;

import calendar.CivilDate;
import calendar.DateConverter;
import calendar.IslamicDate;
import calendar.PersianDate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView dateNum,dayOfWeek,monthAndYear;
    private TextView gregorianDateDay;
    private TextView islamicDateDay;
    private TextView shamsiDateDay;
    private CircularSeekBar circularSeekBar;
    private int selectedDay;

    private  LinearLayout next,prev,circularLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        next=findViewById(R.id.next);
        prev=findViewById(R.id.prev);
        circularLay=findViewById(R.id.circularLay);

        gregorianDateDay = findViewById(R.id.gregorian_date_day);
        islamicDateDay =findViewById(R.id.islamic_date_day);

        shamsiDateDay = findViewById(R.id.shamsi_date_day);
        dateNum = findViewById(R.id.dateNum);
        dayOfWeek = findViewById(R.id.daysOfWeek);
        monthAndYear = findViewById(R.id.monthAndYear);
        circularSeekBar=findViewById(R.id.circularSeekBar1);
        circularSeekBar.setMax(getNumberOfDay());

        circularSeekBar.setProgress(DateConverter.jdnToPersian(Util.getTodayJdn()).getDayOfMonth());

        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    selectedDay = progress;
                    checkVisibility(progress);
                    PersianDate persianDateToday = DateConverter.jdnToPersian(Util.getTodayJdn());
                    PersianDate persianDate = new PersianDate(persianDateToday.getYear(), persianDateToday.getMonth(), progress);
                    selectDayChangeUI(persianDate);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        selectDay(Util.getTodayJdn());
    }

    private void selectDay(long jdn) {

        PersianDate persianDate = DateConverter.jdnToPersian(jdn);
        selectedDay = persianDate.getDayOfMonth();

        CivilDate civilDate = DateConverter.persianToCivil(persianDate);
        IslamicDate hijriDate = DateConverter.civilToIslamic(civilDate, Util.getIslamicOffset());

        shamsiDateDay.setText( Util.getMonthName(persianDate) + " / "+"" + (persianDate.getYear()));
        shamsiDateDay.setText(Util.getMonthName(persianDate));
        gregorianDateDay.setText(civilDate.getDayOfMonth()+ " "+Util.getMonthName(civilDate)+ " " + (civilDate.getYear()));
        islamicDateDay.setText( hijriDate.getDayOfMonth()+ " " + Util.getMonthName(hijriDate)+ " " + (hijriDate.getYear()));

        dateNum.setText("" + persianDate.getDayOfMonth());
        Constants.TODAY_DATE = persianDate.getDayOfMonth();
        dayOfWeek.setText("" + Util.getWeekDayName(persianDate));
        monthAndYear.setText(Util.getMonthName(persianDate) + "  "+(persianDate.getYear()));

    }

    private int getNumberOfDay() {

        PersianDate persianDate = DateConverter.jdnToPersian(Util.getTodayJdn());

        if (persianDate.getMonth() > 6)
            return 30;
        else
            return 31;
    }


    private void selectDayChangeUI(PersianDate persianDate) {
        CivilDate civilDate = DateConverter.persianToCivil(persianDate);
        IslamicDate hijriDate = DateConverter.civilToIslamic(civilDate, Util.getIslamicOffset());

        shamsiDateDay.setText("" + Util.getMonthName(persianDate) + " / "+ "" + (persianDate.getYear()));

        gregorianDateDay.setText(" " + civilDate.getDayOfMonth()+ Util.getMonthName(civilDate)+ " " + (civilDate.getYear()));

        islamicDateDay.setText( "" + hijriDate.getDayOfMonth()+ " " + Util.getMonthName(hijriDate)+ " " + (hijriDate.getYear()));
        dateNum.setText("" + persianDate.getDayOfMonth());
        dayOfWeek.setText("" + Util.getWeekDayName(persianDate));
        monthAndYear.setText( "" + Util.getMonthName(persianDate) + "  " + (persianDate.getYear()));
    }

    private void changeDay(int selectedDay) {
        circularSeekBar.setProgress(selectedDay);
        checkVisibility(selectedDay);
        PersianDate persianDateToday = DateConverter.jdnToPersian(Util.getTodayJdn());
        PersianDate persianDate = new PersianDate(persianDateToday.getYear(), persianDateToday.getMonth(), selectedDay);
        selectDayChangeUI(persianDate);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.next) {
            selectedDay = selectedDay + 1;
            if (!(selectedDay > getNumberOfDay())) {
                changeDay(selectedDay);
            }


        } else if (view.getId() == R.id.prev) {
            selectedDay = selectedDay - 1;
            if (selectedDay > 0) {
                changeDay(selectedDay);
            }

        }
    }

    private void checkVisibility(int day) {
        if (day == 1) {
            prev.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        } else if (day >= getNumberOfDay()) {
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
        }

    }

}