package org.smartregister.chw.tbleprosy_sample.interactor;

import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.interactor.BaseTbLeprosyServiceVisitInteractor;
import org.smartregister.chw.tbleprosy_sample.activity.EntryActivity;


public class TbLeprosyServiceVisitInteractor extends BaseTbLeprosyServiceVisitInteractor {
    public TbLeprosyServiceVisitInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
