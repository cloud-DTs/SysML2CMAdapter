package registry;

import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TwinRegistry {

    private Map<String, TwinEntity> entities = new HashMap<>();
    private Map<String, TwinComponent> components = new HashMap<>();
    private Map<String, TwinStrategy> strategies = new HashMap<>();
    private Map<String,TwinComponentProperty> properties = new HashMap<>();
    private Map<String,TwinConstProperty> constProperties = new HashMap<>();


    public void registerEntity(String id, TwinEntity entity) {
        entities.put(id, entity);
    }

    public void registerComponent(String id, TwinComponent component) {
        components.put(id, component);
    }

    public void registerStrategy(String id, TwinStrategy strategy) {
        strategies.put(id, strategy);
    }

    public void registerProperty(String componentId,String propertyName, TwinComponentProperty property) {
        System.out.println(propertyName);
        properties.put(componentId+propertyName, property);
    }

    public void registerProperty(String entityId,String name, TwinConstProperty property) {
        constProperties.put(entityId+name, property);
    }
    public TwinEntity getEntity(String id) {
        return entities.get(id);
    }

    public TwinComponent getComponent(String id) {
        return components.get(id);
    }

    public TwinStrategy getStrategy(String id) {
        return strategies.get(id);
    }

    public TwinComponentProperty getProperty(String id) {
        return properties.get(id);
    }
    public TwinComponentProperty getProperty(String componentId,String id) {
        return properties.get(componentId+id);
    }

    public TwinConstProperty getConstProperty(String id) {
        return constProperties.get(id);
    }
    public TwinConstProperty getConstProperty(String entityId,String id) {
        return constProperties.get(entityId+id);
    }

    public boolean entityExists(String id) {
        return entities.containsKey(id);
    }

    public boolean componentExists(String id) {
        return components.containsKey(id);
    }

    public boolean strategyExists(String id) {
        return strategies.containsKey(id);
    }

    public boolean propertyExists(String componentId,String propertyName) {
        return properties.containsKey(componentId+propertyName);
    }
    public boolean constPropertyExists(String entityId,String propertyName) {
        return constProperties.containsKey(entityId+propertyName);
    }

    public List<TwinEntity> getTwins() {
        return entities.values().stream().toList();
    }
}


