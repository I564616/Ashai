/**
 *
 */
package com.sabmiller.core.businessenquiry;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;
import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;


/**
 *
 */
public interface BusinessEnquiryService
{
	boolean sendEmail(AbstractBusinessEnquiryData enquiry);

	public SFCompositeResponse createKegIssueWithSalesforce(SabmKegIssueData sabmKegIssueData);

}
