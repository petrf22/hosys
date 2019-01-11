package cz.pfservis.hosys.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.pfservis.hosys.HosysDownloader;
import cz.pfservis.hosys.HosysHtmlProcesor;
import cz.pfservis.hosys.HosysHtmlText;
import cz.pfservis.hosys.dto.SoutezDto;
import cz.pfservis.hosys.enums.HosysPage;
import hosys.pfservis.cz.hosys.R;

/**
 * Created by petr on 13.10.16.
 */
public class SoutezPreference extends DialogPreference implements HosysHtmlText {
    private static final String TAG = "SoutezPreference";

    private Spinner spinnerUroven1;
    private Spinner spinnerUroven2;
    private Spinner spinnerUroven3;
    private ProgressDialog progressDialog;
    private Map<String, Map<String, Map<String, String>>> mapUroven1 = new LinkedHashMap<>();
    private String soutezValue = null;
    private String soutezLabel = null;
    private InitSoutezSpinnerSelect initSoutezSpinnerSelect = InitSoutezSpinnerSelect.EMPTY;
    private final String soutezValueKey;
    private final String soutezLabelKey;

    public SoutezPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        soutezValueKey = context.getString(R.string.pref_soutez_value);
        soutezLabelKey = context.getString(R.string.pref_soutez_label);

        setDialogLayoutResource(R.layout.preferencies_soutez);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        progressDialog = ProgressDialog.show(getContext(),
                getContext().getString(R.string.progress_dialog_downloading_data_title),
                getContext().getString(R.string.progress_dialog_downloading_data_info));
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        spinnerUroven1 = (Spinner) view.findViewById(R.id.spinnerUroven1);
        spinnerUroven2 = (Spinner) view.findViewById(R.id.spinnerUroven2);
        spinnerUroven3 = (Spinner) view.findViewById(R.id.spinnerUroven3);

        ChangeSpinner changeSpinner = new ChangeSpinner();

        spinnerUroven1.setOnItemSelectedListener(changeSpinner);
        spinnerUroven2.setOnItemSelectedListener(changeSpinner);
        spinnerUroven3.setOnItemSelectedListener(changeSpinner);

