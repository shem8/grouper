package grouper.shemmagnezi.com.grouper;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

/**
 * Created by shem.magnezi on 4/27/2016.
 */
public class FirebaseGrouperDao implements IGrouperDao {
    private static final String TAG = FirebaseGrouperDao.class.getSimpleName();
    private static FirebaseGrouperDao instance;
    private Firebase firebaseRef;
    private Map<ItemListener, ValueEventListener> valueListeners;
    private Map<ItemsListener, ChildEventListener> listListeners;

    private FirebaseGrouperDao() {
        firebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data");
        valueListeners = new HashMap<>();
        listListeners = new HashMap<>();
    }

    static FirebaseGrouperDao getInstance() {
        if (instance == null) {
            instance = new FirebaseGrouperDao();
        }
        return instance;
    }

    @Override
    public String addMemberToGroup(Group group, Member member) {
        Firebase members = firebaseRef.child("groups/" + group.getId() + "/members");
        Firebase newMember = members.push();
        member.setId(newMember.getKey());
        newMember.setValue(member);

        return member.getId();
    }

    @Override
    public String addGroup(Group group) {
        Firebase groups = firebaseRef.child("groups");
        Firebase newGroup = groups.push();
        group.setId(newGroup.getKey());
        newGroup.setValue(group);

        return group.getId();
    }

    @Override
    public void setGroupsListListener(final ItemsListener<Group> listener) {
        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data");
        Firebase groupsRef = myFirebaseRef.child("groups");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listener.itemAdded(dataSnapshot.getValue(Group.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                listener.itemChanged(dataSnapshot.getValue(Group.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listener.itemDeleted(dataSnapshot.getValue(Group.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
        groupsRef.addChildEventListener(childEventListener);

        listListeners.put(listener, childEventListener);
    }

    @Override
    public void setGroupListener(String groupId, final ItemListener<Group> listener) {
        Firebase groupRef = firebaseRef.child(groupId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.itemChanged(snapshot.getValue(Group.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "The read failed: " + firebaseError.getMessage());
            }
        };
        groupRef.addValueEventListener(eventListener);
        valueListeners.put(listener, eventListener);
    }

    @Override
    public void setGroupMembersListListener(String groupId, final ItemsListener<Member> listener) {
        Firebase groupRef = firebaseRef.child(groupId);
        Firebase members = groupRef.child("members");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listener.itemAdded(dataSnapshot.getValue(Member.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                listener.itemChanged(dataSnapshot.getValue(Member.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listener.itemDeleted(dataSnapshot.getValue(Member.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
        members.addChildEventListener(childEventListener);
        listListeners.put(listener, childEventListener);
    }

    @Override
    public void setGroupMemberListener(String groupId, String memberId, final ItemListener<Member> listener) {
        Firebase groupRef = firebaseRef.child(groupId);
        Firebase members = groupRef.child("members");
        Firebase member = members.child(memberId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.itemChanged(snapshot.getValue(Member.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "The read failed: " + firebaseError.getMessage());
            }
        };
        member.addValueEventListener(eventListener);
        valueListeners.put(listener, eventListener);

    }

    @Override
    public void removeListener(ItemsListener<?> listener) {
        ChildEventListener toRemove = listListeners.remove(listener);
        if (toRemove != null) {
            firebaseRef.removeEventListener(toRemove);
        }
    }

    @Override
    public void removeListener(ItemListener<?> listener) {
        ValueEventListener toRemove = valueListeners.remove(listener);
        if (toRemove != null) {
            firebaseRef.removeEventListener(toRemove);
        }

    }
}
