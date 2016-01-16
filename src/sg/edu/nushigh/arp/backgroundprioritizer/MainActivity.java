package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	static boolean on;
	Button toggle;
	NumberPicker priopick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toggle = (Button) findViewById(R.id.onoff);
		priopick = (NumberPicker) findViewById(R.id.priopick);
		
		priopick.setMaxValue(15);
		priopick.setMinValue(1);
		priopick.setValue(10);
		priopick.setOnValueChangedListener(new OnValueChangeListener(){
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				PriorityChangeService.setShift(newVal);
			}
		});
		
		on = (PriorityChangeService.instance != null);
		toggle.setText(on?"Deactivate Service":"Activate Service");
	}
	
	public void toggleService(View view){
		if(!on){
			Toast.makeText(getApplicationContext(), "Starting service...", Toast.LENGTH_SHORT).show();
			startService(new Intent(this, PriorityChangeService.class));
			Log.i("priority change", "service started!");
			toggle.setText("Deactivate Service");
			priopick.setEnabled(false);
		}else{
			Toast.makeText(getApplicationContext(), "Stopping service...", Toast.LENGTH_SHORT).show();
			stopService(new Intent(this, PriorityChangeService.class));
			Log.i("priority change", "service stopped!");
			toggle.setText("Activate Service");
			priopick.setEnabled(true);
		}
		on = !on;
	}
	
}
