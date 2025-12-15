/**
 *
 */
package com.sabmiller.core.search.listners;

import com.sabmiller.core.b2b.services.SABMProductExclusionService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.ProductExclusionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The listener interface for receiving productExclusion events. The class that is interested in processing a
 * productExclusion event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addProductExclusionListener<code> method. When the productExclusion event
 * occurs, that object's appropriate method is invoked.
 *
 */
public class ProductExclusionListener implements FacetSearchListener
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ProductExclusionListener.class);

	/** The product exclusion service. */
	@Resource(name = "sabmProductExclusionService")
	private SABMProductExclusionService productExclusionService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource
	private UserService userService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener#beforeSearch(de.hybris.platform.
	 * solrfacetsearch.search.context.FacetSearchContext)
	 */
	@Override
	public void beforeSearch(final FacetSearchContext facetSearchContext) throws FacetSearchException {

		if (!facetSearchContext.getIndexedType().getCode().equals(SabmCoreConstants.SOLR_INDEXTYPE_PRODUCT)) {
			return;
		}
		
		if(!facetSearchContext.getIndexedType().getIdentifier().equalsIgnoreCase("sabmStoreProductType"))
		{
			return;
		}

		final UserModel currentUser = userService.getCurrentUser();

		//To bypass check for backoffice adaptive search
		if(currentUser instanceof B2BCustomerModel) {
			final Set<String> productExclusionEanCodes = productExclusionService.getSessionProductExclusionEanCodes();

			if (CollectionUtils.isNotEmpty(productExclusionEanCodes)) {
				facetSearchContext.getSearchQuery().addFilterQuery("-code_string", Operator.OR, productExclusionEanCodes);
			}
		}
	}

	/**
	 *
	 * @param productExList
	 * @return Set<String>
	 */
	protected Set<String> getProdList(final List<ProductExclusionModel> productExList)
	{
		final Set<String> prodList = new HashSet<>();
		//Populating an array string of prodcut to pass as exclusion filter query to the search engine.
		for (int i = 0; i < productExList.size(); i++)
		{
			try
			{
				final ProductExclusionModel productExcl = productExList.get(i);
				if (productExcl.getProduct() != null)
				{
					final ProductModel material = productService.getProductForCode(productExcl.getProduct());
					if (material instanceof SABMAlcoholVariantProductMaterialModel
							&& ((SABMAlcoholVariantProductMaterialModel) material).getBaseProduct() != null)
					{
						prodList.add(((SABMAlcoholVariantProductMaterialModel) material).getBaseProduct().getCode());
					}
				}
			}
			catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException | ConversionException e)
			{
				LOG.debug(e.getMessage(), e);
				LOG.warn("Error fetching product exclusion for one product");
			}
			finally
			{
				LOG.warn("Error fetching product exclusion for one product");
			}
		}

		LOG.debug("List of products to exclude for customer: {} is: {}", productExList.get(0).getCustomer(), prodList);

		return prodList;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener#afterSearch(de.hybris.platform.
	 * solrfacetsearch.search.context.FacetSearchContext)
	 */
	@Override
	public void afterSearch(final FacetSearchContext paramFacetSearchContext) throws FacetSearchException
	{
		//Empty method.

	}



	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener#afterSearchError(de.hybris.platform.
	 * solrfacetsearch.search.context.FacetSearchContext)
	 */
	@Override
	public void afterSearchError(final FacetSearchContext paramFacetSearchContext) throws FacetSearchException
	{
		//Empty method.
	}




}
