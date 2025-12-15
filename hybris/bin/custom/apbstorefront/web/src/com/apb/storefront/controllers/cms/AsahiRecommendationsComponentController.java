package com.apb.storefront.controllers.cms;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apb.facades.product.AsahiRecommendationFacade;
import com.apb.storefront.controllers.ControllerConstants;
import com.sabm.core.model.cms.components.AsahiRecommendationsComponentModel;
import com.sabmiller.core.enums.SmartRecommendationType;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.impl.DefaultContentSlotData;
import de.hybris.platform.commercefacades.product.data.ProductData;

@Controller("AsahiRecommendationsComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.AsahiRecommendationsComponent)
public class AsahiRecommendationsComponentController extends AbstractAcceleratorCMSComponentController<AsahiRecommendationsComponentModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsahiRecommendationsComponentController.class);

    /**
     * This enables us to know that it should have shown but it was group A, if false, then somethings wrong.
     */
    private static final String SHOW = "show";

    /**
     * null if all recommendations is null
     */
    private static final String SMART_RECOMMENDATIONS = "smartRecommendations";
    /**
     * attribute the says recommendations could potentially show, but a chance that there's no recommendation to show is possible*
     */
    private static final String RECOMMENDATION_ENABLED = "recommendationEnabled";

    private static final String COMPONENT_CONTENT_SLOT = "componentContentSlot";
    private static final String COMPONENT_POSITION = "componentPosition";

    @Resource(name = "asahiRecommendationFacade")
    private AsahiRecommendationFacade asahiRecommendationFacade;

    @Resource(name = "b2bCommerceUnitService")
    private B2BCommerceUnitService b2bCommerceUnitService;

    protected void fillModel(HttpServletRequest request, Model model, AsahiRecommendationsComponentModel component) {
        final B2BUnitModel b2BUnit = b2bCommerceUnitService.getParentUnit();
        if(b2BUnit == null){
            model.addAttribute(SHOW,false);
            return;
        }
        model.addAttribute(SHOW, true);
        model.addAttribute(RECOMMENDATION_ENABLED,true);

        final Map<SmartRecommendationType, ProductData> sgaRecommendations = asahiRecommendationFacade.getSgaProductRecommendations();
        model.addAttribute(SMART_RECOMMENDATIONS, hasRecommendation(sgaRecommendations)?sgaRecommendations:null);

        getContentSlotName(request, component, model);
    }

    protected boolean hasRecommendation(final Map<SmartRecommendationType, ProductData> smartRecommendations){
       return smartRecommendations.values().stream().filter(productData-> productData != null).findAny().isPresent();
    }

    protected void getContentSlotName (final HttpServletRequest request, final AsahiRecommendationsComponentModel component, Model model) {
        final List<String> contentSlotNames = getCmsPageContextService().getCmsPageRequestContextData(request).getPositionToSlot().values().stream()
                .map(v -> ((DefaultContentSlotData)v).getUid())
                .collect(Collectors.toList());
        Optional<ContentSlotModel> contentSlotModel = component.getSlots().stream().filter(c -> contentSlotNames.contains(c.getUid())).findFirst();
        if (contentSlotModel.isPresent()) {
            model.addAttribute(COMPONENT_CONTENT_SLOT, contentSlotModel.get().getUid());
            model.addAttribute(COMPONENT_POSITION, contentSlotModel.get().getCmsComponents().indexOf(component));
        }
    }
}
