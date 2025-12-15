/**
 *
 */
package com.sabmiller.facades.businessenquiry;

import java.util.List;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.generic.data.SABMEnquirySubTypeData;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;
import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;


/**
 * The Interface SabmBusinessEnquiryFacade.
 */
public interface SabmBusinessEnquiryFacade
{

	List<SABMEnquirySubTypeData> fetchEnquirySubType(String enquiryType);

	/**
	 * Send business enquiry email.
	 *
	 * @param enquiry
	 *           the enquiry
	 */
	void sendBusinessEnquiryEmail(AbstractBusinessEnquiryData enquiry);

	public SFCompositeResponse createKegIssueWithSalesforce(AbstractBusinessEnquiryData businessEnquiryData);

	public SFCompositeResponse buildResponse(String status, String message);


}
