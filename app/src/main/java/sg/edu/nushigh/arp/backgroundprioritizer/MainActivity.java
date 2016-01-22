package sg.edu.nushigh.arp.backgroundprioritizer;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	static boolean on;
	Button toggle;
	NumberPicker priopick;
	EditText polltime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toggle = (Button) findViewById(R.id.onoff);
		priopick = (NumberPicker) findViewById(R.id.priopick);
		polltime = (EditText) findViewById(R.id.polltime);

		priopick.setMaxValue(15);
		priopick.setMinValue(1);
		priopick.setValue(PriorityChangeService.NICE_SHIFT);

		polltime.setText(""+PriorityChangeService.POLL_TIME);

		on = (PriorityChangeService.instance != null);
		toggle.setText(on?"Deactivate Service":"Activate Service");
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
