package tsynik.xposed.mod.aftv;

import android.util.Log;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import tsynik.xposed.mod.aftv.BuildConfig;

public class MetricFixer implements IXposedHookLoadPackage
{
	private static final String TAG = "MetricFixer";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable
	{

		// if (BuildConfig.DEBUG) Log.i(TAG, "### packageName ### " + lpparam.packageName);
		Class<?> MetricsServiceConnection = XposedHelpers.findClass("com.amazon.client.metrics.MetricsServiceConnection", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(MetricsServiceConnection, "getService", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			    //if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override getService to null");
		        param.setResult(null);
		    }
		});

		Class<?> AndroidMetricsFactoryImpl = XposedHelpers.findClass("com.amazon.client.metrics.AndroidMetricsFactoryImpl", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(AndroidMetricsFactoryImpl, "shouldRecordMetrics", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			    //if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override shouldRecordMetrics to false");
		        param.setResult(false);
		    }
		});

//		// DeviceControl hook
//		if (lpparam.packageName.equals("com.amazon.tv.devicecontrol"))
//		{
//			XposedBridge.log("Loaded app: " + lpparam.packageName);
//			Class<?> Metric = XposedHelpers.findClass("com.amazon.tv.oz.metrics.Metric", lpparam.classLoader);
//			Class<?> DeviceControlApiService = XposedHelpers.findClass("com.amazon.tv.devicecontrol.api.DeviceControlApiService", lpparam.classLoader);
//			Class<?> AsyncMetricsRecorder = XposedHelpers.findClass("com.amazon.tv.oz.metrics.engine.pmet.recorder.AsyncMetricsRecorder", lpparam.classLoader);
//			Class<?> DcmMetricsRecorder = XposedHelpers.findClass("com.amazon.tv.oz.metrics.engine.pmet.recorder.DcmMetricsRecorder", lpparam.classLoader);
//			Class<?> MetricsRecord = XposedHelpers.findClass("com.amazon.tv.oz.metrics.engine.pmet.MetricsRecord", lpparam.classLoader);
//			XposedHelpers.findAndHookMethod(DeviceControlApiService, "trackMetrics", Metric, new XC_MethodHook() {
//				@Override
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					if (BuildConfig.DEBUG) Log.i(TAG, "### DeviceControl ### override trackMetrics to null");
//					param.setResult(null); // NULL method
//				}
//			});
//			XposedHelpers.findAndHookMethod(AsyncMetricsRecorder, "record", MetricsRecord, new XC_MethodHook() {
//				@Override
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					if (BuildConfig.DEBUG) Log.i(TAG, "### DeviceControl ### override AsyncMetricsRecorder record to null");
//					param.setResult(null); // NULL method
//				}
//			});
//			XposedHelpers.findAndHookMethod(DcmMetricsRecorder, "record", MetricsRecord, new XC_MethodHook() {
//				@Override
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					if (BuildConfig.DEBUG) Log.i(TAG, "### DeviceControl ### override DcmMetricsRecorder record to null");
//					param.setResult(null); // NULL method
//				}
//			});
//		}

//		// Amazon Client Metrics API hook
//		if (lpparam.packageName.equals("com.amazon.client.metrics.api"))
//		{
//			XposedBridge.log("Loaded app: " + lpparam.packageName);
//			Class<?> AndroidMetricsFactoryImpl = XposedHelpers.findClass("com.amazon.client.metrics.AndroidMetricsFactoryImpl", lpparam.classLoader);
//			//Class<?> MetricsServiceConnection = XposedHelpers.findClass("com.amazon.client.metrics.MetricsServiceConnection", lpparam.classLoader);
//			Class<?> AbstractMetricsFactoryImpl = XposedHelpers.findClass("com.amazon.client.metrics.AbstractMetricsFactoryImpl", lpparam.classLoader);
//			Class<?> BaseMetricsFactoryImpl = XposedHelpers.findClass("com.amazon.client.metrics.BaseMetricsFactoryImpl", lpparam.classLoader);
//			Class<?> MetricEvent = XposedHelpers.findClass("com.amazon.client.metrics.MetricEvent", lpparam.classLoader);
//			Class<?> Priority = XposedHelpers.findClass("com.amazon.client.metrics.Priority", lpparam.classLoader);
//			Class<?> Channel = XposedHelpers.findClass("com.amazon.client.metrics.Channel", lpparam.classLoader);
//			Class<?> MetricsServiceWrapper = XposedHelpers.findClass("com.amazon.client.metrics.MetricsServiceWrapper", lpparam.classLoader);
//
//			XposedHelpers.findAndHookMethod(MetricsServiceWrapper, "getBoundService", new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//			    	if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override getBoundService to null");
//		        	return null;
//		        }
//			});
//
//			XposedHelpers.findAndHookMethod(AndroidMetricsFactoryImpl, "getInstance", new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//			    	if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override getInstance to null");
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(AndroidMetricsFactoryImpl, "getInstance", Context.class, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//			    	if (BuildConfig.DEBUG) Log.i(TAG, "### MetricsApi ### override getInstance to null");
//		        	return null;
//		        }
//			});
//
//			XposedHelpers.findAndHookMethod(AbstractMetricsFactoryImpl, "record", MetricEvent, Priority, Channel, new XC_MethodHook() {
//			//	@Override
//			//	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//			//		// pass null event
//			//		param.args[0] = null;
//			//	}
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			// BaseMetricsFactoryImpl
//			Class<?> MetricEventType = XposedHelpers.findClass("com.amazon.client.metrics.MetricEventType", lpparam.classLoader);
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createMetricEvent", String.class, String.class, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createMetricEvent", String.class, String.class, MetricEventType, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createMetricEvent", String.class, String.class, MetricEventType, Boolean.class, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createConcurrentMetricEvent", String.class, String.class, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createConcurrentMetricEvent", String.class, String.class, MetricEventType, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "createConcurrentMetricEvent", String.class, String.class, MetricEventType, Boolean.class, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "shouldRecordMetrics", new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	// return false;
//		        	return Boolean.valueOf(false);
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "record", MetricEvent, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//			XposedHelpers.findAndHookMethod(BaseMetricsFactoryImpl, "record", MetricEvent, Priority, new XC_MethodHook() {
//			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//		        	return null;
//		        }
//			});
//
//		}

//		// Amazon Client Metrics hook
//		if (lpparam.packageName.equals("com.amazon.client.metrics"))
//		{
//			Class<?> AndroidMetricsServiceAdapter = XposedHelpers.findClass("com.amazon.client.metrics.AndroidMetricsServiceAdapter", lpparam.classLoader);
//			XposedHelpers.findAndHookMethod(AndroidMetricsServiceAdapter, "getRecordMetricsSetting", new XC_MethodHook() {
//				@Override
//				protected void afterHookedMethod(MethodHookParam param) throws Throwable
//				{
//					if (BuildConfig.DEBUG) Log.i(TAG, "### com.amazon.client.metrics ### AndroidMetricsFactoryImpl ### override getRecordMetricsSetting to false");
//					param.setResult(false); // return false
//				}
//			});
//		}

	}
}
