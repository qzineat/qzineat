package com.codepath.qzineat.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.codepath.android.qzineat.R;

import java.util.Calendar;

/**
 * Created by glondhe on 3/4/16.
 */
public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public static StringBuilder date;
    private int _year;
    private int _month;
    private int _day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year;
        _month = monthOfYear;
        _day = dayOfMonth;

        this.date = new StringBuilder()
                .append(_year).append("/");

        if (_month < 10) this.date.append(0).append(_month + 1).append("/");
        else this.date.append(_month + 1).append("/");

        if (_day < 10) this.date.append(0).append(_day);
        else this.date.append(_day + 1);
        TextView tvDatePicker = (TextView) getActivity().findViewById(R.id.tvDatePicker);
        tvDatePicker.setText(this.date);

    }
}