package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity {

	Toolbar toolbar;
	ViewPager pager;
	ViewPagerAdapter adapter;
	SlidingTabLayout tabs;
	CharSequence titles[] = {"Process Prioritizer", "Task Killer", "System Information"};
	int numOfTabs = 3, imageResId[] = {R.drawable.ic_tab_process_prioritizer, R.drawable.ic_tab_task_killer,
			R.drawable.ic_tab_system_info};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//toolbar
		toolbar = (Toolbar) findViewById(R.id.toolbar_top);
		toolbar.setTitle(titles[0]);
		setSupportActionBar(toolbar);

		//sliding layout
		adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, imageResId, numOfTabs, getApplicationContext());

		pager = (ViewPager)findViewById(R.id.pager);
		pager.setOffscreenPageLimit(2);
		pager.setAdapter(adapter);
		pager.setPageTransformer(true, new ZoomOutPageTransformer());

		tabs = (SlidingTabLayout)findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true);
		tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
			@Override
			public int getIndicatorColor(int pos) {
				return getResources().getColor(R.color.tabsScrollColor);
			}
		});
		tabs.setCustomTabView(R.layout.custom_tab, 0);
		tabs.updateToolbarTitles(toolbar, titles);
		tabs.setViewPager(pager);

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
		if (id == R.id.action_info) {
			new MaterialDialog.Builder(this)
					.title("Information")
					.content("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sapien urna, finibus vitae mi sit amet, sagittis mollis dolor. Integer pulvinar neque et hendrerit rutrum. Phasellus in purus odio. Sed non tincidunt augue. Cras et risus sed lacus blandit maximus.")
					.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
