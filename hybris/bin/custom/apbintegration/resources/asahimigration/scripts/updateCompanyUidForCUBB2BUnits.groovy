INSERT_UPDATE ScriptingJob	;code[unique=true]		;scriptURI
							;updateCompanyUidScriptJob	;model://updateCompanyUidScriptJob
							
INSERT_UPDATE CronJob	;code[unique=true]			;job(code)				;sessionLanguage(isocode)	;sessionUser(uid)
						;updateCompanyUidScriptCronJob	;updateCompanyUidScriptJob	;en		;admin

INSERT_UPDATE Script; code[unique=true];content;active[default=true, unique=true]
;updateCompanyUidScriptJob;"import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import de.hybris.platform.b2b.model.B2BUnitModel;
import org.apache.log4j.*;
import java.lang.Exception;

final Logger LOG = Logger.getLogger(""asahi"");

flexibleSearchService = spring.getBean(""flexibleSearchService"");
modelService = spring.getBean(""modelService"");

final FlexibleSearchQuery query = new FlexibleSearchQuery(""select {pk} from {B2BUnit!} where {companyUid} is NULL"");
final SearchResult<B2BUnitModel> result = flexibleSearchService
                                                          .search(query);
               
LOG.info(""Total count"" : + result.getCount());			   
for(B2BUnitModel b2bunit : result.getResult())
{
  try
  {
     
      b2bunit.setCompanyUid(""sabmStore"");
      modelService.save(b2bunit);
  
  }
  catch(Exception ex)
  {
        println(""Model Saving exception"" + ex.getMessage());
  }

}";;
	



