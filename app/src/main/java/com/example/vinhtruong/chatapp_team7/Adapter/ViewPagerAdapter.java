package com.example.vinhtruong.chatapp_team7.Adapter;

/**
 * Created by vinhtruong on 4/22/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.vinhtruong.chatapp_team7.Fragments.FragmentAllUsers;
import com.example.vinhtruong.chatapp_team7.Fragments.FragmentFriends;
import com.example.vinhtruong.chatapp_team7.Fragments.FragmentRooms;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                FragmentFriends friendsFragment = new FragmentFriends();
                return friendsFragment;
            case 1:
                FragmentRooms groupsChatFragment = new FragmentRooms();
                return groupsChatFragment;
            case 2:
                FragmentAllUsers allUsersFragment = new FragmentAllUsers();
                return  allUsersFragment;
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "FRIENDS";

            case 1:
                return "ROOMS";

            case 2:

                return "ALL USERS";

            default:
                return null;
        }

    }

}
