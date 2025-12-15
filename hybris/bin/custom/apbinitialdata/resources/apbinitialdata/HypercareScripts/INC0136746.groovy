import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import de.hybris.platform.b2b.model.B2BUnitModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.apache.log4j.*;
import java.lang.Exception;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import org.apache.commons.collections4.CollectionUtils;

final Logger LOG = Logger.getLogger("asahi");

flexibleSearchService = spring.getBean("flexibleSearchService");
modelService = spring.getBean("modelService");

final FlexibleSearchQuery query = new FlexibleSearchQuery("select {pk} from {B2BCustomer!} where {site} is NULL  ");
final SearchResult<B2BCustomerModel> result = flexibleSearchService
                                                          .search(query);
               

final FlexibleSearchQuery query2 = new FlexibleSearchQuery("select {pk} from {BaseSite} where {uid}='sga' ");
BaseSiteModel albSite= flexibleSearchService.search(query2).getResult().get(0);
   
final FlexibleSearchQuery query3 = new FlexibleSearchQuery("select {pk} from {BaseSite} where {uid}='apb' ");
BaseSiteModel apbSite= flexibleSearchService.search(query3).getResult().get(0);
   

LOG.info("Total count" : + result.getCount());			   
println("Total count" : + result.getCount());	

int applicableCount =0;
for(B2BCustomerModel customer : result.getResult())
{
  try
  {
      if (customer.getDefaultB2BUnit() instanceof AsahiB2BUnitModel ){
		applicableCount++;
	     if (customer.getDefaultB2BUnit().getCompanyCode().equalsIgnoreCase("sga")){
			customer.setSite(albSite);
		 }
		 else {
			customer.setSite(apbSite);
		}
      modelService.save(customer);
  }
  
  else{
  if(CollectionUtils.isNotEmpty(customer.getGroups()))
  {
	for(PrincipalGroupModel group:customer.getGroups())
	{
		if (group instanceof AsahiB2BUnitModel ){
		applicableCount++;
	     if (((AsahiB2BUnitModel)group).getCompanyCode().equalsIgnoreCase("sga")){
			customer.setSite(albSite);
		 }
		 else {
			customer.setSite(apbSite);
		}
      modelService.save(customer);
	  break;
  }
	}
  }
  
  }
  }
  catch(Exception ex)
  {
        println("Model Saving exception" + ex.getMessage());
  }

}
LOG.info("Total count" : + applicableCount);			   
println("Total count" : + applicableCount);	

