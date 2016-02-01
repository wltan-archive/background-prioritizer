package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

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
					ArrayList<String> out = Utilities.executeCommand("top -n 1");
					// Data about individual processes only starts after line 7
					// TODO make this less 'gimmicky' i.e. hardcoded
					if(out.size() > 7){
						String foregroundActivityPackageName = Utilities.getForegroundActivity(c);
						Utilities.ProcessUsageData[] data = new Utilities.ProcessUsageData[out.size()-7];
						int toDelete = prev;
						for(int i = 0; i < out.size()-7; i++){
							data[i] = new Utilities.ProcessUsageData(out.get(i+7));
							int pid = data[i].getPid();
							String name = data[i].getName();
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
					}else{
						Log.e("prioritizer service", "out is " + out.size() + " lines long");
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
