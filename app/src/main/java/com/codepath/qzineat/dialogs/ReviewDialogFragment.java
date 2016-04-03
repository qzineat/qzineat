package com.codepath.qzineat.dialogs;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.codepath.qzineat.R;
import com.codepath.qzineat.utils.FragmentCode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/13/16.
 */
public class ReviewDialogFragment extends DialogFragment {

    @Bind(R.id.btnSubmit) Button btnSubmit;
    @Bind(R.id.btnCancel) Button btnCancel;
    @Bind(R.id.etReviewComment) EditText etReviewComment;
    @Bind(R.id.ratingBar) RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_review, container, false);
        ButterKnife.bind(this, view);

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

    private void onSubmit() {
        int rating = Math.round(ratingBar.getRating());
        Intent intent = new Intent();
        intent.putExtra("comment", etReviewComment.getText().toString().trim());
        intent.putExtra("rating", rating);

        getTargetFragment().onActivityResult(getTargetRequestCode(), FragmentCode.EVENT_DETAIL_REVIEW_FRAGMENT_RESULT_CODE, intent);
        dismiss();
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
}
