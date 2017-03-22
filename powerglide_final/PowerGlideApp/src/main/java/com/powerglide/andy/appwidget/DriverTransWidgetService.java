package com.powerglide.andy.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Andy on 2/5/2017.
 */

public class DriverTransWidgetService extends RemoteViewsService {
    public static final String LOG_TAG = DriverTransWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DriverTransWidgetFactory(getApplicationContext(), intent);
    }
}
