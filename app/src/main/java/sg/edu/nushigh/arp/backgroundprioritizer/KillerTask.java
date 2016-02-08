package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class KillerTask extends AsyncTask<Void, Void, ArrayList<Utilities.ProcessUsageData>> {

    private final Context c;
    private final Set<String> exclusionList = new TreeSet<>();

    KillerTask(Context c){
        super();
        this.c = c;
        // TODO add relevant UI-related fields here
        // TODO get exclusion list from some persistent location
    }

    @Override
    protected void onPreExecute(){
        // what do we need, conditions checking?
        // TODO this runs on UI thread, so we can set some loading messages
    }

    @Override
    protected ArrayList<Utilities.ProcessUsageData> doInBackground(Void... params) {
        ArrayList<Utilities.ProcessUsageData> killed = new ArrayList<>();
        Utilities.ProcessUsageData[] data = Utilities.taskList();
        String foregroundActivityPackageName = Utilities.getForegroundActivity(c);
        for(Utilities.ProcessUsageData d: data){
            // skip system processes for stability
            if(d.getUid().equals("system"))
                continue;
            if(d.getUid().equals("root"))
                continue;
            if(d.getUid().equals("shell"))
                continue;
            // skip processes in exclusion list
            if(exclusionList.contains(d.getName()))
                continue;
            // skip foreground process (always backgroundprioritizer for now in this implementation)
            if(d.getName().equals(foregroundActivityPackageName))
                continue;
            // if none of the above, we kill the process
            Utilities.executeSuperCommand("kill " + d.getPid());
            killed.add(d);
        }
        return killed;
    }

    @Override
    protected void onPostExecute(ArrayList<Utilities.ProcessUsageData> result){
        // result is list of killed tasks
        // TODO this runs on UI thread , display whatever UI-side results we want

    }
}
