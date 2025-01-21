package org.smartregister.chw.tbleprosy.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

import timber.log.Timber;

public class TbLeprosyMobilizationRegisterProvider extends TbLeprosyResultsViewProvider {

    protected Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public TbLeprosyMobilizationRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {
        super(context, paginationClickListener, onClickListener, visibleColumns);
        this.visibleColumns = visibleColumns;
    }

    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder registerViewHolder) {
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(registerViewHolder);
        }
    }


    private void populatePatientColumn(final RegisterViewHolder viewHolder) {
        try {
            viewHolder.kitCodeWrapper.setVisibility(View.GONE);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
