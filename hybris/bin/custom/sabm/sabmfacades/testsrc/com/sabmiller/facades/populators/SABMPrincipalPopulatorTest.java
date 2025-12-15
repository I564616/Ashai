/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SABMPrincipalPopulatorTest
{
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@InjectMocks
	private SABMPrincipalPopulator principalPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulator()
	{
		final B2BCustomerModel mockPrincipalModel = mock(B2BCustomerModel.class);
		final PrincipalData principalData = new PrincipalData();
		final PrincipalGroupModel source = mock(PrincipalGroupModel.class);
		given(source.getUid()).willReturn("uid");
		given(source.getName()).willReturn("name");
		given(source.getDisplayName()).willReturn("name");
		final Set<PrincipalGroupModel> set = new HashSet<PrincipalGroupModel>();
		set.add(source);
		given(mockPrincipalModel.getGroups()).willReturn(set);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(b2bUnitService.findTopLevelB2BUnit(mockPrincipalModel)).willReturn(null);

		
		principalPopulator.populate(mockPrincipalModel, principalData);
		Assert.assertEquals("uid", principalData.getGroups().iterator().next().getUid());
		Assert.assertEquals("name", principalData.getGroups().iterator().next().getName());
	}
}
