package edu.buffalo.cse.blue.recordreplay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

public class NewObjectiveDialog extends DialogFragment {

	public static final String FIRST_KEY = "first";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		boolean first = false;
		Bundle b = this.getArguments();
		if (b.containsKey(FIRST_KEY)) {
			first = b.getBoolean(FIRST_KEY);
		}
		MainActivity activity = (MainActivity) this.getActivity();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.objective_dialog_title);
		LayoutInflater inflater = activity.getLayoutInflater();
		builder.setView(
				inflater.inflate(R.layout.create_objective_dialog, null))
				.setPositiveButton(R.string.create,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
		if (!first) {
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							NewObjectiveDialog.this.getDialog().cancel();
						}
					});
		}
		return builder.create();
	}
}
