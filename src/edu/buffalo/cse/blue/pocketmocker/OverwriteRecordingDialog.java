package edu.buffalo.cse.blue.pocketmocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class OverwriteRecordingDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity activity = (MainActivity) this.getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = this.getString(R.string.overwrite_recording_question);
		Bundle b = this.getArguments();
		message.replace("objective", b.getString("objective"));
		builder.setMessage(message)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.overwriteRecording();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						OverwriteRecordingDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

}
