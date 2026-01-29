package org.smartregister.chw.tbleprosy_sample.dao;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.tbleprosy.dao.TbLeprosyDao;
import org.smartregister.repository.Repository;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class TbLeprosyDaoTest extends TbLeprosyDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testIsRegisteredForTbLeprosy() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        boolean registered = TbLeprosyDao.isRegisteredForTbLeprosy("12345");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT count(p.base_entity_id) count FROM ec_tbleprosy_screening p WHERE p.base_entity_id = '12345' AND p.is_closed = 0"), Mockito.any());
        Assert.assertFalse(registered);
    }

    @Test
    public void testGetTbLeprosyTestDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        Date testDate = TbLeprosyDao.getTbLeprosyTestDate("34233");
        Mockito.verify(database).rawQuery(Mockito.contains("select tbleprosy_test_date from ec_tbleprosy_screening where base_entity_id = '34233'"), Mockito.any());
    }


    @Test
    public void testGetVisitNumber() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        int result = TbLeprosyDao.getVisitNumber("89012");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT visit_number  FROM ec_tbleprosy_follow_up_visit WHERE entity_id='89012' ORDER BY visit_number DESC LIMIT 1"), Mockito.any());
        Assert.assertEquals(0, result);
    }

}
