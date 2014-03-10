
package edu.buffalo.cse.blue.pocketmocker;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {

    private static final String TAG = "PM_SETTINGS";
    
    private final ArrayList<String> packagesToMock = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        PackageManager pm = this.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        LinearLayout layout = (LinearLayout) findViewById(R.id.app_linear_layout);
        CheckBox checkBox;
        for (ApplicationInfo info : packages) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                Log.v(TAG, "Found: " + pm.getApplicationLabel(info).toString() + " "
                        + info.packageName);
                checkBox = new CheckBox(this);
                checkBox.setText(pm.getApplicationLabel(info).toString());
                // Hack so we can get this later when it's checked
                checkBox.setHint(info.packageName);
                // Medium-sized text
                checkBox.setTextSize(18);
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                        if(isChecked) {
                            packagesToMock.add(button.getHint().toString());
                        } else {
                            packagesToMock.remove(button.getHint().toString());
                        }
                    }

                });
                layout.addView(checkBox);
            }
        }
    }

    public void saveSettings(View view) {
        Log.v(TAG, "SAVE");
    }

}
