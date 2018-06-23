package refried.rfid_inventory;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import refried.rfid_inventory.help.HelpPage;
import refried.rfid_inventory.PrivacyPolicyWebView.privacy_policy;
import refried.rfid_inventory.settings.PrefsFragment;
import refried.rfid_inventory.scan.ScanPage;
import refried.rfid_inventory.users.FirebaseUsersContract;
import refried.rfid_inventory.users.FirebaseUsersInteractor;
import refried.rfid_inventory.viewinventory.ViewPage;

public class MainContentActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    FirebaseUsersContract mUserModel = new FirebaseUsersInteractor();
    private ViewPage mViewPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.current_username);
        username.setText(mUserModel.getCurrentUser());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, new ScanPage());
        transaction.commit();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    displayView(menuItem.getItemId());
                    return true;
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0){
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId) {
        Fragment fragment = null;

        switch (viewId) {
            case R.id.rfid_scan_page:
                fragment = new ScanPage();
                break;
            case R.id.view_inventory_page:
                mViewPage = ViewPage.newInstance();
                fragment = mViewPage;
                break;
            case R.id.help_page:
                //Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                fragment = new HelpPage();
                break;
            case R.id.privacy_policy_page:
                fragment = new privacy_policy();
                break;
            case R.id.users_manual_page:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/18Xm4-oJY4wLgv17BkHVjRrUe_jxcfs6q/view")));
                break;
            case R.id.settings_page:
                fragment = new PrefsFragment();
                break;
            case R.id.log_out:
                AlertDialog diaBox = logOutConfirmation(this);
                diaBox.show();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private AlertDialog logOutConfirmation(final Context c)
    {
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setIcon(R.drawable.ic_log_out)

                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AuthUI.getInstance().signOut(c);
                        Intent intent = new Intent(c, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(c, "Signed out", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
