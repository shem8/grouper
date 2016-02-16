package grouper.shemmagnezi.com.grouper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFragment extends Fragment {

    public static final String GROUP_ID = "GROUP_ID";
    public static final String MEMBER_ID = "MEMBER_ID";
    private static final String TAG = GroupFragment.class.getSimpleName();
    private RecyclerView membersList;
    private MembersAdapter adapter;
    private String groupId;
    private String memberId;
    private ValueEventListener memberListener;
    private ValueEventListener groupListener;
    private ChildEventListener membersListener;

    public static GroupFragment create(String groupId, String memberId) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putString(MEMBER_ID, memberId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        membersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        adapter = new MembersAdapter();
        membersList.setAdapter(adapter);
        membersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final TextView groupName = (TextView) view.findViewById(R.id.group_name);
        final TextView memberName = (TextView) view.findViewById(R.id.member_name);

        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data/groups");
        groupId = getArguments().getString(GROUP_ID);
        memberId = getArguments().getString(MEMBER_ID);
        groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                groupName.setText(group.toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "The read failed: " + firebaseError.getMessage());
            }
        };
        Firebase groupRef = myFirebaseRef.child(groupId);
        groupRef.addValueEventListener(groupListener);
        membersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.items.add(dataSnapshot.getValue(Member.class));
                adapter.notifyItemInserted(adapter.items.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Member value = dataSnapshot.getValue(Member.class);
                int indexOf = adapter.items.indexOf(value);
                if (indexOf >= 0) {
                    adapter.items.remove(indexOf);
                    adapter.items.add(indexOf, value);
                    adapter.notifyItemChanged(indexOf);
                } else {
                    adapter.items.add(value);
                    adapter.notifyItemInserted(adapter.items.size() - 1);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Member value = dataSnapshot.getValue(Member.class);
                int indexOf = adapter.items.indexOf(value);
                if (indexOf >= 0) {
                    adapter.items.remove(indexOf);
                    adapter.notifyItemRemoved(indexOf);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Nothing here

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Nothing here
            }
        };
        groupRef.child("members").addChildEventListener(membersListener);

        memberListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                if (member.getGroup() > 0) {
                    memberName.setText(member.getName() + " Group: " + member.getGroup());
                } else {
                    memberName.setText(member.getName());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w(TAG, "The read failed: " + firebaseError.getMessage());
            }
        };
        groupRef.child("members").child(memberId).addValueEventListener(memberListener);
    }


    @Override
    public void onDestroy() {
        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data/groups");
        Firebase firebase = myFirebaseRef.child(groupId);
        firebase.removeEventListener(groupListener);
        firebase.child("members").removeEventListener(membersListener);
        Firebase member = firebase.child("members").child(memberId);
        member.removeEventListener(memberListener);
        member.removeValue();
        super.onDestroy();
    }


    private class MembersAdapter extends RecyclerView.Adapter<GroupVH> {
        private List<Member> items = new LinkedList<>();
        @Override
        public GroupVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);;
            return new GroupVH(view);
        }

        @Override
        public void onBindViewHolder(GroupVH holder, int position) {
            Member member = items.get(position);
            holder.text.setText(member.getName());
            if (member.getGroup() > 0) {
                holder.itemView.setBackgroundColor(Color.rgb(119, 190, 250 - member.getGroup() * 30));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class GroupVH extends RecyclerView.ViewHolder {
        TextView text;

        public GroupVH(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.member_item_text);
        }
    }

}
