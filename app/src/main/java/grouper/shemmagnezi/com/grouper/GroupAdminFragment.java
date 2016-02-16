package grouper.shemmagnezi.com.grouper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupAdminFragment extends Fragment {

    public static final String GROUP_ID = "GROUP_ID";
    private static final String TAG = GroupAdminFragment.class.getSimpleName();
    private RecyclerView membersList;
    private String groupId;
    private ValueEventListener listener;
    private MembersAdapter adapter;

    public static GroupAdminFragment create(String groupId) {
        GroupAdminFragment fragment = new GroupAdminFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_admin, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        membersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        adapter = new MembersAdapter();
        membersList.setAdapter(adapter);
        membersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final TextView groupName = (TextView) view.findViewById(R.id.group_name);

        final Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data/groups");
        groupId = getArguments().getString(GROUP_ID);
        listener = new ValueEventListener() {
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
        groupRef.addValueEventListener(listener);

        ChildEventListener membersListener = new ChildEventListener() {
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

        View split = view.findViewById(R.id.group_split);
        split.getBackground().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY));
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<Member> members = new LinkedList<>(adapter.items);
                int groupNum = 1;
                int groupMemebers = 0;
                int maxMembers = Integer.parseInt(((EditText)view.findViewById(R.id.group_size)).getText().toString());
                while (!members.isEmpty()) {
                    Member m = getNext(members);
                    if (groupMemebers >= maxMembers) {
                        groupNum++;
                        groupMemebers = 0;
                    }
                    m.setGroup(groupNum);
                    myFirebaseRef.child(groupId).child("members").child(m.getId()).child("group").setValue(m.getGroup());
                    groupMemebers++;
                }

                adapter.notifyDataSetChanged();
            }

            private Member getNext(LinkedList<Member> members) {
                Random rand = new Random();
                int index = rand.nextInt(members.size());
                return members.remove(index);
            }
        });
    }

    @Override
    public void onDestroy() {
        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data/groups").child(groupId);
        groupId = getArguments().getString(GROUP_ID);
        myFirebaseRef.removeEventListener(listener);
        myFirebaseRef.removeValue();
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
