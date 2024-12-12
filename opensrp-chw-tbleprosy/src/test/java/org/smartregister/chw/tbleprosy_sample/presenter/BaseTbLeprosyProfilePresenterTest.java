package org.smartregister.chw.tbleprosy_sample.presenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.chw.tbleprosy.contract.TbLeprosyProfileContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.presenter.BaseTbLeprosyProfilePresenter;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BaseTbLeprosyProfilePresenterTest {

    @Mock
    private TbLeprosyProfileContract.View view = Mockito.mock(TbLeprosyProfileContract.View.class);

    @Mock
    private TbLeprosyProfileContract.Interactor interactor = Mockito.mock(TbLeprosyProfileContract.Interactor.class);

    @Mock
    private MemberObject tbleprosyMemberObject = new MemberObject();

    private BaseTbLeprosyProfilePresenter profilePresenter = new BaseTbLeprosyProfilePresenter(view, interactor, tbleprosyMemberObject);


    @Test
    public void fillProfileDataCallsSetProfileViewWithDataWhenPassedMemberObject() {
        profilePresenter.fillProfileData(tbleprosyMemberObject);
        verify(view).setProfileViewWithData();
    }

    @Test
    public void fillProfileDataDoesntCallsSetProfileViewWithDataIfMemberObjectEmpty() {
        profilePresenter.fillProfileData(null);
        verify(view, never()).setProfileViewWithData();
    }

    @Test
    public void malariaTestDatePeriodIsLessThanSeven() {
        profilePresenter.recordTbLeprosyButton("");
        verify(view).hideView();
    }

    @Test
    public void malariaTestDatePeriodIsMoreThanFourteen() {
        profilePresenter.recordTbLeprosyButton("EXPIRED");
        verify(view).hideView();
    }

    @Test
    public void refreshProfileBottom() {
        profilePresenter.refreshProfileBottom();
        verify(interactor).refreshProfileInfo(tbleprosyMemberObject, profilePresenter.getView());
    }

    @Test
    public void saveForm() {
        profilePresenter.saveForm(null);
        verify(interactor).saveRegistration(null, view);
    }
}
