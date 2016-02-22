package sg.edu.nushigh.arp.backgroundprioritizer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Tab1 extends Fragment {

    View v, view_status;
    TextView text_status, value_priopick;
    FloatingActionButton toggle;
    SeekBar priopick;
    EditText polltime;
    CoordinatorLayout snackbarPos;
    static boolean on;
    String FILENAME = "settings.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_1, container, false);

        view_status = (View) v.findViewById(R.id.view_status);
        text_status = (TextView) v.findViewById(R.id.text_status);
        toggle = (FloatingActionButton) v.findViewById(R.id.toggle);
        priopick = (SeekBar) v.findViewById(R.id.slider_priopick);
        value_priopick = (TextView) v.findViewById(R.id.text_priopick_value);
        polltime = (EditText) v.findViewById(R.id.text_polltime_value);
        snackbarPos = (CoordinatorLayout) v.findViewById(R.id.snackbar_position);

        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleService(v);
            }
        });

        priopick.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value_priopick.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        polltime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == polltime.getId())
                    polltime.setCursorVisible(true);
            }
        });

        polltime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    polltime.clearFocus();
                    polltime.setCursorVisible(false);
                }
                return false;
            }
        });

        try {
            FileInputStream fin = getContext().openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            String line;
            priopick.setProgress(Integer.valueOf(br.readLine()));
            value_priopick.setText(String.valueOf(priopick.getProgress() + 1));
            polltime.setText(br.readLine());
            br.close();
        }catch(Exception e){
            e.printStackTrace();
            priopick.setProgress(PriorityChangeService.NICE_SHIFT - 1);
            value_priopick.setText(String.valueOf(priopick.getProgress() + 1));
            polltime.setText(String.valueOf(PriorityChangeService.POLL_TIME));
        }

        on = (PriorityChangeService.instance != null);
        if(on){
            view_status.setBackgroundColor(getResources().getColor(R.color.processActiveColor));
            text_status.setText("Active");
            toggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.processInactiveColor)));
            toggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white_36dp));
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            FileOutputStream fout = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(String.valueOf(priopick.getProgress()));
            bw.newLine();
            bw.write(polltime.getText().toString());
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void inactiveToActive(final View v){
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(getResources().getColor(R.color.processInactiveColor), from);   // from color1
        Color.colorToHSV(getResources().getColor(R.color.processActiveColor), to);     // to color2

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(500);                              // for 500 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                v.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();
    }

    private void activeToinactive(final View v){
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(getResources().getColor(R.color.processActiveColor), from);   // from color1
        Color.colorToHSV(getResources().getColor(R.color.processInactiveColor), to);     // to color2

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(500);                              // for 500 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                v.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();
    }

    private static final int MIN_POLLTIME = 1000;

    public void toggleService(View view){
        if(!on){
            //Toast.makeText(getActivity().getApplicationContext(), "Starting service...", Toast.LENGTH_SHORT).show();

            int pollt = polltime.getText().toString().matches("") ? 1 : Integer.parseInt(polltime.getText().toString());
            if(pollt < MIN_POLLTIME){
                pollt = MIN_POLLTIME;
                Snackbar.make(snackbarPos, "Poll time must be at least " + MIN_POLLTIME + "ms", Snackbar.LENGTH_LONG).show();
                polltime.setText(""+pollt);
            }

            PriorityChangeService.NICE_SHIFT = priopick.getProgress() + 1;
            PriorityChangeService.POLL_TIME = pollt;
            getActivity().startService(new Intent(getActivity(), PriorityChangeService.class));
            Log.i("priority change", "service started!");
            inactiveToActive(view_status);
            text_status.setText("Active");
            toggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.processInactiveColor)));
            toggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white_36dp));
            priopick.setEnabled(false);
            polltime.setEnabled(false);
        }else{
            //Toast.makeText(getActivity().getApplicationContext(), "Stopping service...", Toast.LENGTH_SHORT).show();
            getActivity().stopService(new Intent(getActivity(), PriorityChangeService.class));
            Log.i("priority change", "service stopped!");
            activeToinactive(view_status);
            text_status.setText("Inactive");
            toggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.processActiveColor)));
            toggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
            priopick.setEnabled(true);
            polltime.setEnabled(true);
        }
        on = !on;
    }
}
