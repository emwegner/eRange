package de.hsos.ma.erange;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {
    private static final String PREFS_BACKUP_KEY = "prefs_backup";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, "your_prefs_name");
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
