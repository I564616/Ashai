package com.sabmiller.core.jobs;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.BDECustomerImportedModel;
import com.sabmiller.core.model.BDEToB2BUnitRelationImportCronJobModel;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhuo.a.jiang on 13/9/18.
 */
public class BDEToB2BUnitRelationImportJob extends AbstractJobPerformable<BDEToB2BUnitRelationImportCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(BDEToB2BUnitRelationImportJob.class);
    private static final String DEFAULT_SEPARATOR = "|";

    private static final String FAILEDMESSAGE = "BED to B2BUnit relation import failed";

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "mediaService")
    private MediaService mediaService;

    @Resource(name = "sabmSFTPService")
    private SabmSFTPService sabmSFTPService;

    @Resource(name = "b2bUnitService")
    private SabmB2BUnitService b2bUnitService;

    @Resource(name = "sabmB2BCustomerService")
    private SabmB2BCustomerService sabmB2BCustomerService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "sabmCronJobStatus")
    private SabmCronJobStatus sabmCronJobStatus;

    private static final String DEFAULT_PASSWORD_ENCODING = "md5";
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()+";
    private static final int PASSWORD_LENGTH = 16;
    private static final String DEFAULT_B2BINVOICECUSTOMR_USERGROUP = "b2binvoicecustomer";



    private static final String EMAIl_PATTERN = "[^a-zA-Z0-9!#$%&@'*+-/=?^_`{|}~.]+";


    int b2bUnitNotExistsCount = 0;
    int invalidEmail = 0;
    int emptyLine = 0;
    int numberOf_onPremise_kam_user =0 ;
    int numberOf_offPremise_kam_user =0 ;
    int numberOf_bde_user =0 ;

    final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

    @Override
    public PerformResult perform(BDEToB2BUnitRelationImportCronJobModel cronJob) {
        CSVReader csvReader = null;
        boolean SFTPFileExists = false;
        java.io.File SFTPfile = null;
        boolean status = true;
        LOG.info("BED to B2BUnit relation import started");


        try {
            //Reading the CSV file
            if (!cronJob.getFallbackRequired()) {
                SFTPfile = sabmSFTPService.getCSVFile(cronJob.getFileName());
                csvReader = new CSVReader(SFTPfile, CSVConstants.HYBRIS_ENCODING);
                SFTPFileExists = true;
            } else {
                csvReader = new CSVReader(mediaService.getStreamFromMedia(cronJob.getInputFile()), CSVConstants.HYBRIS_ENCODING);
            }

            //Delete the existing BDECustomerImported from Sebiel
            final List<BDECustomerImportedModel> list = sabmB2BCustomerService.getBDECustomerImportedAll();
            if (CollectionUtils.isNotEmpty(list)) {
                modelService.removeAll(list);
            }

            //Parsing the CSV and storing fresh BDECustomerImported to B2BUnit relation
            while (csvReader.readNextLine()) {
                final String currentLine = csvReader.getSourceLine();
                final List<String> splittedValues = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(currentLine, DEFAULT_SEPARATOR));
                if (CollectionUtils.isNotEmpty(splittedValues)) {
                    if (csvReader.getCurrentLineNumber() != 1 && !storeContents(splittedValues,csvReader.getCurrentLineNumber())) {
                        skipLineException(csvReader.getCurrentLineNumber());

                    }
                } else {
                    skipLineException(csvReader.getCurrentLineNumber());
                    emptyLine++;

                }
            }
            LOG.info("BED to B2BUnit relation import successful!");
            LOG.info("b2bUnitNotExistsCount " + b2bUnitNotExistsCount);
            LOG.info("invalidEmail " + invalidEmail);
            LOG.info("emptyLine " + emptyLine);
            LOG.info("numberOf_onPremise_kam_user " + numberOf_onPremise_kam_user);
            LOG.info("numberOf_offPremise_kam_user " + numberOf_offPremise_kam_user);
            LOG.info("numberOf_bde_user " + numberOf_bde_user);

            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final JSchException je) {
            LOG.error(FAILEDMESSAGE + " due to :", je.getMessage());
            status = false;
        } catch (final SftpException se) {
            LOG.error(FAILEDMESSAGE + " due to :", se.getMessage());
            status = false;
        } catch (final Exception e) {
            LOG.error(FAILEDMESSAGE + " due to :", e.getMessage());
            status = false;
        } finally {
            SabmCSVUtils.closeReaderQuietly(csvReader);
            if (SFTPFileExists) {
                FileUtils.deleteQuietly(SFTPfile);
            }

            if (!status)
            {
                sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
            }
        }

        return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
    }

    private void skipLineException(final int lineNumber) {
        LOG.debug("Error. Skipping line: " + lineNumber);
    }

    public boolean storeContents(final List<String> splittedValues, final int lineNumber) {

        // sample BDE CSV file header
        //OUTLET_NUMBER|OUTLET_NAME|SALES_STATE|SALES_STATE_DESC|ZALB_NUM|ON_PREMISE_KAM_EMP_ID|ON_PREMISE_KAM_FULL_NAME|ON_PREM_KAM_EMAIL_ADDR
        //|OFF_PREMISE_KAM_EMP_ID|OFF_PREMISE_KAM_FULL_NAME|OFF_PREM_KAM_EMAIL_ADDR|BDE_FULL_NAME|BDE_AD_USER_ID|BDE_EMAIL_ADDR
        try {
            if (splittedValues.size() >= 11) {
                final String bde_fullName =splittedValues.get(9);
                final String bde_email = splittedValues.get(10).replaceAll(EMAIl_PATTERN,StringUtils.EMPTY);

                final String onPremise_kam_fullName = splittedValues.get(5);
                final String onPremise_kam_email =splittedValues.get(6).replaceAll(EMAIl_PATTERN,StringUtils.EMPTY);

                final String offPremise_kam_fullName = splittedValues.get(7);
                final String offPremise_kam_email =splittedValues.get(8).replaceAll(EMAIl_PATTERN,StringUtils.EMPTY);

                String b2BUnit = StringUtils.trim(splittedValues.get(4));

                b2BUnit = SabmStringUtils.addLeadingZeroes(b2BUnit);

                // find if b2bUnit exists

                B2BUnitModel unit = b2bUnitService.getUnitForUid(b2BUnit);

                if (Objects.isNull(unit)) {
                    LOG.debug(b2BUnit + " doesn't exist.");
                    b2bUnitNotExistsCount++;
                    return false;
                } else {



                    if (StringUtils.isNotEmpty(bde_fullName) && StringUtils.isNotEmpty(bde_email)) {
                        // getOrCreate BDECustomerImported object
                        getOrCreateBDECustomerImported(unit, bde_email, bde_fullName,lineNumber);
                        LOG.debug("bde user");
                        numberOf_bde_user++;

                    }
                    if(StringUtils.isNotEmpty(onPremise_kam_fullName) && StringUtils.isNotEmpty(onPremise_kam_email)){
                        getOrCreateBDECustomerImported(unit, onPremise_kam_email, onPremise_kam_fullName,lineNumber);
                        LOG.debug("onPremise_kam_user");
                        numberOf_onPremise_kam_user++;
                    }
                    if(StringUtils.isNotEmpty(offPremise_kam_fullName) && StringUtils.isNotEmpty(offPremise_kam_email)){
                        getOrCreateBDECustomerImported(unit, offPremise_kam_email,offPremise_kam_fullName,lineNumber);
                        LOG.debug("offPremise_kam_user");
                        numberOf_offPremise_kam_user++;

                    }
                }
                return true;
            }

        } catch (Exception e) {
            LOG.error("error during getOrCreateBDECustomerImported at line: " +lineNumber + " - " + e.getMessage() );
        }

        return false;
    }

    private BDECustomerImportedModel getOrCreateBDECustomerImported(final B2BUnitModel b2bUnitModel, final String emailAddress,
            final String fullName, final int lineNumber) {
        BDECustomerImportedModel bdeCustomerImported = null;
        try {

            if (null != b2bUnitModel) {

                final String uid = emailAddress+("_siebel");
                bdeCustomerImported = sabmB2BCustomerService.getBDECustomerImported(uid);

                if (bdeCustomerImported == null) {
                    bdeCustomerImported = modelService.create(BDECustomerImportedModel.class);
                    bdeCustomerImported.setUid(uid);
                    bdeCustomerImported.setEmail(emailAddress);
                    final String password = generateComplexPassword();
                    userService.setPassword(bdeCustomerImported, password, DEFAULT_PASSWORD_ENCODING);
                    bdeCustomerImported.setName(fullName);

                    final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();

                    groups.add(userService.getUserGroupForUID(DEFAULT_B2BINVOICECUSTOMR_USERGROUP));
                    bdeCustomerImported.setGroups(groups);

                }

                // bdeCustomerImported.setFirstName(getUserService().getCurrentUser().getName());

                final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();

                groups.addAll(bdeCustomerImported.getGroups());
                groups.add(b2bUnitModel);

                bdeCustomerImported.setGroups(groups);

                modelService.save(bdeCustomerImported);
                modelService.refresh(bdeCustomerImported);
            }
        } catch (Exception e) {
            LOG.error("error during getOrCreateBDECustomerImported" + e.getMessage() + "for detail: " + emailAddress + " - "+b2bUnitModel.getUid() + "at line: " +lineNumber );
            invalidEmail++;
        }
        return bdeCustomerImported;

    }

    protected String generateComplexPassword() {
        final StringBuilder sb = new StringBuilder();
        sb.append(UPPERCASE_LETTERS).append(LOWERCASE_LETTERS).append(NUMBERS).append(SYMBOLS);

        final char[] characterSet = sb.toString().toCharArray();

        final Random random = new SecureRandom();
        final char[] result = new char[PASSWORD_LENGTH];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            final int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }


    protected SabmSFTPService getSabmSFTPService() {
        return sabmSFTPService;
    }

    public void setSabmSFTPService(SabmSFTPService sabmSFTPService) {
        this.sabmSFTPService = sabmSFTPService;
    }

    protected SabmB2BUnitService getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(SabmB2BUnitService b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }
}
