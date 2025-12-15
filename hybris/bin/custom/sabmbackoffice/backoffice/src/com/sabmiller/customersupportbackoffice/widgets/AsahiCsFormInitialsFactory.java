package com.sabmiller.customersupportbackoffice.widgets;

import de.hybris.platform.customersupportbackoffice.widgets.DefaultCsFormInitialsFactory;
import com.sabmiller.cockpitng.customersupportbackoffice.data.AsahiCsCreateB2BCustomerForm;
import de.hybris.platform.core.model.user.AddressModel;

public class AsahiCsFormInitialsFactory extends DefaultCsFormInitialsFactory{
    /**
     * Method is used to save last saved address in the Form
     * @return CsCreateB2BCustomerForm
     */
	 public AsahiCsCreateB2BCustomerForm getB2BCustomerForm() {
	        final AsahiCsCreateB2BCustomerForm customerForm = new AsahiCsCreateB2BCustomerForm();
	        final AddressModel addressModel = new AddressModel();
	        if (getLastSavedAddress() != null) {
	            addressModel.setCountry(getLastSavedAddress().getCountry());
	        }
	        customerForm.setAddress(addressModel);
	        return customerForm;
	    }
}
