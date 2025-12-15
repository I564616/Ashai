/**
 *
 */
package com.sabm.core.analytics;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.request.RequestContextHolder;

import com.segment.analytics.Analytics;
import com.segment.analytics.messages.IdentifyMessage;

/**
 * @author ramsatish.jagajyothi
 *
 */
public class SegmentAnalyticsUtils
{



	private static String writeKey = Config.getString("segment.analytics.writekey", "dibKTnfWHMDDdnAkrB5aNKcZ0WPHXIJe");
	static Analytics analytics = Analytics.builder(writeKey).build();



	public static void trackUserLogin(final CustomerData customer)
	{

		final Map<String, String> map = new HashMap<>();

		map.put("name", customer.getName());
		map.put("email", customer.getEmail());
		map.put("hybrisSessionId", RequestContextHolder.currentRequestAttributes().getSessionId());

		analytics.enqueue(IdentifyMessage.builder()
				.userId(customer.getGaUid())
				.traits(map)
			);

	}
}
