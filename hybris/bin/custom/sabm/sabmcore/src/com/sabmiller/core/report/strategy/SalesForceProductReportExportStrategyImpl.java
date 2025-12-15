package com.sabmiller.core.report.strategy;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class SalesForceProductReportExportStrategyImpl implements DefaultProductReportExportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SalesForceProductReportExportStrategyImpl.class);

	private static final String DATE_PATTERN = "yyyyMMddHHmmss";
	private static final String FIND_VARIANT_PRODUCT = "select {pk} from {SABMAlcoholVariantProductEAN! as VP} ,{CatalogVersion as CV}, {Catalog as C} where {VP:catalogversion}={CV:PK} AND {CV:catalog}={C:PK} AND {C:id}='sabmProductCatalog' AND {CV:version}='Online'";

    private FlexibleSearchService flexibleSearchService;
    private SabmCSVFileGenerator sabmCSVFileGenerator;
    private SabmSftpFileUpload sabmSftpFileUpload;
    
    @Resource(name = "sabmSFTPService")
 	 private SabmSFTPService sabmSFTPService;

    @Override
    public List<String> getHeaderLine(final List<String> headers) {

		headers.add("Ean");
        headers.add("Name");
        headers.add("ApproveStatus");
        headers.add("PackageType");
        headers.add("Size");
        headers.add("PackageConfiguration");
        headers.add("Weight");
        headers.add("Length");
        headers.add("Width");
        headers.add("Height");
		  headers.add("LeadSku");
        headers.add("IsNewProduct");
        headers.add("IsPurchasable");

        return headers;

    }

    @Override
    public List<List<String>> getProductReportData() {

        final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_VARIANT_PRODUCT);
        final SearchResult<SABMAlcoholVariantProductEANModel> result = flexibleSearchService.search(fsq);

        if (CollectionUtils.isEmpty(result.getResult())) {
            LOG.info("Nothing to process");
            return null;
        }
        final List<List<String>> units = new ArrayList<List<String>>();
        for (final SABMAlcoholVariantProductEANModel row : result.getResult()) {

            final List<String> unitData = new ArrayList<>();

            unitData.add(row.getCode());
			unitData.add(SabmStringUtils.trimToEmpty(getProductTitle(row)));
			unitData.add(SabmStringUtils.trimToEmpty(row.getApprovalStatus().getCode()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getContainer()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getCapacity()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getPresentation()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getWeight()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getLength()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getWidth()));
			unitData.add(SabmStringUtils.trimToEmpty(row.getHeight()));
			unitData.add(row.getLeadSku() != null ? SabmStringUtils.trimToEmpty(row.getLeadSku().getCode()) : "");
			unitData.add(SabmStringUtils.trimToEmpty(BooleanUtils.toStringTrueFalse(row.getIsNewProduct())));
			unitData.add(SabmStringUtils.trimToEmpty(BooleanUtils.toStringTrueFalse(row.getPurchasable())));

            units.add(unitData);

        }
        return units;

    }

    private String getProductTitle(final SABMAlcoholVariantProductEANModel product)
 	{
 		if (product == null)
 		{
 			return "";
 		}
 		if (StringUtils.isNotEmpty(product.getSellingName()) && StringUtils.isNotEmpty(product.getPackConfiguration()))
 		{
 			return product.getSellingName() + " " + product.getPackConfiguration();
 		}
 		return product.getName();
 	}

    @Override
    public void uploadFileToSFTP() throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        final List<String> headers = new ArrayList<String>();
        final String fileExt = ".csv";
        final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("sku"), sdf.format(new Date()) + "_Sku", fileExt,
                getProductReportData(), getHeaderLine(headers));
        PGPPublicKey key = null;
        final String encryptedFileName = SabmCSVUtils.getFullPath("sku") + File.separator + sdf.format(new Date()) + "_Sku" + ".csv.pgp";
        key = PGPUtils.readPublicKey(Config.getString("salesforce.encryptionkey", ""));
        try (final OutputStream out = new FileOutputStream(encryptedFileName)) {
            PGPUtils.encryptFile(out, file, key, false, false);
        } catch (Exception e) {
            LOG.error("Exception while encrypting file:", e);
            throw e;
        }
        //sabmSftpFileUpload.upload(new File(encryptedFileName));
        sabmSFTPService.uploadCSVFile(file, Config.getString("sabm.sftp.salesforce_pardot.remote.directory", ""));
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }


    public SabmCSVFileGenerator getSabmCSVFileGenerator() {
        return sabmCSVFileGenerator;
    }

    public void setSabmCSVFileGenerator(final SabmCSVFileGenerator sabmCSVFileGenerator) {
        this.sabmCSVFileGenerator = sabmCSVFileGenerator;
    }

    public SabmSftpFileUpload getSabmSftpFileUpload() {
        return sabmSftpFileUpload;
    }

    public void setSabmSftpFileUpload(final SabmSftpFileUpload sabmSftpFileUpload) {
        this.sabmSftpFileUpload = sabmSftpFileUpload;
    }

}
