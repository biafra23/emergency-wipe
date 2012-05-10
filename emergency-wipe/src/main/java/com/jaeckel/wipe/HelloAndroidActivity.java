package com.jaeckel.wipe;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

public class HelloAndroidActivity extends Activity {

  static final int            RESULT_ENABLE = 1;
  private DevicePolicyManager mDPM;
  private ActivityManager     mAM;
  private ComponentName       mDeviceAdminSample;

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

    final DevicePolicyManager mDPM = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);

    mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);

    // Initializes the logging
    Log.init();

    // Log a message (only on dev platform)
    Log.i(this, "onCreate");

    setContentView(R.layout.main);

    final Button enableDeviceAdminButton = (Button) findViewById(R.id.enable_device_admin);
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
    final DevicePolicyManager mDPM = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);

    Button enableDeviceAdminButton = (Button) findViewById(R.id.enable_device_admin);
    if (mDPM.isAdminActive(mDeviceAdminSample)) {
      enableDeviceAdminButton.setText("Disable Device Admin");
    } else {
      enableDeviceAdminButton.setText("Enable Device Admin");

    }
  }
}
