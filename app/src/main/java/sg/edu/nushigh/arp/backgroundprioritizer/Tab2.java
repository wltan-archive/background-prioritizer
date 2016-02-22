package sg.edu.nushigh.arp.backgroundprioritizer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Tab2 extends Fragment {

    View v;
    TextView processCount;
    FloatingActionButton taskKiller;
    ListView list_process;
    CoordinatorLayout snackbarPos;
    List<String> processList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_2, container, false);

        processCount = (TextView) v.findViewById(R.id.text_process_count);
        taskKiller = (FloatingActionButton) v.findViewById(R.id.kill_tasks);
        list_process = (ListView) v.findViewById(R.id.list_process);
        snackbarPos = (CoordinatorLayout) v.findViewById(R.id.snackbar_position);

        Utilities.ProcessUsageData[] data = Utilities.taskList();

        for(Utilities.ProcessUsageData d: data){
            processList.add(d.getName());
        }

        processCount.setText(String.valueOf(data.length)); //TODO get actual number of processes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, processList);
        list_process.setAdapter(adapter);

        final Context c = this.getContext();

        taskKiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KillerTask(c).execute();
            }
        });
        return v;
    }

    public class KillerTask extends AsyncTask<Void, Void, ArrayList<Utilities.ProcessUsageData>> {

        private final Context c;
        private final Set<String> exclusionList = new TreeSet<>();

        KillerTask(Context c){
            super();
            this.c = c;
            // TODO add relevant UI-related fields here
            // TODO get exclusion list from some persistent location
        }

        int previousProcessCount;

        @Override
        protected void onPreExecute(){
            taskKiller.setClickable(false);
            previousProcessCount = Utilities.taskList().length;
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
            Utilities.ProcessUsageData[] data = Utilities.taskList();
            Snackbar.make(snackbarPos, previousProcessCount - data.length + " processes killed", Snackbar.LENGTH_LONG).show();
            ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(previousProcessCount, data.length);
            animator.setDuration(3000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    processCount.setText("" + (int) animation.getAnimatedValue());
                }
            });
            animator.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    taskKiller.setClickable(true);
                }
            }, 3000);
            processList.clear();
            for(Utilities.ProcessUsageData d: data){
                processList.add(d.getName());
            }
        }
    }

}
