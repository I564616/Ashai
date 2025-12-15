/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.sabmiller.core.b2b.services.impl.DefaultSABMDeliveryDateCutOffService;
import com.sabmiller.core.enums.DeliveryModeType;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.model.ExcludedDeliveryDayModel;
import com.sabmiller.core.model.PlantCutOffModel;
import com.sabmiller.core.model.PlantDeliveryDayModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SabmDeliveryModeMappingModel;
import com.sabmiller.core.model.TimeZoneModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultSABMDeliveryDateCutOffServiceTest
{
	@InjectMocks
	private final DefaultSABMDeliveryDateCutOffService defaultSABMDeliveryDateCutOffService = new DefaultSABMDeliveryDateCutOffService();

	@Mock
	private BaseStoreService baseStoreService;
	@Mock(name = "sabmDeliveryMethodDao")
	private GenericDao<SabmDeliveryModeMappingModel> genericDao;
	@Mock(name = "caldendarDeliveryDayMap")
	private Map<Integer, Integer> caldendarDeliveryDayMap;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private PlantModel plant;
	@Mock
	private TimeZoneModel timeZone;
	@Mock
	private PlantCutOffModel plantCutOffModel1, plantCutOffModel2, plantCutOffModel3, plantCutOffModel4, plantCutOffModel5,
			plantCutOffModel6, plantCutOffModel7;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetActiveDeliveryDates()
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		final TimeZoneModel timeZoneModel = new TimeZoneModel();
		timeZoneModel.setCode("Australia/Melbourne");
		baseStore.setTimeZone(timeZoneModel);
		final UnloadingPointModel unloadingPointModel = new UnloadingPointModel();
		unloadingPointModel.setCode("PACK-MTWTHF-2");
		final Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		final Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList("130000","180000","080000","120000"));
		map.put("6", set);
		map.put("1", set);
		map.put("2", set);
		map.put("3", set);
		map.put("4", set);
		map.put("5", set);
		unloadingPointModel.setMap(map);
		final SabmDeliveryModeMappingModel deliveryModeMappingModel = new SabmDeliveryModeMappingModel();
		deliveryModeMappingModel.setMode(2);
		deliveryModeMappingModel.setMethod(DeliveryModeType.CUSTOMER_DELIVERY);
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setUnloadingPoints(Arrays.asList(unloadingPointModel));

		final PlantModel plantModel = new PlantModel();
		final PlantCutOffModel plantCutOffModel = new PlantCutOffModel();
		plantCutOffModel.setDayOfWeek(1);
		plantCutOffModel.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel2 = new PlantCutOffModel();
		plantCutOffModel2.setDayOfWeek(2);
		plantCutOffModel2.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel3 = new PlantCutOffModel();
		plantCutOffModel3.setDayOfWeek(3);
		plantCutOffModel3.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel4 = new PlantCutOffModel();
		plantCutOffModel4.setDayOfWeek(4);
		plantCutOffModel4.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel5 = new PlantCutOffModel();
		plantCutOffModel5.setDayOfWeek(5);
		plantCutOffModel5.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel6 = new PlantCutOffModel();
		plantCutOffModel6.setDayOfWeek(6);
		plantCutOffModel6.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel7 = new PlantCutOffModel();
		plantCutOffModel7.setDayOfWeek(7);
		plantCutOffModel7.setLeadTime(3);
		plantModel.setCutOffs(Arrays.asList(plantCutOffModel, plantCutOffModel2, plantCutOffModel3, plantCutOffModel4,
				plantCutOffModel5, plantCutOffModel6, plantCutOffModel7));
		final PlantDeliveryDayModel deliveryDayModel = new PlantDeliveryDayModel();
		deliveryDayModel.setDayOfWeek(1);
		final PlantDeliveryDayModel deliveryDayModel1 = new PlantDeliveryDayModel();
		deliveryDayModel1.setDayOfWeek(2);
		final PlantDeliveryDayModel deliveryDayModel2 = new PlantDeliveryDayModel();
		deliveryDayModel2.setDayOfWeek(3);
		final PlantDeliveryDayModel deliveryDayModel3 = new PlantDeliveryDayModel();
		deliveryDayModel3.setDayOfWeek(4);
		final PlantDeliveryDayModel deliveryDayModel4 = new PlantDeliveryDayModel();
		deliveryDayModel4.setDayOfWeek(5);

		plantModel.setDeliveryDays(
				Arrays.asList(deliveryDayModel, deliveryDayModel1, deliveryDayModel2, deliveryDayModel3, deliveryDayModel4));
		b2bUnit.setPlant(plantModel);

		given(genericDao.find()).willReturn(Arrays.asList(deliveryModeMappingModel));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(1))).willReturn(Integer.valueOf(7));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(2))).willReturn(Integer.valueOf(1));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(3))).willReturn(Integer.valueOf(2));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(4))).willReturn(Integer.valueOf(3));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(5))).willReturn(Integer.valueOf(4));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(6))).willReturn(Integer.valueOf(5));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(7))).willReturn(Integer.valueOf(6));
		given(baseStoreService.getBaseStoreForUid("sabmStore")).willReturn(baseStore);
		ReflectionTestUtils.setField(defaultSABMDeliveryDateCutOffService, "maxDayCalendar", 15);

		final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData = defaultSABMDeliveryDateCutOffService
				.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, true);

		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData);
		Assert.assertEquals(PackType.PACK, deliveryModePackTypeDeliveryDatesData.get(0).getPackType());
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData.get(0).getDateList());

		final Date date = new Date();
		date.setTime(deliveryModePackTypeDeliveryDatesData.get(0).getDateList().get(0));
		final ExcludedDeliveryDayModel excludedDeliveryDayModel = new ExcludedDeliveryDayModel();
		excludedDeliveryDayModel.setCalendarDay(date);
		b2bUnit.setExcludedDeliveryDates(Stream.of(excludedDeliveryDayModel).collect(Collectors.toSet()));

		final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData2 = defaultSABMDeliveryDateCutOffService
				.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, true);
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData);
		Assert.assertEquals(PackType.PACK, deliveryModePackTypeDeliveryDatesData.get(0).getPackType());
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData.get(0).getDateList());

		Assert.assertFalse(deliveryModePackTypeDeliveryDatesData2.get(0).getDateList()
				.contains(deliveryModePackTypeDeliveryDatesData.get(0).getDateList().get(0)));

	}

	@Test
	public void testGetInActiveDeliveryDates()
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		final TimeZoneModel timeZoneModel = new TimeZoneModel();
		timeZoneModel.setCode("Australia/Melbourne");
		baseStore.setTimeZone(timeZoneModel);
		final UnloadingPointModel unloadingPointModel = new UnloadingPointModel();
		unloadingPointModel.setCode("PACK-MTWTHF-2");
		final Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		final Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList("130000", "180000", "080000", "120000"));
		map.put("6", set);
		map.put("1", set);
		map.put("2", set);
		map.put("3", set);
		map.put("4", set);
		map.put("5", set);
		unloadingPointModel.setMap(map);
		final SabmDeliveryModeMappingModel deliveryModeMappingModel = new SabmDeliveryModeMappingModel();
		deliveryModeMappingModel.setMode(2);
		deliveryModeMappingModel.setMethod(DeliveryModeType.CUSTOMER_DELIVERY);
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setUnloadingPoints(Arrays.asList(unloadingPointModel));

		final PlantModel plantModel = new PlantModel();
		final PlantCutOffModel plantCutOffModel = new PlantCutOffModel();
		plantCutOffModel.setDayOfWeek(1);
		plantCutOffModel.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel2 = new PlantCutOffModel();
		plantCutOffModel2.setDayOfWeek(2);
		plantCutOffModel2.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel3 = new PlantCutOffModel();
		plantCutOffModel3.setDayOfWeek(3);
		plantCutOffModel3.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel4 = new PlantCutOffModel();
		plantCutOffModel4.setDayOfWeek(4);
		plantCutOffModel4.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel5 = new PlantCutOffModel();
		plantCutOffModel5.setDayOfWeek(5);
		plantCutOffModel5.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel6 = new PlantCutOffModel();
		plantCutOffModel6.setDayOfWeek(6);
		plantCutOffModel6.setLeadTime(3);
		final PlantCutOffModel plantCutOffModel7 = new PlantCutOffModel();
		plantCutOffModel7.setDayOfWeek(7);
		plantCutOffModel7.setLeadTime(3);
		plantModel.setCutOffs(Arrays.asList(plantCutOffModel, plantCutOffModel2, plantCutOffModel3, plantCutOffModel4,
				plantCutOffModel5, plantCutOffModel6, plantCutOffModel7));
		final PlantDeliveryDayModel deliveryDayModel = new PlantDeliveryDayModel();
		deliveryDayModel.setDayOfWeek(1);
		final PlantDeliveryDayModel deliveryDayModel1 = new PlantDeliveryDayModel();
		deliveryDayModel1.setDayOfWeek(2);
		final PlantDeliveryDayModel deliveryDayModel2 = new PlantDeliveryDayModel();
		deliveryDayModel2.setDayOfWeek(3);
		final PlantDeliveryDayModel deliveryDayModel3 = new PlantDeliveryDayModel();
		deliveryDayModel3.setDayOfWeek(4);
		final PlantDeliveryDayModel deliveryDayModel4 = new PlantDeliveryDayModel();
		deliveryDayModel4.setDayOfWeek(5);
		plantModel.setDeliveryDays(
				Arrays.asList(deliveryDayModel, deliveryDayModel1, deliveryDayModel2, deliveryDayModel3, deliveryDayModel4));
		b2bUnit.setPlant(plantModel);

		given(genericDao.find()).willReturn(Arrays.asList(deliveryModeMappingModel));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(1))).willReturn(Integer.valueOf(7));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(2))).willReturn(Integer.valueOf(1));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(3))).willReturn(Integer.valueOf(2));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(4))).willReturn(Integer.valueOf(3));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(5))).willReturn(Integer.valueOf(4));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(6))).willReturn(Integer.valueOf(5));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(7))).willReturn(Integer.valueOf(6));
		given(baseStoreService.getBaseStoreForUid("sabmStore")).willReturn(baseStore);
		ReflectionTestUtils.setField(defaultSABMDeliveryDateCutOffService, "maxDayCalendar", 15);

		final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData = defaultSABMDeliveryDateCutOffService
				.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, false);

		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData);
		Assert.assertEquals(PackType.PACK, deliveryModePackTypeDeliveryDatesData.get(0).getPackType());
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData.get(0).getDateList());

		final Date date = new Date();
		final ExcludedDeliveryDayModel excludedDeliveryDayModel = new ExcludedDeliveryDayModel();
		excludedDeliveryDayModel.setCalendarDay(date);
		b2bUnit.setExcludedDeliveryDates(Stream.of(excludedDeliveryDayModel).collect(Collectors.toSet()));

		final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData2 = defaultSABMDeliveryDateCutOffService
				.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, false);
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData);
		Assert.assertEquals(PackType.PACK, deliveryModePackTypeDeliveryDatesData.get(0).getPackType());
		Assert.assertNotNull(deliveryModePackTypeDeliveryDatesData.get(0).getDateList());

		Assert.assertFalse(deliveryModePackTypeDeliveryDatesData2.get(0).getDateList()
				.contains(date.getTime()));

	}

	@Test
	public void getCutOffTimeforCalendarToDisplayTest()
	{
		given(b2bUnitModel.getPlant()).willReturn(plant);
		given(plantCutOffModel1.getDayOfWeek()).willReturn(1);
		given(plantCutOffModel1.getLeadTime()).willReturn(3);
		given(plantCutOffModel2.getDayOfWeek()).willReturn(2);
		given(plantCutOffModel2.getLeadTime()).willReturn(3);
		given(plantCutOffModel3.getDayOfWeek()).willReturn(3);
		given(plantCutOffModel3.getLeadTime()).willReturn(3);
		given(plantCutOffModel4.getDayOfWeek()).willReturn(4);
		given(plantCutOffModel4.getLeadTime()).willReturn(3);
		given(plantCutOffModel5.getDayOfWeek()).willReturn(5);
		given(plantCutOffModel5.getLeadTime()).willReturn(3);
		given(plantCutOffModel6.getDayOfWeek()).willReturn(6);
		given(plantCutOffModel6.getLeadTime()).willReturn(3);
		given(plantCutOffModel7.getDayOfWeek()).willReturn(7);
		given(plantCutOffModel7.getLeadTime()).willReturn(3);
		given(plant.getCutOffs()).willReturn(Arrays.asList(plantCutOffModel1, plantCutOffModel2, plantCutOffModel3,
				plantCutOffModel4, plantCutOffModel5, plantCutOffModel6, plantCutOffModel7));

		given(caldendarDeliveryDayMap.get(Integer.valueOf(1))).willReturn(Integer.valueOf(7));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(2))).willReturn(Integer.valueOf(1));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(3))).willReturn(Integer.valueOf(2));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(4))).willReturn(Integer.valueOf(3));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(5))).willReturn(Integer.valueOf(4));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(6))).willReturn(Integer.valueOf(5));
		given(caldendarDeliveryDayMap.get(Integer.valueOf(7))).willReturn(Integer.valueOf(6));

		given(plantCutOffModel1.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel2.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel3.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel4.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel5.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel6.getTimeZone()).willReturn(timeZone);
		given(plantCutOffModel7.getTimeZone()).willReturn(timeZone);

		given(timeZone.getCode()).willReturn("timeZoneCode");
		Config.setParameter("plant.cutoff.time.pattern", "HHmm");
		Config.setParameter("plant.cutoff.timeZones.toStoreTimeZone", "");

		given(plantCutOffModel1.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel2.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel3.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel4.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel5.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel6.getCutOffTime()).willReturn("1200");
		given(plantCutOffModel7.getCutOffTime()).willReturn("1200");
		assertNotNull(defaultSABMDeliveryDateCutOffService.getCutOffTimeforCalendarToDisplay(b2bUnitModel, new Date()));
	}
}
