package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PriorityChangeService extends Service {

	static int POLL_TIME = 3000;	// time between executions (in ms)
	static int NICE_SHIFT = 15;	// amount to renice processes with
	
	private volatile boolean kill = false; // flag to stop service
	private volatile int prev = -1; // PID of previously buffed process
	
	static volatile PriorityChangeService instance = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		final Context c = this;
		final Handler handler = new Handler();
	    final Runnable task = new Runnable(){
	    	@Override
		    public void run() {
		    	if(!kill){
					String foregroundActivityPackageName = Utilities.getForegroundActivity(c);
					Utilities.ProcessUsageData[] data = Utilities.taskList();

					int toDelete = prev;

					for(Utilities.ProcessUsageData d: data){
						int pid = d.getPid();
						String name = d.getName();
						if(pid == toDelete && name.equals(foregroundActivityPackageName)){
							// no change since last execution
							Log.i("priority change", "priority still with " + name);
							break;
						}
						if(pid == toDelete){
							// change niceness back to normal
							Utilities.executeCommand("renice +" + NICE_SHIFT + " " + pid);
							Log.i("priority change", NICE_SHIFT + " priority taken from " + name);
						}
						if(name.equals(foregroundActivityPackageName)){
							// reduce niceness to give more priority
							Utilities.executeCommand("renice -" + NICE_SHIFT + " " + pid);
							Log.i("priority change", NICE_SHIFT + " priority given to " + name);
							prev = pid;
						}
					}
		    		handler.postDelayed(this, POLL_TIME);
		    	}
		    }
	    };
	    handler.postDelayed(task, POLL_TIME);
		return START_STICKY;
	}

	@Override
	public void onCreate(){
		instance = this;
	}

	@Override
	public void onDestroy(){
		kill = true;
		instance = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
