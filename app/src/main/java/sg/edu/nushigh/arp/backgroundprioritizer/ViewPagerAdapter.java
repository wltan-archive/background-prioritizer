package sg.edu.nushigh.arp.backgroundprioritizer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{
    CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int numOfTabs){
        super(fm);

        this.titles = titles;
        this.numOfTabs = numOfTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int pos){
        switch(pos){
            case 0:
                return new Tab1();
            case 1:
                return new Tab2();
            case 2:
                return new Tab3();
            default:
                //nothing should happen
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int pos){
        return titles[pos];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount(){
        return numOfTabs;
    }
}
