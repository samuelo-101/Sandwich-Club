package com.udacity.sandwichclub;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.exception.AppException;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.AppUtils;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    private ProgressBar mProgressBarLoadingDetails;
    private ConstraintLayout mConstraintLayoutDetailsContainer;
    private ImageView ingredientsIv;
    private TextView mTextViewAlsoKnownAs;
    private TextView mTextViewIngredients;
    private TextView mTextViewDescription;
    private TextView mTextViewOrigin;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mConstraintLayoutDetailsContainer = findViewById(R.id.constraintLayout_details_container);
        mProgressBarLoadingDetails = findViewById(R.id.progressBar_loading_details);
        ingredientsIv = findViewById(R.id.image_iv);
        mTextViewAlsoKnownAs = findViewById(R.id.also_known_tv);
        mTextViewIngredients = findViewById(R.id.ingredients_tv);
        mTextViewDescription = findViewById(R.id.description_tv);
        mTextViewOrigin = findViewById(R.id.origin_tv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }


        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];

        showLoadingIndicator(true);
        try {
            Sandwich sandwich = JsonUtils.parseSandwichJson(json);
            if (sandwich == null) {
                // Sandwich data unavailable
                closeOnError();
                return;
            }

            populateUI(sandwich);
            Picasso.with(this)
                    .load(sandwich.getImage())
                    .error(R.drawable.ic_broken_image)
                    .placeholder(R.drawable.ic_image)
                    .into(ingredientsIv);

            setTitle(sandwich.getMainName());
        } catch (AppException ex) {
            handleApiException(ex);
        } finally {
            showLoadingIndicator(false);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        String alsoKnownAs = AppUtils.getConcatenatedStringFromListOfStrings(sandwich.getAlsoKnownAs());
        String ingredients = AppUtils.getConcatenatedStringFromListOfStrings(sandwich.getIngredients());
        String description = sandwich.getDescription();
        String placeOfOrigin = sandwich.getPlaceOfOrigin();
        mTextViewAlsoKnownAs.setText(TextUtils.isEmpty(alsoKnownAs) ? getString(R.string.ui_empty_string_placeholder) : alsoKnownAs);
        mTextViewIngredients.setText(TextUtils.isEmpty(ingredients) ? getString(R.string.ui_empty_string_placeholder) : ingredients);
        mTextViewDescription.setText(TextUtils.isEmpty(description) ? getString(R.string.ui_empty_string_placeholder) : description);
        mTextViewOrigin.setText(TextUtils.isEmpty(placeOfOrigin) ? getString(R.string.ui_empty_string_placeholder) : placeOfOrigin);
    }

    private void showLoadingIndicator(boolean showProgressBar) {
        mProgressBarLoadingDetails.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
        mConstraintLayoutDetailsContainer.setVisibility(showProgressBar ? View.GONE : View.VISIBLE);
    }

    private void handleApiException(AppException ex) {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.generic_error_message_title))
                .setMessage(AppUtils.getStringResourceByName(this, ex.getCode()))
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
