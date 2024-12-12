package org.smartregister.chw.tbleprosy.provider;

import static org.smartregister.util.Utils.getName;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.tbleprosy.fragment.BaseTbLeprosyRegisterFragment;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.chw.tbleprosy.util.TbLeprosyUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;
import org.smartregister.chw.tbleprosy.R;

import java.text.MessageFormat;
import java.util.Set;

import timber.log.Timber;

public class TbLeprosyRegisterProvider implements RecyclerViewProvider<TbLeprosyRegisterProvider.RegisterViewHolder> {

    private final LayoutInflater inflater;

    private View.OnClickListener paginationClickListener;
    protected View.OnClickListener onClickListener;
    private Context context;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public TbLeprosyRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {

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

    private String updateMemberGender(CommonPersonObjectClient commonPersonObjectClient) {
        if ("0".equals(Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.IS_ANC_CLOSED, false))) {
            return context.getResources().getString(R.string.anc_string);
        } else if ("0".equals(Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.IS_PNC_CLOSED, false))) {
            return context.getResources().getString(R.string.pnc_string);
        } else {
            String gender = Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.GENDER, true);
            return TbLeprosyUtil.getGenderTranslated(context, gender);
        }
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, final RegisterViewHolder viewHolder) {
        try {

            String firstName = getName(
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true));

            String dobString = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = new Period(new DateTime(dobString), new DateTime()).getYears();

            String patientName = getName(firstName, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true));
            viewHolder.patientName.setText(patientName + ", " + age);
            viewHolder.textViewGender.setText(updateMemberGender(pc));
            viewHolder.textViewVillage.setText(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, true));
            viewHolder.patientColumn.setOnClickListener(onClickListener);
            viewHolder.patientColumn.setTag(pc);
            viewHolder.patientColumn.setTag(R.id.VIEW_ID, BaseTbLeprosyRegisterFragment.CLICK_VIEW_NORMAL);

            viewHolder.dueButton.setOnClickListener(onClickListener);
            viewHolder.dueButton.setTag(pc);
            viewHolder.dueButton.setTag(R.id.VIEW_ID, BaseTbLeprosyRegisterFragment.FOLLOW_UP_VISIT);
            viewHolder.registerColumns.setOnClickListener(onClickListener);

            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());
            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.dueButton.performClick());

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
        View view = inflater.inflate(R.layout.tbleprosy_register_list_row, parent, false);
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
        public TextView patientName;
        public TextView parentName;
        public TextView textViewVillage;
        public TextView textViewGender;
        public Button dueButton;
        public View patientColumn;

        public View registerColumns;
        public View dueWrapper;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            parentName = itemView.findViewById(R.id.patient_parent_name);
            patientName = itemView.findViewById(R.id.patient_name_age);
            textViewVillage = itemView.findViewById(R.id.text_view_village);
            textViewGender = itemView.findViewById(R.id.text_view_gender);
            dueButton = itemView.findViewById(R.id.due_button);
            patientColumn = itemView.findViewById(R.id.patient_column);
            registerColumns = itemView.findViewById(R.id.register_columns);
            dueWrapper = itemView.findViewById(R.id.due_button_wrapper);
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
