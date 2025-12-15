package com.sabmiller.core.event;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.io.Serial;

public class TrackOrderETANotificationEmailEvent  extends AbstractCommerceUserEvent<BaseSiteModel> {

	@Serial
	private static final long serialVersionUID = 1L;

    private B2BUnitModel b2bUnit;
    private AbstractOrderModel order;

    private String startETA;

    private String endETA;


    public TrackOrderETANotificationEmailEvent()
    {
        super();
    }

    /**
     * @return the b2bUnit
     */
    public B2BUnitModel getB2bUnit()
    {
        return b2bUnit;
    }

    /**
     * @param b2bUnit
     *           the b2bUnit to set
     */
    public void setB2bUnit(final B2BUnitModel b2bUnit)
    {
        this.b2bUnit = b2bUnit;
    }

    /**
     *
     * @return
     */
    public AbstractOrderModel getOrder() {
        return order;
    }

    /**
     *
     * @param order
     */
    public void setOrder(final AbstractOrderModel order) {
        this.order = order;
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
