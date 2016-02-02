package sg.edu.nushigh.arp.backgroundprioritizer;

import android.os.AsyncTask;

public class KillerTask extends AsyncTask<Void, Void, Void> {
    // TODO we can have better type arguments, will use as we develop

    @Override
    protected void onPreExecute(){
        // what do we need, conditions checking?
    }

    @Override
    protected Void doInBackground(Void... params) {
        // we do the actual killing here
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        // a list of killed tasks?
    }
}
