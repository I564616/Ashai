package web.src.com.apb.storefront.forms;

import java.util.List;

import com.apb.facades.data.QuickOrderData;


/**
 * This class is used to get products and quantity data for quick order functionality for adding to cart.
 */

public class AsahiQuickOrderForm {
	
	private List<QuickOrderData> quickOrderDataList;

	private boolean clearCart;
	
	public boolean getClearCart() {
		return clearCart;
	}

	public void setClearCart(boolean clearCart) {
		this.clearCart = clearCart;
	}

	public List<QuickOrderData> getQuickOrderDataList() {
		return quickOrderDataList;
	}

	public void setQuickOrderDataList(List<QuickOrderData> quickOrderDataList) {
		this.quickOrderDataList = quickOrderDataList;
	}
}
