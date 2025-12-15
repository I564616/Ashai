import com.sabm.core.model.cms.components.BestsellerComponentModel
import com.sabm.core.model.cms.components.CUBPicksComponentModel
import com.sabm.core.model.cms.components.RecommendationsComponentModel
import com.sabmiller.core.model.LinkParagraphComponentModel
import de.hybris.platform.catalog.CatalogVersionService
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.cms2.model.contents.CMSItemModel
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel
import de.hybris.platform.cms2.model.pages.ContentPageModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.apache.commons.lang.StringUtils

import java.util.function.Function
import java.util.stream.Collectors


typeToPrefixMap = new HashMap<String, String>()
typeToSpecialHandlerMap = new HashMap<String, Function>()

typeToPrefixMap.put(LinkParagraphComponentModel._TYPECODE, "")
typeToPrefixMap.put(ContentPageModel._TYPECODE, "cntPage")
typeToPrefixMap.put(ContentSlotModel._TYPECODE, "")
typeToPrefixMap.put(RecommendationsComponentModel._TYPECODE, "recCmp")
typeToPrefixMap.put(BestsellerComponentModel._TYPECODE, "bestSellerCmp")
typeToPrefixMap.put(CUBPicksComponentModel._TYPECODE, "cubPicksCmp")



def contentPageHandler = new Function<ContentPageModel, Void>() {
    @Override
    Void apply(ContentPageModel contentPage) {
        contentPage.setLabel(contentPage.getUid()) //update the label
        return null
    }
}
typeToSpecialHandlerMap.put(ContentPageModel._TYPECODE, contentPageHandler)

def renameCMS(String typeCode, String name) {
    String prefix = typeToPrefixMap.get(typeCode)
    if (prefix == null) {
        println "not supported:" + typeCode
    }

    if (StringUtils.isBlank(prefix)) { //if prefix is blank, that means to need to rename.
        return name
    }

    return prefix.concat(StringUtils.capitalize(name))
}

def viewOnly() {
    return false
}

def dryRunOnly() {
    return false
}

def execute() {
    final String catalogName = "sabmContentCatalog"
    final String VERSION_ONLINE = "Online"
    final String VERSION_STAGE = "Staged"

    println("Dryrun only:"+dryRunOnly())
    println("View only:"+viewOnly())

    execute(catalogName, VERSION_STAGE)
   // execute(catalogName, VERSION_ONLINE)
}

protected void execute(final String catalogName, final String version) {
    final CatalogVersionService catalogVersionService = spring.getBean("catalogVersionService", CatalogVersionService.class)
    List<String> duplicates = getDuplicatedCMSItemFor(catalogVersionService.getCatalogVersion(catalogName, version))

    if (viewOnly()) {
        printDuplicates(duplicates)
        return
    }

    final List<CMSItemModel> cmsItems = toCmsItems(duplicates)
    rename(cmsItems)
}

def printDuplicates(List<String> duplicates) {
    List<CMSItemModel> cmsItems = toCmsItems(duplicates)
    println(cmsItems.stream().map({ item -> ("(" + item._TYPECODE + ")" + item.getUid()) }).collect(Collectors.joining(',')))
}

def rename(final List<CMSItemModel> cmsItemModels) {
    Map<String, List<CMSItemModel>> cmsItemModelMap = cmsItemModels.stream().collect(Collectors.groupingBy({ cmsItemModel -> cmsItemModel.uid.toLowerCase() }))

    for (Map.Entry<String, List<CMSItemModel>> entry : cmsItemModelMap.entrySet()) {

        entry.value.sort({ cms1, cm2 ->
            cms1._TYPECODE <=> cm2._TYPECODE
        })

        for (CMSItemModel cmsItemModel : entry.value) {
            final String currentUid = cmsItemModel.uid

            String newName = renameCMS(cmsItemModel._TYPECODE, currentUid)
            final Function function = typeToSpecialHandlerMap.get(cmsItemModel._TYPECODE)
            if (function != null) {

                println(String.format("Applying a special handler for [%s] [%s].", currentUid, cmsItemModel._TYPECODE))
                if (!dryRunOnly()) {
                    function.apply(cmsItemModel)
                }

            }

            println(String.format("Setting name of [%s] to [%s] which is a [%s]", currentUid, newName, cmsItemModel._TYPECODE))
            if (!dryRunOnly()) {
                cmsItemModel.setUid(newName)
            }
        }

        entry.value.sort({ cms1, cms2 ->
            def prefix1 = typeToPrefixMap.get(cms1._TYPECODE)
            def prefix2 = typeToPrefixMap.get(cms2._TYPECODE)
            if(prefix1 == null || prefix1 == "") {
                return 1
            }
            if(prefix2 == null || prefix2 == ""){
                return -1
            }

            return 0
        })

        println entry.value.stream().map({ cms -> cms.uid }).collect(Collectors.joining(','))
        if (!dryRunOnly()) {
            for(ItemModel item: entry.value){
                spring.getBean("modelService", ModelService.class).save(item)
            }

        }

    }


}

List<String> getDuplicatedCMSItemFor(final CatalogVersionModel catalogVersionModel) {
    final FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService", FlexibleSearchService.class)

    final String query = "SELECT STRING_AGG({pk},',')  FROM {CMSItem}  WHERE {catalogVersion}=?version GROUP BY {uid} HAVING count(*) > 1"
    FlexibleSearchQuery<Object> flexibleSearchQuery = new FlexibleSearchQuery(query)
    flexibleSearchQuery.setResultClassList(Arrays.asList(String.class))
    flexibleSearchQuery.addQueryParameter("version", catalogVersionModel)

    return flexibleSearchService.search(flexibleSearchQuery).result

}

List<CMSItemModel> toCmsItems(List<String> csvPK) {
    final List<CMSItemModel> cmsItemModelList = new ArrayList<>()
    for (String csv : csvPK) {
        String[] pks = csv.split(",")
        for (String pk : pks) {
            cmsItemModelList.add(getFromPk(pk))
        }
    }
    return cmsItemModelList
}

CMSItemModel getFromPk(final String pk) {
    final ModelService modelService = spring.getBean("modelService", ModelService.class)
    final CMSItemModel cmsItemModel = modelService.get(PK.fromLong(Long.valueOf(pk)))
    return cmsItemModel
}

execute()

