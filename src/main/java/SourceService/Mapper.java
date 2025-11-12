package SourceService;

import SourceService.MappingRules.*;
import SourceService.SourceXMIElements.Attribute.Attribute;
import SourceService.SourceXMIElements.Connector.Connector;
import SourceService.SourceXMIElements.Element.EAElement;
import SourceService.SourceXMIElements.Mapper.Stereotypes;
import model.TwinEntity;
import registry.TwinRegistry;

import java.util.List;

public class Mapper {

    public XMIParser twinParser;
    public TwinRegistry twinRegistry;
    public XMIContext xmiContext;


    public Mapper(XMIParser twinParser, TwinRegistry twinRegistry, XMIContext xmiContext) {
        this.twinParser = twinParser;
        this.twinRegistry = twinRegistry;
        this.xmiContext = xmiContext;
    }


    public List<TwinEntity> parse(){
        TwinRule twinRule = new TwinRule(xmiContext,twinRegistry);
        ComponentRule componentRule = new ComponentRule(xmiContext,twinRegistry);
        DeviceRule deviceRule = new DeviceRule(xmiContext,twinRegistry);
        StrategyRule strategyRule = new StrategyRule(xmiContext,twinRegistry);
        ConstPropRule constPropRule = new ConstPropRule(xmiContext,twinRegistry);
        MeasurePropRule meaPropRule = new MeasurePropRule(xmiContext,twinRegistry);
        TriggerRule triggerRule = new TriggerRule(xmiContext,twinRegistry);
        FeedBackRule feedBackRule = new FeedBackRule(xmiContext,twinRegistry);

        for(EAElement eaElement:twinParser.getStereoTypeElementMap().get(Stereotypes.TWIN)){
            twinRule.apply(eaElement);
        }
        for(EAElement eaElement:twinParser.getStereoTypeElementMap().get(Stereotypes.COMPONENT_INSTANCE)){
            componentRule.apply(eaElement);
        }

        for(EAElement eaElement:twinParser.getStereoTypeElementMap().get(Stereotypes.DEVICE_INSTANCE)){
            deviceRule.apply(eaElement);
        }

        for(EAElement eaElement:twinParser.getStereoTypeElementMap().get(Stereotypes.STRATEGY_INSTANCE)){
            strategyRule.apply(eaElement);
        }

        for(Attribute attribute:twinParser.getStereoTypeAttributeMap().get(Stereotypes.CONST_INSTANCE)){
            constPropRule.apply(attribute);
        }

        for(Connector connector:twinParser.getStereoTypeConnectorMap().get(Stereotypes.MEASURES)){
            meaPropRule.apply(connector);

        }
        for(Connector connector:twinParser.getStereoTypeConnectorMap().get(Stereotypes.TRIGGERS)){
            triggerRule.apply(connector);
        }

        for(Connector connector:twinParser.getStereoTypeConnectorMap().get(Stereotypes.FEEDBACK_TOPIC)){
            feedBackRule.apply(connector);
        }

        return twinRegistry.getTwins().stream().filter(x->x.getDefinitionId().equals(x.getId())).toList();
    }

}
