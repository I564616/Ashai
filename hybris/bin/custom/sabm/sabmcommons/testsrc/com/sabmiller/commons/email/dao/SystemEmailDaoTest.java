/**
 *
 */
package com.sabmiller.commons.email.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.sabmiller.commons.model.SystemEmailMessageModel;



/**
 *
 */
@IntegrationTest
public class SystemEmailDaoTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private SystemEmailDao systemEmailDao;

	private final String body1 = "body1";
	private final String body2 = "body2";
	private final String body3 = "body3";
	private final String body4 = "body4";
	private final String body5 = "body5";

	@Before
	public void setUp()
	{
		final SystemEmailMessageModel systemEmail1 = modelService.create(SystemEmailMessageModel.class);
		final SystemEmailMessageModel systemEmail2 = modelService.create(SystemEmailMessageModel.class);
		final SystemEmailMessageModel systemEmail3 = modelService.create(SystemEmailMessageModel.class);
		final SystemEmailMessageModel systemEmail4 = modelService.create(SystemEmailMessageModel.class);
		final SystemEmailMessageModel systemEmail5 = modelService.create(SystemEmailMessageModel.class);

		systemEmail1.setSent(false);
		systemEmail1.setBody(body1);
		systemEmail1.setReplyToAddress("test@test.com");
		systemEmail1.setSubject("test");

		systemEmail2.setSent(false);
		systemEmail2.setBody(body2);
		systemEmail2.setReplyToAddress("test@test.com");
		systemEmail2.setSubject("test");

		systemEmail3.setSent(false);
		systemEmail3.setBody(body3);
		systemEmail3.setReplyToAddress("test@test.com");
		systemEmail3.setSubject("test");

		systemEmail4.setSent(true);
		systemEmail4.setBody(body4);
		systemEmail4.setReplyToAddress("test@test.com");
		systemEmail4.setSubject("test");

		systemEmail5.setSent(true);
		systemEmail5.setBody(body5);
		systemEmail5.setReplyToAddress("test@test.com");
		systemEmail5.setSubject("test");

		modelService.save(systemEmail1);
		modelService.save(systemEmail2);
		modelService.save(systemEmail3);
		modelService.save(systemEmail4);
		modelService.save(systemEmail5);
	}


	@Test
	public void testFindSystemEmailsBySentStatus()
	{
		final List<SystemEmailMessageModel> emailsSentFalse = systemEmailDao.findSystemEmailsBySentStatus(false);
		assertTrue(emailsSentFalse.size() == 3);
		assertEquals(body1, emailsSentFalse.get(0).getBody());

		final List<SystemEmailMessageModel> emailsSentTrue = systemEmailDao.findSystemEmailsBySentStatus(true);
		assertTrue(emailsSentTrue.size() == 2);
		assertEquals(body4, emailsSentTrue.get(0).getBody());
	}

}
