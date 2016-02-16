package grouper.shemmagnezi.com.grouper;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

import grouper.shemmagnezi.com.grouper.pojo.Group;
import grouper.shemmagnezi.com.grouper.pojo.Member;

public class MainActivity extends AppCompatActivity implements AddGroupFragment.AddGroupFragmentListener,
        GroupsListFragment.GroupsListFragmentListener {

    private Toolbar toolbar;

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
    public void openAddGroup() {
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, new AddGroupFragment());
        tran.addToBackStack(null);
        tran.commit();
    }

    @Override
    public void groupSelected(Group group, String name) {
        if (name.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please choose a user name");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data");
        Firebase members = myFirebaseRef.child("groups/" + group.getId() + "/members");
        Firebase newMember = members.push();
        Member member = new Member(newMember.getKey(), name);
        newMember.setValue(member);

        getSupportFragmentManager().popBackStack();
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, GroupFragment.create(group.getId(), member.getId()));
        tran.addToBackStack(null);
        tran.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void addGroup(String name) {
        if (name.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please choose a group name");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        Firebase myFirebaseRef = new Firebase("https://amber-fire-737.firebaseio.com/data");
        Firebase groups = myFirebaseRef.child("groups");
        Firebase newGroup = groups.push();
        newGroup.setValue(new Group(newGroup.getKey(), name));

        getSupportFragmentManager().popBackStack();
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, GroupAdminFragment.create(newGroup.getKey()));
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
