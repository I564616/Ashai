package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabm.core.model.TrackOrderETANotificationEmailProcessModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.util.SabmFeatureUtil;

public class TrackOrderETANotificationEmailContext extends AbstractEmailContext<TrackOrderETANotificationEmailProcessModel> {

    private CustomerData customerData;
    private OrderData orderData;

    @Resource(name = "customerConverter")
    private Converter<UserModel, CustomerData> customerConverter;

    @Resource(name = "orderConverter")
    private Converter<OrderModel, OrderData> orderConverter;

    private String requestedDeliveryDate;

    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUti;

    private String startETA;

    private String endETA;

    @Override
    public void init(final TrackOrderETANotificationEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
    {
        super.init(businessProcessModel, emailPageModel);
        put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");
        customerData = customerConverter.convert(businessProcessModel.getCustomer());
        orderData = orderConverter.convert(businessProcessModel.getOrder());
        orderData.setStatusDisplay( StringUtils.capitalize(businessProcessModel.getOrder().getStatusDisplay()));
        requestedDeliveryDate = SabmDateUtils.getFormattedDate(orderData.getRequestedDeliveryDate());
        startETA = businessProcessModel.getStartETA();
        endETA = businessProcessModel.getEndETA();
    }

    public void setOrderData(final OrderData orderData) {
        this.orderData = orderData;
    }

    public OrderData getOrderData() {
        return orderData;
    }

    @Override
    protected BaseSiteModel getSite(final TrackOrderETANotificationEmailProcessModel businessProcessModel)
    {
        return businessProcessModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(final TrackOrderETANotificationEmailProcessModel businessProcessModel)
    {
        return businessProcessModel.getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(final TrackOrderETANotificationEmailProcessModel businessProcessModel)
    {
        return businessProcessModel.getLanguage();
    }

    public String getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(final String requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(final CustomerData customerData) {
        this.customerData = customerData;
    }

    public String getStartETA() {
        return startETA;
    }

    public void setStartETA(final String startETA) {
        this.startETA = startETA;
    }

    public String getEndETA() {
        return endETA;
    }

    public void setEndETA(final String endETA) {
        this.endETA = endETA;
    }
}