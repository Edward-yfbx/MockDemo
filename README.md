# MockDemo

Android 6.0 以前可以通过Settings中的信息获得虚拟定位的开关状态

```
    public static boolean isMockSettingsON(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }
```   

而6.0以后无法通过以上方法来获取虚拟定位开关状态，所以需要从其他思路来解决这个问题。

此Demo中的思路是查询所有已安装的APP,检测是否使用了权限：
```
android.permission.ACCESS_MOCK_LOCATION 
```
如果使用了该权限，且该APP正在运行，则认为虚拟定位为开启状态
