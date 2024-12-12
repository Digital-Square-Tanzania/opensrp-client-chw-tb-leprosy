package org.smartregister.chw.tbleprosy_sample.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.tbleprosy.contract.TbLeprosyRegisterFragmentContract;
import org.smartregister.chw.tbleprosy.presenter.BaseTbLeprosyRegisterFragmentPresenter;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseTbLeprosyRegisterFragmentPresenterTest {
    @Mock
    protected TbLeprosyRegisterFragmentContract.View view;

    @Mock
    protected TbLeprosyRegisterFragmentContract.Model model;

    private BaseTbLeprosyRegisterFragmentPresenter baseTbLeprosyRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseTbLeprosyRegisterFragmentPresenter = new BaseTbLeprosyRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseTbLeprosyRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals(" ec_tbleprosy_enrollment.is_closed = 0 ", baseTbLeprosyRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(tbleprosy_test_date,7,4)|| '-' || SUBSTR(tbleprosy_test_date,4,2) || '-' || SUBSTR(tbleprosy_test_date,1,2),'')) as integer) between 7 and 14) ", baseTbLeprosyRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.TBLEPROSY_ENROLLMENT + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseTbLeprosyRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.TBLEPROSY_ENROLLMENT, baseTbLeprosyRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseTbLeprosyRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams(Constants.TABLES.TBLEPROSY_ENROLLMENT, null, null);
        Mockito.verify(view).initializeQueryParams(Constants.TABLES.TBLEPROSY_ENROLLMENT, null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}