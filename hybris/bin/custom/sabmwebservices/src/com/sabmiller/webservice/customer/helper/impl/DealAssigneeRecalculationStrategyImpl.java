package com.sabmiller.webservice.customer.helper.impl;

import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.model.SalesOrgDataModel;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.helper.DealAssigneeRecalculationStrategy;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DealAssigneeRecalculationStrategyImpl implements DealAssigneeRecalculationStrategy {

    private static final String DELAY_TIME_UNIT = "com.sabmiller.webservice.customer.helper.impl.dealassigneerecalculationstrategyimpl.delay.timeunit";
    private static final String DELAY_VALUE = "com.sabmiller.webservice.customer.helper.impl.dealassigneerecalculationstrategyimpl.delay.value";
    private static final String INITIAL_CRON_EXPRESSION = "com.sabmiller.webservice.customer.helper.impl.dealassigneerecalculationstrategyimpl.initialcronexpression";


    private static final Logger LOG = LoggerFactory.getLogger(DealAssigneeRecalculationStrategyImpl.class);

    private CronJobService cronJobService;
    private ModelService modelService;
    private String cronJobCode;
    private ConfigurationService configurationService;

    private static final int DEFAULT_DELAY_VALUE = 30;

    private WeakReference<CronJobModel> cronJob;

    @Override
    public boolean requiresDealAssigneeRecalculation(B2BUnitModel b2BUnit, Customer customer) {

        if(!equalSalesOrgData(b2BUnit,customer)){
            return true;
        }

        if(!equalSalesData(b2BUnit,customer)){
            return true;
        }

        if(!equalSapGroup(b2BUnit,customer)){
            return true;
        }

        return false;
    }

    @Override
    public void recalculateDealAssignees() {

        final CronJobModel cronJobModel = getCronJob();

        if(cronJobModel == null){ // ahh bugger.. fix it!!!
            return;
        }

        getModelService().refresh(cronJobModel);

        if(BooleanUtils.isNotTrue(cronJobModel.getActive())){
            return;
        }

        //get the triggers
        final List<TriggerModel> triggers = new ArrayList<>(cronJobModel.getTriggers());

        if(CollectionUtils.isEmpty(triggers)){ // nope, nothin in there. unlikely to happen though
            final TriggerModel trigger = createTrigger(cronJobModel); // create with initial cron expression
            reschedule(trigger); // save separately to avoid clash with cronexpression
            return;
        }

        final TriggerModel trigger = triggers.get(0); // sort of dumb can't think of anything

        if(StringUtils.isEmpty(trigger.getCronExpression())){ // just additional safety checks. unlikely to happen
            trigger.setCronExpression(getInitialCronExpression());
            modelService.save(trigger);// need to save early, else it might clash with setActivationTime call
        }

        if(!shouldReschedule(trigger)){ // do we require to resched?
            return; // nope
        }

        reschedule(trigger);
    }


    /**
     * Checks if a reschedule is required
     * @param trigger
     * @return
     */
    private boolean shouldReschedule(final TriggerModel trigger){
        final Date activationTime = trigger.getActivationTime();

        return activationTime == null || activationTime.before(Calendar.getInstance().getTime());
    }

    private void reschedule(final TriggerModel triggerModel){
        triggerModel.setActivationTime(getNextSchedule());
        getModelService().save(triggerModel);
    }



    private TriggerModel createTrigger(final CronJobModel parent){
        final TriggerModel trigger = getModelService().create(TriggerModel.class);
        trigger.setCronJob(parent);
        trigger.setActive(true);
        trigger.setCronExpression(getInitialCronExpression());
        getModelService().save(trigger);

        return trigger;
    }

    private String getInitialCronExpression(){
        return getConfigurationService().getConfiguration().getString(INITIAL_CRON_EXPRESSION);
    }

    /**
     * Get's the next schedule to run the job based on configurations
     */
    private Date getNextSchedule(){
        final DateTime now = DateTime.now();
        final int delay = getConfigurationService().getConfiguration().getInt(DELAY_VALUE,DEFAULT_DELAY_VALUE);

        switch (getConfiguredDelayTimeUnit()) {
            case HOURS:
                return now.plusHours(delay).toDate();
            case SECONDS:
                return now.plusSeconds(delay).toDate();
            default:
                return now.plusMinutes(delay).toDate();
        }
    }

    private DelayTimeUnit getConfiguredDelayTimeUnit(){
        final String configuredDelayTimeUnit = getConfigurationService().getConfiguration().getString(DELAY_TIME_UNIT);

        if(StringUtils.isEmpty(configuredDelayTimeUnit)){
            return DelayTimeUnit.MINUTES;
        }

        try{
            return DelayTimeUnit.valueOf(configuredDelayTimeUnit);
        }catch (Exception e){
            LOG.warn("Invalid delay time unit set {}",configuredDelayTimeUnit);
        }

        return DelayTimeUnit.MINUTES;
    }

    /**
     * Helper method to get the cronjob either from weakreference <-- get's cleared if it's running out of memory
     * @return
     */
    private synchronized CronJobModel getCronJob(){
        CronJobModel cronJobModel;
        if(cronJob == null){
            cronJobModel = reload();
        }else{
            cronJobModel = cronJob.get();
            if(cronJobModel == null){
                cronJobModel = reload();
            }
        }

        if(cronJobModel != null){
            cronJob = new WeakReference<>(cronJobModel);
        }

        return cronJobModel;
    }

    private CronJobModel reload(){
        try {
            return getCronJobService().getCronJob(getCronJobCode());
        }catch (Exception e){
            LOG.warn("Unable to retrieve cronjob for deal assignee recalculation {}",getCronJobCode());
        }

        return null;
    }

    protected boolean equalSalesOrgData(final B2BUnitModel b2BUnit,final Customer customer){
        final SalesOrgDataModel unitSalesOrgData =  b2BUnit.getSalesOrgData();
        final Customer.SalesOrgData customerSalesOrgData = CollectionUtils.isEmpty(customer.getSalesOrgData())?null:customer.getSalesOrgData().get(0);

        if(bothNull(unitSalesOrgData,customerSalesOrgData)){
            return true;
        }

        if(isOtherNull(unitSalesOrgData,customerSalesOrgData)){ // if the other is null
            return false;
        }

        // if we reach here, means that both are not null. we'll compare per value

        if(!Objects.equals(unitSalesOrgData.getSalesGroup(),customerSalesOrgData.getSalesGroup())){
            return false;
        }

        if(!Objects.equals(unitSalesOrgData.getSalesOfficeCode(),customerSalesOrgData.getSalesOfficeCode())){
            return false;
        }

        if(!Objects.equals(unitSalesOrgData.getCustomerGroup(),customerSalesOrgData.getCustomerGroup())){
            return false;
        }

        if(!Objects.equals(unitSalesOrgData.getPriceGroup(),customerSalesOrgData.getPriceGroup())){
            return false;
        }

        return true;
    }

    protected boolean equalSapGroup(final B2BUnitModel b2BUnit, final Customer customer){
        final B2BUnitGroupModel unitB2bUnitGroup = b2BUnit.getSapGroup();
        final Customer.Common.CustERPRplctnReqCom customerB2bUnitGroup =  customer.getCommon() != null ? customer.getCommon().getCustERPRplctnReqCom():null;

        if(bothNull(unitB2bUnitGroup,customerB2bUnitGroup)){
            return true;
        }

        if(isOtherNull(unitB2bUnitGroup,customerB2bUnitGroup)){ // if the other is null
            return false;
        }

        //compare per value

        if(!Objects.equals(unitB2bUnitGroup.getGroupKey(),customerB2bUnitGroup.getGroupKey())){
            return false;
        }

        if(!Objects.equals(unitB2bUnitGroup.getPrimaryGroupKey(),customerB2bUnitGroup.getPrimaryGroupKey())){
            return false;
        }

        if(!Objects.equals(unitB2bUnitGroup.getSubGroupKey(),customerB2bUnitGroup.getSubGroupKey())){
            return false;
        }

        if(!Objects.equals(unitB2bUnitGroup.getSubChannel(),customerB2bUnitGroup.getSubChannel())){
            return false;
        }

        return true;
    }

    protected boolean equalSalesData(final B2BUnitModel b2BUnit, final Customer customer){
        final SalesDataModel unitSalesData = b2BUnit.getSalesData();
        final Customer.SalesData customerSalesData = CollectionUtils.isEmpty(customer.getSalesData())?null:customer.getSalesData().get(0);

        if(bothNull(unitSalesData,customerSalesData)){
            return true;
        }

        if(isOtherNull(unitSalesData,customerSalesData)){
            return false;
        }


        // per value comparison
        if(!Objects.equals(unitSalesData.getDivision(),customerSalesData.getDivision() == null? null:customerSalesData.getDivision().getValue())){
            return false;
        }

        if(!Objects.equals(unitSalesData.getDistributionChannel(),customerSalesData.getDistributionChannel() == null?null:customerSalesData.getDistributionChannel().getValue())){
            return false;
        }

        // check plant id
        final PlantModel plant = b2BUnit.getPlant();

        if(!Objects.equals(plant == null?null:plant.getPlantId(),customerSalesData.getDefaultDeliveryPlant() == null?null:customerSalesData.getDefaultDeliveryPlant().getValue())){
            return false;
        }

        return true;
    }

    public enum DelayTimeUnit{
        SECONDS,
        MINUTES,
        HOURS
    }

    /**
     * Extra carefull, this is like a helper method only, actual usage is to return true if one == two, but in our case,
     * we are using it to verify if both values are null. we'll pass entirely different objects
     * @param one
     * @param two
     * @return
     */
    private boolean bothNull(final Object one, Object two){
        return one == two;
    }

    /**
     * Helper method to check if the other is null but not the other, haha.
     * @param one
     * @param two
     * @return
     */
    private boolean isOtherNull(final Object one,final Object two){
        return (one == null && two != null) || (one != null && two == null);
    }


    protected CronJobService getCronJobService() {
        return cronJobService;
    }

    public void setCronJobService(CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    protected String getCronJobCode() {
        return cronJobCode;
    }

    public void setCronJobCode(String cronJobCode) {
        this.cronJobCode = cronJobCode;
    }

    public void setCronJob(WeakReference<CronJobModel> cronJob) {
        this.cronJob = cronJob;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }


    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
