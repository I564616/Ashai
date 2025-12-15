package com.apb.facades.populators;
import com.apb.facades.contactust.data.ApbContactUsData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import com.apb.core.util.AsahiSiteUtil;
import jakarta.annotation.Resource;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.enumeration.EnumerationService;

public class AsahiEnquiryPopulator implements Populator<CsTicketModel, ApbContactUsData>
{

    @Resource
    private AsahiSiteUtil asahiSiteUtil;

    @Resource(name = "enumerationService")
    private EnumerationService enumerationService;

    @Override
    public void populate(final CsTicketModel source, final ApbContactUsData target) throws ConversionException
    {

if(!asahiSiteUtil.isCub())
        {
            if(null !=source)
            {
                target.setRequestRefNumber(source.getTicketID());
                target.setDatePlaced(source.getDatePlaced());
                target.setName(source.getName());
                if(null != source.getEnquiryType())
                    target.setEnquiryType(	enumerationService.getEnumerationName(source.getEnquiryType()));
                if(null != source.getEnquirySubType())
                    target.setEnquirySubType(enumerationService.getEnumerationName(source.getEnquirySubType()));
                target.setContact(source.getContact());
            }
        }

    }

    public void setAsahiSiteUtil(AsahiSiteUtil asahiSiteUtil) {
        this.asahiSiteUtil = asahiSiteUtil;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

}
