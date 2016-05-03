package grouper.shemmagnezi.com.grouper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import grouper.shemmagnezi.com.grouper.pojo.Group;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupsListFragment extends Fragment {

    public interface GroupsListFragmentListener {
        void openAddGroup();
        void groupSelected(Group group, String name);
    }

    private GroupNameAdapter adapter;
    private TextView memberName;
    private IGrouperDao dao = FirebaseGrouperDao.getInstance();
    private RecyclerView groupsList;
    private FloatingActionButton fab;
    private GroupsListFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);

        dao.setGroupsListListener(new IGrouperDao.ItemsListener<Group>() {
            @Override
            public void itemAdded(Group group) {
                adapter.items.add(group);
                adapter.notifyItemInserted(adapter.items.size() - 1);
            }

            @Override
            public void itemChanged(Group item) {
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
            public void itemDeleted(Group item) {
                int indexOf = adapter.items.indexOf(item);
                if (indexOf >= 0) {
                    adapter.items.remove(indexOf);
                    adapter.notifyItemRemoved(indexOf);
                }
            }
        });


    }

    private void initView(View view) {
        memberName = (TextView) view.findViewById(R.id.member_name);
        groupsList = (RecyclerView) view.findViewById(R.id.groups_list);
        adapter = new GroupNameAdapter();
        groupsList.setAdapter(adapter);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                listener.openAddGroup();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        fab.show();
                    }
                }, 1000);
            }
        });
    }

    private void itemClicked(int adapterPosition) {
        listener.groupSelected(adapter.items.get(adapterPosition), memberName.getText().toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (GroupsListFragmentListener) context;
    }

    private class GroupNameAdapter extends RecyclerView.Adapter<GroupVH> {
        private List<Group> items = new LinkedList<>();
        @Override
        public GroupVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
            GroupVH groupVH = new GroupVH(view);
            return groupVH;
        }

        @Override
        public void onBindViewHolder(GroupVH holder, int position) {
            Group group = items.get(position);
            holder.text.setText(group.toString());
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
            text = (TextView) itemView.findViewById(R.id.group_item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(getAdapterPosition());
                }
            });
        }
    }
}
