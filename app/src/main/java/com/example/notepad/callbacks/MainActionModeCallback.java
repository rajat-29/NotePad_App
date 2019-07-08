package com.example.notepad.callbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.notepad.R;


public abstract class MainActionModeCallback implements ActionMode.Callback {

    private ActionMode action;
    private MenuItem countItem;
    private MenuItem shareItem;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
        this.action = mode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        this.shareItem = menu.findItem(R.id.action_share_notes);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {


    }

    public void setCount(String checkedCount)
    {
        if (countItem != null)
        this.countItem.setTitle(checkedCount);
    }

    public ActionMode getAction() {
        return action;
    }
}
