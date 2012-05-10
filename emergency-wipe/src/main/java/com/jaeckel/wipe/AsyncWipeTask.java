package com.jaeckel.wipe;

import java.io.File;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author biafra
 * @date 5/10/12 12:40 PM
 */
public class AsyncWipeTask extends AsyncTask<File, Integer, Boolean> {

  private Context         context;
  protected static String TAG = "AsyncWipeTask";

  public AsyncWipeTask(Context context) {
    this.context = context;
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

    mDPM.wipeData(0);

  }

  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns false.
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
//    Log.d(TAG, "Deleting: " + dir.getName());
    return dir.delete();
  }
}
