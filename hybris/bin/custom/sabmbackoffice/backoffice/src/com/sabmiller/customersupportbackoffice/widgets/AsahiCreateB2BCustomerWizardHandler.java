package com.sabmiller.customersupportbackoffice.widgets;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customersupportbackoffice.widgets.CsCreateWizardBaseHandler;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.Map;

import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import de.hybris.platform.customersupportbackoffice.widgets.CsCreateWizardBaseHandler;
import de.hybris.platform.servicelayer.user.UserService;
import org.zkoss.util.resource.Labels;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowContextParameterNames;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowController;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;
import com.sabmiller.cockpitng.customersupportbackoffice.data.AsahiCsCreateB2BCustomerForm;
import com.sabmiller.customersupportbackoffice.strategies.impl.AsahiCsCreateB2BCustomerStrategy;

public class AsahiCreateB2BCustomerWizardHandler extends CsCreateWizardBaseHandler implements FlowActionHandler {

	private AsahiCsCreateB2BCustomerStrategy asahiCsCreateB2BCustomerStrategy;

	private NotificationService notificationService;

	private UserService userService;


    /**
     *  Method to create the Customer and the b2bCustomer form taking the values from the
     *  Wizard of Backoffice
     */
	public void perform(CustomType customType, FlowActionHandlerAdapter adapter, Map<String, String> parameters)
	    {
	    try
	     {
	    	AsahiCsCreateB2BCustomerForm form =
	       (AsahiCsCreateB2BCustomerForm)adapter.getWidgetInstanceManager().getModel().getValue("customersupport_backoffice_customerForm", AsahiCsCreateB2BCustomerForm.class);

	       getAsahiCsCreateB2BCustomerStrategy().createCustomer(form);

	      UserModel userModel = userService.getUserForUID(form.getEmail().toLowerCase());
			Map<String, Object> contextMap = new HashMap<>();
			contextMap.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "B2BCustomer");
			contextMap.put("uid", userModel.getUid());
			adapter.getWidgetInstanceManager().sendOutput("wizardResult", contextMap);


	      Context internalContext = new DefaultContext();
	      internalContext.addAttribute("updatedObjectIsNew", Boolean.TRUE);
	      publishEvent("objectUpdated", userModel, internalContext);
	   }
	     catch (DuplicateUidException localDuplicateUidException)
	     {
	     ConfigurableFlowController controller = (ConfigurableFlowController)adapter.getWidgetInstanceManager()
	        .getWidgetslot().getAttribute("widgetController");

	       if ("step2".equals(controller.getCurrentStep().getId()))
	      {
	        adapter.back();
	     }
	     
	       
	       getNotificationService().notifyUser(this.notificationService.getWidgetNotificationSource(adapter.getWidgetInstanceManager()), "CreateObject", Level.FAILURE, new Object[]{Labels.getLabel("customersupport_backoffice_create_customer_error")});
	      return;
	     }
	     adapter.done();
	   }


	/**
	 * @return the notificationService
	 */
	public NotificationService getNotificationService()
	{
		return notificationService;
	}


	/**
	 * @param notificationService the notificationService to set
	 */
	public void setNotificationService(NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}


	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}


	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the asahiCsCreateB2BCustomerStrategy
	 */
	public AsahiCsCreateB2BCustomerStrategy getAsahiCsCreateB2BCustomerStrategy() {
		return asahiCsCreateB2BCustomerStrategy;
	}


	/**
	 * @param asahiCsCreateB2BCustomerStrategy the asahiCsCreateB2BCustomerStrategy to set
	 */
	public void setAsahiCsCreateB2BCustomerStrategy(
			AsahiCsCreateB2BCustomerStrategy asahiCsCreateB2BCustomerStrategy) {
		this.asahiCsCreateB2BCustomerStrategy = asahiCsCreateB2BCustomerStrategy;
	}
}
