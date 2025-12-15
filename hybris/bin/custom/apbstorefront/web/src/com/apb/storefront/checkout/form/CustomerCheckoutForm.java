package com.apb.storefront.checkout.form;

import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;

import java.util.List;

import com.apb.facades.card.payment.AsahiCreditCardTypeEnum;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.storefront.forms.ApbKegReturnKegSizForm;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;



public class CustomerCheckoutForm
{
	private String deliveryAddressId;
	private String deliveryInstruction;
	private DeliveryMethodForm deliveryMethod;
	private boolean isReturnKeg;
	private String paymentMethod;
	private String poNumber;
	private PaymentDetailsForm paymentDetailsForm;
	private AsahiCreditCardTypeEnum asahiCreditCardType;
	private AsahiPaymentDetailsForm asahiPaymentDetailsForm;
	private String kegComments;
	private List<KegSizeData> kegSizeDataList;
	private List<ApbKegReturnKegSizForm> apbKegReturnKegSizForm;
	private Boolean kegReturnFlag;
	private boolean saveCreditCard;
	private String selectedDeliveryAddress; 
	private String deliveryAddressInstruction; 
	private String paymentType;
	private BdeOrderDetailsForm bdeCheckoutForm;


	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isSaveCreditCard()
	{
		return saveCreditCard;
	}

	public void setSaveCreditCard(final boolean saveCreditCard)
	{
		this.saveCreditCard = saveCreditCard;
	}

	public AsahiPaymentDetailsForm getAsahiPaymentDetailsForm()
	{
		return asahiPaymentDetailsForm;
	}

	public void setAsahiPaymentDetailsForm(final AsahiPaymentDetailsForm asahiPaymentDetailsForm)
	{
		this.asahiPaymentDetailsForm = asahiPaymentDetailsForm;
	}

	public AsahiCreditCardTypeEnum getAsahiCreditCardType()
	{
		return asahiCreditCardType;
	}

	public void setAsahiCreditCardType(final AsahiCreditCardTypeEnum asahiCreditCardType)
	{
		this.asahiCreditCardType = asahiCreditCardType;
	}

	public String getDeliveryAddressId()
	{
		return deliveryAddressId;
	}

	public void setDeliveryAddressId(final String deliveryAddressId)
	{
		this.deliveryAddressId = deliveryAddressId;
	}

	public String getDeliveryInstruction()
	{
		return deliveryInstruction;
	}

	public void setDeliveryInstruction(final String deliveryInstruction)
	{
		this.deliveryInstruction = deliveryInstruction;
	}

	public DeliveryMethodForm getDeliveryMethod()
	{
		return deliveryMethod;
	}

	public void setDeliveryMethod(final DeliveryMethodForm deliveryMethod)
	{
		this.deliveryMethod = deliveryMethod;
	}

	/**
	 * @return the isReturnKeg
	 */

	public boolean isReturnKeg()
	{
		return isReturnKeg;
	}

	/**
	 * @param isReturnKeg
	 *           the isReturnKeg to set
	 */

	public void setReturnKeg(final boolean isReturnKeg)
	{
		this.isReturnKeg = isReturnKeg;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public PaymentDetailsForm getPaymentDetailsForm()
	{
		return paymentDetailsForm;
	}

	public void setPaymentDetailsForm(final PaymentDetailsForm paymentDetailsForm)
	{
		this.paymentDetailsForm = paymentDetailsForm;
	}

	public String getPoNumber()
	{
		return poNumber;
	}

	public void setPoNumber(final String poNumber)
	{
		this.poNumber = poNumber;
	}

	/**
	 * @return the kegComments
	 */
	public String getKegComments()
	{
		return kegComments;
	}

	/**
	 * @param kegComments
	 *           the kegComments to set
	 */
	public void setKegComments(final String kegComments)
	{
		this.kegComments = kegComments;
	}

	/**
	 * @return the kegSizeDataList
	 */
	public List<KegSizeData> getKegSizeDataList()
	{
		return kegSizeDataList;
	}

	/**
	 * @param kegSizeDataList
	 *           the kegSizeDataList to set
	 */
	public void setKegSizeDataList(final List<KegSizeData> kegSizeDataList)
	{
		this.kegSizeDataList = kegSizeDataList;
	}

	/**
	 * @return the apbKegReturnKegSizForm
	 */
	public List<ApbKegReturnKegSizForm> getApbKegReturnKegSizForm()
	{
		return apbKegReturnKegSizForm;
	}

	/**
	 * @param apbKegReturnKegSizForm
	 *           the apbKegReturnKegSizForm to set
	 */
	public void setApbKegReturnKegSizForm(final List<ApbKegReturnKegSizForm> apbKegReturnKegSizForm)
	{
		this.apbKegReturnKegSizForm = apbKegReturnKegSizForm;
	}

	/**
	 * @return the kegReturnFlag
	 */
	public Boolean getKegReturnFlag()
	{
		return kegReturnFlag;
	}

	/**
	 * @param kegReturnFlag
	 *           the kegReturnFlag to set
	 */
	public void setKegReturnFlag(final Boolean kegReturnFlag)
	{
		this.kegReturnFlag = kegReturnFlag;
	}


	public String getSelectedDeliveryAddress() {
		return selectedDeliveryAddress;
	}

	public void setSelectedDeliveryAddress(String selectedDeliveryAddress) {
		this.selectedDeliveryAddress = selectedDeliveryAddress;
	}

	public String getDeliveryAddressInstruction() {
		return deliveryAddressInstruction;
	}

	public void setDeliveryAddressInstruction(String deliveryAddressInstruction) {
		this.deliveryAddressInstruction = deliveryAddressInstruction;
	}

	public BdeOrderDetailsForm getBdeCheckoutForm() {
		return bdeCheckoutForm;
	}

	public void setBdeCheckoutForm(BdeOrderDetailsForm bdeCheckoutForm) {
		this.bdeCheckoutForm = bdeCheckoutForm;
	}




}
