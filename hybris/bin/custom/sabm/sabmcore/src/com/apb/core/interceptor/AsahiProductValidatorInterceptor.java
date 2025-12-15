package com.apb.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.exception.AsahiBusinessException;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * The Class AsahiProductValidatorInterceptor.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiProductValidatorInterceptor implements ValidateInterceptor<ApbProductModel>
{

	private String PRODUCT_NAME_VALIDATION_LENGTH = "product.name.validation.length.apb";
	private String PRODUCT_DETAIL_VALIDATION_LENGTH = "product.detail.validation.length.apb";
	private String PRODUCT_DESCRIPTION_VALIDATION_LENGTH = "product.description.validation.length.apb";
	private String USE_PRODUCT_BACKEND_NAME = "use.product.backend.name.apb";
	private String USE_PRODUCT_BACKEND_UNIT_VOLUME = "use.product.backend.unit.volume.apb";
	private String USE_PRODUCT_BACKEND_UNIT_PER_CASE = "use.product.backend.unit.per.case.apb";
	private String PRODUCT_RANK_MAX_LIMIT = "product.rank.max.limit.apb";


	private static final String SGA_BASE_STORE = "sga";
	private static final Logger LOG = Logger.getLogger(AsahiProductValidatorInterceptor.class);

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The apb product reference service. */
	@Resource(name = "apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;


	/**
	 * @param productModel
	 */
	public void updateConfigurationKeys(final ApbProductModel productModel)
	{
		ArrayList<BaseStoreModel> storeList = null;
		String baseStoreId = "";

		if (null != productModel.getCatalogVersion().getCatalog().getBaseStores())
		{
			storeList = new ArrayList<BaseStoreModel>(productModel.getCatalogVersion().getCatalog().getBaseStores());

		}


		if (null != storeList && null != storeList.get(0))
		{
			baseStoreId = storeList.get(0).getUid();
		}


		if (StringUtils.isNotEmpty(baseStoreId) && baseStoreId.equalsIgnoreCase(SGA_BASE_STORE))
		{
			PRODUCT_NAME_VALIDATION_LENGTH = "product.name.validation.length.sga";
			PRODUCT_DETAIL_VALIDATION_LENGTH = "product.detail.validation.length.sga";
			PRODUCT_DESCRIPTION_VALIDATION_LENGTH = "product.description.validation.length.sga";
			USE_PRODUCT_BACKEND_NAME = "use.product.backend.name.sga";
			USE_PRODUCT_BACKEND_UNIT_VOLUME = "use.product.backend.unit.volume.sga";
			USE_PRODUCT_BACKEND_UNIT_PER_CASE = "use.product.backend.unit.per.case.sga";
			PRODUCT_RANK_MAX_LIMIT = "product.rank.max.limit.sga";

		}

	}

	/**
	 * On validate.
	 *
	 * @param productModel
	 *           the product model
	 * @param arg1
	 *           the arg 1
	 * @throws InterceptorException
	 *            the interceptor exception
	 */
	public void onValidate(final ApbProductModel productModel, final InterceptorContext arg1) throws InterceptorException
	{
		if (null != productModel)
		{
			updateConfigurationKeys(productModel);

			//Update Product name with BackendName value if USE_PRODUCT_BACKEND_NAME property is True.
			if (null != productModel.getBackendName()
					&& this.asahiConfigurationService.getString(USE_PRODUCT_BACKEND_NAME, "false").equals(Boolean.TRUE))
			{

				productModel.setName(productModel.getBackendName());
			}

			//Update Product unitVolume with BackendUnitVolume value if USE_PRODUCT_BACKEND_UNIT_VOLUME property is True.
			if (null != productModel.getBackendUnitVolume() && null != productModel.getPortalUnitVolume()
					&& this.asahiConfigurationService.getString(USE_PRODUCT_BACKEND_UNIT_VOLUME, "false").equals(Boolean.TRUE))
			{

				productModel.getPortalUnitVolume().setName(String.valueOf(productModel.getBackendUnitVolume()));
			}

			//Update Product unitPerCase with BackendUnitPerCase value if USE_PRODUCT_BACKEND_UNIT_PER_CASE property is True.
			if (null != productModel.getBackendUnitPerCase() && null != productModel.getPackageSize()
					&& this.asahiConfigurationService.getString(USE_PRODUCT_BACKEND_UNIT_PER_CASE, "false").equals(Boolean.TRUE))
			{

				productModel.getPackageSize().setName(String.valueOf(productModel.getBackendUnitPerCase()));
			}

			final StringBuffer errorMessage = new StringBuffer();
			boolean checkError = false;

			//validating Product Name length. It should be less than with configured Value
			checkError = this.validateProductName(productModel.getName(), errorMessage, checkError);

			//validating Product Description length. It should be less than with configured Value
			checkError = this.validateProductDescription(productModel.getDescription(), errorMessage, checkError);

			//validating Product Detail. It should be less than with configured Value.
			checkError = this.validateProductDetail(productModel.getProductDetail(), errorMessage, checkError);

			//validating Product Rank. It should be between 1 to configured Value.
			checkError = this.validateProductRank(productModel.getRank(), errorMessage, checkError);

			//validating Product Unit Volume. Product Volume should have reference in UnitVolume Master table.
			checkError = this.validateProductUnitVolume(productModel.getPortalUnitVolume(), errorMessage, checkError);

			//validating Product Package Size. Product Package Size should have reference in PackageSize Master table.
			checkError = this.validatePackageSize(productModel.getPackageSize(), errorMessage, checkError);

			//If product validation gets fail, throw an error with combined message.
			if (checkError)
			{
				throw new AsahiBusinessException(errorMessage.toString());
			}
		}

	}

	/**
	 * Validate package size.
	 *
	 * @param packageSize
	 *           the package size
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validatePackageSize(final PackageSizeModel packageSize, final StringBuffer errorMessage, boolean checkError)
	{
		if (null == packageSize)
		{
			errorMessage.append("Package Size can not be null.");
		}
		else
		{
			if (null == this.apbProductReferenceService.getPackageSizeForCode(packageSize.getCode()))
			{
				errorMessage.append("PackageSize with code " + packageSize.getCode() + " does not exist in Master Table ");
				checkError = true;
			}
		}

		return checkError;
	}

	/**
	 * Validate product unit volume.
	 *
	 * @param unitVolumeModel
	 *           the unit volume model
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validateProductUnitVolume(final UnitVolumeModel unitVolumeModel, final StringBuffer errorMessage,
			boolean checkError)
	{
		if (null == unitVolumeModel)
		{
			errorMessage.append("Unit Volume can not be null.");
		}
		else
		{
			if (null == this.apbProductReferenceService.getUnitVolumeForCode(unitVolumeModel.getCode()))
			{
				errorMessage.append("Unit Volume with code " + unitVolumeModel.getCode() + " does not exist in Master Table ");
				checkError = true;
			}
		}

		return checkError;
	}

	/**
	 * Validate product description.
	 *
	 * @param description
	 *           the product model
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validateProductDescription(final String description, final StringBuffer errorMessage, boolean checkError)
	{
		final int productDescriptionLength = Integer
				.parseInt(this.asahiConfigurationService.getString(PRODUCT_DESCRIPTION_VALIDATION_LENGTH, "2000"));
		if (null != description && description.length() >= productDescriptionLength)
		{
			errorMessage.append("Product Description value should be less than " + productDescriptionLength + ": ");
			checkError = true;
		}
		return checkError;
	}

	/**
	 * Validate product name.
	 *
	 * @param name
	 *           the product model
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validateProductName(final String name, final StringBuffer errorMessage, boolean checkError)
	{
		final int productNameLength = Integer
				.parseInt(this.asahiConfigurationService.getString(PRODUCT_NAME_VALIDATION_LENGTH, "100"));
		if (null != name && name.length() >= productNameLength)
		{
			errorMessage.append("Product Name value should be less than " + productNameLength + ": ");
			checkError = true;
		}
		return checkError;
	}

	/**
	 * Validate product detail.
	 *
	 * @param productDetail
	 *           the product detail
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validateProductDetail(final String productDetail, final StringBuffer errorMessage, boolean checkError)
	{
		final int productDetailLength = Integer
				.parseInt(this.asahiConfigurationService.getString(PRODUCT_DETAIL_VALIDATION_LENGTH, "2000"));
		if (null != productDetail && productDetail.length() >= productDetailLength)
		{
			errorMessage.append("Product Detail value should be less than " + productDetailLength + ": ");
			checkError = true;
		}
		return checkError;
	}

	/**
	 * Validate product rank.
	 *
	 * @param rank
	 *           the rank
	 * @param errorMessage
	 *           the error message
	 * @param checkError
	 *           the check error
	 * @return true, if successful
	 */
	private boolean validateProductRank(final Integer rank, final StringBuffer errorMessage, boolean checkError)
	{
		final int productRankMaxLimit = Integer.parseInt(this.asahiConfigurationService.getString(PRODUCT_RANK_MAX_LIMIT, "1000"));
		if (null != rank && rank > productRankMaxLimit)
		{
			errorMessage.append("Rank should be 1 to " + productRankMaxLimit + ": ");
			checkError = true;
		}
		return checkError;
	}
}
