package com.sabmiller.facades.apb;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.customer.dao.impl.AsahiCustomerAccountDaoImpl;
import com.sabmiller.core.enums.AsahiEnquirySubType;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.model.EnquiryTypeContactMappingModel;


@UnitTest
public class AsahiCustomerAccountDaoImplTest {

    @InjectMocks
    private final AsahiCustomerAccountDaoImpl asahiCustomerAccountDaoImpl = new AsahiCustomerAccountDaoImpl();

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);


        final EnumerationService enumerationService = Mockito.mock(EnumerationService.class);
        asahiCustomerAccountDaoImpl.setEnumerationService(enumerationService);
        final EnquiryTypeContactMappingModel contact = Mockito.mock(EnquiryTypeContactMappingModel.class);
        final List<Object> resList = new ArrayList<Object>();
        resList.add(contact);

        final SearchResult<Object> res = new SearchResultImpl<Object>(resList, resList.size(), 0, 0);

        given(flexibleSearchService.search(any(FlexibleSearchQuery.class))).willReturn(res);
    }

    @Test
    public void getContactByEnquiryTypeTest()
    {

        final EnquiryTypeContactMappingModel result = asahiCustomerAccountDaoImpl.getContactByEnquiryType(AsahiEnquiryType.WEBSITE_SUPPORT, AsahiEnquirySubType.LOGIN_ISSUE);
        Assert.assertNotNull(result);

    }

}
