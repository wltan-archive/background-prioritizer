package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.os.AsyncTask;

public class KillerTask extends AsyncTask<Context, Void, Void> {
    // TODO we can have better type arguments, will use as we develop

    @Override
    protected void onPreExecute(){
        // what do we need, conditions checking?
    }

    @Override
    protected Void doInBackground(Context... params) {
        Utilities.ProcessUsageData[] data = Utilities.taskList();
        String foregroundActivityPackageName = Utilities.getForegroundActivity(params[0]);
        for(Utilities.ProcessUsageData d: data){
            // skip system processes for stability
            if(d.getUid().equals("system"))
                continue;
            if(d.getUid().equals("root"))
                continue;
            if(d.getUid().equals("shell"))
                continue;
            // skip foreground process (always backgroundprioritizer for now in this implementation)
            if(d.getName().equals(foregroundActivityPackageName))
                continue;
            // if none of the above, we kill the process
            Utilities.executeCommand("kill " + d.getPid());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        // a list of killed tasks?
    }
}
