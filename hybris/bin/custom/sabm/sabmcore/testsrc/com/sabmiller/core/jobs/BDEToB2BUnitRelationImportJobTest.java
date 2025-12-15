package com.sabmiller.core.jobs;

import static junit.framework.TestCase.assertFalse;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.BDEToB2BUnitRelationImportCronJobModel;

/**
 * Created by zhuo.a.jiang on 17/9/18.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BDEToB2BUnitRelationImportJobTest {

    @InjectMocks
    private final BDEToB2BUnitRelationImportJob bdeToB2BUnitRelationImportJob = new  BDEToB2BUnitRelationImportJob();

    @Mock
    private BDEToB2BUnitRelationImportCronJobModel cronjob;

    @Mock
    private SabmB2BUnitService b2bUnitService;

    @Mock
    private SabmB2BCustomerService sabmB2BCustomerService;

    @Mock
    private SabmCronJobStatus sabmCronJobStatus;

    private static final char DEFAULT_SEPARATOR = '|';

    @Mock
    private SabmSFTPService sabmSFTPService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        /*bdeToB2BUnitRelationImportJob.setSabmSFTPService(sabmSFTPService);
        bdeToB2BUnitRelationImportJob.setB2bUnitService(b2bUnitService);*/
    }

    @Test
    public void testEmptyMedia() throws Exception {

		 Mockito.lenient().when(cronjob.getInputFile()).thenReturn(null);

        Mockito.when(cronjob.getFileName()).thenReturn("Dummy File Name");

        final PerformResult result  = bdeToB2BUnitRelationImportJob.perform(cronjob);

        BDDMockito.verify(sabmB2BCustomerService, times(0)).getBDECustomerImportedAll();
    }

    @Test
    public void testStoreContentsWithMissMatchingColumnsFromExcel(){



        // String is less than 7 columns separate by "|"
        final String currentLine = "20416164|FIVEWAYS HOTEL|AU04|Queensland|Dene Rauchle|RAUCHLED@gcn.ab-inbev.com|1172983";


        final List<String> splittedValues = Arrays.asList(StringUtils.split(currentLine, DEFAULT_SEPARATOR));

        final boolean result = bdeToB2BUnitRelationImportJob.storeContents(splittedValues,99);

        assertFalse(result);

    }

    @Test
    public void testStoreContentsIfB2bUnitDoesNotExist(){

		 Mockito.lenient().when(b2bUnitService.getUnitForUid("1172828")).thenReturn(null);

        final String currentLine = "20403576|LATROBE VALLEY HOTEL MORWELL.|AU03|Victoria|Closed VIC|VIC_TERR_CLOSED|VIC_TERR_CLOSED@gcn.ab-inbev.com|1172828";


        final List<String> splittedValues = Arrays.asList(StringUtils.split(currentLine, DEFAULT_SEPARATOR));

        final boolean result = bdeToB2BUnitRelationImportJob.storeContents(splittedValues,99);

        assertFalse(result);

    }




}