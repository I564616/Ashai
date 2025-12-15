package com.sabmiller.services;

import com.sabmiller.model.EnablePortalUserModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zul.Messagebox;

import jakarta.annotation.Resource;
import java.util.Objects;

public class EnableUserService {

    private static final Logger LOGGER = Logger.getLogger(EnableUserService.class);

    @Resource
    private ModelService modelService;

    @Resource
    private UserService userService;

    @Resource
    private ConfigurationService configurationService;

    public void perform(final EnablePortalUserModel object) {
        if (Objects.nonNull(object)) {
            try {
                final UserModel userModel = object.getUser();
                final String password = object.getPassword();

                if (StringUtils.isNotEmpty(password))
                    userService.setPassword(userModel, password, configurationService.getConfiguration().getString("default.password.encoding", "pbkdf2"));

                userModel.setLoginDisabled(false);

                modelService.save(userModel);
                modelService.refresh(userModel);

                if (StringUtils.isNotEmpty(object.getPassword()))
                    Messagebox.show("Password reset is successful, user is enabled!", "SUCCESSFUL", 0, Messagebox.INFORMATION);
                else
                    Messagebox.show("User is enabled!", "User Enabled", 0, Messagebox.INFORMATION);
            } catch (Exception e) {
                LOGGER.error("Error in resetting user -> ", e);
                Messagebox.show("There is some technical issue, we are not able to enable or reset password at the moment", "FAILED", 0, Messagebox.ERROR);
            }
        }
    }
}
