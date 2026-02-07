package api.models;

import lombok.Data;
import java.util.List;

@Data
public class Properties extends BaseModel {
    private List<Property> property;
    private int count;
    private String href;
}

@Data
class Property extends BaseModel {
    private String name;
    private String value;
    private boolean inherited;
    private PropertyType type;
}

@Data
class PropertyType extends BaseModel {
    private String rawValue;
}
