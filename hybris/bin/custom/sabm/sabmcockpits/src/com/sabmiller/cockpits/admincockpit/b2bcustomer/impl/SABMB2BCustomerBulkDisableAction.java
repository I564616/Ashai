package com.sabmiller.cockpits.admincockpit.b2bcustomer.impl;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menupopup;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cockpit.components.listview.AbstractListViewAction;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.wizards.Wizard;
import de.hybris.platform.cockpit.wizards.impl.DefaultWizardContext;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * The Class SABMB2BCustomerBulkDisableAction.
 */
public class SABMB2BCustomerBulkDisableAction extends AbstractListViewAction
{

	/** The Constant ICON_FUNC_APPROVAL_ACTION_AVAILABLE. */
	private static final String ICON_FUNC_APPROVAL_ACTION_AVAILABLE = "cockpit/images/icon_locked.png";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BCustomerBulkDisableAction.class);

	/** The model service. */
	private ModelService modelService;


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.cockpit.components.listview.ListViewAction#getImageURI(de.hybris.platform.cockpit.components
	 * .listview.ListViewAction.Context)
	 */
	@Override
	public String getImageURI(final Context paramContext)
	{
		return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.cockpit.components.listview.AbstractListViewAction#getMultiSelectImageURI(de.hybris.platform
	 * .cockpit.components.listview.ListViewAction.Context)
	 */
	@Override
	public String getMultiSelectImageURI(final Context context)
	{
		return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.cockpit.components.listview.ListViewAction#getEventListener(de.hybris.platform.cockpit.
	 * components .listview.ListViewAction.Context)
	 */
	@Override
	public EventListener getEventListener(final Context paramContext)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.cockpit.components.listview.AbstractListViewAction#getMultiSelectEventListener(de.hybris.
	 * platform .cockpit.components.listview.ListViewAction.Context)
	 */
	@Override
	public EventListener getMultiSelectEventListener(final Context context)
	{

		EventListener ret = null;
		final List<TypedObject> selectedItems = getSelectedItems(context);
		LOG.debug("Selected items: {}", selectedItems);
		if (CollectionUtils.isNotEmpty(selectedItems))
		{
			ret = new EventListener()
			{
				@Override
				public void onEvent(final Event arg0)
				{
					final List<B2BCustomerModel> customers = activeCustomers(selectedItems);
					final DefaultWizardContext hpc = new DefaultWizardContext();

					hpc.setAttribute("customers", customers);
					Wizard.show("b2bCustomersBulkDisableWizard", hpc);
				}
			};
		}
		return ret;
	}

	/**
	 * Active customers.
	 *
	 * @param selectedItems the selected items
	 * @return the list
	 */
	private List<B2BCustomerModel> activeCustomers(final List<TypedObject> selectedItems)
	{
		final List<B2BCustomerModel> customers = new ArrayList<B2BCustomerModel>();
		for (final Iterator<TypedObject> iter = selectedItems.iterator(); iter.hasNext();)
		{
			final TypedObject obj = iter.next();
			if (obj.getObject() instanceof B2BCustomerModel)
			{
				final B2BCustomerModel customer = (B2BCustomerModel) obj.getObject();
				customer.setActive(true);
			}
		}
		if (CollectionUtils.isNotEmpty(customers))
		{
			modelService.saveAll(customers);
		}
		return customers;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.cockpit.components.listview.ListViewAction#getPopup(de.hybris.platform.cockpit.components.
	 * listview .ListViewAction.Context)
	 */
	@Override
	public Menupopup getPopup(final Context paramContext)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.cockpit.components.listview.ListViewAction#getContextPopup(de.hybris.platform.cockpit.
	 * components .listview.ListViewAction.Context)
	 */
	@Override
	public Menupopup getContextPopup(final Context paramContext)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.cockpit.components.listview.ListViewAction#getTooltip(de.hybris.platform.cockpit.components
	 * .listview.ListViewAction.Context)
	 */
	@Override
	public String getTooltip(final Context paramContext)
	{
		return "Disable Customers";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.cockpit.components.listview.AbstractListViewAction#doCreateContext(de.hybris.platform.cockpit
	 * .components.listview.ListViewAction.Context)
	 */
	@Override
	protected void doCreateContext(final Context paramContext)
	{
		// do nothing
	}

	/**
	 * Gets the model service.
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
