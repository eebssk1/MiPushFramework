package com.xiaomi.xmsf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityManagerCompat;
import androidx.core.content.ContextCompat;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.xiaomi.xmsf.push.notification.NotificationController;
import com.xiaomi.xmsf.utils.LogUtils;

import java.util.Date;

import top.trumeet.common.db.EventDb;
import top.trumeet.common.utils.Utils;


public class ManageSpaceActivity extends PreferenceActivity {

    private MyPreferenceFragment preferenceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        top.trumeet.mipush.provider.DatabaseUtils.init(this);
        preferenceFragment = new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x233) {
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            SwitchPreference iceboxSupported = (SwitchPreference) preferenceFragment
                    .getPreferenceScreen().findPreference("IceboxSupported");
            iceboxSupported.setChecked(granted);
            Toast.makeText(getApplicationContext(),
                    getString(granted ?
                            R.string.icebox_permission_granted :
                            R.string.icebox_permission_not_granted),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        public MyPreferenceFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fragmented_preferences);

            Context context = getActivity();

            //Too bad in ui thread

            //TODO: Three messages seem to be too much, and need separate strings for toast.
            getPreferenceScreen().findPreference("clear_history").setOnPreferenceClickListener(preference -> {
                Toast.makeText(context, getString(R.string.settings_clear_history) + getString(R.string.start), Toast.LENGTH_SHORT).show();
                EventDb.deleteHistory(context, null);
                Toast.makeText(context, getString(R.string.settings_clear_history) + getString(R.string.end), Toast.LENGTH_SHORT).show();
                return true;
            });

            getPreferenceScreen().findPreference("clear_log").setOnPreferenceClickListener(preference -> {
                Toast.makeText(context, getString(R.string.settings_clear_log) + getString(R.string.start), Toast.LENGTH_SHORT).show();
                LogUtils.clearLog(context);
                Toast.makeText(context, getString(R.string.settings_clear_log) + getString(R.string.end), Toast.LENGTH_SHORT).show();
                return true;
            });
            getPreferenceScreen().findPreference("clear_all").setOnPreferenceClickListener(preference -> {
                boolean res = ((ActivityManager)context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                if(res) ((Activity)(context)).finishAndRemoveTask();
                return true;
            });

            getPreferenceScreen().findPreference("mock_notification").setOnPreferenceClickListener(preference -> {
                String packageName = BuildConfig.APPLICATION_ID;
                Date date = new Date();
                String title = context.getString(R.string.debug_test_title);
                String description = context.getString(R.string.debug_test_content) + date.toString();
                NotificationController.test(context, packageName, title, description);
                return true;
            });

            SwitchPreference iceboxSupported = (SwitchPreference) getPreferenceScreen().findPreference("IceboxSupported");
            if (!Utils.isAppInstalled(IceBox.PACKAGE_NAME)) {
                iceboxSupported.setEnabled(false);
                iceboxSupported.setTitle(R.string.settings_icebox_not_installed);
            } else {
                if (!iceBoxPermissionGranted()) {
                    iceboxSupported.setChecked(false);
                }
                iceboxSupported.setOnPreferenceChangeListener((preference, newValue) -> {
                    Boolean value = (Boolean) newValue;
                    if (value && !iceBoxPermissionGranted()) {
                        requestIceBoxPermission();
                    }
                    return true;
                });

            }


        }

        private void requestIceBoxPermission() {
            ActivityCompat.requestPermissions(getActivity(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
        }

        private boolean iceBoxPermissionGranted() {
            return ContextCompat.checkSelfPermission(getActivity(), IceBox.SDK_PERMISSION) == PackageManager.PERMISSION_GRANTED;
        }
    }


}
