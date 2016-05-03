package grouper.shemmagnezi.com.grouper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView groupName;
    private TextView memberName;
    private IGrouperDao.ItemListener<Group> groupItemListener;
    private IGrouperDao.ItemsListener<Member> itemsListener;
    private IGrouperDao.ItemListener<Member> memberItemListener;

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
        setupView(view);
        groupId = getArguments().getString(GROUP_ID);
        memberId = getArguments().getString(MEMBER_ID);

        IGrouperDao dao = FirebaseGrouperDao.getInstance();
        groupItemListener = new IGrouperDao.ItemListener<Group>() {
            @Override
            public void itemChanged(Group item) {
                if (item == null) {
                    groupName.setText("null");
                } else {
                    groupName.setText(item.toString());
                }
            }
        };
        dao.setGroupListener(groupId, groupItemListener);

        itemsListener = new IGrouperDao.ItemsListener<Member>() {
            @Override
            public void itemAdded(Member item) {
                adapter.items.add(item);
                adapter.notifyItemInserted(adapter.items.size() - 1);
            }

            @Override
            public void itemChanged(Member item) {
                int indexOf = adapter.items.indexOf(item);
                if (indexOf >= 0) {
                    adapter.items.remove(indexOf);
                    adapter.items.add(indexOf, item);
                    adapter.notifyItemChanged(indexOf);
                } else {
                    adapter.items.add(item);
                    adapter.notifyItemInserted(adapter.items.size() - 1);
                }
            }

            @Override
            public void itemDeleted(Member item) {
                int indexOf = adapter.items.indexOf(item);
                if (indexOf >= 0) {
                    adapter.items.remove(indexOf);
                    adapter.notifyItemRemoved(indexOf);
                }
            }
        };
        dao.setGroupMembersListListener(groupId, itemsListener);

        memberItemListener = new IGrouperDao.ItemListener<Member>() {
            @Override
            public void itemChanged(Member item) {
                if (item == null) {
                    memberName.setText("null");
                } else if (item.getGroup() > 0) {
                    memberName.setText(item.getName() + " Group: " + item.getGroup());
                } else {
                    memberName.setText(item.getName());
                }
            }
        };
        dao.setGroupMemberListener(groupId, memberId, memberItemListener);
    }

    private void setupView(View view) {
        membersList = (RecyclerView) view.findViewById(R.id.group_members_list);
        adapter = new MembersAdapter();
        membersList.setAdapter(adapter);
        membersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupName = (TextView) view.findViewById(R.id.group_name);
        memberName = (TextView) view.findViewById(R.id.member_name);
    }


    @Override
    public void onDestroy() {
        IGrouperDao dao = FirebaseGrouperDao.getInstance();
        dao.removeListener(groupItemListener);
        dao.removeListener(itemsListener);
        dao.removeListener(memberItemListener);
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
