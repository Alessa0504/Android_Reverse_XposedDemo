package com.example.xposeddemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Jillian
 * @Description: hook入口类
 * @date 2024/8/3
 */
public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //包名匹配才hook
        if (!loadPackageParam.packageName.equals("com.zj.wuaipojie")) {
            return;
        }
        /**
         * 一、Hook普通方法
         */
        //hook method a:
//        public final String a(String str) {
//            return "这是一个" + str + "方法";
//        }
        //以下代码生成，查看图片：![4dad205fb0260910e7d8fbb1b5ed1c58.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b4845f1178f64e52b9f64e3731d84937~tplv-k3u1fbpfcp-watermark.image?)
        //className通过jadx反编译教程Demo查看获得；通过loadPackageParam.classLoader获取该类的类加载器；参数替换String.class
        XposedHelpers.findAndHookMethod("com.zj.wuaipojie.Demo", loadPackageParam.classLoader, "a", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //打印传入参数值
                //方法1:
                Log.e("zj2595普通方法", param.args[0].toString());
                //方法2:
                XposedBridge.log(param.args[0].toString());
                //修改传入参数的值
                String a = "pt";
                param.args[0] = a;
                Log.e("zj2595", param.args[0].toString());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //打印返回值
                Log.e("zj2595普通方法", param.getResult().toString());
                //修改返回值：需要根据实际返回值类型，比如boolean型就返回true
                param.setResult("123456");
            }
        });

        /**
         * 二、Hook复杂&自定义参数
         */
//        private final void complexParameterFunc(String str, HashMap<Object, Object> map) {
//            Log.d(Tag, "这是复杂参数方法 || " + str);
//        }
        Class a = loadPackageParam.classLoader.loadClass("com.zj.wuaipojie.Demo");
        XposedBridge.hookAllMethods(a, "complexParameterFunc", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("zj2595复杂&自定义参数", param.args[0].toString());  //获取第一个入参: String str
            }
        });

        /**
         * 三、Hook替换函数
         */
//        private final void repleaceFunc() {
//            Log.d(Tag, "这是替换函数");
//        }
        Class b = loadPackageParam.classLoader.loadClass("com.zj.wuaipojie.Demo");
        XposedBridge.hookAllMethods(b, "repleaceFunc", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return "";    //原本会输出 "这是替换函数"，修改后不输出内容
            }
        });
        /**
         * 四、Hook加固通杀：首先得拿到classLoader
         */
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                ClassLoader classLoader = context.getClassLoader();   //通过Application的attach方法拿到ClassLoader
                //hook逻辑在这里面写, 这里copy 一、Hook普通方法，替换拿到的ClassLoader
                XposedHelpers.findAndHookMethod("com.zj.wuaipojie.Demo", classLoader, "a", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //打印传入参数值
                        //方法1:
                        Log.e("zj2595普通方法", param.args[0].toString());
                        //方法2:
                        XposedBridge.log(param.args[0].toString());
                        //修改传入参数的值
                        String a = "pt";
                        param.args[0] = a;
                        Log.e("zj2595", param.args[0].toString());
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //打印返回值
                        Log.e("zj2595普通方法", param.getResult().toString());
                        //修改返回值：需要根据实际返回值类型，比如boolean型就返回true
                        param.setResult("123456");
                    }
                });
            }
        });

    }
}
