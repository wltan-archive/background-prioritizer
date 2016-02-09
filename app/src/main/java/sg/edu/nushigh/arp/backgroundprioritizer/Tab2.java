package sg.edu.nushigh.arp.backgroundprioritizer;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skyfishjy.library.RippleBackground;

public class Tab2 extends Fragment {

    View v;
    FloatingActionButton killTasks;
    RippleBackground ripple, ripple2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_2, container, false);

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

        return v;
    }
}
