package com.powerglide.andy.utility;

import android.view.View;

/**
 * Created by Andy on 2/22/2017.
 */

public interface ViewRequestFragmentListener {
    void PrintSnackBarNoConnectionMessage(View parentView, String msg);

    void ShowProgressBar(boolean toShow);
}
