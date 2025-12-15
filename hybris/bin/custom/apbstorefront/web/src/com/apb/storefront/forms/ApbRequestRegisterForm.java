package com.apb.storefront.forms;

import org.springframework.web.multipart.MultipartFile;


/**
 *
 */
public class ApbRequestRegisterForm
{
	private boolean requestCustomerType;
	private String outletName;
	private String tradingName;
	private String companyName;
	private String streetNumber;
	private String streetName;
	private String streetAbreviation;
	private String unitNoShopNo;
	private String level;
	private String suburb;
	private String stateInvoice;
	private String postcodeInvoice;
	private String contactName;
	private String alternateContact;
	private String phoneNoInvoice;
	private String customerType; // list box
	private String alternativePhoneNo;
	private String warehouseNo;
	private String emailAddress;
	private String abn;
	private String liquorLicense;
	private String acn;
	private boolean sameasInvoiceAddress;
	private String shippingStreet;
	private String shippingSuburb;
	private String stateDelivery;
	private String postcodeDelivery;
	private String deliveryInstructions;
	private boolean applicantCarry;
	private String typeofEntity; // list box
	private String typeofBusiness; // list box
	private String dateBusinessEstablished;
	private String licensedPremisesAddress;
	private String licensee;
	private String bannerGroup;
	private String dateandExpiryofLiquorLicense;
	private String purchasingOfficer;
	private String accountsContact;
	private String name;
	private String position;
	private String address;
	private String dateofBirth;
	private String name1;
	private String position1;
	private String address1;
	private String dateofBirth1;
	private String bankBranch;
	private String contact;
	private String phoneNoReference;
	private boolean requestTermsConditions;
	private String trustName;
	private String trustDeed;
	private String trustAbn;
	private String email;
	private MultipartFile pdfFile;
	private boolean addAnother;
	private boolean sameasDeliveryAddressLPA;
	private String phoneNo;
	private String phoneNo1;



	/**
	 * @return the pdfFile
	 */
	public MultipartFile getPdfFile()
	{
		return pdfFile;
	}

