package com.apb.core.services.impl;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.impl.DefaultOrgUnitHierarchyService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;

/**
 * The Class AsahiOrgUnitHierarchyServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiOrgUnitHierarchyServiceImpl extends DefaultOrgUnitHierarchyService
{
    private static final int MAX_CHILD_LOOPS = 10;

    private static final Logger LOG = LoggerFactory.getLogger(AsahiOrgUnitHierarchyServiceImpl.class);

    private static final String CALCULATE_ORG_UNIT_PATHS_ON_UPDATE_SYSTEM = "calculate.orgunit.paths.on.update.system";

    private static final String UPDATE_ROOT = "UPDATE usergroups root\n" +
            "SET root.p_path = '" + DELIMITER + "' || root.p_uid\n" +
            "WHERE (root.p_path IS NULL OR root.p_path !=  '" + DELIMITER + "' || root.p_uid)\n" +
            "      AND EXISTS(SELECT 1\n" +
            "                 FROM composedtypes ct\n" +
            "                 WHERE root.TypePkString = ct.PK AND\n" +
            "                       ct.internalcodelowercase = ?) AND\n" +
            "      NOT EXISTS(SELECT 1\n" +
            "                 FROM pgrels pgr\n" +
            "                 WHERE pgr.sourcepk = root.pk)";

    private static final String UPDATE_CHILDREN = "INSERT INTO usergroups t1\n" +
            "USING (SELECT\n" +
            "         child.pk                            AS pk,\n" +
            "         parent.p_path || '" + DELIMITER + "' || child.p_uid AS p_path\n" +
            "       FROM usergroups child\n" +
            "         LEFT JOIN pgrels pgr ON (child.pk = pgr.sourcepk)\n" +
            "         LEFT JOIN usergroups parent ON (parent.pk = pgr.targetpk)\n" +
            "         LEFT JOIN composedtypes ct ON (parent.TypePkString = ct.PK AND child.TypePkString = ct.PK)\n" +
            "       WHERE ct.internalcodelowercase = ?\n" +
            "             AND (child.p_path IS NULL OR child.p_path != parent.p_path || '" + DELIMITER + "' || child.p_uid)) t2\n" +
            "ON (t1.pk = t2.pk)\n" +
            "WHEN MATCHED THEN UPDATE SET t1.p_path = t2.p_path";
    
    @Resource
    private AsahiConfigurationService asahiConfigurationService;
    
    @Resource(name = "baseStoreService")
    protected BaseStoreService baseStoreService;
    
    @Resource
 	 private AsahiSiteUtil asahiSiteUtil;

    //Need to validate for ALB/APB after CUB merge
    @Override
    public synchronized <T extends OrgUnitModel> void generateUnitPaths(final Class<T> unitType)
    {
   	 if(!asahiSiteUtil.isCub())
   	 {
           if (this.asahiConfigurationService.getBoolean(CALCULATE_ORG_UNIT_PATHS_ON_UPDATE_SYSTEM, true))
           {
               LOG.info("Generate Unit Paths for type {}", unitType.getSimpleName());
               if (isMySQLUsed())
               {
                   generateUnitPathsForMySQL(unitType);
               } else
               {
                  superGenerateUnitPaths(unitType);
               }
           }
   	 }
   	 else
   	 {
   		 super.generateUnitPaths(unitType);
   	 }
 		}

    private <T extends OrgUnitModel> void generateUnitPathsForMySQL(Class<T> unitType)
    {
        String modelName = unitType.getSimpleName().substring(0, unitType.getSimpleName().indexOf("Model")).toLowerCase();
        try (Connection connection = getCurrentTenantConnection())
        {
            updateRootUnitPaths(modelName, connection);
            updateChildrenUnitPaths(modelName, connection);
        } catch (SQLException e)
        {
            LOG.error("Error updating unit path values", e);
        }
    }

    private void updateChildrenUnitPaths(final String modelName, final Connection connection) throws SQLException
    {
        LOG.info("Updating Unit Paths for children {}", modelName);
        int totalUpdated = 0;
        for (int i = 1; i <= MAX_CHILD_LOOPS; i++)
        {
            int updateCount;
            try (PreparedStatement st = connection.prepareStatement(UPDATE_CHILDREN))
            {
                st.setString(1, modelName);
                updateCount = st.executeUpdate();
            }
            totalUpdated += updateCount;
            if (updateCount == 0 )
            {
                LOG.info("Finished updating children, number of {} rows impacted {}", modelName, totalUpdated);
                break;
            } else
            {
                LOG.info("Updated {} {} unit path rows", modelName, updateCount);
            }

            if (i == MAX_CHILD_LOOPS) {
                LOG.warn("Hit max loop limit of {} for updating child paths, this probably means bad data, number of {} rows updated {}", MAX_CHILD_LOOPS, modelName, totalUpdated);
            }
        }
    }

    private void updateRootUnitPaths(final String modelName, final Connection connection) throws SQLException
    {
        try (PreparedStatement st = connection.prepareStatement(UPDATE_ROOT))
        {
            LOG.info("Updating Root {}", modelName);
            st.setString(1, modelName);
            int count = st.executeUpdate();
            LOG.info("Updated Root {} rows {}", modelName, count);
        }
    }
  //Need to validate for ALB/APB after CUB merge
    @Override
    protected Set<OrgUnitModel> generatePathForUnit(final OrgUnitModel unit, final OrgUnitModel parentUnit)
    {
   	 if(!asahiSiteUtil.isCub())
   	 {
           beforeUpdate(unit, parentUnit);
   
           final Set<OrgUnitModel> unitsToSave = new HashSet<>();
           final StringBuilder pathBuilder = new StringBuilder();
   
           if (parentUnit != null)
           {
               validatePath(parentUnit);
               pathBuilder.append(parentUnit.getPath());
           }
           pathBuilder.append(DELIMITER).append(unit.getUid());
           final String path = pathBuilder.toString();
   
           // Performance optimization
           // Do not bother setting the path and making it dirty (forcing a save)
           if (!path.equals(unit.getPath()))
           {
               unit.setPath(path);
               unitsToSave.add(unit);
   
               LOG.debug("Path for unit [{}]: {}", unit.getUid(), path);
           }
   
           getChildrenOfSameType(unit)
                   .forEach(child -> unitsToSave.addAll(generatePathForUnit(child, unit)));

        return unitsToSave;
   	 }
   	 else
   	 {
   		 return super.generatePathForUnit(unit,parentUnit);
   	 }
    }

    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    protected Connection getCurrentTenantConnection() throws SQLException
    {
        return Registry.getCurrentTenant().getDataSource().getConnection();
    }

    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    protected <T extends OrgUnitModel> void superGenerateUnitPaths(Class<T> unitType)
    {
        super.generateUnitPaths(unitType);
    }

    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    protected boolean isMySQLUsed()
    {
        return Config.isMySQLUsed();
    }

    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    @Override
    protected void beforeUpdate(final OrgUnitModel unit, final OrgUnitModel parentUnit)
    {
        super.beforeUpdate(unit, parentUnit);
    }

    @SuppressWarnings("common-java:InsufficientBranchCoverage")
    @Override
    protected Set<OrgUnitModel> getChildrenOfSameType(final OrgUnitModel unit)
    {
        return super.getChildrenOfSameType(unit);
    }
}