package com.jaeckel.wipe;

import java.io.File;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @author biafra
 * @date 5/10/12 9:59 AM
 */
public class AdminReceiver extends DeviceAdminReceiver {

  private final static String TAG                     = AdminReceiver.class.getSimpleName();

  private final int           MAX_FAILED_UNLOCK_COUNT = 3;
  private boolean             wipeExternal            = true;

  static SharedPreferences getSamplePreferences(Context context) {
    return context.getSharedPreferences(AdminReceiver.class.getName(), 0);
  }

  static String PREF_CUR_FAILED_PW    = "current_failed_pw";

  void showToast(Context context, CharSequence msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onEnabled(Context context, Intent intent) {

    resetFailedCount(context);

    showToast(context, "Sample Device Admin: enabled");
    Log.d(TAG, "Sample Device Admin: enabled");

  }

  @Override
  public CharSequence onDisableRequested(Context context, Intent intent) {
    return "This is an optional message to warn the user about disabling.";
  }

  @Override
  public void onDisabled(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: disabled");
    Log.d(TAG, "Sample Device Admin: disabled");
  }

  @Override
  public void onPasswordChanged(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: pw changed");
  }

  @Override
  public void onPasswordFailed(Context context, Intent intent) {

    SharedPreferences prefs = context.getSharedPreferences(AdminReceiver.class.getName(), 0);

    incFailedCount(context);

    showToast(context, "Sample Device Admin: pw failed");
    Log.d(TAG, "onPasswordFailed: " + intent);
    if (intent.getExtras() != null) {
      Log.d(TAG, "has extras: " + intent.getExtras());
    } else {
      Log.d(TAG, "Intent has no extras!");
    }

    Log.d(TAG, "onPasswordFailed: failedUnlockCount: " + getFailedCount(context));

    if (getFailedCount(context) > prefs.getInt(HelloAndroidActivity.PREF_MAX_FAILED_UNLOCK, 3)) {

      Log.d(TAG, "Wiping device...");

      final DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

      if (wipeExternal && Build.VERSION.SDK_INT > 8) {

        Log.d(TAG, "Wiping device and sdcard");
        mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);

        Log.d(TAG, "Deleting files on " + Environment.getExternalStorageDirectory().getAbsolutePath() + " in background...");

        new AsyncWipeTask(context).execute(Environment.getExternalStorageDirectory());

      } else {

        Log.d(TAG, "Wiping device");

        mDPM.wipeData(0);
      }

    }
  }

  @Override
  public void onPasswordSucceeded(Context context, Intent intent) {

    resetFailedCount(context);

    showToast(context, "Sample Device Admin: pw succeeded");
    Log.d(TAG, "onPasswordSucceeded: " + intent);
  }

  // HELPER

  private void resetFailedCount(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(AdminReceiver.class.getName(), 0);

    SharedPreferences.Editor editor = prefs.edit();

    editor.remove(PREF_CUR_FAILED_PW);

    editor.commit();

  }

  private void incFailedCount(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(AdminReceiver.class.getName(), 0);

    int failedCount = prefs.getInt(PREF_CUR_FAILED_PW, 0);

    SharedPreferences.Editor editor = prefs.edit();

    editor.putInt(PREF_CUR_FAILED_PW, failedCount + 1);

    editor.commit();

  }

  private int getFailedCount(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(AdminReceiver.class.getName(), 0);

    return prefs.getInt(PREF_CUR_FAILED_PW, 0);

  }


}
