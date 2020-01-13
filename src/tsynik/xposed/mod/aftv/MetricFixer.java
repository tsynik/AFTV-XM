package tsynik.xposed.mod.aftv;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.util.List;

import tsynik.xposed.mod.aftv.BuildConfig;

public class MetricFixer implements IXposedHookLoadPackage
{
	private static final String TAG = "MetricFixer";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable
	{
		if (!lpparam.packageName.equals("android") &&
			lpparam.packageName.contains("com.amazon")) {
			// if (BuildConfig.DEBUG) Log.i(TAG, " ### IN ### called for package: " + lpparam.packageName);
			Class<?> MetricsServiceConnection = XposedHelpers.findClassIfExists("com.amazon.client.metrics.MetricsServiceConnection", lpparam.classLoader);
			if (MetricsServiceConnection != null) XposedHelpers.findAndHookMethod(MetricsServiceConnection, "getService", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					// if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override getService to null in " + lpparam.packageName);
					param.setResult(null);
				}
			});
			Class<?> AndroidMetricsFactoryImpl = XposedHelpers.findClassIfExists("com.amazon.client.metrics.AndroidMetricsFactoryImpl", lpparam.classLoader);
			if (AndroidMetricsFactoryImpl != null) XposedHelpers.findAndHookMethod(AndroidMetricsFactoryImpl, "shouldRecordMetrics", new XC_MethodHook() {
//				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//					if (BuildConfig.DEBUG) Log.d(TAG, "### AndroidMetricsFactoryImpl ### replace shouldRecordMetrics() false in " + lpparam.packageName);
//					return false;
//				}
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override shouldRecordMetrics to false");
					param.setResult(false);
				}
			});
			// MetricsBlacklist
			Class<?> MetricsBlacklist = XposedHelpers.findClassIfExists("com.amazon.client.metrics.MetricsBlacklist", lpparam.classLoader);
			if (MetricsBlacklist != null) XposedHelpers.findAndHookMethod(MetricsBlacklist, "isBlacklisted", String.class, String.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					// if (BuildConfig.DEBUG) Log.d(TAG, "### MetricsBlacklist ### override isBlacklisted to true in " + lpparam.packageName);
					param.setResult(true);
				}
			});
			// MetricsServiceWrapper
			Class<?> MetricsServiceWrapper = XposedHelpers.findClassIfExists("com.amazon.client.metrics.MetricsServiceWrapper", lpparam.classLoader);
			if (MetricsServiceWrapper != null) {
				XposedHelpers.findAndHookMethod(MetricsServiceWrapper, "getRecordMetricsSetting", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						if (BuildConfig.DEBUG) Log.d(TAG, "### MetricsServiceWrapper ### override getRecordMetricsSetting to false in " + lpparam.packageName);
						param.setResult(false);
					}
				});
				// void record(int priority, String program, String source, long timestamp, List<DataPointEnvelope> datapoints)
				XposedHelpers.findAndHookMethod(MetricsServiceWrapper, "record", int.class, String.class, String.class, long.class, List.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsServiceWrapper ### override record in " + lpparam.packageName);
						param.setResult(null);
					}
				});
			}
		}
		// KFTV Launcher
		if (lpparam.packageName.equals("com.amazon.tv.launcher")) {
			// ERR: AndroidRuntime:
			// java.lang.IncompatibleClassChangeError:
			// Class 'com.amazon.client.metrics.NullClickStreamMetricEvent' does not implement
			// interface 'java.lang.CharSequence' in call to 'int java.lang.CharSequence.length()'
			// AndroidRuntime:
			// at android.text.TextUtils.isEmpty()
			// at com.amazon.tv.metrics.LauncherSessionIdManager.getSessionId()
			Class<?> AndroidMetricsFactoryImpl = XposedHelpers.findClassIfExists("com.amazon.tv.metrics.LauncherSessionIdManager", lpparam.classLoader);
			if (AndroidMetricsFactoryImpl != null) XposedHelpers.findAndHookMethod(AndroidMetricsFactoryImpl, "getSessionId", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					//if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### fix null SessionID in " + lpparam.packageName);
					param.setResult(" "); // null SessionID
				}
			});
		}
		// TTS Lib
		if (lpparam.packageName.contains("com.ivona.tts")) {
			Class<?> AmazonMetrics = XposedHelpers.findClassIfExists("com.ivona.ttslib.metrics.a", lpparam.classLoader);
			if (AmazonMetrics != null) {
				Class<?> IvonaMetricEvent = XposedHelpers.findClassIfExists("com.ivona.ttslib.metrics.IvonaMetricEvent", lpparam.classLoader);
				if (IvonaMetricEvent != null) XposedHelpers.findAndHookMethod(AmazonMetrics, "a", IvonaMetricEvent, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							// if (BuildConfig.DEBUG) Log.d(TAG, "### VoiceApp ### NULL a(IvonaMetricEvent) in " + lpparam.packageName);
							param.setResult(null);
						}
				});
			}
		}
		// AVOD
		if (lpparam.packageName.equals("com.amazon.avod")) {
			Class<?> DcmConfiguration = XposedHelpers.findClassIfExists("com.amazon.avod.metrics.DcmConfiguration", lpparam.classLoader);
			if (DcmConfiguration != null) {
				XposedHelpers.findAndHookMethod(DcmConfiguration, "shouldRecordMetricsForDefaultProgramName", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						if (BuildConfig.DEBUG) Log.d(TAG, "### avod ### shouldRecordMetricsForDefaultProgramName false in " + lpparam.packageName);
						param.setResult(false);
					}
				});
				XposedHelpers.findAndHookMethod(DcmConfiguration, "shouldRecordMetricsForAlternateProgramName", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						if (BuildConfig.DEBUG) Log.d(TAG, "### avod ### shouldRecordMetricsForAlternateProgramName false in " + lpparam.packageName);
						param.setResult(false);
					}
				});
			}
		}
	}
}
