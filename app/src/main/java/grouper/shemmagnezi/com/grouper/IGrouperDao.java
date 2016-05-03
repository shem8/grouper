package grouper.shemmagnezi.com.grouper;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

/**
 * Created by shem.magnezi on 4/27/2016.
 */
public interface IGrouperDao {
    String addMemberToGroup(Group group, Member member);
    String addGroup(Group group);

    void setGroupsListListener(ItemsListener<Group> listener);
    void setGroupListener(String groupId, ItemListener<Group> listener);

    void setGroupMembersListListener(String groupId, ItemsListener<Member> listener);
    void setGroupMemberListener(String groupId, String memberId, ItemListener<Member> listener);

    void removeListener(ItemsListener<?> listener);
    void removeListener(ItemListener<?> listener);

    interface ItemListener<T> {
        void itemChanged(T item);
    }

    interface ItemsListener<T> extends  ItemListener<T>{
        void itemAdded(T item);
        void itemDeleted(T item);
    }
}