	/**
	 * @param pdfFile
	 *           the pdfFile to set
	 */
	public void setPdfFile(final MultipartFile pdfFile)
	{
		this.pdfFile = pdfFile;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @return the requestCustomerType
	 */
	public boolean isRequestCustomerType()
	{
		return requestCustomerType;
	}

	/**
	 * @param requestCustomerType
	 *           the requestCustomerType to set
	 */
	public void setRequestCustomerType(final boolean requestCustomerType)
	{
		this.requestCustomerType = requestCustomerType;
	}

	/**
	 * @return the outletName
	 */
	public String getOutletName()
	{
		return outletName;
	}

	/**
	 * @param outletName
	 *           the outletName to set
	 */
	public void setOutletName(final String outletName)
	{
		this.outletName = outletName;
	}

	/**
	 * @return the tradingName
	 */
	public String getTradingName()
	{
		return tradingName;
	}

	/**
	 * @param tradingName
	 *           the tradingName to set
	 */
	public void setTradingName(final String tradingName)
	{
		this.tradingName = tradingName;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName()
	{
		return companyName;
	}

	/**
	 * @param companyName
	 *           the companyName to set
	 */
	public void setCompanyName(final String companyName)
	{
		this.companyName = companyName;
	}

	/**
	 * @return the streetNumber
	 */
	public String getStreetNumber()
	{
		return streetNumber;
	}

	/**
	 * @param streetNumber
	 *           the streetNumber to set
	 */
	public void setStreetNumber(final String streetNumber)
	{
		this.streetNumber = streetNumber;
	}

	/**
	 * @return the streetName
	 */
	public String getStreetName()
	{
		return streetName;
	}

	/**
	 * @param streetName
	 *           the streetName to set
	 */
	public void setStreetName(final String streetName)
	{
		this.streetName = streetName;
	}

	/**
	 * @return the streetAbreviation
	 */
	public String getStreetAbreviation()
	{
		return streetAbreviation;
	}

	/**
	 * @param streetAbreviation
	 *           the streetAbreviation to set
	 */
	public void setStreetAbreviation(final String streetAbreviation)
	{
		this.streetAbreviation = streetAbreviation;
	}

	/**
	 * @return the unitNoShopNo
	 */
	public String getUnitNoShopNo()
	{
		return unitNoShopNo;
	}

	/**
	 * @param unitNoShopNo
	 *           the unitNoShopNo to set
	 */
	public void setUnitNoShopNo(final String unitNoShopNo)
	{
		this.unitNoShopNo = unitNoShopNo;
	}

	/**
	 * @return the level
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param level
	 *           the level to set
	 */
	public void setLevel(final String level)
	{
		this.level = level;
	}

	/**
	 * @return the suburb
	 */
	public String getSuburb()
	{
		return suburb;
	}

	/**
	 * @param suburb
	 *           the suburb to set
	 */
	public void setSuburb(final String suburb)
	{
		this.suburb = suburb;
	}

	/**
	 * @return the stateInvoice
	 */
	public String getStateInvoice()
	{
		return stateInvoice;
	}

	/**
	 * @param stateInvoice
	 *           the stateInvoice to set
	 */
	public void setStateInvoice(final String stateInvoice)
	{
		this.stateInvoice = stateInvoice;
	}

	/**
	 * @return the postcodeInvoice
	 */
	public String getPostcodeInvoice()
	{
		return postcodeInvoice;
	}

	/**
	 * @param postcodeInvoice
	 *           the postcodeInvoice to set
	 */
	public void setPostcodeInvoice(final String postcodeInvoice)
	{
		this.postcodeInvoice = postcodeInvoice;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName()
	{
		return contactName;
	}

	/**
	 * @param contactName
	 *           the contactName to set
	 */
	public void setContactName(final String contactName)
	{
		this.contactName = contactName;
	}

	/**
	 * @return the alternateContact
	 */
	public String getAlternateContact()
	{
		return alternateContact;
	}

	/**
	 * @param alternateContact
	 *           the alternateContact to set
	 */
	public void setAlternateContact(final String alternateContact)
	{
		this.alternateContact = alternateContact;
	}

	/**
	 * @return the phoneNoinvoice
	 */
	public String getPhoneNoInvoice()
	{
		return phoneNoInvoice;
	}

	/**
	 * @param phoneNoInvoice
	 *           the phoneNoinvoice to set
	 */
	public void setPhoneNoInvoice(final String phoneNoInvoice)
	{
		this.phoneNoInvoice = phoneNoInvoice;
	}

	/**
	 * @return the customerType
	 */
	public String getCustomerType()
	{
		return customerType;
	}

	/**
	 * @param customerType
	 *           the customerType to set
	 */
	public void setCustomerType(final String customerType)
	{
		this.customerType = customerType;
	}

	/**
	 * @return the alternativePhoneNo
	 */
	public String getAlternativePhoneNo()
	{
		return alternativePhoneNo;
	}

	/**
	 * @param alternativePhoneNo
	 *           the alternativePhoneNo to set
	 */
	public void setAlternativePhoneNo(final String alternativePhoneNo)
	{
		this.alternativePhoneNo = alternativePhoneNo;
	}

	/**
	 * @return the warehouseNo
	 */
	public String getWarehouseNo()
	{
		return warehouseNo;
	}

	/**
	 * @param warehouseNo
	 *           the warehouseNo to set
	 */
	public void setWarehouseNo(final String warehouseNo)
	{
		this.warehouseNo = warehouseNo;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *           the emailAddress to set
	 */
	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the abn
	 */
	public String getAbn()
	{
		return abn;
	}

	/**
	 * @param abn
	 *           the abn to set
	 */
	public void setAbn(final String abn)
	{
		this.abn = abn;
	}

	/**
	 * @return the liquorLicense
	 */
	public String getLiquorLicense()
	{
		return liquorLicense;
	}

	/**
	 * @param liquorLicense
	 *           the liquorLicense to set
	 */
	public void setLiquorLicense(final String liquorLicense)
	{
		this.liquorLicense = liquorLicense;
	}

	/**
	 * @return the acn
	 */
	public String getAcn()
	{
		return acn;
	}

	/**
	 * @param acn
	 *           the acn to set
	 */
	public void setAcn(final String acn)
	{
		this.acn = acn;
	}

	/**
	 * @return the sameasInvoiceAddress
	 */
	public boolean isSameasInvoiceAddress()
	{
		return sameasInvoiceAddress;
	}

	/**
	 * @param sameasInvoiceAddress
	 *           the sameasInvoiceAddress to set
	 */
	public void setSameasInvoiceAddress(final boolean sameasInvoiceAddress)
	{
		this.sameasInvoiceAddress = sameasInvoiceAddress;
	}

	/**
	 * @return the shippingStreet
	 */
	public String getShippingStreet()
	{
		return shippingStreet;
	}

	/**
	 * @param shippingStreet
	 *           the shippingStreet to set
	 */
	public void setShippingStreet(final String shippingStreet)
	{
		this.shippingStreet = shippingStreet;
	}

	/**
	 * @return the shippingSuburb
	 */
	public String getShippingSuburb()
	{
		return shippingSuburb;
	}

	/**
	 * @param shippingSuburb
	 *           the shippingSuburb to set
	 */
	public void setShippingSuburb(final String shippingSuburb)
	{
		this.shippingSuburb = shippingSuburb;
	}

	/**
	 * @return the statedDlivery
	 */
	public String getStateDelivery()
	{
		return stateDelivery;
	}

	/**
	 * @param stateDelivery
	 *           the statedDlivery to set
	 */
	public void setStateDelivery(final String stateDelivery)
	{
		this.stateDelivery = stateDelivery;
	}

	/**
	 * @return the postcodeDelivery
	 */
	public String getPostcodeDelivery()
	{
		return postcodeDelivery;
	}

	/**
	 * @param postcodeDelivery
	 *           the postcodeDelivery to set
	 */
	public void setPostcodeDelivery(final String postcodeDelivery)
	{
		this.postcodeDelivery = postcodeDelivery;
	}

	/**
	 * @return the deliveryInstructions
	 */
	public String getDeliveryInstructions()
	{
		return deliveryInstructions;
	}

	/**
	 * @param deliveryInstructions
	 *           the deliveryInstructions to set
	 */
	public void setDeliveryInstructions(final String deliveryInstructions)
	{
		this.deliveryInstructions = deliveryInstructions;
	}



	/**
	 * @return the applicantCarry
	 */
	public boolean isApplicantCarry()
	{
		return applicantCarry;
	}

	/**
	 * @param applicantCarry
	 *           the applicantCarry to set
	 */
	public void setApplicantCarry(final boolean applicantCarry)
	{
		this.applicantCarry = applicantCarry;
	}

	/**
	 * @param phoneNoReference
	 *           the phoneNoReference to set
	 */
	public void setPhoneNoReference(final String phoneNoReference)
	{
		this.phoneNoReference = phoneNoReference;
	}

	/**
	 * @return the typeofEntity
	 */
	public String getTypeofEntity()
	{
		return typeofEntity;
	}

	/**
	 * @param typeofEntity
	 *           the typeofEntity to set
	 */
	public void setTypeofEntity(final String typeofEntity)
	{
		this.typeofEntity = typeofEntity;
	}

	/**
	 * @return the typeofBusiness
	 */
	public String getTypeofBusiness()
	{
		return typeofBusiness;
	}

	/**
	 * @param typeofBusiness
	 *           the typeofBusiness to set
	 */
	public void setTypeofBusiness(final String typeofBusiness)
	{
		this.typeofBusiness = typeofBusiness;
	}

	/**
	 * @return the dateBusinessEstablished
	 */
	public String getDateBusinessEstablished()
	{
		return dateBusinessEstablished;
	}

	/**
	 * @param dateBusinessEstablished
	 *           the dateBusinessEstablished to set
	 */
	public void setDateBusinessEstablished(final String dateBusinessEstablished)
	{
		this.dateBusinessEstablished = dateBusinessEstablished;
	}

	/**
	 * @return the licensedPremisesAddress
	 */
	public String getLicensedPremisesAddress()
	{
		return licensedPremisesAddress;
	}

	/**
	 * @param licensedPremisesAddress
	 *           the licensedPremisesAddress to set
	 */
	public void setLicensedPremisesAddress(final String licensedPremisesAddress)
	{
		this.licensedPremisesAddress = licensedPremisesAddress;
	}

	/**
	 * @return the licensee
	 */
	public String getLicensee()
	{
		return licensee;
	}

	/**
	 * @param licensee
	 *           the licensee to set
	 */
	public void setLicensee(final String licensee)
	{
		this.licensee = licensee;
	}

	/**
	 * @return the bannerGroup
	 */
	public String getBannerGroup()
	{
		return bannerGroup;
	}

	/**
	 * @param bannerGroup
	 *           the bannerGroup to set
	 */
	public void setBannerGroup(final String bannerGroup)
	{
		this.bannerGroup = bannerGroup;
	}

	/**
	 * @return the dateandExpiryofLiquorLicense
	 */
	public String getDateandExpiryofLiquorLicense()
	{
		return dateandExpiryofLiquorLicense;
	}

	/**
	 * @param dateandExpiryofLiquorLicense
	 *           the dateandExpiryofLiquorLicense to set
	 */
	public void setDateandExpiryofLiquorLicense(final String dateandExpiryofLiquorLicense)
	{
		this.dateandExpiryofLiquorLicense = dateandExpiryofLiquorLicense;
	}

	/**
	 * @return the purchasingOfficer
	 */
	public String getPurchasingOfficer()
	{
		return purchasingOfficer;
	}

	/**
	 * @param purchasingOfficer
	 *           the purchasingOfficer to set
	 */
	public void setPurchasingOfficer(final String purchasingOfficer)
	{
		this.purchasingOfficer = purchasingOfficer;
	}

	/**
	 * @return the accountsContact
	 */
	public String getAccountsContact()
	{
		return accountsContact;
	}

	/**
	 * @param accountsContact
	 *           the accountsContact to set
	 */
	public void setAccountsContact(final String accountsContact)
	{
		this.accountsContact = accountsContact;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the position
	 */
	public String getPosition()
	{
		return position;
	}

	/**
	 * @param position
	 *           the position to set
	 */
	public void setPosition(final String position)
	{
		this.position = position;
	}


	/**
	 * @return the dateofBirth
	 */
	public String getDateofBirth()
	{
		return dateofBirth;
	}

	/**
	 * @param dateofBirth
	 *           the dateofBirth to set
	 */
	public void setDateofBirth(final String dateofBirth)
	{
		this.dateofBirth = dateofBirth;
	}

	/**
	 * @return the bankBranch
	 */
	public String getBankBranch()
	{
		return bankBranch;
	}

	/**
	 * @param bankBranch
	 *           the bankBranch to set
	 */
	public void setBankBranch(final String bankBranch)
	{
		this.bankBranch = bankBranch;
	}

	/**
	 * @return the contact
	 */
	public String getContact()
	{
		return contact;
	}

	/**
	 * @param contact
	 *           the contact to set
	 */
	public void setContact(final String contact)
	{
		this.contact = contact;
	}

	/**
	 * @return the phoneNorRference
	 */
	public String getPhoneNoReference()
	{
		return phoneNoReference;
	}

	/**
	 * @param phoneNoReference
	 *           the phoneNorRference to set
	 */
	public void setPhoneNorReference(final String phoneNoReference)
	{
		this.phoneNoReference = phoneNoReference;
	}

	/**
	 * @return the termsConditions
	 */
	public boolean isRequestTermsConditions()
	{
		return requestTermsConditions;
	}

	/**
	 * @param requestTermsConditions
	 *           the termsConditions to set
	 */
	public void setTermsConditions(final boolean requestTermsConditions)
	{
		this.requestTermsConditions = requestTermsConditions;
	}

	/**
	 * @return the trustName
	 */
	public String getTrustName()
	{
		return trustName;
	}

	/**
	 * @param trustName
	 *           the trustName to set
	 */
	public void setTrustName(final String trustName)
	{
		this.trustName = trustName;
	}

	/**
	 * @return the trustDeed
	 */
	public String getTrustDeed()
	{
		return trustDeed;
	}

	/**
	 * @param trustDeed
	 *           the trustDeed to set
	 */
	public void setTrustDeed(final String trustDeed)
	{
		this.trustDeed = trustDeed;
	}

	/**
	 * @param requestTermsConditions
	 *           the requestTermsConditions to set
	 */
	public void setRequestTermsConditions(final boolean requestTermsConditions)
	{
		this.requestTermsConditions = requestTermsConditions;
	}

	/**
	 * @return the trustAbn
	 */
	public String getTrustAbn()
	{
		return trustAbn;
	}

	/**
	 * @param trustAbn
	 *           the trustAbn to set
	 */
	public void setTrustAbn(final String trustAbn)
	{
		this.trustAbn = trustAbn;
	}

	/**
	 * @return the position1
	 */
	public String getPosition1()
	{
		return position1;
	}

	/**
	 * @param position1
	 *           the position1 to set
	 */
	public void setPosition1(final String position1)
	{
		this.position1 = position1;
	}

	/**
	 * @return the dateofBirth1
	 */
	public String getDateofBirth1()
	{
		return dateofBirth1;
	}

	/**
	 * @param dateofBirth1
	 *           the dateofBirth1 to set
	 */
	public void setDateofBirth1(final String dateofBirth1)
	{
		this.dateofBirth1 = dateofBirth1;
	}

	/**
	 * @return the name1
	 */
	public String getName1()
	{
		return name1;
	}

	/**
	 * @param name1
	 *           the name1 to set
	 */
	public void setName1(final String name1)
	{
		this.name1 = name1;
	}

	/**
	 * @return the addAnother
	 */
	public boolean isAddAnother()
	{
		return addAnother;
	}

	/**
	 * @param addAnother
	 *           the addAnother to set
	 */
	public void setAddAnother(final boolean addAnother)
	{
		this.addAnother = addAnother;
	}



	/**
	 * @return the sameasDeliveryAddressLPA
	 */
	public boolean isSameasDeliveryAddressLPA()
	{
		return sameasDeliveryAddressLPA;
	}

	/**
	 * @param sameasDeliveryAddressLPA
	 *           the sameasDeliveryAddressLPA to set
	 */
	public void setSameasDeliveryAddressLPA(final boolean sameasDeliveryAddressLPA)
	{
		this.sameasDeliveryAddressLPA = sameasDeliveryAddressLPA;
	}

	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo()
	{
		return phoneNo;
	}

	/**
	 * @param phoneNo
	 *           the phoneNo to set
	 */
	public void setPhoneNo(final String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	/**
	 * @return the phoneNo1
	 */
	public String getPhoneNo1()
	{
		return phoneNo1;
	}

	/**
	 * @param phoneNo1
	 *           the phoneNo1 to set
	 */
	public void setPhoneNo1(final String phoneNo1)
	{
		this.phoneNo1 = phoneNo1;
	}

	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(final String address)
	{
		this.address = address;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1()
	{
		return address1;
	}

	/**
	 * @param address1
	 *           the address1 to set
	 */
	public void setAddress1(final String address1)
	{
		this.address1 = address1;
	}


}
