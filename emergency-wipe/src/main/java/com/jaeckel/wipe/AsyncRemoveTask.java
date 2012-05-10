package com.jaeckel.wipe;

import java.io.File;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import de.akquinet.android.androlog.Log;

/**
 * @author biafra
 * @date 5/10/12 12:40 PM
 */
public class AsyncRemoveTask extends AsyncTask<File, Integer, Boolean> {

  private Context           context;
  protected static String   TAG = "AsyncWipeTask";
  private SharedPreferences prefs;

  private static String     storageDir;

  public AsyncRemoveTask(Context context) {
    this.context = context;
    prefs = context.getSharedPreferences(AdminReceiver.class.getName(), 0);
    storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
  }

  @Override
  final protected Boolean doInBackground(File... params) {

    for (File file : params) {
      deleteDir(file);
    }

    return null;
  }

  final protected void onPostExecute(Boolean result) {

    final DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    if (prefs.getBoolean(WipeConfigActivity.PREF_WIPE_INTERNAL, false)) {
      Log.d(TAG, "wipe and reboot");
      mDPM.wipeData(0);
    }

  }

  // Deletes all files and subdirectories under dir.
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      if (children != null) {
        for (String child : children) {
          //          Log.d(TAG, "Deleting (recursively): " + dir.getName());
          deleteDir(new File(dir, child));
        }
      }
    }

    // The directory is now empty or a file so delete it
    if (!storageDir.equals(dir.getAbsolutePath())) {
      Log.d(TAG, "Deleting: " + dir.getName());
      return dir.delete();
    }
    return true;
  }
}
