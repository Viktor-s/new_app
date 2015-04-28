package me.justup.upme.launcher;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

import me.justup.upme.LauncherActivity;

public class LauncherAppWidgetHost extends AppWidgetHost {

    LauncherActivity mLauncher = null;

	public LauncherAppWidgetHost(LauncherActivity launcher, int hostId) {
		super(launcher, hostId);
		mLauncher = launcher;
	}

	@Override
	protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
			AppWidgetProviderInfo appWidget) {
		return new LauncherAppWidgetHostView(context);
	}

	@Override
	public void stopListening() {
		super.stopListening();
		clearViews();
	}

	/*
	 * Called when the set of available widgets changes (ie. widget containing
	 * packages are added, updated or removed, or widget components are enabled
	 * or disabled.)
	 */
	protected void onProvidersChanged() {
		mLauncher.bindPackagesUpdated();
	}
}
