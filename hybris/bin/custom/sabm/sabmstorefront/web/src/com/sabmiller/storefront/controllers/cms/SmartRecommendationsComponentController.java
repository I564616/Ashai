package com.sabmiller.storefront.controllers.cms;

import com.google.common.base.Stopwatch;
import com.sabm.core.model.cms.components.SmartRecommendationsComponentModel;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.RecommendationGroupType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.impl.DefaultContentSlotData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller("SmartRecommendationsComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SmartRecommendationsComponent)
public class SmartRecommendationsComponentController extends AbstractCMSComponentController<SmartRecommendationsComponentModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartRecommendationsComponentController.class);

    /**
     * This enables us to know that it should have shown but it was group A, if false, then somethings wrong.
     */
    private static final String SHOW = "show";

    /**
     * null if all recommendations is null
     */
    private static final String SMART_RECOMMENDATIONS = "smartRecommendations";

    /**
     * The group of course, too obvious isn't it?
     */
    private static final String SMART_RECOMMENDATION_GROUP = "smartRecommendationGroup";

    /**
     * attribute the says recommendations could potentially show, but a chance that there's no recommendation to show is possible*
     */
    private static final String RECOMMENDATION_ENABLED = "recommendationEnabled";

    private static final String COMPONENT_CONTENT_SLOT = "componentContentSlot";
    private static final String COMPONENT_POSITION = "componentPosition";

    private static final RecommendationGroupType DEFAULT_RECOMMENDATION_GROUP_TYPE = RecommendationGroupType.A;

    @Resource(name = "sabmRecommendationFacade")
    private SABMRecommendationFacade sabmRecommendationFacade;

    @Resource(name = "b2bCommerceUnitService")
    private B2BCommerceUnitService b2bCommerceUnitService;

    protected void fillModel(HttpServletRequest request, Model model, SmartRecommendationsComponentModel component) {

        final boolean debug = LOGGER.isDebugEnabled();

        final Stopwatch stopwatch = debug?Stopwatch.createStarted():null;

        final B2BUnitModel b2BUnit = b2bCommerceUnitService.getParentUnit();
        if(b2BUnit == null){
            model.addAttribute(SHOW,false);
            return;
        }

        final RecommendationGroupType recommendationGroupType = getRecommendationGroupType(b2BUnit);
        model.addAttribute(SMART_RECOMMENDATION_GROUP,recommendationGroupType);

        if(RecommendationGroupType.A.equals(recommendationGroupType)){
            model.addAttribute(SHOW, true);
            model.addAttribute(RECOMMENDATION_ENABLED,false);
            return;
        }

        model.addAttribute(SHOW, true);
        model.addAttribute(RECOMMENDATION_ENABLED,true);

        final Map<SmartRecommendationType, Optional<ProductData>> smartRecommendations = sabmRecommendationFacade.calculateSmartRecommendations();
        model.addAttribute(SMART_RECOMMENDATIONS, hasRecommendation(smartRecommendations)?smartRecommendations:null);

        getContentSlotName(request, component, model);

        if(debug){
            logDebug(stopwatch);
        }
    }

    protected static void logDebug(final Stopwatch stopwatch){

        if(stopwatch == null){
            return;
        }

        LOGGER.debug(String.format("SmartRecommendation Finished in [%s]",stopwatch.toString()));
    }

    protected boolean hasRecommendation(final Map<SmartRecommendationType, Optional<ProductData>> smartRecommendations){
       return smartRecommendations.values().stream().filter(Optional::isPresent).findAny().isPresent();
    }

    protected RecommendationGroupType getRecommendationGroupType(final B2BUnitModel b2BUnit){
        final RecommendationGroupType recommendationGroupType = b2BUnit.getRecommendationGroup();
        if(recommendationGroupType == null){
            return DEFAULT_RECOMMENDATION_GROUP_TYPE;
        }

        return recommendationGroupType;
    }

    protected void getContentSlotName (final HttpServletRequest request, final SmartRecommendationsComponentModel component, Model model) {
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
