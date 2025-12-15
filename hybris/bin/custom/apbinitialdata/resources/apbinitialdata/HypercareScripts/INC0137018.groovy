
#######Remove cub units#############################removeCUBUnitsandNotifsForInactiveCUBCustomer

import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.servicelayer.cronjob.PerformResult
import de.hybris.platform.commerceservices.enums.SalesApplication
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.SearchResult
import com.sabmiller.core.model.AsahiB2BUnitModel


import de.hybris.platform.core.model.user.UserGroupModel;
import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.apache.commons.collections4.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import de.hybris.platform.util.Config
import org.apache.commons.lang.StringUtils;

def customerFacade=spring.getBean("customerFacade")
def sessionService = spring.getBean("sessionService")
def fss=spring.getBean("flexibleSearchService");
def b2bCommerceUnitService=spring.getBean("b2bCommerceUnitService")
def b2bUnitService=spring.getBean("b2bUnitService")
def baseStoreService=spring.getBean("baseStoreService")
def ms = spring.getBean("modelService")

$replaceCustomerUids = Add all b2bcustomer UIDs for which CUB information needs to be removed

final String QUERY_GET_CUSTOMERS= "SELECT {pk} from {B2BCustomer}  where {uid} IN ($replaceCustomerUids)
	
final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_GET_CUSTOMERS);
final SearchResult<B2BCustomerModel> result = fss.search(fsq);
final List<B2BCustomerModel> listOfcustomer = result.getCount() > 0 ? result.getResult() : Collections.<B2BCustomerModel> emptyList();

Set<UserGroupModel> b2bUnits = new HashSet<>();

println listOfcustomer.size();

Map<String,B2BCustomerModel> customerUsermap = new HashMap<String,B2BCustomerModel>();
int count =0;
int defaultCUBUnitTied =0;

for(customer in listOfcustomer)
{	
	b2bUnits = customer.getGroups();
	final Set<UserGroupModel> updatedb2bUnits = new HashSet<>();

	for(unit in b2bUnits){
		if((unit instanceof B2BUnitModel) &&  StringUtils.isNotBlank (unit.getCompanyUid()) &&   unit.getCompanyUid().equalsIgnoreCase("sabmStore")) //APB/sabmStore/sga
		{
			//do nothing
		} else {
			updatedb2bUnits.add(unit);
		}
		
		if (null != customer.getDefaultB2BUnit() && customer.getDefaultB2BUnit().getCompanyUid().equalsIgnoreCase("sabmStore")){
			//link customer to AsahiB2BUnit
			defaultCUBUnitTied++;
			for (b2bunit in b2bUnits){
				if (b2bunit instanceof AsahiB2BUnitModel){
					customer.setDefaultB2BUnit(b2bunit);
					break;
				}
			
			}
		}
    }
	customer.setGroups(updatedb2bUnits);
	customer.setMobileContactNumber("");
	modelService.save(customer);
	if(updatedb2bUnits.size() >1)
	{
		print("Customer: "+customer.getUid());
	
      println();
      count++;
	}
	
}
println count;


############################Remove Notifications##################################

import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.servicelayer.cronjob.PerformResult
import de.hybris.platform.commerceservices.enums.SalesApplication
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.SearchResult
import com.sabmiller.core.model.SABMNotificationModel;

import de.hybris.platform.core.model.user.UserGroupModel;
import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.apache.commons.collections4.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import de.hybris.platform.util.Config

def customerFacade=spring.getBean("customerFacade")
def sessionService = spring.getBean("sessionService")
def fss=spring.getBean("flexibleSearchService");
def b2bCommerceUnitService=spring.getBean("b2bCommerceUnitService")
def b2bUnitService=spring.getBean("b2bUnitService")
def baseStoreService=spring.getBean("baseStoreService")
def ms = spring.getBean("modelService")


$replaceCustomerUids = Add all b2bcustomer UIDs for which CUB information needs to be removed

final String QUERY_GET_NOTIF= "SELECT {pk} from {SABMNotification}  where {user} IN ({{select {pk} from {b2bcustomer} where {uid} IN ($replaceCustomerUids) }})"
	
final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_GET_NOTIF);
final SearchResult<SABMNotificationModel> result = fss.search(fsq);
final List<SABMNotificationModel> notifs = result.getCount() > 0 ? result.getResult() : Collections.<SABMNotificationModel> emptyList();

println ("Total notifications pref to be removed "+ notifs.size());


int count =0;

for(notif in notifs)
{	
	ms.removeAll(notif.getNotificationPreferences());
	ms.remove(notif);
	
}
  	
	
	

##########################Remove mobileContactNumber############################


import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.servicelayer.cronjob.PerformResult
import de.hybris.platform.commerceservices.enums.SalesApplication
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.core.model.user.UserGroupModel;
import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.apache.commons.collections4.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import de.hybris.platform.util.Config

def customerFacade=spring.getBean("customerFacade")
def sessionService = spring.getBean("sessionService")
def fss=spring.getBean("flexibleSearchService");
def b2bCommerceUnitService=spring.getBean("b2bCommerceUnitService")
def b2bUnitService=spring.getBean("b2bUnitService")
def baseStoreService=spring.getBean("baseStoreService")
def ms = spring.getBean("modelService")


$replaceCustomerUids = Add all b2bcustomer UIDs for which CUB information needs to be removed

final String QUERY_GET_CUSTOMERS= "SELECT {pk} from {B2BCustomer}  where {uid} IN ($replaceCustomerUids)"
	
final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_GET_CUSTOMERS);
final SearchResult<B2BCustomerModel> result = fss.search(fsq);
final List<B2BCustomerModel> listOfcustomer = result.getCount() > 0 ? result.getResult() : Collections.<B2BCustomerModel> emptyList();

Set<B2BUnitModel> b2bUnits = new HashSet<>();

println listOfcustomer.size();

Map<String,B2BCustomerModel> customerUsermap = new HashMap<String,B2BCustomerModel>();
int count =0;
int defaultCUBUnitTied =0;

for(customer in listOfcustomer)
{	

	customer.setMobileContactNumber("");
	customer.setActive(Boolean.TRUE);
	customer.setLoginDisabled(Boolean.FALSE);
	modelService.save(customer);	
	
}
println count




