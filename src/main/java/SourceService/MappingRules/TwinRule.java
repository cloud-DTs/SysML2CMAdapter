package SourceService.MappingRules;

import SourceService.SourceXMIElements.Element.EAElement;
import SourceService.SourceXMIElements.Mapper.Stereotypes;
import SourceService.XMIContext;
import model.TwinDataBase;
import model.TwinEntity;
import registry.TwinRegistry;

import java.util.List;

public class TwinRule extends MappingRule<EAElement> {

    public TwinRule(XMIContext xmiContext, TwinRegistry twinRegistry) {
        super(xmiContext,twinRegistry);
    }

    @Override
    public boolean matches(EAElement element) {
        return element.getStereotypeName().equals(Stereotypes.TWIN);
    }

    @Override
    public void apply(EAElement element) {
        if(element.getIdref() == null){
            return;
        }
        addTwinAsEntity(element);
    }

    private void addTwinAsEntity(EAElement element) {
        TwinEntity twinEntity = new TwinEntity();
        twinEntity.setEntityId(element.getIdref());
        twinEntity.setEntityName(element.getName());
        twinEntity.setDefinitionId(element.getIdref());

        DatabasePair databasePair = buildDatabases(element);
        twinEntity.setCold(databasePair.cold);
        twinEntity.setHot(databasePair.hot);
        getTwinRegistry().registerEntity(element.getIdref(),twinEntity);
    }


    private DatabasePair buildDatabases(EAElement twinElement) {
        List<EAElement> databaseInstances = getXmiContext().getElementsByOwner(
                SourceService.SourceXMIElements.Mapper.Stereotypes.DATABASE_INSTANCE,
                twinElement.getIdref()
        );

        if (databaseInstances.size() != 2) {
            throw new IllegalStateException(
                    String.format("Twin '%s' must have exactly 2 database instances (hot and cold), found: %d",
                            twinElement.getName(), databaseInstances.size()));
        }

        EAElement db1 = databaseInstances.get(0);
        EAElement db2 = databaseInstances.get(1);

        EAElement db1Definition = getXmiContext().findDefinitonBlockForInstance(db1);
        EAElement db2Definition = getXmiContext().findDefinitonBlockForInstance(db2);

        TwinDataBase twinDataBase1 = new TwinDataBase();
        twinDataBase1.setDataBaseId(db1.getIdref());
        twinDataBase1.setDefinitionId(db1Definition.getIdref());
        twinDataBase1.setRetentionDays(getRetentionDays(db2));

        TwinDataBase twinDataBase2 = new TwinDataBase();
        twinDataBase2.setDataBaseId(db2.getIdref());
        twinDataBase2.setDefinitionId(db2Definition.getIdref());
        twinDataBase2.setRetentionDays(getRetentionDays(db2));


        if (getTier(db1).contains("HOT") && getTier(db2).contains("COLD")) {

            return new DatabasePair(twinDataBase1, twinDataBase2);
        } else if (getTier(db1).contains("COLD") && getTier(db2).contains("HOT")) {
            return new DatabasePair(twinDataBase2, twinDataBase1);
        } else {
            throw new IllegalStateException(
                    String.format("Twin '%s' must have exactly one HOT and one COLD database", twinElement.getName()));
        }
    }

    private String getRetentionDays(EAElement db2) {
        return getXmiContext().getTaggedValue(db2,"retentionPeriod");
    }

    private String getTier(EAElement databaseElement){

        return getXmiContext().getTaggedValue(databaseElement,"dataBaseTier");
    }
    private static class DatabasePair {
        final TwinDataBase hot;
        final TwinDataBase cold;

        DatabasePair(TwinDataBase hot, TwinDataBase cold) {
            this.hot = hot;
            this.cold = cold;
        }
    }
}
