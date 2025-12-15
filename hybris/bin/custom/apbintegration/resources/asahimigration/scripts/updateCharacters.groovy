INSERT_UPDATE ScriptingJob	;code[unique=true]		;scriptURI
							;updateSpecialCharacterB2BUnit	;model://updateSpecialCharacterB2BUnit
							
INSERT_UPDATE CronJob	;code[unique=true]			;job(code)				;sessionLanguage(isocode)	;sessionUser(uid)
						;updateSpecialCharacterB2BUnit	;updateSpecialCharacterB2BUnit	;en		;admin

INSERT_UPDATE ScriptingJob	;code[unique=true]		;scriptURI
							;updateSpecialCharacterAddress	;model://updateSpecialCharacterAddress
							
INSERT_UPDATE CronJob	;code[unique=true]			;job(code)				;sessionLanguage(isocode)	;sessionUser(uid)
						;updateSpecialCharacterAddress	;updateSpecialCharacterAddress	;en		;admin
						
INSERT_UPDATE ScriptingJob	;code[unique=true]		;scriptURI
							;updateSpecialCharacterOrder	;model://updateSpecialCharacterOrder
							
INSERT_UPDATE CronJob	;code[unique=true]			;job(code)				;sessionLanguage(isocode)	;sessionUser(uid)
						;updateSpecialCharacterOrder	;updateSpecialCharacterOrder	;en		;admin

INSERT_UPDATE ScriptingJob	;code[unique=true]		;scriptURI
							;updateSpecialCharacterContactUsQueryEmail	;model://updateSpecialCharacterContactUsQueryEmail
							
INSERT_UPDATE CronJob	;code[unique=true]			;job(code)				;sessionLanguage(isocode)	;sessionUser(uid)
						;updateSpecialCharacterContactUsQueryEmail	;updateSpecialCharacterContactUsQueryEmail	;en		;admin

							
##########Update Email Address , LocName with Semi-colon in Asahi B2BUnit #################

INSERT_UPDATE Script; code[unique=true];content;active[default=true, unique=true]
;updateSpecialCharacterB2BUnit;"import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import java.lang.Exception;
import org.apache.commons.lang3.StringUtils;

flexibleSearchService = spring.getBean(""flexibleSearchService"");
modelService = spring.getBean(""modelService"");

final FlexibleSearchQuery query = new FlexibleSearchQuery(""select {pk} from {AsahiB2bUnit} where {emailAddress} like '%+++%' or {locName} like '%+++%' or {cellularPhone} like '%+++%' or {phone} like '%+++%' or {emailAddress} like '%||%' or {locName} like '%||%' or {emailAddress} like '%===%' or {locName} like '%===%' or {cellularPhone} like '%===%' or {phone} like '%===%' "");
final SearchResult<AsahiB2BUnitModel> result = flexibleSearchService
                                                          .search(query);
println(""Total count: ""+result.getCount()) ;                                                         
for(AsahiB2BUnitModel b2bunit : result.getResult())
{
  try
  {
      String emailAddress = b2bunit.getEmailAddress();	
      if (StringUtils.isNotBlank(emailAddress))
      {
     	 emailAddress = emailAddress.replace('+++','\n');
      	 emailAddress = emailAddress.replace('||',';');
      	 emailAddress = emailAddress.replace('===','\r');
      	 
     	 b2bunit.setEmailAddress(emailAddress);
      }
      String locName = b2bunit.getLocName();	
      if (StringUtils.isNotBlank(locName))
      {
      	 locName = locName.replace('+++','\n');
      	 locName = locName.replace('||',';');
      	  locName = locName.replace('===','\r');
     	 b2bunit.setLocName(locName);
      }
     	 
     String cellularPhone=b2bunit.getCellularPhone();
		if(null!= cellularPhone)
		 {
		 	cellularPhone= cellularPhone.replace('+++','\n');
		 	cellularPhone= cellularPhone.replace('===','\r');
			b2bunit.setCellularPhone(cellularPhone);
		 }	 
		 
	 	 
     String phone=b2bunit.getPhone();
		if(null!= phone)
		 {
		 	phone= phone.replace('+++','\n');
		 	phone= phone.replace('===','\r');
			b2bunit.setPhone(phone);
		 }
      modelService.save(b2bunit);
  
  }
  catch(Exception ex)
  {
        println(""Model Saving exception"" + ex.getMessage());
  }

}";;



##Update streetName and eclDeliveryInstruction with Semi-colon in Addresses#################

INSERT_UPDATE Script; code[unique=true];content;active[default=true, unique=true]
;updateSpecialCharacterAddress;"import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import de.hybris.platform.core.model.user.AddressModel;
import java.lang.Exception;
import org.apache.commons.lang3.StringUtils;

flexibleSearchService = spring.getBean(""flexibleSearchService"");
modelService = spring.getBean(""modelService"");

