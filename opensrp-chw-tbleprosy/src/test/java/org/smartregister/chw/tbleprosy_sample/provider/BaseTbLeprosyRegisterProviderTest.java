package org.smartregister.chw.tbleprosy_sample.provider;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.tbleprosy.provider.TbLeprosyRegisterProvider;
import org.smartregister.chw.tbleprosy.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.chw.tbleprosy.R;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.validateMockitoUsage;

public class BaseTbLeprosyRegisterProviderTest {
    @Mock
    public CommonPersonObjectClient commonPersonObjectClient;
    @Mock
    public View.OnClickListener listener;
    @Mock
    public TbLeprosyRegisterProvider.RegisterViewHolder viewHolder;
    @Mock
    private TbLeprosyRegisterProvider testRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void updateClients() {
        Assert.assertNull(testRegisterProvider.updateClients(null, null, null, null));
    }

    @Test
    public void newFormLauncher() {
        Assert.assertNull(testRegisterProvider.newFormLauncher(null, null, null));
    }

    @Test
    public void checkInflater() {
        Assert.assertNull(testRegisterProvider.inflater());
    }

    @Test
    public void checkFooter() {
        Assert.assertNotNull(testRegisterProvider.isFooterViewHolder(null));
    }

    @Test
    public void checkFooterCreation() {
        Assert.assertNull(testRegisterProvider.createFooterHolder(null));
    }

    @Test
    public void checkHolderCreation() {
        Assert.assertNull(testRegisterProvider.createViewHolder(null));
    }

    @Test
    public void isAncClosed() throws Exception {
        Resources resources = Mockito.mock(Resources.class);
        Activity activity = Mockito.mock(Activity.class);
        TbLeprosyRegisterProvider provider = Mockito.spy(new TbLeprosyRegisterProvider(activity, listener, listener, null));
        Map<String, String> map = new HashMap<>();
        map.put("is_anc_closed", "0");
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.anc_string), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }

    @Test
    public void isPncClosed() throws Exception {
        Resources resources = Mockito.mock(Resources.class);
        Activity activity = Mockito.mock(Activity.class);
        TbLeprosyRegisterProvider provider = Mockito.spy(new TbLeprosyRegisterProvider(activity, listener, listener, null));
        Map<String, String> map = new HashMap<>();
        map.put("is_pnc_closed", "0");
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.pnc_string), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }

    @Test
    public void updateMemberGender() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources = Mockito.mock(Resources.class);
        TbLeprosyRegisterProvider provider = new TbLeprosyRegisterProvider(activity, listener, listener, null);
        Map<String, String> map = new HashMap<>();
        map.put(DBConstants.KEY.GENDER, "Male");

        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.male), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }


    @Test(expected = Exception.class)
    public void getView() throws Exception {
        testRegisterProvider.getView(null, null, viewHolder);
        PowerMockito.when(testRegisterProvider, "populatePatientColumn", commonPersonObjectClient, viewHolder).then(DoesNothing.doesNothing());
        PowerMockito.verifyPrivate(testRegisterProvider).invoke("populatePatientColumn", commonPersonObjectClient, viewHolder);
    }

}
