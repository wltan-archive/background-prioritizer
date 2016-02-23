package sg.edu.nushigh.arp.backgroundprioritizer;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        processCount.setText(String.valueOf(data.length));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, processList);
        list_process.setAdapter(adapter);

        final Context c = this.getContext();

        taskKiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KillerTask().execute();
            }
        });

        final Activity a = this.getActivity();

        Runnable update = new Runnable(){
            @Override
            public void run(){
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processCount.setText(String.valueOf(Utilities.taskList().length));
                    }
                });
            }
        };

        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(10);
        s.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);

        return v;
    }

    public class KillerTask extends AsyncTask<Void, Void, ArrayList<Utilities.ProcessUsageData>> {

        private final Set<String> exclusionList = new TreeSet<>();

        KillerTask(){
            super();
            PackageManager pm = getContext().getPackageManager();

            for(PackageInfo pi : pm.getInstalledPackages(0)) {
                ApplicationInfo ai = null;
                try {
                    ai = pm.getApplicationInfo(pi.packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // will never happen, we get names from pm itself
                }

                // exclude system apps
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    exclusionList.add(pi.packageName);
            }
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
            String foregroundActivityPackageName = Utilities.getForegroundActivity(getContext());
            for(Utilities.ProcessUsageData d: data){
                // skip processes in exclusion list, includes system processes
                if(exclusionList.contains(d.getName()))
                    continue;
                // skip system processes for stability (as a temporary failsafe)
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
                Utilities.executeSuperCommand("kill " + d.getPid());
                Log.i("Process killed", d.getName());
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
