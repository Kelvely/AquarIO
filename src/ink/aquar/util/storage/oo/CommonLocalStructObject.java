package ink.aquar.util.storage.oo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommonLocalStructObject implements LocalStructObject {

    protected final Map<String, OosObject> members = new HashMap<>();

    /**
     * Extend this class to make it accessible. <br/>
     */
    protected CommonLocalStructObject() { }

    @Override
    public OosObject getMember(String field) {
        return members.get(field);
    }

    @Override
    public void setMember(String field, OosObject object) {
        members.put(field, object);
    }

    @Override
    public OosObject removeMember(String field) {
        return members.remove(field);
    }

    @Override
    public Map<String, OosObject> getMembers() {
        return new HashMap<>(members);
    }

    @Override
    public void setMembers(Map<String, OosObject> members) {
        this.members.putAll(members);
    }

    @Override
    public void removeMembers(Set<String> fields) {
        for(String field : fields) members.remove(field);
    }

    @Override
    public void removeAllMembers() {
        members.clear();
    }

}
