package com.apb.core.checkout.dao;

import java.util.List;

import org.joda.time.DateTime;

import com.sabmiller.core.model.HolidayModel;
import com.apb.integration.data.AsahiProductInfo;

import de.hybris.platform.b2b.model.B2BUnitModel;

public interface ApbCheckoutDao {

	List<HolidayModel> getHolidayModelForRegionDate(String regionCode, DateTime dateTime);

	/**
	 * Gets the b 2 B unit for uid.
	 *
	 * @param b2bUnit the b 2 b unit
	 * @return the b 2 B unit for uid
	 */
	B2BUnitModel getB2BUnitForUid(String b2bUnit);
	
	
	/**
	 * To get products present in cart or order.
	 *
	 * @param updateCart the update cart
	 * @param formQty the form qty
	 * @param code the code
	 * @return the product details from cart
	 */
	List<AsahiProductInfo> getProductDetailsFromCart(boolean updateCart, long formQty,String code);

}
