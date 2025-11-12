package SourceService.MappingRules;

import SourceService.SourceXMIElements.Attribute.Attribute;
import SourceService.SourceXMIElements.Connector.Connector;
import SourceService.XMIContext;
import model.TwinComponent;
import model.TwinComponentProperty;
import model.TwinEntity;
import registry.TwinRegistry;

public class MeasurePropRule extends MappingRule<Connector>{
    public MeasurePropRule(XMIContext xmiContext, TwinRegistry twinRegistry) {
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
        String attributeGuid = getXmiContext().getTagValue(element,"measurement").get();

        TwinComponent twinComponent = linkComponentAndEntity(sourceId,targetId);
        addProperty(twinComponent,getXmiContext().getAttribute(attributeGuid));
    }

    private void addProperty(TwinComponent twinComponent, Attribute attribute) {
        TwinComponentProperty twinComponentProperty = new TwinComponentProperty();
        twinComponentProperty.setPropertyGuid(attribute.getEaGuid());
        twinComponentProperty.setName(attribute.getName());
        twinComponentProperty.setDataType(getXmiContext().getTagValue(attribute,"dataType").get());
        twinComponentProperty.setComponent(twinComponent);
        twinComponentProperty.attachToParent();

        getTwinRegistry().registerProperty(twinComponent.getId(),twinComponentProperty.getPropertyGuid(),twinComponentProperty);

    }

    private TwinComponent linkComponentAndEntity(String sourceId, String targetId) {
        TwinEntity twinEntity = getTwinRegistry().getEntity(sourceId);
        TwinComponent twinComponent = getTwinRegistry().getComponent(targetId);
        if(twinEntity == null){
            twinEntity = getTwinRegistry().getEntity(targetId);
            twinComponent = getTwinRegistry().getComponent(sourceId);
        }
        if(twinEntity == null || twinComponent == null){
            throw new IllegalStateException("Entity or Component not found for a measure connector;");

        }

        twinComponent.setParentEntity(twinEntity);
        twinComponent.attachToParent();

        return twinComponent;
    }
}
