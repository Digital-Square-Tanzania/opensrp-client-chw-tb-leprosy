package org.smartregister.chw.tbleprosy.fragment;

import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.adapter.TbLeprosyMobilizationRegisterAdapter;
import org.smartregister.chw.tbleprosy.dao.TbLeprosyMobilizationDao;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyRegisterFragmentModel;
import org.smartregister.chw.tbleprosy.model.TbLeprosyMobilizationModel;
import org.smartregister.chw.tbleprosy.presenter.TbLeprosyMobilizationRegisterFragmentPresenter;
import org.smartregister.chw.tbleprosy.provider.TbLeprosyMobilizationRegisterProvider;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class BaseTbLeprosyMobilizationRegisterFragment extends BaseTbLeprosyRegisterFragment {

    protected Toolbar toolbar;
    protected LinearLayout emptyViewLayout;
    private TbLeprosyMobilizationRegisterAdapter adapter;

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        TbLeprosyMobilizationRegisterProvider mobilizationRegisterProvider = new TbLeprosyMobilizationRegisterProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, mobilizationRegisterProvider, null);
        clientAdapter.setTotalcount(0);
        clientAdapter.setCurrentlimit(20);
        setUpAdapter();

    }

    protected void setUpAdapter() {
        List<TbLeprosyMobilizationModel> tbLeprosyMobilizationModels = TbLeprosyMobilizationDao.getMobilizationSessions();
        if (tbLeprosyMobilizationModels != null && !tbLeprosyMobilizationModels.isEmpty()) {
            adapter = new TbLeprosyMobilizationRegisterAdapter(tbLeprosyMobilizationModels, requireActivity());
            clientsView.setAdapter(adapter);
            showEmptyState();
        }
    }

    protected void showEmptyState() {
        if(emptyViewLayout != null){
            if (adapter.getItemCount() >= 1 ) {
                emptyViewLayout.setVisibility(android.view.View.GONE);
            } else {
                emptyViewLayout.setVisibility(android.view.View.VISIBLE);
            }
        }
    }

    @Override
    public void setupViews(android.view.View view) {
        initializePresenter();
        super.setupViews(view);

        emptyViewLayout = view.findViewById(R.id.empty_view_ll);
        toolbar = view.findViewById(R.id.register_toolbar);

        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);


        android.view.View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(getString(R.string.tbleprosy_sessions));
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        android.view.View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setVisibility(android.view.View.GONE);

        android.view.View topLeftLayout = view.findViewById(R.id.top_left_layout);
        topLeftLayout.setVisibility(android.view.View.GONE);

        android.view.View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(android.view.View.VISIBLE);

        android.view.View sortFilterBarLayout = view.findViewById(R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(android.view.View.GONE);

        android.view.View filterSortLayout = view.findViewById(R.id.filter_sort_layout);
        filterSortLayout.setVisibility(android.view.View.GONE);

        android.view.View dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(android.view.View.GONE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);
        if (getSearchView() != null) {
            getSearchView().setVisibility(android.view.View.GONE);
        }
    }


    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new TbLeprosyMobilizationRegisterFragmentPresenter(this, new BaseTbLeprosyRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (clientsView.getAdapter() != null) {
            clientsView.getAdapter().notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        new android.os.Handler().postDelayed(this::setUpAdapter, 1000);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mobilization_register;
    }

    @Override
    public void countExecute() {
        Cursor c = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() + " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }


            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));
            Timber.v("total count here %s", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    @Override
    protected void openProfile(String baseEntityId) {
        //implement when needed
    }


    @Override
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(android.view.View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(android.view.View.VISIBLE);
            syncButton.setPadding(0, 0, 10, 0);
            syncButton.setImageDrawable(context().getDrawable(R.drawable.ic_add_24));
            syncButton.setOnClickListener(view -> {
                JSONObject form;
                try {
                    form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(requireActivity(), Constants.FORMS.TBLEPROSY_MOBILIZATION_SESSION);
                    if (form != null) {
                        String randomId = generateRandomUUIDString();
                        form.put(ENTITY_ID, randomId);
                        startForm(form);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                }
            });
        }
    }

    protected void startForm(JSONObject form) {
        //Start Form in APP
    }
}
