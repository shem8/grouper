package grouper.shemmagnezi.com.grouper;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.client.Firebase;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

public class MainActivity extends AppCompatActivity implements AddGroupFragment.AddGroupFragmentListener,
        GroupsListFragment.GroupsListFragmentListener {

    private Toolbar toolbar;
    private IGrouperDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void groupSelected(Group group, String name) {
        if (name.length() == 0) {
            errorDialog();
            return;
        }
        Member member = new Member();
        member.setName(name);

        String id = dao.addMemberToGroup(group, member);

        moveToGroupScreen(group, id);

    }

    @Override
    public void addGroup(String name) {
        if (name.length() == 0) {
            errorDialog();
            return;
        }
        Group group = new Group();
        group.setName(name);
        String id = dao.addGroup(group);

        moveToGroupAdminScreen(id);
    }

    @Override
    public void openAddGroup() {
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, new AddGroupFragment());
        tran.addToBackStack(null);
        tran.commit();
    }

    private void moveToGroupScreen(Group group, String memeber) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, GroupFragment.create(group.getId(), memeber));
        tran.addToBackStack(null);
        tran.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }
    private void errorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please choose a name =(");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void moveToGroupAdminScreen(String id) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, GroupAdminFragment.create(id));
        tran.addToBackStack(null);
        tran.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
}
