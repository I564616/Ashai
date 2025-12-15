/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BUnitModel;
//import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.sabm.core.model.ConfirmEnabledDealProcessModel;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.user.EmployeeData;


/**
 * The context of confirm enabled deals, need to be update in the future stories
 *
 */
public class ConfirmEnabledDealEmailContext extends AbstractEmailContext<ConfirmEnabledDealProcessModel>
{
	private String behaviourRequirements;
	private List<String> activatedDeals;
	private List<String> deactivatedDeals;
	private B2BUnitData b2bUnitData;
	private EmployeeData employeeData;
	private String date;

	private String primaryAdminStatus;

	public static final String DATE_SAFE_FORMAT = "dd/MM/yyyy";

	private Converter<UserModel, EmployeeData> employeeConverter;
	private Converter<DealModel, DealJson> dealJsonConverter;
	private DealsService dealsService;

	/** The b2 b unit converter. */
	@Resource(name = "b2bUnitConverter")
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;

	@Override
	public void init(final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(confirmEnabledDealProcessModel, emailPageModel);
		this.behaviourRequirements = confirmEnabledDealProcessModel.getBehaviourRequirements();
		this.b2bUnitData = b2BUnitConverter.convert(getB2bUnit(confirmEnabledDealProcessModel));
		this.activatedDeals = confirmEnabledDealProcessModel.getActivatedDealTitles();
		this.deactivatedDeals = confirmEnabledDealProcessModel.getDeactivatedDealTitles();
		this.date = DateFormatUtils.format(confirmEnabledDealProcessModel.getCreationtime(), DATE_SAFE_FORMAT);
		this.employeeData = getEmployeeConverter().convert(confirmEnabledDealProcessModel.getFromUser());
		//		this.customerData = getCustomerConverter().convert(confirmEnabledDealProcessModel.getCustomer());
		this.primaryAdminStatus = confirmEnabledDealProcessModel.getPrimaryAdminStatus();

		//this is just for pass the validate when generate the email
		put(EMAIL, confirmEnabledDealProcessModel.getToEmails().get(0));
	}

	protected B2BUnitModel getB2bUnit(final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel)
	{
		return confirmEnabledDealProcessModel.getEmailUnit();
	}

	@Override
	protected BaseSiteModel getSite(final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel)
	{
		return confirmEnabledDealProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel)
	{
		return confirmEnabledDealProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final ConfirmEnabledDealProcessModel confirmEnabledDealProcessModel)
	{
		return confirmEnabledDealProcessModel.getLanguage();
	}

	/**
	 * @return the behaviourRequirements
	 */
	public String getBehaviourRequirements()
	{
		return behaviourRequirements;
	}

	/**
	 * @param behaviourRequirements
	 *           the behaviourRequirements to set
	 */
	public void setBehaviourRequirements(final String behaviourRequirements)
	{
		this.behaviourRequirements = behaviourRequirements;
	}

	/**
	 * @return the activatedDeals
	 */
	public List<String> getActivatedDeals()
	{
		return activatedDeals;
	}

	/**
	 * @param activatedDeals
	 *           the activatedDeals to set
	 */
	public void setActivatedDeals(final List<String> activatedDeals)
	{
		this.activatedDeals = activatedDeals;
	}

	/**
	 * @return the deactivatedDeals
	 */
	public List<String> getDeactivatedDeals()
	{
		return deactivatedDeals;
	}

	/**
	 * @param deactivatedDeals
	 *           the deactivatedDeals to set
	 */
	public void setDeactivatedDeals(final List<String> deactivatedDeals)
	{
		this.deactivatedDeals = deactivatedDeals;
	}

	/**
	 * @return the b2bUnitData
	 */
	public B2BUnitData getB2bUnitData()
	{
		return b2bUnitData;
	}

	/**
	 * @param b2bUnitData
	 *           the b2bUnitData to set
	 */
	public void setB2bUnitData(final B2BUnitData b2bUnitData)
	{
		this.b2bUnitData = b2bUnitData;
	}

	public Converter<UserModel, EmployeeData> getEmployeeConverter()
	{
		return employeeConverter;
	}

	public void setEmployeeConverter(final Converter<UserModel, EmployeeData> employeeConverter)
	{
		this.employeeConverter = employeeConverter;
	}

	public Converter<DealModel, DealJson> getDealJsonConverter()
	{
		return dealJsonConverter;
	}

	public void setDealJsonConverter(final Converter<DealModel, DealJson> dealJsonConverter)
	{
		this.dealJsonConverter = dealJsonConverter;
	}

	public DealsService getDealsService()
	{
		return dealsService;
	}

	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	/**
	 * @return the employeeData
	 */
	public EmployeeData getEmployeeData()
	{
		return employeeData;
	}

	/**
	 * @param employeeData
	 *           the employeeData to set
	 */
	public void setEmployeeData(final EmployeeData employeeData)
	{
		this.employeeData = employeeData;
	}

	/**
	 * @return the date
	 */
	public String getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *           the date to set
	 */
	public void setDate(final String date)
	{
		this.date = date;
	}

	/**
	 * @return the primaryAdminStatus
	 */
	public String getPrimaryAdminStatus()
	{
		return primaryAdminStatus;
	}

	/**
	 * @param primaryAdminStatus
	 *           the primaryAdminStatus to set
	 */
	public void setPrimaryAdminStatus(final String primaryAdminStatus)
	{
		this.primaryAdminStatus = primaryAdminStatus;
	}

	/**
	 * @return the b2BUnitConverter
	 */
	public Converter<B2BUnitModel, B2BUnitData> getB2BUnitConverter()
	{
		return b2BUnitConverter;
	}

	/**
	 * @param b2bUnitConverter
	 *           the b2BUnitConverter to set
	 */
	public void setB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		b2BUnitConverter = b2bUnitConverter;
	}
}
