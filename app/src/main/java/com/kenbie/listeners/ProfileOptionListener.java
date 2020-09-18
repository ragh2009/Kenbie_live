package com.kenbie.listeners;

import com.kenbie.model.OptionsData;

import java.util.ArrayList;

/**
 * Created by rajaw on 7/28/2017.
 */

public interface ProfileOptionListener {
    public void getAction(OptionsData value);
    public void getDataList(ArrayList<OptionsData> value, int position, int type);
}
