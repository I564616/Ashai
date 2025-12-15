/**
 *
 */
package com.sabmiller.core.customer.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMUserAccessHistoryModel;


/**
 * @author bonnie
 *
 */
public class SABMUserAccessHistroyDaoTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMUserAccessHistroyDaoTest.class);

	@Resource
	private SABMUserAccessHistoryDao userAccessHistoryDao;
	@Resource
	private ModelService modelService;

	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel1;
	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel2;
	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel3;
	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel4;
	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel5;
	private SABMUserAccessHistoryModel sabmUserAccessHistoryModel6;

	@Before
	public void setUp()
	{
		sabmUserAccessHistoryModel1 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel1.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel1);
		sabmUserAccessHistoryModel2 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel2.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel2);
		sabmUserAccessHistoryModel3 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel3.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel3);
		sabmUserAccessHistoryModel4 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel4.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel4);
		sabmUserAccessHistoryModel5 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel5.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel5);
		sabmUserAccessHistoryModel6 = new SABMUserAccessHistoryModel();
		sabmUserAccessHistoryModel6.setUid("user1");
		modelService.save(sabmUserAccessHistoryModel6);
	}

	@Test
	public void testFindAllOldUserAccessHistory()
	{
		assertNotNull("findAllBaseSites returned null", userAccessHistoryDao.findOldUserAccessHistory(new Date(), 0));

		System.out.println(userAccessHistoryDao.findOldUserAccessHistory(new Date(), 0).size());
	}

	@Test
	public void testFindThreeOldUserAccessHistory()
	{
		final List<SABMUserAccessHistoryModel> searchResult = userAccessHistoryDao.findOldUserAccessHistory(new Date(), 3);
		LOG.info("====================: " + searchResult.size());
		assertEquals("FindAllOldUserAccessHistory not three", 3, searchResult.size());
		assertNotNull("FindAllOldUserAccessHistory returned null", userAccessHistoryDao.findOldUserAccessHistory(new Date(), 3));
	}
}
