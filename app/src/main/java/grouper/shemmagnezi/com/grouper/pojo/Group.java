package grouper.shemmagnezi.com.grouper.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shem.magnezi on 2/15/2016.
 */
public class Group {

    private String name;
    private String id;
    private Map<String, Member> members = new HashMap<>();

    public Group() {
    }

    public Group(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Member> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName() + " (" + getMembers().size() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return name != null ? name.equals(group.name) : group.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
