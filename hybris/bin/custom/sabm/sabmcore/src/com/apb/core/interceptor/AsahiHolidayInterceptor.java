package com.apb.core.interceptor;

import com.sabmiller.core.model.HolidayModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

public class AsahiHolidayInterceptor implements PrepareInterceptor<HolidayModel>{

	@Override
	public void onPrepare(HolidayModel holidayModel, InterceptorContext arg1) throws InterceptorException {
		
			if (null != holidayModel && null != holidayModel.getDate())
			{
				holidayModel.setDate(holidayModel.getDate());
			}
		
	}

}
