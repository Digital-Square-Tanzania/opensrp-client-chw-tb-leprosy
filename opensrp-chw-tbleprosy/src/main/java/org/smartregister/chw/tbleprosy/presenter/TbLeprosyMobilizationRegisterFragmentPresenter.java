package org.smartregister.chw.tbleprosy.presenter;

import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy.contract.TbLeprosyRegisterFragmentContract;

public class TbLeprosyMobilizationRegisterFragmentPresenter extends BaseTbLeprosyRegisterFragmentPresenter {

    public TbLeprosyMobilizationRegisterFragmentPresenter(TbLeprosyRegisterFragmentContract.View view, TbLeprosyRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.TBLEPROSY_MOBILIZATION;
    }

    @Override
    public String getMainCondition() {
        return " is_closed is 0 ";
    }

}
