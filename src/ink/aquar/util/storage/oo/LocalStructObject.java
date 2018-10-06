package ink.aquar.util.storage.oo;

import java.util.Map;
import java.util.Set;

/**
 * A struct object that is in local. <br/>
 * Use <code>RemoteStructObject.set(..., localDataObject, ...)</code>
 * can upload this object and make a field refers to the remote uploaded object. <br/>
 *
 * @author Kelby Iry
 */
public interface LocalStructObject extends LocalObject {

    /**
     * Get the member that the field refers to. <br/>
     * @param field the field that refers to the desired member object
     * @return The member that the field refers to
     */
    public OosObject getMember(String field);

    /**
     * Set member a field refers to. <br/>
     * @param field The field that desired to refer the object
     * @param object The object that the field should refers to
     */
    public void setMember(String field, OosObject object);

    /**
     * Remove a field. <br/>
     * @param field The field that is ought to be removed
     * @return The object that the field previous refers to
     */
    public OosObject removeMember(String field);

    /**
     * Get all fields and corresponding member objects.
     * Modifying the map returned will not affect the members inside this local struct object. <br/>
     * @return All fields and corresponding member objects
     */
    public Map<String, OosObject> getMembers();

    /**
     * Set a set of fields and corresponding member objects. <br/>
     * The original fields will not be removed, and the preexisted fields will be overridden. <br/>
     * Modifying the map provided will not affect the members inside this local struct object. <br/>
     * @param members A set of fields and corresponding member objects
     */
    public void setMembers(Map<String, OosObject> members);

    /**
     * Remove a set of fields. <br/>
     * @param fields The set of fields that are ought to be removed
     */
    public void removeMembers(Set<String> fields);

    /**
     * Remove all fields. <br/>
     */
    public void removeAllMembers();

    public static LocalStructObject create() {
        return new CommonLocalStructObject();
    }



}
