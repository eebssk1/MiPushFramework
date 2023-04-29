package top.trumeet.mipushframework.wizard;

import android.app.AppOpsManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.setupwizardlib.view.NavigationBar;
import com.xiaomi.xmsf.R;

import top.trumeet.mipushframework.utils.PermissionUtils;

/**
 * Created by Trumeet on 2017/8/25.
 *
 * @author Trumeet
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class AlertWindowPermissionActivity extends PushControllerWizardActivity implements NavigationBar.NavigationBarListener {
    private boolean allow;
    private boolean nextClicked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check();
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || allow) {
            nextPage();
            finish();
            return;
        }
        connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        check();
        if (allow) {
            nextPage();
            finish();
        } else if (nextClicked) {
            layout.getNavigationBar()
                    .getNextButton()
                    .setText(R.string.retry);
        }
    }

    private void check() {
        allow = Settings.canDrawOverlays(this);
    }

    @Override
    public void onConnected(Bundle savedInstanceState) {
        super.onConnected(savedInstanceState);
        check();

        if (allow) {
            nextPage();
            finish();
            return;
        }
        layout.getNavigationBar()
                .setNavigationBarListener(this);
        mText.setText(Html.fromHtml(getString(R.string.wizard_title_alert_window_text)));
        layout.setHeaderText(Html.fromHtml(getString(R.string.wizard_title_alert_window_permission)));
        setContentView(layout);
    }

    @Override
    public void onNavigateBack() {
        onBackPressed();
    }

    @Override
    public void onNavigateNext() {
        if (!allow) {
            if (!nextClicked || !PermissionUtils.canAppOpsPermission()) {
                nextClicked = true;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                PermissionUtils.lunchAppOps(this,
                        AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        getString(R.string.wizard_title_alert_window_text));
            }
        } else {
            nextPage();
        }
    }

    private void nextPage() {
        startActivity(new Intent(this,
                FinishWizardActivity.class));
    }

}
