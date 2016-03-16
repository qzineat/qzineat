package com.codepath.qzineat.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.utils.FragmentCode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by Shyam Rokde on 3/13/16.
 */
public class EnrollDialogFragment extends DialogFragment {

    @Bind(R.id.btnSubmit) Button btnSubmit;
    @Bind(R.id.btnCancel) Button btnCancel;
    @Bind(R.id.etGuestCount) EditText etGuestCount;
    @Bind(R.id.etCardNumber) EditText etCardNumber;
    @Bind(R.id.etCardDate) EditText etCardDate;
    @Bind(R.id.etCardCVC) EditText etCardCVC;
    @Bind(R.id.tvTotalPrice) TextView tvTotalPrice;
    @Bind(R.id.llCard) LinearLayout llCard;


    Double eventPrice;
    String position;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_enroll, container, false);
        ButterKnife.bind(this, view);

        Bundle mArgs = getArguments();
        if(mArgs != null){
            eventPrice = mArgs.getDouble("price");
            if(eventPrice > 0){
                tvTotalPrice.setText(String.format("%.2f", eventPrice));
            }else {
                tvTotalPrice.setText("FREE");
                llCard.setVisibility(View.GONE);
            }
            if(mArgs.getString("position")!=null){
                position = mArgs.getString("position");
            }
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void onSubmit(){

        // GuestCount
        int guestCount = 1;
        if(!etGuestCount.getText().toString().trim().isEmpty()){
            guestCount = Integer.parseInt(etGuestCount.getText().toString().trim());
        }

        Intent intent = new Intent();
        intent.putExtra("guestCount", guestCount);
        if(position != null && !position.isEmpty()){
            intent.putExtra("position", position);
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), FragmentCode.ENROLL_DIALOG_FRAGMENT_RESULT_CODE, intent);
        dismiss();
    }

    @OnTextChanged(value = R.id.etGuestCount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onGuestCountTextChanged(Editable s) {
        if(eventPrice > 0){
            if(!etGuestCount.getText().toString().trim().isEmpty()){
                double d = Double.parseDouble(etGuestCount.getText().toString().trim());
                tvTotalPrice.setText(String.format("%.2f", eventPrice * d));
            }
        }
    }

    @OnTextChanged(value = R.id.etCardNumber, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }
    @OnTextChanged(value = R.id.etCardDate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }
    @OnTextChanged(value = R.id.etCardCVC, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }
        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;
}
