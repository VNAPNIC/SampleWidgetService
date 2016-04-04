package com.vnapnic.samplewidgetservice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SampleAppWidget extends AppWidgetProvider {

    private final String TAG = SampleAppWidget.class.getName();

    private final String ACTION_CLICK = "widget_click";
    private final String EXTRA_KEY_COUNT_DOWN = "count_down";

    private final int REQUEST_CODE_START = 1421;
    private final int REQUEST_CODE_STOP = 1422;

    public PendingIntent getPendingIntent(Context context, String action, int flag, int request, boolean isRun) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra(EXTRA_KEY_COUNT_DOWN, isRun);
        return PendingIntent.getBroadcast(context, request, intent, flag);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetID : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            views.setOnClickPendingIntent(R.id.btnStart, getPendingIntent(context, ACTION_CLICK, PendingIntent.FLAG_UPDATE_CURRENT, REQUEST_CODE_START, true));
            views.setOnClickPendingIntent(R.id.btnStop, getPendingIntent(context, ACTION_CLICK, PendingIntent.FLAG_CANCEL_CURRENT, REQUEST_CODE_STOP, false));

            appWidgetManager.updateAppWidget(appWidgetID, views);
        }
    }

    myCountDown countDown;

    public SampleAppWidget() {
        super();
        Log.d(TAG, "SampleAppWidget");
        if (countDown != null) {
            Log.d(TAG, "countDown.Cancel");
            countDown.cancel();
        }
        countDown = new myCountDown(System.currentTimeMillis(), 1000);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceiveeeeeeeeeeeeeeeeeee");


        if (ACTION_CLICK.equals(intent.getAction())) {
            boolean isRun = intent.getBooleanExtra(EXTRA_KEY_COUNT_DOWN, false);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            countDown.setContext(context);
            countDown.setViews(views);
            countDown.setSettings();

            if (isRun) {
                Log.d(TAG, "isRunnnnnnnnnnnnnnnnnnnnnnnnnnn");
                SharedPreferences settings = context.getSharedPreferences("myappname", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isRun", true);
                editor.putInt("count", 0);
                editor.commit();

                views.setViewVisibility(R.id.txtDone, View.GONE);
                views.setViewVisibility(R.id.txtStart, View.VISIBLE);
                views.setTextViewText(R.id.txtStart, "Widget on start");

                countDown.start();
            } else {
                SharedPreferences settings = context.getSharedPreferences("myappname", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isRun", false);
                editor.putInt("count", 0);
                editor.commit();

                Log.d(TAG, "not isRunnnnnnnnnnnnnnnnnnnnnnnnnnn");
                views.setViewVisibility(R.id.txtStart, View.GONE);
                views.setViewVisibility(R.id.txtDone, View.VISIBLE);
                views.setTextViewText(R.id.txtDone, "Widget on Stop");
                updateWidget(context, views);
            }
        }
    }

    public void updateWidget(Context context, RemoteViews views) {
        ComponentName componentName = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName, views);
    }

    private int i;

    class myCountDown extends CountDownTimer {
        private RemoteViews views;

        private Context context;
        private SharedPreferences settings;

        public void setViews(RemoteViews views) {
            this.views = views;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setSettings() {
            settings = context.getSharedPreferences("myappname", 0);
        }

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public myCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            Log.d(TAG, "myCountDownnnnnnnnnnnnnnnnn");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (settings == null) {
                return;
            }
            if (settings.getBoolean("isRun", false)) {
                views.setViewVisibility(R.id.txtCount, View.VISIBLE);
                views.setTextViewText(R.id.txtCount, i++ + "");
                updateWidget(context, views);
            } else {
                i = 0;
                this.cancel();
            }
        }


        @Override
        public void onFinish() {
        }

    }
}
