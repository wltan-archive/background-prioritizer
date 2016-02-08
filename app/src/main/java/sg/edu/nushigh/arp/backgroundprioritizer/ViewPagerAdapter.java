package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{
    private CharSequence titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int numOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private int imageResId[];
    private Context c;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int imageResId[], int numOfTabs, Context c){
        super(fm);

        this.titles = titles;
        this.imageResId = imageResId;
        this.numOfTabs = numOfTabs;
        this.c = c;
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
        Drawable image = c.getResources().getDrawable(imageResId[pos]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount(){
        return numOfTabs;
    }
}