        HosysDownloader hd = new HosysDownloader(this, "", getContext());
        hd.execute(HosysPage.soutezNastaveni);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        soutezValue = getSharedPreferences().getString(soutezValueKey, "");
        soutezLabel = getSharedPreferences().getString(soutezLabelKey, "");
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return "";
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            soutezValue = getPersistedString("");
        } else {
            soutezValue = (String) defaultValue;
        }
    }

    void persistSoutezValueLabel() {
        if (shouldPersist()) {
            // Shouldn't store null
            if (TextUtils.equals(soutezValue, getPersistedString(null))) {
                // It's already there, so the same as persisting
                return;
            }

            SharedPreferences.Editor editor = getEditor();
            editor.putString(soutezValueKey, soutezValue);
            editor.putString(soutezLabelKey, soutezLabel);
            editor.commit();

            callChangeListener(soutezValue);
        }

        notifyDependencyChange(false);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (callChangeListener(soutezValue)) {
                persistSoutezValueLabel();
            }
        }

        super.onDialogClosed(positiveResult);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        SavedSoutezState myState = new SavedSoutezState(superState);

        myState.soutezValue = soutezValue;
        myState.soutezLabel = soutezLabel;

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedSoutezState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedSoutezState myState = (SavedSoutezState) state;

        super.onRestoreInstanceState(myState.getSuperState());

        //persistSoutezValueLabel(myState.soutezValue, myState.soutezLabel);
    }

    @Override
    public void processHtmlText(HosysHtmlProcesor hosysHtmlProcesor) {
        SoutezDto[] souteze = hosysHtmlProcesor.getJson();
        Log.d(TAG, "souteze.length: " + souteze.length);

        try {
            Map<String, Map<String, String>> mapUroven2 = null;
            Map<String, String> mapUroven3 = null;
            InitSoutezSpinnerSelect initSoutezSpinnerSelectTmp = new InitSoutezSpinnerSelect();
            String uroven1Last = null;
            String uroven2Last = null;

            for (SoutezDto soutez : souteze) {
                Log.d(TAG, soutez.toString());
                if (soutez.getUroven() == 1) {
                    mapUroven3 = new LinkedHashMap<>();
                    mapUroven2 = new LinkedHashMap<>();
                    mapUroven1.put(soutez.getNazev(), mapUroven2);
                    uroven1Last = soutez.getNazev();
                } else if (soutez.getUroven() == 2) {
                    mapUroven3 = new LinkedHashMap<>();
                    mapUroven2.put(soutez.getNazev(), mapUroven3);
                    uroven2Last = soutez.getNazev();
                } else if (soutez.getUroven() == 3) {
                    mapUroven3.put(soutez.getNazev(), soutez.getHosysSoutezId());

                    if (StringUtils.isBlank(initSoutezSpinnerSelectTmp.uroven3) && StringUtils.equals(soutezValue, soutez.getHosysSoutezId())) {
                        // jen pokud je hodnota z nastavení
                        initSoutezSpinnerSelectTmp.uroven1 = uroven1Last;
                        initSoutezSpinnerSelectTmp.uroven2 = uroven2Last;
                        initSoutezSpinnerSelectTmp.uroven3 = soutez.getNazev();
                    }
                } else {
                    Log.e(TAG, "Změnily se stránky WWW hosys.cz.");
                }
            }

            initSoutezSpinnerSelect = StringUtils.isNotBlank(initSoutezSpinnerSelectTmp.uroven3) ?
                    initSoutezSpinnerSelectTmp : InitSoutezSpinnerSelect.EMPTY;

            String[] values = mapUroven1.keySet().toArray(new String[mapUroven1.keySet().size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, values);

            adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

            spinnerUroven1.setAdapter(adapter);

            if (!initSoutezSpinnerSelect.isInit()) {
                int pos = adapter.getPosition(initSoutezSpinnerSelect.uroven1);

                spinnerUroven1.setSelection(pos);
            }

            Log.d(TAG, "mapUroven1: " + mapUroven1);
            // Log.v(TAG, "HTML page: " + htmlPage);

            //Toast.makeText(getContext(), htmlPage, Toast.LENGTH_LONG).show();
        } finally {
            progressDialog.dismiss();
        }
    }

    private static class SavedSoutezState extends BaseSavedState {
        private String soutezValue;
        private String soutezLabel;

        public SavedSoutezState(Parcel source) {
            super(source);

            soutezValue = source.readString();
            soutezLabel = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeString(soutezValue);
            dest.writeString(soutezLabel);
        }

        public SavedSoutezState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedSoutezState> CREATOR = new Parcelable.Creator<SavedSoutezState>() {
            public SavedSoutezState createFromParcel(Parcel in) {
                return new SavedSoutezState(in);
            }

            public SavedSoutezState[] newArray(int size) {
                return new SavedSoutezState[size];
            }
        };
    }

    private class ChangeSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            String selected1 = spinnerUroven1.getSelectedItem().toString();
            String selected2 = ObjectUtils.toString(spinnerUroven2.getSelectedItem());
            String selected3 = ObjectUtils.toString(spinnerUroven3.getSelectedItem());
            Map<String, Map<String, String>> mapUroven2 = mapUroven1.get(selected1);
            Map<String, String> mapUroven3 = selected2 != null ? mapUroven2.get(selected2) : null;

            int spinnerId = parentView.getId();

            if (spinnerId == R.id.spinnerUroven1) {
                if (mapUroven2 == null) {
                    Log.e(TAG, "Nepodařilo se načíst data pro úroveň 2.");
                    return;
                }

                String[] values = mapUroven2.keySet().toArray(new String[mapUroven2.keySet().size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, values);

                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spinnerUroven2.setAdapter(adapter);

                if (!initSoutezSpinnerSelect.isInit()) {
                    int pos = adapter.getPosition(initSoutezSpinnerSelect.uroven2);

                    spinnerUroven2.setSelection(pos);
                }
            } else if (spinnerId == R.id.spinnerUroven2) {
                if (mapUroven3 == null) {
                    Log.e(TAG, "Nepodařilo se načíst data pro úroveň 3.");
                    return;
                }

                String[] values = mapUroven3.keySet().toArray(new String[mapUroven3.keySet().size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, values);

                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spinnerUroven3.setAdapter(adapter);

                if (!initSoutezSpinnerSelect.isInit()) {
                    initSoutezSpinnerSelect.setInit();

                    int pos = adapter.getPosition(initSoutezSpinnerSelect.uroven3);

                    spinnerUroven3.setSelection(pos);
                }
            } else if (spinnerId == R.id.spinnerUroven3) {
                if (mapUroven3 == null) {
                    Log.e(TAG, "Nepodařilo se načíst data pro úroveň 3.");
                    return;
                }

                soutezValue = mapUroven3.get(selected3);
                soutezLabel = selected1 + ": " + selected3;

                //persistSoutezValueLabel(soutezValue, soutezLabel);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    }

    private static class InitSoutezSpinnerSelect {
        private static final InitSoutezSpinnerSelect EMPTY = new InitSoutezSpinnerSelect(true);
        String uroven1;
        String uroven2;
        String uroven3;
        boolean init = false;

        private InitSoutezSpinnerSelect() {
        }

        private InitSoutezSpinnerSelect(boolean init) {
            this.init = init;
        }


        boolean isInit() {
            return init;
        }

        void setInit() {
            init = true;
        }
    }
}
