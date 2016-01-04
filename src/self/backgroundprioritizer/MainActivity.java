package self.backgroundprioritizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	static boolean on;
	Button toggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toggle = (Button) findViewById(R.id.button1);
		
		on = (PriorityChangeService.instance != null);
		toggle.setText(on?"Deactivate Service":"Activate Service");
	}
	
	public void toggleService(View view){
		if(!on){
			Toast.makeText(getApplicationContext(), "Starting service...", Toast.LENGTH_SHORT).show();
			startService(new Intent(this, PriorityChangeService.class));
			toggle.setText("Deactivate Service");
		}else{
			Toast.makeText(getApplicationContext(), "Stopping service...", Toast.LENGTH_SHORT).show();
			stopService(new Intent(this, PriorityChangeService.class));
			toggle.setText("Activate Service");
		}
		on = !on;
	}
	
}
