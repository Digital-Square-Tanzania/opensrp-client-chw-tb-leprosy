package org.smartregister.chw.tbleprosy_sample.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.tbleprosy.model.BaseTbLeprosyRegisterFragmentModel;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.util.HashSet;
import java.util.Set;

public class BaseTbLeprosyRegisterFragmentModelTest {

    @Mock
    private BaseTbLeprosyRegisterFragmentModel baseTestRegisterFragmentModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDefaultRegisterConfiguration() {
        RegisterConfiguration configuration = new RegisterConfiguration();
        Mockito.when(baseTestRegisterFragmentModel.defaultRegisterConfiguration())
                .thenReturn(configuration);
        Assert.assertEquals(configuration, baseTestRegisterFragmentModel.defaultRegisterConfiguration());
    }

    @Test
    public void testGetViewConfiguration() {
        ViewConfiguration viewConfiguration = new ViewConfiguration();
        Mockito.when(baseTestRegisterFragmentModel.getViewConfiguration(Mockito.anyString()))
                .thenReturn(viewConfiguration);
        Assert.assertEquals(viewConfiguration, baseTestRegisterFragmentModel.getViewConfiguration(Mockito.anyString()));
    }

    @Test
    public void testGetRegisterActiveColumns() {
        Set<View> views = new HashSet<View>();
        Mockito.when(baseTestRegisterFragmentModel.getRegisterActiveColumns(Mockito.anyString()))
                .thenReturn(views);
        Assert.assertEquals(views, baseTestRegisterFragmentModel.getRegisterActiveColumns(Mockito.anyString()));
    }

    @Test
    public void testCountSelect() {
        String countString = "0";
        Mockito.when(baseTestRegisterFragmentModel.countSelect(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(countString);
        Assert.assertEquals(countString, baseTestRegisterFragmentModel.countSelect(Mockito.anyString(), Mockito.anyString()));
    }

    @Test
    public void testMainSelect() {
        String countString = "mainSelect";
        Mockito.when(baseTestRegisterFragmentModel.mainSelect(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(countString);
        Assert.assertEquals(countString, baseTestRegisterFragmentModel.mainSelect(Mockito.anyString(), Mockito.anyString()));
    }

}
