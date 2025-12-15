/**
 *
 */
package com.sabmiller.facades.pa.search.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.pa.search.PersonalAssistanceSearchFacade;
import com.sabmiller.facades.user.SearchB2bUnitData;


/**
 * @author dale.bryan.a.mercado
 *
 */
public class PersonalAssistanceSearchFacadeImpl implements PersonalAssistanceSearchFacade
{
	private SabmB2BUnitService b2bUnitService;
	private SabmB2BCustomerService b2bCustomerService;

	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	private Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter;

	@Override
	public List<SearchB2bUnitData> searchB2BUnitByAccount(final String accountNumber)
	{
		final List<SearchB2bUnitData> b2bUnitDataList = new ArrayList<SearchB2bUnitData>();

		for (final B2BUnitModel b2bUnitModel : getB2bUnitService().searchB2BUnitByAccount(accountNumber))
		{
			final List<B2BCustomerModel> zadpUsersByB2BUnit = b2bUnitService.getZADPUsersByB2BUnit(b2bUnitModel);
			final List<B2BCustomerModel> customersExceptZADP = b2bUnitService.getNoneZADPUsersWithSpecifiedBusinessUnit(b2bUnitModel,
					null);
			b2bUnitDataList.add(createSearchB2BUnitData(b2bUnitModel, zadpUsersByB2BUnit, customersExceptZADP));
		}

		return b2bUnitDataList;
	}

	/**
	 * @param b2bUnitModel
	 * @param zadpUsersByB2BUnit
	 * @param customersExceptZADP
	 * @return
	 */
	private SearchB2bUnitData createSearchB2BUnitData(final B2BUnitModel b2bUnitModel,
			final List<B2BCustomerModel> zadpUsersByB2BUnit, final List<B2BCustomerModel> customersExceptZADP)
	{
		final SearchB2bUnitData data = new SearchB2bUnitData();
		final List<CustomerData> zadpcustomerData = new ArrayList<>();
		try
		{
			for (final B2BCustomerModel customer : zadpUsersByB2BUnit)
			{
				zadpcustomerData.add(b2bCustomerConverter.convert(customer));
			}

			final List<CustomerData> customerData = new ArrayList<>();
			for (final B2BCustomerModel customer : customersExceptZADP)
			{
				customerData.add(b2bCustomerConverter.convert(customer));
			}

			if (CollectionUtils.isNotEmpty(zadpcustomerData))
			{
				sortListByLastName(zadpcustomerData);
			}

			if (CollectionUtils.isNotEmpty(customerData))
			{
				sortListByLastName(customerData);
			}

			final List<CustomerData> customers = new ArrayList<>();
			customers.addAll(zadpcustomerData);
			customers.addAll(customerData);

			data.setCustomers(customers);
			data.setName(b2bUnitModel.getName());
			data.setUid(b2bUnitModel.getUid());
		}
		catch (final Exception e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}


	@Override
	public List<SearchB2bUnitData> searchB2BUnitByCustomer(final String customerNumber, final String customerName)
	{
		final List<SearchB2bUnitData> b2bUnitDataList = new ArrayList<SearchB2bUnitData>();
		for (final B2BUnitModel b2bUnitModel : getB2bUnitService().searchB2BUnitByCustomer(customerNumber, customerName))
		{
			final B2BUnitModel zadpB2BUnit = b2bUnitService.findTopLevelB2BUnit(b2bUnitModel.getPayerId());
			final List<B2BCustomerModel> zadpUsersByB2BUnit = b2bUnitService.getZADPUsersByB2BUnit(zadpB2BUnit);
			final List<B2BCustomerModel> customersExceptZADP = b2bUnitService.getNoneZADPUsersWithSpecifiedBusinessUnit(b2bUnitModel,
					null);
			b2bUnitDataList.add(createSearchB2BUnitData(b2bUnitModel, zadpUsersByB2BUnit, customersExceptZADP));
		}
		return b2bUnitDataList;
	}

	@Override
	public List<SearchB2bUnitData> searchB2BUnitByUser(final List<B2BCustomerModel> searchB2BCustomerByEmail)
	{
		final List<SearchB2bUnitData> b2bUnitDataList = new ArrayList<SearchB2bUnitData>();
		final HashMap<B2BUnitModel, List<B2BCustomerModel>> hashData = new HashMap<B2BUnitModel, List<B2BCustomerModel>>();

		for (final B2BCustomerModel b2bCustomerModel : searchB2BCustomerByEmail)
		{
			if (b2bCustomerModel.getClass().getName().equals(B2BCustomerModel.class.getName()))
			{
				final List<B2BUnitModel> zadpUnits = b2bUnitService.findCustomerTopLevelUnit(b2bCustomerModel);
				for (final B2BUnitModel zadpB2BUnit : zadpUnits)
				{
					final List<B2BCustomerModel> customers = new ArrayList<>();
					if (hashData.containsKey(zadpB2BUnit))
					{
						customers.addAll(hashData.get(zadpB2BUnit));
					}
					customers.add(b2bCustomerModel);
					hashData.put(zadpB2BUnit, customers);
				}
			}
		}
		for (final Map.Entry<B2BUnitModel, List<B2BCustomerModel>> entry : hashData.entrySet())
		{
			final List<B2BCustomerModel> zadpCustomers = new ArrayList<B2BCustomerModel>();
			final List<B2BCustomerModel> customersExceptZADP = new ArrayList<B2BCustomerModel>();
			for (final B2BCustomerModel customer : entry.getValue())
			{
				if (getB2bUnitService().findTopLevelB2BUnit(customer) != null)
				{
					zadpCustomers.add(customer);
				}
				else
				{
					customersExceptZADP.add(customer);
				}
			}
			b2bUnitDataList.add(createSearchB2BUnitData(entry.getKey(), zadpCustomers, customersExceptZADP));
		}

		return b2bUnitDataList;
	}


	/**
	 * Method that sorts customer data list alphabetically by last name
	 *
	 * @param customerData
	 */
	private void sortListByLastName(final List<CustomerData> customerData)
	{
		Collections.sort(customerData, new Comparator<CustomerData>()
		{
			@Override
			public int compare(final CustomerData o1, final CustomerData o2)
			{
				final String o1LastName = o1.getLastName() != null ? o1.getLastName() : "";
				final String o2LastName = o2.getLastName() != null ? o2.getLastName() : "";
				if (o1LastName.compareTo(o2LastName) > 0)
				{
					return 1;
				}
				else if (o1LastName.compareTo(o2LastName) < 0)
				{
					return -1;
				}
				return 0;
			}
		});

	}


	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the b2bUnitConverter
	 */
	public Converter<B2BUnitModel, B2BUnitData> getB2bUnitConverter()
	{
		return b2bUnitConverter;
	}

	/**
	 * @param b2bUnitConverter
	 *           the b2bUnitConverter to set
	 */
	public void setB2bUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		this.b2bUnitConverter = b2bUnitConverter;
	}

	/**
	 * @return the b2bCustomerService
	 */
	public SabmB2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	/**
	 * @param b2bCustomerService
	 *           the b2bCustomerService to set
	 */
	public void setB2bCustomerService(final SabmB2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	/**
	 * @return the b2bCustomerConverter
	 */
	public Converter<B2BCustomerModel, CustomerData> getB2bCustomerConverter()
	{
		return b2bCustomerConverter;
	}

	/**
	 * @param b2bCustomerConverter
	 *           the b2bCustomerConverter to set
	 */
	public void setB2bCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter)
	{
		this.b2bCustomerConverter = b2bCustomerConverter;
	}

}
