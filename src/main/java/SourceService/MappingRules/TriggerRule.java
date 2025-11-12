package SourceService.MappingRules;

import SourceService.SourceXMIElements.Connector.Connector;
import SourceService.XMIContext;
import model.*;
import registry.TwinRegistry;

public class TriggerRule extends MappingRule<Connector> {
    public TriggerRule(XMIContext xmiContext, TwinRegistry twinRegistry) {
        super(xmiContext, twinRegistry);
    }


    @Override
    public boolean matches(Connector element) {
        return false;
    }

    @Override
    public void apply(Connector element) {
        String sourceId = element.getSource().getSourceId();
        String targetId = element.getTarget().getTargetId();
        String lhsGuid = getXmiContext().getTagValue(element,"lhsProperty").get();
        String rhsGuid = getXmiContext().getTagValue(element,"rhsProperty").get();
        String operator = getXmiContext().getTagValue(element,"operator").get();

        if(operator.isEmpty()){
            operator = "==";
        }

        TwinStrategy twinStrategy = linkStrategyAndComponent(sourceId,targetId);

        TwinComponentProperty lhsProp = getTwinRegistry().getProperty(twinStrategy.getComponent().getId(),lhsGuid);
        TwinComponentProperty rhsProp = getTwinRegistry().getProperty(twinStrategy.getComponent().getId(),rhsGuid);
        TwinComponentProperty lhsConstProp = getTwinRegistry().getProperty(twinStrategy.getComponent().getParentEntity().getId(),lhsGuid);
        TwinComponentProperty rhsConstProp = getTwinRegistry().getProperty(twinStrategy.getComponent().getParentEntity().getId(),rhsGuid);

        if(lhsProp == null){
            twinStrategy.setLhs(lhsConstProp);
        }
        else {
            twinStrategy.setLhs(lhsProp);
        }
        if(rhsProp == null){
            twinStrategy.setRhs(rhsConstProp);
        }
        else {
            twinStrategy.setRhs(rhsProp);
        }

        if(twinStrategy.getLhs() == null || twinStrategy.getRhs() == null){
            throw new IllegalStateException("Invalid Strategy "+twinStrategy.getName()+"\nLeftHandSide or rightHandSide is missing!");
        }

        twinStrategy.setOperator(operator);
    }

    private TwinStrategy linkStrategyAndComponent(String sourceId, String targetId) {
        TwinStrategy twinStrategy = getTwinRegistry().getStrategy(sourceId);
        TwinComponent twinComponent = getTwinRegistry().getComponent(targetId);
        if(twinStrategy == null){
            twinStrategy = getTwinRegistry().getStrategy(targetId);
            twinComponent = getTwinRegistry().getComponent(sourceId);
        }
        if(twinStrategy == null || twinComponent == null){
            throw new IllegalStateException("Strategy or Component not found for a trigger connector;");

        }

        twinStrategy.setComponent(twinComponent);
        twinStrategy.attachToParent();

        return twinStrategy;
    }
}
