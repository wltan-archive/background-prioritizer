package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private Toolbar toolbarTop, toolbarBtm;
	ViewPager pager;
	ViewPagerAdapter adapter;
	SlidingTabLayout tabs;
	CharSequence titles[] = {"Tab 1", "Tab 2", "Tab 3", "Tab 3"};
	int numOfTabs = 4;
	static boolean on;
	Button toggle;
	NumberPicker priopick;
	EditText polltime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//toolbar
		toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
		//toolbarBtm = (Toolbar) findViewById(R.id.toolbar_btm);
		setSupportActionBar(toolbarTop);
		/*
		toolbarBtm.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_settings:
						break;
				}
				return true;
			}
		});
		toolbarBtm.inflateMenu(R.menu.toolbar_menu_btm);
		*/

		//sliding layout
		adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, numOfTabs);

		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);

		tabs = (SlidingTabLayout)findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true);
		tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer(){
			@Override
			public int getIndicatorColor(int pos){
				return getResources().getColor(R.color.tabsScrollColor);
			}
		});
		tabs.setViewPager(pager);

		toggle = (Button) findViewById(R.id.onoff);
		priopick = (NumberPicker) findViewById(R.id.priopick);
		polltime = (EditText) findViewById(R.id.polltime);

		priopick.setMaxValue(15);
		priopick.setMinValue(1);
		priopick.setValue(PriorityChangeService.NICE_SHIFT);

		polltime.setText(""+PriorityChangeService.POLL_TIME);

		on = (PriorityChangeService.instance != null);
		toggle.setText(on?"Deactivate Service":"Activate Service");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			//code to color status bar for >= lollipop
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

			//code to color app manager header for >= lollipop
			ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
			this.setTaskDescription(taskDesc);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) { //inflate menu for top toolbar
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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

	private static final int MIN_POLLTIME = 1000;

	public void toggleService(View view){
		if(!on){
			Toast.makeText(getApplicationContext(), "Starting service...", Toast.LENGTH_SHORT).show();

			int pollt = Integer.parseInt(polltime.getText().toString());
			if(pollt < MIN_POLLTIME){
				pollt = MIN_POLLTIME;
				Toast.makeText(getApplicationContext(), "Poll time must be at least " + MIN_POLLTIME + "ms", Toast.LENGTH_SHORT).show();
				polltime.setText(""+pollt);
			}

			PriorityChangeService.NICE_SHIFT = priopick.getValue();
			PriorityChangeService.POLL_TIME = pollt;
			startService(new Intent(this, PriorityChangeService.class));
			Log.i("priority change", "service started!");
			toggle.setText("Deactivate Service");
			priopick.setEnabled(false);
			polltime.setEnabled(false);
		}else{
			Toast.makeText(getApplicationContext(), "Stopping service...", Toast.LENGTH_SHORT).show();
			stopService(new Intent(this, PriorityChangeService.class));
			Log.i("priority change", "service stopped!");
			toggle.setText("Activate Service");
			priopick.setEnabled(true);
			polltime.setEnabled(true);
		}
		on = !on;
	}

}
