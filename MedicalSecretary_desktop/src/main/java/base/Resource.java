package base;

public class Resource {
    private String id;
    private String type;
    private String name;
    private String resource;

    public Resource(String id, String type, String name, String resource) {
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.type =type;
    }

    public String getId() {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }
}
