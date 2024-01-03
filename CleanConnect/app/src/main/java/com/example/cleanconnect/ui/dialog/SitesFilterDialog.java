package com.example.cleanconnect.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.DialogSitesFilterBinding;
import com.example.cleanconnect.interfaces.ClickCallBack;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SitesFilterDialog extends DialogFragment {

    private static final String TAG = "SitesFilterDialogFragment";

    private DialogSitesFilterBinding bind;
    private ClickCallBack click;

    public SitesFilterDialog(ClickCallBack click){
        this.click = click;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bind = DialogSitesFilterBinding.inflate(getLayoutInflater());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setView(bind.getRoot());
        final Spinner spinner = bind.spinnerSortBy;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set up the buttons
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the user input and notify the listener
                String distString = bind.editTextMaxDistance.getText().toString();
                double maxDistance = Double.parseDouble(distString.isEmpty() ? "0" : distString.toString());
                String sortBy = spinner.getSelectedItem().toString();
                Log.d(TAG, sortBy);
                click.onFilterApply(maxDistance, sortBy);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

}

