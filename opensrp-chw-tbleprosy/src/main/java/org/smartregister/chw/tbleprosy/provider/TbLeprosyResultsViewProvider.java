package org.smartregister.chw.tbleprosy.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

public class TbLeprosyResultsViewProvider implements RecyclerViewProvider<TbLeprosyResultsViewProvider.RegisterViewHolder> {

    private final LayoutInflater inflater;
    protected View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public TbLeprosyResultsViewProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paginationClickListener = paginationClickListener;
        this.onClickListener = onClickListener;
        this.visibleColumns = visibleColumns;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder registerViewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, registerViewHolder);
        }
    }


    @SuppressLint("SetTextI18n")
    private void populatePatientColumn(CommonPersonObjectClient pc, final RegisterViewHolder viewHolder) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String kitCode = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.KIT_CODE, false);
            String collectionDate = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.COLLECTION_DATE, false);
            String kitFor = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.KIT_FOR, false);
            String hivstResult = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.HIVST_RESULT, false);

            if (StringUtils.isBlank(hivstResult)) {
                viewHolder.hivstWrapper.setVisibility(View.GONE);
                viewHolder.recordHivstResult.setVisibility(View.VISIBLE);
            } else {
                int resVal = context.getResources().getIdentifier("hivst_result_" + hivstResult, "string", context.getPackageName());
                if (resVal != 0) {
                    viewHolder.hivstResult.setText(context.getString(resVal));
                } else {
                    viewHolder.hivstResult.setText(hivstResult);
                }

                if (hivstResult.equalsIgnoreCase("not_returned")) {
                    viewHolder.hivstWrapper.setVisibility(View.VISIBLE);
                    viewHolder.recordHivstResult.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.hivstWrapper.setVisibility(View.VISIBLE);
                    viewHolder.recordHivstResult.setVisibility(View.GONE);
                }
            }

            if (StringUtils.isBlank(kitCode)) {
                viewHolder.kitCodeWrapper.setVisibility(View.GONE);
            } else {
                viewHolder.kitCodeWrapper.setVisibility(View.VISIBLE);
            }

            viewHolder.kitCode.setText(kitCode);
            viewHolder.kitFor.setText(context.getString(context.getResources().getIdentifier("kit_for_" + kitFor, "string", context.getPackageName())));
            viewHolder.collectionDate.setText(collectionDate);
            viewHolder.recordHivstResult.setTag(pc);
//            viewHolder.recordHivstResult.setTag(R.id.VIEW_ID, BaseHivstResultViewFragment.CLICK_VIEW_NORMAL);
            viewHolder.recordHivstResult.setOnClickListener(onClickListener);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
//        implement
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.hivst_result_list_row, parent, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView kitCode;
        public TextView kitFor;
        public TextView hivstResult;
        public TextView collectionDate;
        public RelativeLayout hivstWrapper;
        public RelativeLayout kitCodeWrapper;
        public TextView resultTitle;

        public Button recordHivstResult;


        public RegisterViewHolder(View itemView) {
            super(itemView);

            kitCode = itemView.findViewById(R.id.kit_code);
            kitFor = itemView.findViewById(R.id.kit_for);
            collectionDate = itemView.findViewById(R.id.collection_date);
            hivstResult = itemView.findViewById(R.id.result);
            hivstWrapper = itemView.findViewById(R.id.rlRecordHivstWrapper);
            kitCodeWrapper = itemView.findViewById(R.id.rlKitCodeWrapper);
            recordHivstResult = itemView.findViewById(R.id.record_result_button);
            resultTitle = itemView.findViewById(R.id.hivst_result_label);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }
}
