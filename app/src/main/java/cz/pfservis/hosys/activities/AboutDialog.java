package cz.pfservis.hosys.activities;

/**
 * Created by petr on 3.11.16.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import hosys.pfservis.cz.hosys.R;


public class AboutDialog extends Dialog {
    public AboutDialog(Context context) {
        super(context);
    }

    /**
     * Standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        String appInfoText = getContext().getString(R.string.html_app_info);
        String version = null;

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);

            version = pInfo.versionName + " (" + pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            version = "1.x";
            Log.e("ABOUT_DIALOG", "Nepodařilo se načíst číslo verze", e);
        }

        TextView tvLic = (TextView) findViewById(R.id.legal_text);

        tvLic.setText(getContext().getString(R.string.text_licence));
        tvLic.setMovementMethod(new ScrollingMovementMethod());

        TextView tvAppInfo = (TextView) findViewById(R.id.info_text);
        tvAppInfo.setText(Html.fromHtml(appInfoText.replace("${VERSION}", version)));
        tvAppInfo.setLinkTextColor(Color.BLACK);

        Linkify.addLinks(tvAppInfo, Linkify.ALL);
    }
}
