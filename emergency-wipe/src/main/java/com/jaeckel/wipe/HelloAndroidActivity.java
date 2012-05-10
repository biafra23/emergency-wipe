package com.jaeckel.wipe;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HelloAndroidActivity extends Activity {

  private final static String TAG                    = HelloAndroidActivity.class.getSimpleName();

  static final int            RESULT_ENABLE          = 1;
  public static final String  PREF_MAX_FAILED_UNLOCK = "max_failed_unlock_attempts";
  private ActivityManager     mAM;
  private ComponentName       mDeviceAdminSample;
  private Button              enableDeviceAdminButton;
  private DevicePolicyManager mDPM;

  private EditText            failedPwAttemptsField;
  private SharedPreferences   prefs;

  /**
   * Called when the activity is first created.
   * 
   * @param savedInstanceState
   *          If the activity is being re-initialized after previously being shut down then this Bundle
   *          contains the data it most recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise
   *          it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    prefs = HelloAndroidActivity.this.getSharedPreferences(AdminReceiver.class.getName(), 0);

    mDPM = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);

    mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);

    Log.i(TAG, "onCreate");

    setContentView(R.layout.main);

    failedPwAttemptsField = (EditText) findViewById(R.id.failed_pw_attempts);
    failedPwAttemptsField.setText("" + prefs.getInt(PREF_MAX_FAILED_UNLOCK, 3));
    failedPwAttemptsField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        Log.d(TAG, "onEditorAction");
        SharedPreferences.Editor editor = prefs.edit();

        String maxFailedPwAttempts = failedPwAttemptsField.getText().toString();

        editor.putInt(PREF_MAX_FAILED_UNLOCK, Integer.valueOf(maxFailedPwAttempts));

        editor.commit();

        return false;
      }
    });

    enableDeviceAdminButton = (Button) findViewById(R.id.enable_device_admin);
    if (mDPM.isAdminActive(mDeviceAdminSample)) {
      enableDeviceAdminButton.setText("Disable Device Admin");
    } else {
      enableDeviceAdminButton.setText("Enable Device Admin");

    }
    enableDeviceAdminButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
          // disable here
          Toast.makeText(HelloAndroidActivity.this, "Disabling Device Admin", Toast.LENGTH_LONG).show();
          mDPM.removeActiveAdmin(mDeviceAdminSample);
          if (!mDPM.isAdminActive(mDeviceAdminSample)) {
            enableDeviceAdminButton.setText("Enable Device Admin");
          }
        } else {
          // enable here

          Toast.makeText(HelloAndroidActivity.this, "Enabling Device Admin", Toast.LENGTH_LONG).show();
          // Launch the activity to have the user enable our admin.
          Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
          intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
          intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                          "Emergency Wipe needs these Device Admin privileges to monitor failed password attempts and to wipe the device. ");
          startActivityForResult(intent, RESULT_ENABLE);

          if (mDPM.isAdminActive(mDeviceAdminSample)) {
            enableDeviceAdminButton.setText("Disable Device Admin");
          }

        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mDPM.isAdminActive(mDeviceAdminSample)) {
      enableDeviceAdminButton.setText("Disable Device Admin");
    } else {
      enableDeviceAdminButton.setText("Enable Device Admin");

    }

    failedPwAttemptsField.setText("" + prefs.getInt(PREF_MAX_FAILED_UNLOCK, 3));

  }
}
