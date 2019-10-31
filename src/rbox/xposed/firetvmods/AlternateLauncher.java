package rbox.xposed.firetvmods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.util.Set;

public class AlternateLauncher implements IXposedHookLoadPackage
{
	private static final String TAG = "AlternateLauncher";
	private boolean freeze = true;
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable
	{

		// hook KFTV Launcher
		if (lpparam.packageName.equals("com.amazon.tv.launcher"))
		{
			// Log.i(TAG, " ### IN ### com.amazon.tv.launcher");
//			findAndHookMethod("com.amazon.tv.launcher.ui.HomeActivity_vNext", lpparam.classLoader, "onNewIntent", Intent.class, new XC_MethodHook() {
//				@Override
//				protected void afterHookedMethod(MethodHookParam param) throws Throwable
//				{
//					Log.i("### onNewIntent ### ", "com.amazon.tv.launcher.ui.HomeActivity_vNext");
//					Intent intent = (Intent)param.args[0];
//					Bundle extras = intent.getExtras();
//					if (extras != null) {
//						Log.i("### has extras ### ", extras.toString());
//					}
//				}
//			});
//		}

			findAndHookMethod("com.amazon.tv.GlobalSettings", lpparam.classLoader, "getFrozenMode", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, " ### com.amazon.tv.GlobalSettings ### getFrozenMode: " + freeze);
					if (freeze) {
						// FREEZE
						param.setResult(freeze);
					}
				}
			});
		}

		if (!lpparam.packageName.equals("android"))
			return;

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
				// DEBUG FireOS 6.2.6.6
				// 0 ### : com.amazon.tv.launcher.ui.HomeActivity_vNext priority: 950
				// 1 ### : com.amazon.firehomestarter.HomeStarterActivity priority: 1
				// 2 ### : com.amazon.tv.leanbacklauncher.MainActivity priority: 0
				// 3 ### : com.amazon.tv.settings.v2.system.FallbackHome priority: -1000
//				Log.d("### I ### ", param.args[0].toString());
//				if (extras != null) {
//					for (String key : extras.keySet()) {
//						Log.d("### E ### ", key + " : " + (extras.get(key) != null ? extras.get(key) : "NULL"));
//					}
//				}
//				for (int i=0; i < query.size(); i++) {
//					Log.d("### " + i + " ### ", query.get(i).activityInfo.name + " priority: " + query.get(i).priority);
//				}

				if (Intent.ACTION_MAIN.equals(intent.getAction())
					&& categories != null
					&& categories.size() == 1
					&& categories.contains(Intent.CATEGORY_HOME)) {

					// Check if we load Settings
					if (extras != null && extras.containsKey("navigate_node") && extras.get("navigate_node").equals("l_settings")) {
						loadSettings = true;
					}
					// If there are at least 3 activities and 1st one is Amazon Launcher
					// swap them so the 3rd one is used (don't swap with FallbackHome)
					if (query.size() > 2
						&& query.get(0).activityInfo.name.contains("com.amazon.tv.launcher.ui.HomeActivity")
						&& query.get(2).priority == 0
						&& !query.get(2).activityInfo.name.contains("com.amazon.tv.settings.v2.system.FallbackHome")
						&& !loadSettings)
					{
						ResolveInfo thirdLauncher = query.get(2);
						query.set(2, query.get(0));
						query.set(0, thirdLauncher);
					}
					if (query.size() < 4) // no user launcher
						freeze = false;
				}
			}
		});
	}
}
