package com.thetorine.android.samaritan.utilities;

import com.thetorine.android.samaritan.InputActivity;
import com.thetorine.samaritan.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DialogAdd extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_addphrase, null);
		builder.setView(view);
		
		builder.setTitle("Add Phrase");
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getDialog().cancel();
			}
		});
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et = (EditText) ((AlertDialog)dialog).findViewById(R.id.add_phrase_field);
				String s = et.getText().toString();
				if(s.length() > 0) {
					((InputActivity) getActivity()).addPhrase(s);
				}
			}
		});
		
		return builder.create();
	}
}