final FlexibleSearchQuery query = new FlexibleSearchQuery(""select {pk} from {Address} where {streetname} like '%||%' or {eclDeliveryInstruction} like '%||%' OR {streetname} like '%+++%' or {eclDeliveryInstruction} like '%+++%' OR {streetname} like '%===%' OR {eclDeliveryInstruction} like '%===%' or {company} like '%***%' OR {company} like '%+++%' OR {firstname} like '%+++%' "");
final SearchResult<AddressModel> result = flexibleSearchService
                                                          .search(query);
                                                          
println(""Total count: ""+result.getCount()) ;                                                             
for(AddressModel address : result.getResult())
{
  try
  {
      String firstname = address.getFirstname();	
      if (StringUtils.isNotBlank(firstname))
      {
     	 address.setFirstname(firstname.replace('+++','\n'));     	
      }
      
      String streetName = address.getStreetname();	
      if (StringUtils.isNotBlank(streetName))
      {
     	 address.setStreetname(streetName.replace('||',';'));
     	 address.setStreetname(streetName.replace('+++','\n'));
		 address.setStreetname(streetName.replace('===','\r'));
      }
      String eclDeliveryInstruction = address.getEclDeliveryInstruction();	
      if (StringUtils.isNotBlank(eclDeliveryInstruction))
      {
     	 address.setEclDeliveryInstruction(eclDeliveryInstruction.replace('||',';'));
     	 address.setEclDeliveryInstruction(eclDeliveryInstruction.replace('+++','\n'));
     	 address.setEclDeliveryInstruction(eclDeliveryInstruction.replace('===','\r'));
      
      }
      String company = address.getCompany();	
      if (StringUtils.isNotBlank(company))
      {
     	 address.setCompany(company.replace('***','\u2009'));
		 address.setCompany(company.replace('+++','\n'));
      }
      
      modelService.save(address);
  
  }
  catch(Exception ex)
  {
        println(""Model Saving exception"" + ex.getMessage());
  }

}";;



##########deliveryInstruction with Semi-colon, new-line, carriage return in Order#################

INSERT_UPDATE Script; code[unique=true];content;active[default=true, unique=true]
;updateSpecialCharacterOrder;"import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import de.hybris.platform.core.model.order.OrderModel;
import java.lang.Exception;
import org.apache.commons.lang3.StringUtils;

flexibleSearchService = spring.getBean(""flexibleSearchService"");
modelService = spring.getBean(""modelService"");

final FlexibleSearchQuery query = new FlexibleSearchQuery(""select {pk} from {Order} where {deliveryInstruction} like '%||||%' OR {deliveryInstruction} like '%+++%' OR {deliveryInstruction} like '%===%' OR {purchaseOrderNumber} like '%||||%' OR {purchaseOrderNumber} like '%+++%' OR {purchaseOrderNumber} like '%===%' "");
final SearchResult<OrderModel> result = flexibleSearchService
                                                          .search(query);
       
println(""Total count: ""+result.getCount()) ;                                                       
for(OrderModel order : result.getResult())
{
  try
  {
      String deliveryInstruction = order.getDeliveryInstruction();	
      if (StringUtils.isNotBlank(deliveryInstruction))
      {
     	 order.setDeliveryInstruction(deliveryInstruction.replace('||||',';'));
     	  order.setDeliveryInstruction(deliveryInstruction.replace('+++','\n'));
     	  order.setDeliveryInstruction(deliveryInstruction.replace('===','\r'));
      }
	  
	   String poNumber = order.getPurchaseOrderNumber();	
      if (StringUtils.isNotBlank(poNumber))
      {
     	 order.setPurchaseOrderNumber(poNumber.replace('||||',';'));
     	  order.setPurchaseOrderNumber(poNumber.replace('+++','\n'));
     	  order.setPurchaseOrderNumber(poNumber.replace('===','\r'));
      }
	  
		modelService.save(order);
     
  
  }
  catch(Exception ex)
  {
        println(""Model Saving exception"" + ex.getMessage());
  }

}";;


##########furtherDetail in ContactUsQueryEmail#################

INSERT_UPDATE Script; code[unique=true];content;active[default=true, unique=true]
;updateSpecialCharacterContactUsQueryEmail;"import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.Collections;
import com.apb.core.model.ContactUsQueryEmailModel;
import java.lang.Exception;
import org.apache.commons.lang3.StringUtils;

flexibleSearchService = spring.getBean(""flexibleSearchService"");
modelService = spring.getBean(""modelService"");

final FlexibleSearchQuery query = new FlexibleSearchQuery(""select {pk} from {ContactUsQueryEmail} where {furtherDetail} like '%+++%'  OR {furtherDetail} like '%===%'  "");
final SearchResult<OrderModel> result = flexibleSearchService.search(query);
println(""Total count: ""+result.getCount()) ;                                                       
for(ContactUsQueryEmailModel email : result.getResult())
{
  try
  {
      String furtherDetail = email.getFurtherDetail();	
      if (StringUtils.isNotBlank(furtherDetail))
      {  	
     	  email.setFurtherDetail(furtherDetail.replace('+++','\n'));
     	  email.setFurtherDetail(furtherDetail.replace('===','\r'));    
          modelService.save(email);
      }
  
  }
  catch(Exception ex)
  {
        println(""Model Saving exception"" + ex.getMessage());
  }

}";;



