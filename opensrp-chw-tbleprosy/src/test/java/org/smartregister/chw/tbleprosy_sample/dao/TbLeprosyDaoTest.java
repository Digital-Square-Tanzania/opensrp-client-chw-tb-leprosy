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

import java.util.Arrays;
import java.util.Collections;
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

    @Test
    public void testMergeObservationResultsTbSavedThenLeprosySaved() {
        ObservationResults latestLeprosyOnly = createObservation(
                "20240102010101",
                "",
                "",
                "",
                "",
                "skin_smear",
                "",
                "2024-01-02",
                "no_leprosy_detected"
        );
        ObservationResults olderTbOnly = createObservation(
                "20240101010101",
                "tb_investigation",
                "gene_xpert",
                "tb_dr_tb_undetected",
                "",
                "",
                "2024-01-01",
                "",
                ""
        );

        ObservationResults merged = mergeObservationResults(Arrays.asList(latestLeprosyOnly, olderTbOnly));

        Assert.assertNotNull(merged);
        Assert.assertEquals("20240102010101", merged.getLastInteractedWith());
        Assert.assertEquals("tb_investigation", merged.getInvestigationType());
        Assert.assertEquals("gene_xpert", merged.getTbDiagnosticTestType());
        Assert.assertEquals("tb_dr_tb_undetected", merged.getTbSampleTestResults());
        Assert.assertEquals("skin_smear", merged.getLeprosyDiagnosticMethod());
        Assert.assertEquals("2024-01-01", merged.getTbTreatmentStartDate());
        Assert.assertEquals("2024-01-02", merged.getLeprosyTreatmentStartDate());
        Assert.assertEquals("no_leprosy_detected", merged.getLeprosyInvestigationResults());
    }

    @Test
    public void testMergeObservationResultsLeprosySavedThenTbSaved() {
        ObservationResults latestTbOnly = createObservation(
                "20240102010101",
                "tb_investigation",
                "chest_xray",
                "poor_quality_sample",
                "non_suggestive",
                "",
                "2024-01-02",
                "",
                ""
        );
        ObservationResults olderLeprosyOnly = createObservation(
                "20240101010101",
                "",
                "",
                "",
                "",
                "skin_smear",
                "",
                "2024-01-01",
                "suspected_leprosy"
        );

        ObservationResults merged = mergeObservationResults(Arrays.asList(latestTbOnly, olderLeprosyOnly));

        Assert.assertNotNull(merged);
        Assert.assertEquals("20240102010101", merged.getLastInteractedWith());
        Assert.assertEquals("tb_investigation", merged.getInvestigationType());
        Assert.assertEquals("chest_xray", merged.getTbDiagnosticTestType());
        Assert.assertEquals("poor_quality_sample", merged.getTbSampleTestResults());
        Assert.assertEquals("non_suggestive", merged.getClinicalDecision());
        Assert.assertEquals("skin_smear", merged.getLeprosyDiagnosticMethod());
        Assert.assertEquals("2024-01-02", merged.getTbTreatmentStartDate());
        Assert.assertEquals("2024-01-01", merged.getLeprosyTreatmentStartDate());
        Assert.assertEquals("suspected_leprosy", merged.getLeprosyInvestigationResults());
        Assert.assertTrue(merged.isPoorQualitySample());
    }

    @Test
    public void testMergeObservationResultsSingleCombinedEntry() {
        ObservationResults combined = createObservation(
                "20240103010101",
                "tb_investigation",
                "gene_xpert",
                "tb_dr_tb_undetected",
                "non_suggestive",
                "skin_smear",
                "2024-01-03",
                "2024-01-03",
                "no_leprosy_detected"
        );

        ObservationResults merged = mergeObservationResults(Collections.singletonList(combined));

        Assert.assertNotNull(merged);
        Assert.assertEquals("20240103010101", merged.getLastInteractedWith());
        Assert.assertEquals("tb_dr_tb_undetected", merged.getTbSampleTestResults());
        Assert.assertEquals("no_leprosy_detected", merged.getLeprosyInvestigationResults());
    }

    private ObservationResults createObservation(String lastInteractedWith, String investigationType, String tbDiagnosticTestType,
                                                 String tbSampleTestResults, String clinicalDecision, String leprosyDiagnosticMethod,
                                                 String tbTreatmentStartDate, String leprosyTreatmentStartDate, String leprosyInvestigationResults) {
        return new ObservationResults(
                lastInteractedWith,
                investigationType,
                tbDiagnosticTestType,
                tbSampleTestResults,
                clinicalDecision,
                leprosyDiagnosticMethod,
                tbTreatmentStartDate,
                leprosyTreatmentStartDate,
                leprosyInvestigationResults,
                false
        );
    }

}
