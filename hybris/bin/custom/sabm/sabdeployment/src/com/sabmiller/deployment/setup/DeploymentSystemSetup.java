package com.sabmiller.deployment.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sabmiller.deployment.constants.SabdeploymentConstants;


@SystemSetup(extension = SabdeploymentConstants.EXTENSIONNAME)
public class DeploymentSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = LoggerFactory.getLogger(DeploymentSystemSetup.class);

	private static final List<String> LOAD_DELTA_DATA = Lists.newArrayList("local", "dev", "test", "uat");
	private static final String CONFIG_ENV_PROPERTY = "config.env";

	private static final String RELEASE_IMPEX_PATH = "/sabdeployment/release/";
	private static final String SELECT_HEADER = "- Select -";


	// Wave 1 impexes - Start
	private static final Map<String, String> WAVE1_IMPEXES = wave1ImpexesMap();

	private static Map<String, String> wave1ImpexesMap()
	{
		final Map<String, String> map = Maps.newLinkedHashMap();
		map.put("-----------------" + SELECT_HEADER + "-------------------", "");
		map.put("release-1.00", "wave1/release-1.00");
		map.put("release-1.01", "wave1/release-1.01");
		map.put("release-1.02", "wave1/release-1.02");
		map.put("release-1.03", "wave1/release-1.03");
		map.put("release-2.00", "wave1/release-2.00");
		map.put("release-3.00", "wave1/release-3.00");
		map.put("release-4.00", "wave1/release-4.00");
		map.put("release-rc-8", "wave1/release-rc-8");
		map.put("release-w2-enh-rc1.impex", "wave1/release-w2-enh-rc1.impex");
		map.put("release-w2-rc3", "wave1/release-w2-rc3");
		map.put("release-w2-rc4", "wave1/release-w2-rc4");
		map.put("SAB-2708", "wave1/SAB-2708");
		map.put("sabmc-1171", "wave1/sabmc-1171");
		map.put("sabmc-1894", "wave1/sabmc-1894");
		map.put("SABMC-1896", "wave1/SABMC-1896");
		map.put("SABMC-1901", "wave1/SABMC-1901");
		map.put("sabmc-1903", "wave1/sabmc-1903");
		return Collections.unmodifiableMap(map);
	}
	// Wave 1 impexes - End


	// Wave 2 impexes - Start
	private static final Map<String, String> WAVE2_IMPEXES = wave2ImpexesMap();

	private static Map<String, String> wave2ImpexesMap()
	{
		final Map<String, String> map = Maps.newLinkedHashMap();
		map.put("-----------------" + SELECT_HEADER + "-------------------", "");

		map.put("S26-keg-pack-story", "wave2-enhancements/KegAndPackStory/S26-keg-pack-story");
		map.put("S30_StockStatus", "wave2-enhancements/S30_StockStatus");
		map.put("S68-personalized-message", "wave2-enhancements/S68-personalized-message");
		map.put("S4-S25-Cleanup", "wave2-enhancements/S4-S25-Cleanup");
		map.put("S115-emailcontent", "wave2-enhancements/S115-emailcontent");
		map.put("S71_Recommendationreporting", "wave2-enhancements/S71_Recommendationreporting");
		map.put("S75-resetpasswd-emailcontent", "wave2-enhancements/S75-resetpasswd-emailcontent");
		map.put("S75-welcome-emailcontent", "wave2-enhancements/S75-welcome-emailcontent");
		map.put("customer-email-search", "wave2-enhancements/bug-fixes/customer-email-search");
		map.put("email-notifications-content", "wave2-enhancements/bug-fixes/email-notifications-content");

		map.put("cms-content", "wave2-enhancements/cms-content");
		map.put("cms-notifications-content", "wave2-enhancements/cms-notifications-content");
		map.put("cms-recommendations-content", "wave2-enhancements/cms-recommendations-content");
		map.put("cms-smartorder-content", "wave2-enhancements/cms-smartorder-content");
		map.put("email-notifications-content", "wave2-enhancements/email-notifications-content");
		map.put("ENH-RC-Sprint3", "wave2-enhancements/ENH-RC-Sprint3");
		map.put("S115-emailcontent", "wave2-enhancements/S115-emailcontent");
		map.put("SEO_internallinks", "wave2-enhancements/SEO_internallinks");
		map.put("sprint1-bde-status-solr", "wave2-enhancements/sprint1-bde-status-solr");
		map.put("WAVE2-S4-S25", "wave2-enhancements/WAVE2-S4-S25");

		return Collections.unmodifiableMap(map);

	}
	// Wave 2 impexes - End

	// Wave 3 impexes - Start
	private static final Map<String, String> WAVE3_IMPEXES = wave3ImpexesMap();

	private static Map<String, String> wave3ImpexesMap()
	{
		final Map<String, String> map = Maps.newLinkedHashMap();
		map.put("-----------------" + SELECT_HEADER + "-------------------", "");

		map.put("live-chat-cms-content", "wave3/live-chat-cms-content");
		map.put("salesforce-jobs", "wave3/salesforce-jobs");
		map.put("bde-ordering", "wave3/bde-ordering");
		map.put("track-order.01_cms-content", "wave3/track-order.01.cms-content");
		map.put("track-order.02_cms-email-notifications-content", "wave3/track-order.02.cms-email-notifications-content");
		map.put("autopay.00_jobs", "wave3/autopay.00.jobs");
		map.put("autopay.01_cms-media-content", "wave3/autopay.01.cms-media-content");
		//map.put("autopay.02_cms-generic-components-homepage-content",		"wave3/autopay.02.cms-generic-components-homepage-content");
		//map.put("autopay.03_cms-generic-confirmation-component", 			"wave3/autopay.03.cms-generic-confirmation-component");
		map.put("autopay.04_cms-landing-page-content", "wave3/autopay.04.cms-landing-page-content");
		map.put("autopay.05_cms-generic-components-template-structures",
				"wave3/autopay.05.cms-generic-components-template-structures");
		map.put("autopay.06_cms-homepage-banner-component", "wave3/autopay.06.cms-homepage-banner-component");
		map.put("credit.00.cms-credit-content", "wave3/cms-credit-content");
		map.put("credit.01.cms-support-page-components", "wave3/cms-credit-support-page-components");
		map.put("ConfirmEnabledDeal-EmailNotification-Banner", "NewBanner_ConfirmEnabledDeal");
		map.put("credit.02.cms-invoice-discrenpany-components", "wave3/cms-credit-invoice-discrepancy");
		map.put("ConfirmEnabledDeal-EmailNotification-Banner", "wave3/NewBanner_ConfirmEnabledDeal");
		map.put("brand_cms", "wave3/brand_cms");
		map.put("imc-importcockpit", "wave3/imc-importcockpit");
		map.put("non-alcoholic", "wave3/non-alcoholic");
		map.put("wine", "wave3/wine");
		map.put("bulkrecommendationexpire", "wave3/bulkrecommendationexpire");
		map.put("banking-cms-content", "wave3/banking-cms-content");
		return Collections.unmodifiableMap(map);

	}
	// Wave 3 impexes - End


	// Wave 3 impexes - Start
	/*
	 * private static final Map<String, String> LIVECHAT_IMPEXES = liveChatImpexesMap();
	 *
	 * private static Map<String, String> liveChatImpexesMap() { final Map<String, String> map = Maps.newLinkedHashMap();
	 * map.put("----" + SELECT_HEADER + "-----", ""); map.put("live-chat-cms-content", "wave3/live-chat-cms-content");
	 * return Collections.unmodifiableMap(map); }
	 *
	 * private static final Map<String, String> SALESFORCE_IMPEXES = salesforceImpexesMap();
	 *
	 * private static Map<String, String> salesforceImpexesMap() { final Map<String, String> map =
	 * Maps.newLinkedHashMap(); map.put("----" + SELECT_HEADER + "-----", ""); map.put("salesforce-jobs",
	 * "wave3/salesforce-jobs"); return Collections.unmodifiableMap(map); }
	 *
	 * private static final Map<String, String> BDE_ORDERING_IMPEXES = bdeOrderingImpexesMap();
	 *
	 * private static Map<String, String> bdeOrderingImpexesMap() { final Map<String, String> map =
	 * Maps.newLinkedHashMap(); map.put("---" + SELECT_HEADER + "---", ""); map.put("bde-ordering",
	 * "wave3/bde-ordering"); return Collections.unmodifiableMap(map); }
	 *
	 * private static final Map<String, String> TRACK_ORDER_IMPEXES = trackOrderImpexesMap();
	 *
	 * private static Map<String, String> trackOrderImpexesMap() { final Map<String, String> map =
	 * Maps.newLinkedHashMap(); map.put("--------------------" + SELECT_HEADER + "----------------------", "");
	 * map.put("track-order.01_cms-content", "wave3/track-order.01.cms-content");
	 * map.put("track-order.02_cms-email-notifications-content", "wave3/track-order.02.cms-email-notifications-content");
	 * return Collections.unmodifiableMap(map); }
	 *
	 * private static final Map<String, String> AUTOPAY_IMPEXES = rewardsImpexesMap();
	 *
	 * private static Map<String, String> rewardsImpexesMap() { final Map<String, String> map = Maps.newLinkedHashMap();
	 * map.put("-------------------------" + SELECT_HEADER + "-------------------------", ""); map.put("autopay.00_jobs",
	 * "wave3/autopay.00.jobs"); map.put("autopay.01_cms-media-content", "wave3/autopay.01.cms-media-content");
	 * //map.put("autopay.02_cms-generic-components-homepage-content",
	 * "wave3/autopay.02.cms-generic-components-homepage-content");
	 * //map.put("autopay.03_cms-generic-confirmation-component", "wave3/autopay.03.cms-generic-confirmation-component");
	 * map.put("autopay.04_cms-landing-page-content", "wave3/autopay.04.cms-landing-page-content");
	 * map.put("autopay.05_cms-generic-components-template-structures",
	 * "wave3/autopay.05.cms-generic-components-template-structures");
	 * map.put("autopay.06_cms-homepage-banner-component", "wave3/autopay.06.cms-homepage-banner-component"); return
	 * Collections.unmodifiableMap(map); } // Wave 3 impexes - End
	 *
	 * private static final Map<String, String> CREDIT_IMPEXES = creditsImpexesMap();
	 *
	 * private static Map<String, String> creditsImpexesMap() { final Map<String, String> map = Maps.newLinkedHashMap();
	 * map.put("-------------------------" + SELECT_HEADER + "-------------------------", "");
	 * map.put("credit.00.cms-credit-content", "wave3/cms-credit-content");
	 * map.put("credit.01.cms-support-page-components", "wave3/cms-credit-support-page-components");
	 * map.put("ConfirmEnabledDeal-EmailNotification-Banner", "NewBanner_ConfirmEnabledDeal");
	 * map.put("credit.02.cms-invoice-discrenpany-components", "wave3/cms-credit-invoice-discrepancy"); return
	 * Collections.unmodifiableMap(map); }
	 *
	 * private static final Map<String, String> NEWBANNER_IMPEXES = newBannerImpexesMap();
	 *
	 * private static Map<String, String> newBannerImpexesMap() { final Map<String, String> map =
	 * Maps.newLinkedHashMap(); map.put("-------------------------" + SELECT_HEADER + "-------------------------", "");
	 * map.put("ConfirmEnabledDeal-EmailNotification-Banner", "NewBanner_ConfirmEnabledDeal"); return
	 * Collections.unmodifiableMap(map); }
	 */

	// Map of system/application features and the associated impexes
	private static final Map<String, Map<String, String>> FEATURES = featuresMap();

	private static Map<String, Map<String, String>> featuresMap()
	{
		final Map<String, Map<String, String>> map = Maps.newLinkedHashMap();


		map.put("wave1", WAVE1_IMPEXES);
		map.put("wave2", WAVE2_IMPEXES);

		map.put("wave3", WAVE3_IMPEXES);

		return Collections.unmodifiableMap(map);
	}


	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<>();

		final boolean loadDeltaData = LOAD_DELTA_DATA.contains(Config.getParameter(CONFIG_ENV_PROPERTY));
		final List<String> featureList = Lists.newArrayList(FEATURES.keySet());
		final String firstFeature = featureList.get(0);

		params.add(createFeatureImpexesSystemSetupParameter(firstFeature, FEATURES.get(firstFeature),
				"Select the impexes to be imported for the following system features:</br></br>" + firstFeature, loadDeltaData));
		featureList.subList(1, featureList.size()).stream().forEach(feature -> {
			params.add(createFeatureImpexesSystemSetupParameter(feature, FEATURES.get(feature), feature, loadDeltaData));
		});

		return params;
	}


	private SystemSetupParameter createFeatureImpexesSystemSetupParameter(final String key,
			final Map<String, String> featureImpexes, final String label, final boolean selected)
	{
		final SystemSetupParameter params = new SystemSetupParameter(key);
		params.setLabel(label);
		params.setMultiSelect(true);
		//params.addValues(featureImpexes.keySet().toArray(new String[featureImpexes.size()]));

		final List<String> featureImpexList = Lists.newArrayList(featureImpexes.keySet());
		params.addValue(featureImpexList.get(0), false); // this is the "- Select -" (SELECT_HEADER) item which is the first in the list

		featureImpexList.subList(1, featureImpexList.size()).stream().forEach(impex -> {
			params.addValue(impex, selected);
		});

		return params;
	}


	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final List<String> featureList = Lists.newArrayList(FEATURES.keySet());

		featureList.stream().forEach(feature -> {
			final String[] selectedFeatureImpexes = context.getParameters(context.getExtensionName() + "_" + feature);

			if (Objects.nonNull(selectedFeatureImpexes))
			{
				Arrays.asList(selectedFeatureImpexes).stream().forEach(selectedImpex -> {
					if (!selectedImpex.contains(SELECT_HEADER))
					{
						final String impexPathFileName = RELEASE_IMPEX_PATH + FEATURES.get(feature).get(selectedImpex) + ".impex";
						LOG.info(String.format("Importing impex [%s] for [%s] feature...", impexPathFileName, feature));
						importImpexFile(context, impexPathFileName);
					}
				});
			}
		});

		LOG.info("Running one more sync for delta impexes...");

		if (LOAD_DELTA_DATA.contains(Config.getParameter(CONFIG_ENV_PROPERTY)))
		{
			try
			{
				synchronizeProductCatalog(this, context, "sabm");
				synchronizeContentCatalog(this, context, "sabm");
			}
			catch (final Exception e)
			{
				LOG.error("Error syncing catalogs", e);
			}
		}
	}

	public boolean synchronizeProductCatalog(final AbstractSystemSetup systemSetup, final SystemSetupContext context,
			final String catalogName)
	{
		systemSetup.logInfo(context, String.format("Begin synchronizing Product Catalog [%s]", catalogName));

		final PerformResult syncCronJobResult = this.getSetupSyncJobService()
				.executeCatalogSyncJob(String.format("%sProductCatalog", catalogName));
		if (this.isSyncRerunNeeded(syncCronJobResult))
		{
			systemSetup.logInfo(context, String.format("Product Catalog [%s] sync has issues.", catalogName));
			return false;
		}

		return true;
	}

	public boolean synchronizeContentCatalog(final AbstractSystemSetup systemSetup, final SystemSetupContext context,
			final String catalogName)
	{
		systemSetup.logInfo(context, String.format("Begin synchronizing Content Catalog [%s]", catalogName));

		final PerformResult syncCronJobResult = this.getSetupSyncJobService()
				.executeCatalogSyncJob(String.format("%sContentCatalog", catalogName));
		if (this.isSyncRerunNeeded(syncCronJobResult))
		{
			systemSetup.logInfo(context, String.format("Content Catalog [%s] sync has issues.", catalogName));
			return false;
		}

		return true;
	}
}
