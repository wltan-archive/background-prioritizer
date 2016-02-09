package sg.edu.nushigh.arp.backgroundprioritizer;

import android.animation.ValueAnimator;
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

public class Tab2 extends Fragment {

    View v;
    TextView processCount;
    FloatingActionButton taskKiller;
    ListView list_process;
    CoordinatorLayout snackbarPos;
    /*
    FloatingActionButton killTasks;
    RippleBackground ripple, ripple2;
    */

    int currProcessCount = 157, newProcessCount = 78;
    List<String> processList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_2, container, false);

        processCount = (TextView) v.findViewById(R.id.text_process_count);
        taskKiller = (FloatingActionButton) v.findViewById(R.id.kill_tasks);
        list_process = (ListView) v.findViewById(R.id.list_process);
        snackbarPos = (CoordinatorLayout) v.findViewById(R.id.snackbar_position);

        for(int i = 0; i < 20;i++){
            processList.add(String.valueOf(i));
        }

        processCount.setText(String.valueOf(currProcessCount)); //TODO get actual number of processes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, processList);
        list_process.setAdapter(adapter);

        taskKiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskKiller.setClickable(false);
                int previousProcessCount = currProcessCount; //TODO get actual number of processes
                //TODO kill tasks
                Snackbar.make(snackbarPos, previousProcessCount - newProcessCount + " processes killed", Snackbar.LENGTH_LONG).show();
                ValueAnimator animator = new ValueAnimator();
                animator.setObjectValues(previousProcessCount, newProcessCount); //TODO get actual number of processes
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
            }
        });
        /*
        killTasks = (FloatingActionButton) v.findViewById(R.id.kill_tasks);
        ripple = (RippleBackground) v.findViewById(R.id.ripple_background);
        ripple2 = (RippleBackground) v.findViewById(R.id.ripple_background_2);

        ripple2.startRippleAnimation();

        killTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ripple.isRippleAnimationRunning()) {
                    ripple.stopRippleAnimation();
                    ripple2.setVisibility(View.VISIBLE);
                } else {
                    ripple.startRippleAnimation();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (ripple.isRippleAnimationRunning()) {
                                ripple2.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 2000);
                }
            }
        });
        */

        return v;
    }
}
