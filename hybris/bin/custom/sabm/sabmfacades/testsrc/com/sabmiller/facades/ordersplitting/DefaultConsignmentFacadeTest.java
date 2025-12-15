package com.sabmiller.facades.ordersplitting;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Created by zhuo.a.jiang on 6/02/2018.
 */
public class DefaultConsignmentFacadeTest {

    @InjectMocks
    private DefaultConsignmentFacade DefaultConsignmentFacade;


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void updateConsignmentStatusFromRetriever() throws Exception {

    }

}