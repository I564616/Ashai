/*
 *
 */
package com.apb.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderHistoryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryEntryData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.apb.core.checkout.service.impl.ApbCheckoutServiceImpl;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.core.util.AsahiAdhocCoreUtil;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.sabmiller.core.enums.OrderType;
import com.sabmiller.core.model.BDECustomerModel;

public class ApbOrderHistoryPopulator extends OrderHistoryPopulator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ApbCheckoutServiceImpl.class);

	/** The Constant ASAHI_USER_TIMEOFFSET_COOKIE. */
    private static final String ASAHI_USER_TIMEOFFSET_COOKIE = "asahiUserTimeOffsetCookie";

	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;

	private static final String REP = "Rep:";
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private I18NService i18nService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;


	@Resource
	private CommerceCommonI18NService commerceCommonI18NService;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/** The session service. */
	@Resource
	private SessionService sessionService;

	@Autowired
    private AsahiAdhocCoreUtil adhocCoreUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	private static final String ORDER_NUMBER_FOR_BLANK = "order.number.for.not.process.order";

	@Override
	public void populate(final OrderModel source, final OrderHistoryData target)
	{
		super.populate(source, target);

		if(!asahiSiteUtil.isCub())
		{
   		if (source.getOrderType() != null)
   		{
   			final String orderType = getEnumerationService().getEnumerationName(source.getOrderType(),
   					i18nService.getCurrentLocale());
   			target.setOrderType(orderType);

   		}
   		/* String firstName = source.getUser().getName(); */

   		String userName = getUserName(source);
			if (asahiSiteUtil.isSga())
			{
				if (null != source.getOrderType() && source.getOrderType().equals(
						enumerationService.getEnumerationValue(OrderType.class, ApbFacadesConstants.ORDER_TYPE_CALLCENTERORDER)))
				{
					userName = userName.substring(0, userName.indexOf(' '));
				}

				if(userName.indexOf(":") > 0) {
					userName = userName.replaceFirst("Rep:", "Rep: ");
				}


			}
			else if (asahiSiteUtil.isApb() && StringUtils.isNotEmpty(userName) && userName.indexOf(' ') > 0)
   		{
   			userName = userName.substring(0, userName.indexOf(' '));
   		}
   		target.setFirstName(userName);

   		if(null!=source.getStatus()){

   			if(source.getSite().getUid().equalsIgnoreCase("apb"))
   			{
   				target.setStatusDisplay(asahiOrderService.getDisplayOrderStatus(source.getStatus().getCode()
   						,source.getSite().getUid()));
   			}
   			else if(source.getSite().getUid().equalsIgnoreCase("sga"))
   			{
   				target.setStatusDisplay(asahiOrderService.getDisplayOrderStatus(source.getStatus().getCode()
   						,source.getSite().getUid()));
   			}

   		}
   		if (source.getTotalPrice() != null)
   		{
   			target.setTotal(getPriceDataFactory().create(PriceDataType.BUY, getTotalPriceWithTax(source.getTotalPrice(), source.getNet(), source.getOrderGST(), source.getOrderCDL(),source.getInvoiceAmountWithGST()), source.getCurrency()));
   			 target.setOrderNetPrice(getPriceDataFactory().create(PriceDataType.BUY,BigDecimal.valueOf(source.getTotalPrice()),source.getCurrency()));
     			target.setOrderCDL(getPriceDataFactory().create(PriceDataType.BUY,BigDecimal.valueOf(source.getOrderCDL()!=null?source.getOrderCDL():0.0),source.getCurrency()));
     			target.setOrderGST(getPriceDataFactory().create(PriceDataType.BUY,BigDecimal.valueOf(source.getOrderGST()!=null?source.getOrderGST():0.0),source.getCurrency()));
     			target.setSubTotal(getPriceDataFactory().create(PriceDataType.BUY,BigDecimal.valueOf(source.getSubtotal()!=null?source.getSubtotal():0.0),source.getCurrency()));
   		}

     		final List<AbstractOrderEntryModel> orderEntries = source.getEntries();

     		if (CollectionUtils.isNotEmpty(orderEntries))
     		{
     			final List<OrderHistoryEntryData> entries = new ArrayList<>();

     			orderEntries.stream().forEach(e->
     			{
     			final OrderHistoryEntryData entry = new OrderHistoryEntryData();
     			final ProductModel product = e.getProduct();

     			if(product instanceof ApbProductModel)
     			{
     				entry.setProductName(product.getName());

     				final PackageSizeModel packageSize = ((ApbProductModel)product).getPackageSize();

     				if(packageSize !=null)
     				{
     					entry.setProductPackageSize(packageSize.getName(commerceCommonI18NService.getCurrentLocale()));
     				}

     				final UnitVolumeModel unitVolume = ((ApbProductModel)product).getPortalUnitVolume();

     				if(unitVolume !=null)
     				{
     					entry.setProductUnitVolume(unitVolume.getName());
     				}

     				if(e.getQuantity()!=null)
     				{
     					entry.setOrderQuantity(e.getQuantity().toString());
     				}
     				if(e.getBasePrice()!=null)
     				{
     					entry.setProductBasePrice(getPriceDataFactory().create(PriceDataType.BUY,BigDecimal.valueOf(e.getBasePrice()),source.getCurrency()));
     				}

     			}
     			   entries.add(entry);
     			});
     			target.setEntries(entries);
     		}
   		if (source.getSalesOrderId() != null)
   		{
   			target.setSalesOrderId(source.getSalesOrderId());
   		}
   		else
   		{
   			if(this.asahiSiteUtil.isSga()){

   				target.setSalesOrderId(target.getCode());
   			}else{

   				final String salesOrderId = asahiConfigurationService.getString(ORDER_NUMBER_FOR_BLANK, "In Process");
   				target.setSalesOrderId(salesOrderId);
   			}
   		}

   		if(CollectionUtils.isNotEmpty(source.getEntries())){
   			target.setIsOnlyBonus(!asahiCoreUtil.isNonBonusProductExist(source.getEntries()));
   		}

   		// Added to check whether all product are Excluded/InActive -- Re-Order Button will be disable
   		if(asahiSiteUtil.isSga() && CollectionUtils.isNotEmpty(source.getEntries())){
   			target.setAllProductExcluded(inclusionExclusionProductStrategy.allProdExcl(source.getEntries()));
   		}

   		String timeZone = sessionService.getAttribute(ASAHI_USER_TIMEOFFSET_COOKIE);
   		if(null == timeZone){
   			final HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
   			if(null!=request){
   				for (final Cookie cookie : request.getCookies())
   				{
   					if (null != cookie && null != cookie.getName() && ASAHI_USER_TIMEOFFSET_COOKIE.equals(cookie.getName()))
   					{
   						sessionService.setAttribute(ASAHI_USER_TIMEOFFSET_COOKIE, cookie.getValue());
   						timeZone = cookie.getValue();
   						break;
   					}
   				}
   			}
   		}
   		if(null != timeZone){
   		    target.setOrderPlacedDate(adhocCoreUtil.getOrderDateInUserTimeZone(timeZone, source.getDate()));
   		}
   		if(!asahiSiteUtil.isSga() && null != source.getDeliveryRequestDate()){
            final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            target.setDeliveryRequestDate(dateFormat.format(source.getDeliveryRequestDate()));
        }
		}
	}

	/**
	 * Convert time.
	 *
	 * @param fromCal the from cal
	 * @param toCal the to cal
	 * @return the calendar
	 */
	private static Calendar convertTime(final Calendar fromCal, final Calendar toCal) {
	     Calendar result;

	       toCal.set(0, fromCal.get(0));
	       toCal.set(1, fromCal.get(1));
	       toCal.set(2, fromCal.get(2));
	       toCal.set(5, fromCal.get(5));
	       toCal.set(11, fromCal.get(11));
	       toCal.set(12, fromCal.get(12));
	       toCal.set(13, fromCal.get(13));
	       toCal.set(14, fromCal.get(14));
	       result = toCal;

	     return result;
	}
	private BigDecimal getTotalPriceWithTax(final Double orderTotalPrice, final Boolean isNet, final Double orderGST, final Double orderCDL, final Double invoiceAmountWithGST)
	{
		BigDecimal totalPrice;
		if (invoiceAmountWithGST != null)
		{
			totalPrice = BigDecimal.valueOf(invoiceAmountWithGST.doubleValue());
		}
		else
		{

			totalPrice = BigDecimal.valueOf(orderTotalPrice.doubleValue());
			// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
			if (Boolean.TRUE.equals(isNet) && totalPrice.compareTo(BigDecimal.ZERO) != 0 && orderGST != null)
			{
				totalPrice = totalPrice.add(BigDecimal.valueOf(orderGST.doubleValue()));
			}
		}
		// Adding CDL in Order Total
		/*if (asahiSiteUtil.isSga() && null!=orderCDL)
		{
			totalPrice = totalPrice.add(BigDecimal.valueOf(orderCDL.doubleValue()));
		}*/
		return totalPrice;

	}

	private String getUserName(final OrderModel source)
	{
		String userName = StringUtils.EMPTY;
		UserModel userModel = source.getPlacedBy();
		if (null == userModel)
		{
			userModel = source.getUser();
			if (null != userModel)
			{
				if (userModel instanceof BDECustomerModel)
				{
					userName = REP.concat(userModel.getName());
				}
				else
				{
					userName = userModel.getName();
				}

			}
		}
		else
		{
			userName = REP.concat(userModel.getName());
		}
		return userName;
	}
}
