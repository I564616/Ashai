/**
 *
 */
package com.sabmiller.facades.deal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.deal.impl.SABMRepDrivenDealConditionStatusFacadeImpl;
import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;


/**
 * @author xue.zeng
 *
 */
public class SABMRepDrivenDealConditionStatusFacadeTest
{
	@InjectMocks
	private SABMRepDrivenDealConditionStatusFacadeImpl dealConditionStatusFacade;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private SabmB2BUnitService b2bUnitService;

	private List<RepDrivenDealConditionData> repDrivenDealConditions;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		repDrivenDealConditions = new ArrayList<RepDrivenDealConditionData>();
		final RepDrivenDealConditionData conditionData = new RepDrivenDealConditionData();
		conditionData.setDealConditionNumber("deal_11");
		conditionData.setStatus(Boolean.TRUE.booleanValue());
		repDrivenDealConditions.add(conditionData);
	}

	@Test
	public void testSaveRepDrivenDealConditionStatus()
	{
		final String uid = "123";

		final UserModel user = mock(UserModel.class);

		final B2BCustomerModel customer = new B2BCustomerModel();
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		customer.setDefaultB2BUnit(b2bUnit);

		given(userService.getCurrentUser()).willReturn(user);
		given(b2bCommerceUnitService.getCustomerForUid(uid)).willReturn(customer);
		given(b2bUnitService.getUnitForUid(uid)).willReturn(b2bUnit);

		dealConditionStatusFacade.saveRepDrivenDealConditionStatus(uid, repDrivenDealConditions);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveRepDrivenDealConditionStatusAssignedToIsNull()
	{
		dealConditionStatusFacade.saveRepDrivenDealConditionStatus(null, repDrivenDealConditions);
	}
}
