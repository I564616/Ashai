package com.apb.core.ticket.strategy;

import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.enums.CsEmailRecipients;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.strategies.impl.DefaultTicketEventEmailStrategy;

import jakarta.annotation.Resource;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.apb.core.util.AsahiSiteUtil;


/**
 * ApbTicketEventEmailStrategy implementation of {@link DefaultTicketEventEmailStrategy}
 *
 * Stop Send Email for ticket
 *
 *
 */
public class ApbTicketEventEmailStrategy extends DefaultTicketEventEmailStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger("ApbTicketEventEmailStrategy");
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void sendEmailsForEvent(final CsTicketModel ticket, final CsTicketEventModel event)
	{
		if(!asahiSiteUtil.isCub())
		{
		LOG.debug("Sending Email Stop for Tickets");
		}
		else
		{
			super.sendEmailsForEvent(ticket, event);
		}
	}

	@Override
	public void sendEmailsForAssignAgentTicketEvent(final CsTicketModel ticket, final CsTicketEventModel event,
			final CsEmailRecipients recepientType)
	{
		if(!asahiSiteUtil.isCub())
		{
		LOG.debug("Sending Email Stop for Tickets");
		}
		else
		{
			super.sendEmailsForAssignAgentTicketEvent(ticket, event, recepientType);
		}
	}
}
