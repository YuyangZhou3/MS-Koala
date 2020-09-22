package base;

public class Resource {
    private String id;
    private String name;
    private String resource;

    public Resource(String id, String name, String resource) {
        this.id = id;
        this.name = name;
        this.resource = resource;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }
}
