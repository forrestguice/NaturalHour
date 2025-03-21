/**
    Copyright (C) 2024 Forrest Guice
    This file is part of SuntimesWidget.

    SuntimesWidget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesWidget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesWidget.  If not, see <http://www.gnu.org/licenses/>.
*/ 

package com.forrestguice.suntimes.naturalhour.ui.tiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.forrestguice.suntimes.naturalhour.R;

import java.lang.ref.WeakReference;

/**
 * @see SuntimesTileService
 * @see SuntimesTileActivity
 */
@SuppressWarnings("Convert2Diamond")
public abstract class SuntimesTileBase
{
    protected abstract int appWidgetId();

    @Nullable
    protected abstract Intent getConfigIntent(Context context);

    @Nullable
    protected abstract Intent getLaunchIntent(Context context);

    @Nullable
    protected abstract Intent getLockScreenIntent(Context context);

    @Nullable
    protected abstract Drawable getDialogIcon(Context context);

    @Nullable
    protected abstract CharSequence formatDialogTitle(Context context);

    @Nullable
    protected abstract CharSequence formatDialogMessage(Context context);

    protected WeakReference<Activity> activityRef;
    protected TextView dialogView_title, dialogView_message;

    public SuntimesTileBase(@Nullable Activity activity)
    {
        super();
        activityRef = new WeakReference<>(activity);
    }

    protected void initDefaults(Context context) {}

    protected LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    protected void updateViews(Context context, TextView titleView, TextView messageView)
    {
        if (titleView != null) {
            titleView.setText(formatDialogTitle(context));
        }
        if (messageView != null) {
            messageView.setText(formatDialogMessage(context));
        }
    }

    @Nullable
    protected Dialog createDialog(final Context context)
    {
        @SuppressLint("InflateParams")
        View view = getLayoutInflater(context).inflate(R.layout.layout_dialog_tile, null);
        dialogView_title = view.findViewById(android.R.id.title);
        dialogView_message = view.findViewById(android.R.id.message);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.LockScreenDialogStyle);
        dialog.setView(view);

        Drawable icon = getDialogIcon(context);
        ImageView iconView = view.findViewById(android.R.id.icon);
        if (iconView != null)
        {
            if (icon != null) {
                iconView.setImageDrawable(icon);
            }
            iconView.setVisibility(icon != null ? View.VISIBLE : View.GONE);

        } else if (icon != null) {
            dialog.setIcon(icon);
        }

        ImageButton settingsButton = view.findViewById(R.id.button_settings);
        final Intent configIntent = getConfigIntent(context);
        if (configIntent != null && settingsButton == null) {
            dialog.setNeutralButton(context.getString(R.string.action_settings), null);
        }

        final Intent launchIntent = getLaunchIntent(context);
        if (launchIntent != null) {
            dialog.setPositiveButton(getLaunchIntentTitle(context), null);
        }

        final WeakReference<Context> contextRef = new WeakReference<>(context);
        final Dialog d = dialog.create();
        d.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                startUpdateTask(context, d);

                Button settingsButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                if (settingsButton != null) {
                    settingsButton.setOnClickListener(onActionClickListener(activityRef, contextRef, d, configIntent));
                }

                Button launchButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                if (launchButton != null) {
                    launchButton.setOnClickListener(onActionClickListener(activityRef, contextRef, d, launchIntent));
                }
            }
        });

        d.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopUpdateTask();
                Activity activity = activityRef.get();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        if (settingsButton != null)
        {
            if (configIntent != null) {
                settingsButton.setOnClickListener(onActionClickListener(activityRef, contextRef, d, configIntent));
            }
            settingsButton.setVisibility(configIntent != null ? View.VISIBLE : View.GONE);
        }

        refreshUpdateTaskViews(context, d);
        return d;
    }

    private View.OnClickListener onActionClickListener(final WeakReference<Activity> activityRef, final WeakReference<Context> contextRef, final Dialog d, final Intent intent)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unlockAndRun(activityRef.get(), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (intent != null)
                        {
                            Context context = contextRef.get();
                            if (context != null) {
                                context.startActivity(intent);
                            }
                            d.dismiss();
                        }
                    }
                });
            }
        };
    }

    /**
     * When the activity is non-null and the device is locked, unlock it first and run r; otherwise just run r.
     * @param activity may be null
     * @param r to be run afterward
     */
    protected void unlockAndRun(final Activity activity, final Runnable r)
    {
        if (activity != null && getLaunchIntentNeedsUnlock())
        {
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null && isKeyguardSecure(keyguardManager)) {
                requestDismissKeyguard(activity, keyguardManager, r);

            } else {
                r.run();
            }
        } else {
            r.run();
        }
    }

    protected void requestDismissKeyguard(@NonNull Activity activity, @NonNull KeyguardManager keyguardManager, @Nullable final Runnable r)
    {
        if (Build.VERSION.SDK_INT >= 26)
        {
            keyguardManager.requestDismissKeyguard(activity, new KeyguardManager.KeyguardDismissCallback()
            {
                @Override
                public void onDismissSucceeded() {
                    if (r != null) {
                        r.run();
                    }
                }
            });

        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            if (r != null) {
                r.run();  // TODO: run only on success; how?
            }
        }
    }

    protected boolean isKeyguardSecure(@NonNull KeyguardManager keyguardManager)
    {
        if (Build.VERSION.SDK_INT >= 16) {
            return keyguardManager.isKeyguardSecure();
        } else return false;
    }

    protected boolean getLaunchIntentNeedsUnlock() {
        return true;
    }

    @NonNull
    protected String getLaunchIntentTitle(Context context) {
        return context.getString(R.string.app_name);
    }

    private Handler handler;
    private Runnable updateTask;
    protected final Runnable updateTask(final WeakReference<Context> contextRef, final Dialog dialog)
    {
        return new Runnable() {
            @Override
            public void run()
            {
                Context context = contextRef.get();
                if (context!= null)
                {
                    refreshUpdateTaskViews(context, dialog);
                    handler.postDelayed(this, updateTaskRateMs());
                }
            }
        };
    }
    public static final int UPDATE_RATE = 3000;     // update rate: 3s
    public int updateTaskRateMs() {
        return UPDATE_RATE;
    }

    protected void startUpdateTask(Context context, Dialog dialog)
    {
        //Log.d("DEBUG", "startUpdateTask");
        if (handler == null) {
            handler = new Handler();
        }
        if (updateTask != null) {
            stopUpdateTask();
        }
        updateTask = updateTask(new WeakReference<Context>(context), dialog);
        handler.postDelayed(updateTask, updateTaskRateMs());
    }

    protected void stopUpdateTask()
    {
        //Log.d("DEBUG", "stopUpdateTask");
        if (handler != null && updateTask != null) {
            handler.removeCallbacks(updateTask);
            updateTask = null;
        }
    }

    protected void refreshUpdateTaskViews(Context context, @Nullable Dialog dialog) {
        updateViews(context, dialogView_title, dialogView_message);
    }

}
