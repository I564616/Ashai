/**
 *
 */
package com.sabmiller.core.cdlvalue.service;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabm.core.model.CDLValueModel;
import com.sabmiller.core.cdlvalue.dao.SabmCDLValueDao;


/**
 * @author GQ485VQ
 *
 */
public class DefaultSabmCDLValueServiceTest
{
	@InjectMocks
	DefaultSabmCDLValueService defaultSabmCDLValueService;

	private static final String CONTAINER_TYPE = "MockContainer";
	private static final String PRESENTATION = "2X3X1";
	private static final String MOCK_UID_CUST = "MockUidCustomer";
	private static final String MOCK_UID_UNIT = "MockUid";
	private static final String MOCK_ISOCODE = "MockISOCode";

	@Mock
	UserService userService;
	@Mock
	SabmCDLValueDao sabmCDLValueDao;
	@Mock
	AddressModel addressModel;
	@Mock
	RegionModel regionModel;
	@Mock
	B2BUnitModel unit;
	@Mock
	B2BCustomerModel user;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(user.getDefaultB2BUnit()).thenReturn(unit);
		Mockito.when(unit.getDefaultShipTo()).thenReturn(addressModel);
		Mockito.when(addressModel.getRegion()).thenReturn(regionModel);
		Mockito.when(regionModel.getIsocode()).thenReturn(MOCK_ISOCODE);

	}

	@Test
	public void getCDLPriceTest()
	{
		Mockito.when(sabmCDLValueDao.getCDLValueModel(MOCK_ISOCODE, CONTAINER_TYPE))
				.thenReturn(createResult(MOCK_ISOCODE, CONTAINER_TYPE, PK.fromLong(2)));

		final BigDecimal cdlPrice = defaultSabmCDLValueService.getCDLPrice(CONTAINER_TYPE, PRESENTATION);
		Assert.assertNotNull(cdlPrice);
	}

	protected Optional<CDLValueModel> createResult(final String location, final String containerType, final PK pk)
	{

		final CDLValueModel cdlValueModel = new CDLValueModel();
		cdlValueModel.setValue(4.0);
		cdlValueModel.setLocation(location);
		cdlValueModel.setContainerType(containerType);
		return Optional.of(cdlValueModel);
	}

}
