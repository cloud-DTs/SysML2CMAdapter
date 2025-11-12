package SourceService.MappingRules;

import SourceService.SourceXMIElements.Element.EAElement;
import SourceService.SourceXMIElements.Mapper.Stereotypes;
import SourceService.XMIContext;
import model.*;
import org.json.JSONObject;
import registry.TwinRegistry;

public class StrategyRule extends MappingRule<EAElement> {
    public StrategyRule(XMIContext xmiContext, TwinRegistry twinRegistry) {
        super(xmiContext, twinRegistry);
    }

    @Override
    public boolean matches(EAElement element) {
        return element.getStereotypeName().equals(Stereotypes.STRATEGY_INSTANCE);
    }

    @Override
    public void apply(EAElement element) {
        EAElement strategyDefinition = getXmiContext().findDefinitonBlockForInstance(element);
        if (strategyDefinition == null) {
            throw new IllegalStateException("No StrategyDefinition found for instance: " + element.getName());
        }

        addStrategy(element, strategyDefinition);
    }

    private void addStrategy(EAElement element, EAElement strategyDefinition) {
        TwinStrategy twinStrategy = new TwinStrategy();
        twinStrategy.setPathToCode(getXmiContext().getTaggedValue(strategyDefinition, "pathToCode"));
        twinStrategy.setStrategyType(getXmiContext().getTaggedValue(strategyDefinition, "type").toLowerCase());
        twinStrategy.setStrategyId(element.getIdref());
        twinStrategy.setName(element.getName());
        twinStrategy.setDefinitionId(strategyDefinition.getIdref());
        FeedBack feedBack = initFeedBack(element);
        twinStrategy.setFeedBack(feedBack);
        getTwinRegistry().registerStrategy(element.getIdref(), twinStrategy);
    }

    private FeedBack initFeedBack(EAElement element) {
        String topicType = getXmiContext().getTaggedValue(element,"feedBackTopicType");
        String payloadType = getXmiContext().getTaggedValue(element, "feedBackPayloadType");
        String payload = getXmiContext().getTaggedValue(element,"customPayload");
        String topic = getXmiContext().getTaggedValue(element,"customTopic");
        return getFeedBack(topicType,payloadType,payload,topic);
    }

    private FeedBack getFeedBack(String topicType, String payloadType,String customPayload,String customTopic) {

        switch (topicType){
            case "INTERNAL" -> {
                InternalFeedBackTopic internalFeedBackTopic = new InternalFeedBackTopic();
                internalFeedBackTopic.setPayload(getPayload(payloadType,customPayload));
                return  internalFeedBackTopic;
            }
            case "EXTERNAL" -> {
                ExternalFeedBackTopic externalFeedBackTopic = new ExternalFeedBackTopic();
                externalFeedBackTopic.setTopic(customTopic);
                externalFeedBackTopic.setPayload(getPayload(payloadType,customPayload));
                return externalFeedBackTopic;

            }
            case "NONE" -> {
                return new NoneFeedBackTopic();
            }
            default -> throw new IllegalStateException("Unexpected value: " + topicType);
        }
    }

    private Payload getPayload(String payloadType,String customPayload) {
        switch (payloadType){
            case "custom" -> {
                CustomPayload payload = new CustomPayload();
                System.out.println(customPayload);
                JSONObject jsonObject = new JSONObject(customPayload);
                payload.setPayload(jsonObject);
                return payload;
            }
            case "strategyResult" -> {
                ActionResultPayload actionResultPayload = new ActionResultPayload();
                actionResultPayload.setPayload("action-result");
                return actionResultPayload;
            }
            default -> throw new IllegalStateException("Unexpected value: " + payloadType);
        }
    }
}

