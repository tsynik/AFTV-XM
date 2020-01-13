package tsynik.xposed.mod.aftv;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;

import java.util.List;
import java.util.Set;

import tsynik.xposed.mod.aftv.BuildConfig;

public class AlternateLauncher implements IXposedHookLoadPackage
{
	private static final String TAG = "AlternateLauncher";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable
	{
		if (lpparam.packageName.equals("android")) {
			// Use the alternate launcher instead of the Amazon launcher
			findAndHookMethod("com.android.server.pm.PackageManagerService", lpparam.classLoader, "chooseBestActivity", Intent.class, String.class, int.class, List.class, int.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					@SuppressWarnings("unchecked")
					List<ResolveInfo> query = (List<ResolveInfo>)param.args[3];
					Intent intent = (Intent)param.args[0];
					Set<String> categories = intent.getCategories();
					Bundle extras = intent.getExtras();
					boolean loadSettings = false;
					boolean freeze = true;
					// DEBUG FireOS 6.2.6.8
					// 0 : com.amazon.tv.launcher.ui.HomeActivity_vNext priority: 950
					// 1 : com.google.android.leanbacklauncher.MainActivity priority: 2
					// 2 : com.amazon.firehomestarter.HomeStarterActivity priority: 1
					// 3 : com.amazon.tv.leanbacklauncher.MainActivity priority: 0
					// 4 : com.amazon.tv.settings.v2.system.FallbackHome priority: -1000
					if (BuildConfig.DEBUG) Log.d(TAG, "### I ### " + param.args[0].toString());
					if (extras != null) {
						for (String key : extras.keySet()) {
							if (BuildConfig.DEBUG) Log.d(TAG, "### E ### " + key + " : " + (extras.get(key) != null ? extras.get(key) : "NULL"));
						}
					}
					for (int i=0; i < query.size(); i++) {
						if (BuildConfig.DEBUG) Log.d(TAG, "### " + i + " ### " + query.get(i).activityInfo.name + " priority: " + query.get(i).priority);
					}

					if (Intent.ACTION_MAIN.equals(intent.getAction())
						&& categories != null
						&& categories.size() == 1
						&& categories.contains(Intent.CATEGORY_HOME)) {

						// Check if we load Settings
						if (extras != null && extras.containsKey("navigate_node") && extras.get("navigate_node").equals("l_settings")) {
							loadSettings = true;
						}
						// Find user launcher index
						int index = 0;
						for (int i=0; i < query.size(); i++) {
							if (query.get(i).activityInfo.name.contains("com.google.android.leanbacklauncher")) {
								index = i;
								if (BuildConfig.DEBUG) Log.d(TAG, "### L ### found leanbacklauncher at index " + index);
								// break; // allow override with user launcher
							}
							if (query.get(i).priority == 0) {
								index = i;
								if (BuildConfig.DEBUG) Log.d(TAG, "### L ### found user launcher at index " + index);
								break;
							}
						}
						// If user or leanback launcher found and 1st one is Amazon Launcher
						// swap them so the user one is used instead
						if (index > 0
							&& query.get(0).activityInfo.name.contains("com.amazon.tv.launcher.ui.HomeActivity")
							&& !loadSettings)
						{
							ResolveInfo userLauncher = query.get(index);
							query.set(index, query.get(0));
							query.set(0, userLauncher);
						}
						// (un)freeze Amazon Launcher
//						if (index == 0) { // no user launcher installed
//							freeze = false;
//						}
//						try {
//							if (BuildConfig.DEBUG) Log.d(TAG, "### Z ### freeze amazon launcher: " + freeze);
//							Context context = (Context) AndroidAppHelper.currentApplication();
//							Settings.Global.putString(context.getContentResolver(), "frozenMode", freeze ? "enabled" : "disabled");
//						} catch(Exception e) {
//							// e.printStackTrace();
//						}
					}
				}
			});
		}
		// hook KFTV Launcher
		if (lpparam.packageName.equals("com.amazon.tv.launcher"))
		{
			// force frozenMode in Amazon Launcher
			findAndHookMethod("com.amazon.tv.GlobalSettings", lpparam.classLoader, "getFrozenMode", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					if (BuildConfig.DEBUG) Log.i(TAG, "### com.amazon.tv.GlobalSettings ### override getFrozenMode to true");
					// FREEZE KFTV
					param.setResult(true);
				}
			});
		}
	}
}
