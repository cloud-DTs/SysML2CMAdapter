package model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class TwinConstProperty extends TwinProperty{
    private String propertyGuid;
    private String name;
    private String dataType;
    private String value;
    private TwinComponent twinComponent;


    public TwinConstProperty(){
        this.setPropertyGuid(UUID.randomUUID().toString());
    }
    @Override
    public void attachToParent() {

        this.getTwinComponent().getConstProperties().add(this);
    }

    @Override
    public String getId() {
        return twinComponent.getId()+name;
    }

}
