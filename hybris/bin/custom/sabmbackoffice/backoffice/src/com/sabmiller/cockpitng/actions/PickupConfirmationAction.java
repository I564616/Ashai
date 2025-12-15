package com.sabmiller.cockpitng.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;

import jakarta.annotation.Resource;

public class PickupConfirmationAction implements CockpitAction {

    @Resource private BusinessProcessService businessProcessService;

    @Resource private ObjectFacade objectFacade;

    public ActionResult perform(final ActionContext ctx) {
        final Object data = ctx.getData();
        if(data instanceof ConsignmentModel) {
            try {
                ((ConsignmentModel) data).setStatus(ConsignmentStatus.PICKUP_COMPLETE);
                objectFacade.save(data);
                for (final ConsignmentProcessModel process : ((ConsignmentModel) data).getConsignmentProcesses()) {
                    businessProcessService.triggerEvent(process.getCode() + "_ConsignmentPickup");
                }
            } catch(final ObjectSavingException e) {
                throw new RuntimeException(e);
            }

            return new ActionResult(ActionResult.SUCCESS);
        }

        return new ActionResult(ActionResult.ERROR);
    }

    public boolean canPerform(final ActionContext ctx) {
        return (ctx.getData() instanceof ConsignmentModel) && !((ConsignmentModel) ctx.getData()).getStatus()
                .equals(ConsignmentStatus.PICKUP_COMPLETE);
    }

    public boolean needsConfirmation(final ActionContext ctx) {
        return false;
    }

    public String getConfirmationMessage(final ActionContext ctx) {
        return "Are you sure?";
    }

}