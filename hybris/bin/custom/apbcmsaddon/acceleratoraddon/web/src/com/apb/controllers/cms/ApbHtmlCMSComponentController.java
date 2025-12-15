package com.apb.controllers.cms;

import com.apb.core.util.AsahiAdhocCoreUtil;
import com.apb.model.ApbHtmlCMSComponentModel;
import com.apb.model.AsahiBannerComponentModel;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller("ApbHtmlCMSComponentController")
@RequestMapping(value = "/view/ApbHtmlCMSComponentController")
public class ApbHtmlCMSComponentController extends AbstractCMSAddOnComponentController<AbstractCMSComponentModel> {

    private static final String DEFAULT_VALUE = "100%";

    @Autowired
    private I18NService i18NService;

    @Autowired
    private AsahiAdhocCoreUtil adhocCoreUtil;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel absComponent) {

        final ApbHtmlCMSComponentModel component = adhocCoreUtil.validateAndGetModel(absComponent);
        if (null != component) {
            String htmlContent = component.getContent(i18NService.getCurrentLocale());
            model.addAttribute("htmlContent", HtmlUtils.htmlUnescape(htmlContent));
            model.addAttribute("animatedEffect", component.getAnimation().getCode());
            model.addAttribute("timeout", component.getAnimationDelay());
            model.addAttribute("duration", component.getAnimationDuration());
            model.addAttribute("useCustomized", component.getUseCustomized());
            model.addAttribute("banners", filterValidBanners(component.getBanners()));
            model.addAttribute("desktop_width", getDefaults(component.getDesktop_width()));
            model.addAttribute("desktop_height", getDefaults(component.getDesktop_height()));
            model.addAttribute("mobile_width", getDefaults(component.getMobile_width()));
            model.addAttribute("mobile_height", getDefaults(component.getMobile_height()));
        }
    }

    private String getDefaults(String attr) {
        if (null != attr && attr.endsWith("px")) {
            if (isValid(attr, 2)) {
                return attr;
            } else {
                return DEFAULT_VALUE;
            }
        } else if (null != attr && attr.endsWith("%")) {
            if (isValid(attr, 1)) {
                return attr;
            } else {
                return DEFAULT_VALUE;
            }
        }

        return DEFAULT_VALUE;
    }

    private boolean isValid(String content, int minusIndex) {
        try {
            Integer.parseInt(content.substring(0, content.length() - minusIndex));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<AsahiBannerComponentModel> filterValidBanners(final List<AsahiBannerComponentModel> banners) {
        List<AsahiBannerComponentModel> filteredBanners = new ArrayList<>();
        filteredBanners = banners.stream().filter(banner -> (banner.getVisible() && null != banner.getMedia(i18NService.getCurrentLocale()))).collect(Collectors.toList());
        return filteredBanners;
    }
}
