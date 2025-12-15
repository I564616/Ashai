
Below Transformations are required after running the corresponding export scripts:

1. After loading siteSetup01 and cmsContent , associate homepage from each catalog with their sites.

siteSetup01.csv

	a. replace  classpath:/apbcore/messages/ with classpath:/sabmcore/messages/ in the generated csv for siteSetup01
	b. Add businessCode[default=asahi] to Region header
	
cmsContent.csv
	
	a. Remove ShareOnSocialNetworkAction from values of action for component -ProductAddToCartComponent in cmsContent
	b. Replace
	  1. apbContentCatalog:Online:CUB Premium Beverages - Primary Logo - White (PROD).png for SiteLogoComponent with 
	      apbContentCatalog:Staged:CUB Premium Beverages - Primary Logo - White (PROD).png
	      
	  2. sgaProductCatalog:Staged:DirectDebitForm.pdf with sgaContentCatalog:Staged:DirectDebitForm.pdf 
	     and media corresponding to DirectDebitForm.pdf with catalog version sgaContentCatalog:Staged, similar for online entries


2.  02-siteSetup:


3. solr.csv

	1. Replace the below values with corresponding mapping values
	
	img-515Wx515H -> img-SABMHeroFormat
	img-300Wx300H -> img-SABMNormalFormat
	img-96Wx96H -> img-SABMThumbnailFormat
	img-65Wx65H -> img-SABMCartFormat
	img-30Wx30H  -> img-SABMWatchFormat
	
	
	image515ValueProvider -> imageHeroValueProvider
	image300ValueProvider -> imageNormalValueProvider
	image96ValueProvider -> imageThumbnailValueProvider
	image65ValueProvider -> imageCartValueProvider
	image30ValueProvider -> imageWatchValueProvider
	
	2. Replace enumValueProvider for SolrIndexpropert for indexed Type : BackofficeProduct, name : approvalStatus
		to solrEnumValueResolver

    3. Replace solrserverConfig name for all three entries (backoffice, apbindex, sgaindex)  to use default rather than SmartStack values from on-prem Asahi System"

4. productMedia.impex


	Replace the value for media format in Media Table with below values:
	
	,1200Wx1200H,  ->  ,SABMZoomFormat,
	,515Wx515H,     -> ,SABMHeroFormat,
	,300Wx300H,     -> ,SABMNormalFormat,
	,96Wx96H,       -> ,SABMThumbnailFormat,
	,65Wx65H,       -> ,SABMCartFormat,
	,30Wx30H,       -> ,SABMWatchFormat,
	
5. cronjobs.csv


Replace the  failedDirectDebits(pk) with failedDirectDebits(code) while importing

6. Employees.impex

a. The impex for AsahiEmployee will fail for records which have conflicting UIDs for Employee in CUB.
During cutover delete such CUB Employees first and run the impex for AsahiEmployee again :
Once for creation of records  with all fields as in employees.impex and second for appending usergroups -asahigroup,cubgroup for conflicting users. (User groups are created under miscellaneous.impex)
Also append bdegroup to all conflicting employees which had this group in CUB Production
UPDATE AsahiEmployee;uid[unique=true,allownull=true];groups(uid)[collection-delimiter=,mode=append, default=asahigroup,cubgroup];


b. Run the impex to append usergroup for exported Asahi records of Employee type and non-conflicting AsahiEmployee type

UPDATE Employee;uid[unique=true,allownull=true];groups(uid)[collection-delimiter=,mode=append, default=asahigroup];
UPDATE AsahiEmployee;uid[unique=true,allownull=true];groups(uid)[collection-delimiter=,mode=append, default=asahigroup];

Since the CUB Employees are tied to orders (placed by attribute) , run a groovy script to find all such orders 
and update the association again after loading AsahiEmployees (uid, placedBy)
select {pk} from {Order} where {placedBy} IN ({{select {pk}  from {Employee}}})


