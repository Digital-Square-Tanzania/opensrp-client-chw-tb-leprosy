package org.smartregister.chw.tbleprosy_sample.presenter;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.tbleprosy.contract.TbLeprosyRegisterContract;
import org.smartregister.chw.tbleprosy.presenter.BaseTbLeprosyRegisterPresenter;

@PrepareForTest(BaseTbLeprosyRegisterPresenter.class)
public class BaseTbLeprosyRegisterPresenterTest {
    @Mock
    protected BaseTbLeprosyRegisterPresenter baseTbLeprosyRegisterPresenter;

    @Mock
    protected TbLeprosyRegisterContract.Interactor interactor;
    @Mock
    protected TbLeprosyRegisterContract.Model model;
    @Mock
    protected TbLeprosyRegisterContract.View baseView;
    private BaseTbLeprosyRegisterPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new BaseTbLeprosyRegisterPresenter(baseView, model, interactor);
    }

    @Test
    public void startFormWhenEntityIdIsNull() throws Exception {
        presenter.startForm("formName", "", "121212121212", "231231231231");
        Mockito.verify(baseView, Mockito.never()).startFormActivity(null);
    }

    @Test
    public void startFormWhenEntityIdIsNotNull() throws Exception {
        JSONObject form = model.getFormAsJson("formName", "12131212", "231231231231");
        Mockito.when(model.getFormAsJson("formName", "12131212", "231231231231")).thenReturn(form);
        Mockito.doNothing().when(baseView).startFormActivity(form);
        presenter.startForm("formName", "12131212", "121212121212", "231231231231");
        Mockito.verify(baseView).startFormActivity(form);
    }

    @Test
    public void saveForm() {
        presenter.saveForm("{}");
        Mockito.verify(interactor).saveRegistration("{}", presenter);
    }

    @Test
    public void getViewWhenViewIsNull() throws Exception {
        Assert.assertNull(Whitebox.invokeMethod(baseTbLeprosyRegisterPresenter, "getView"));
    }

    @Test
    public void onRegistrationSaved() {
        presenter.onRegistrationSaved();
        Mockito.verify(baseView).hideProgressDialog();
    }

}
