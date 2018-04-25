# android-permission-handler
A permission helper systemizing runtime permission handling on Android devices following permission guidelines

Distribution and setup
=====
We currently only offer aar files that can be imported. Online aar is planned but not yet available. 
A simple way to use the aar file is:
1. introduce a new permissionhandler module by importing via Android Studio.
2. add `implementation project(':permissionhandler')` in the build.gradle dependencies of your apps.

Usage
=====
By "Following permission guidelines" we mean that PermissionHandler can help you organize permission request gracefully and in the meanwhile reach the responsive requirement.

For example, we show snackbars that educates user why you are asking for a permission after they have rejected the permission or they have checked "Never ask again". There is also some handling to make your app able to continue after user have granted the permission with opening your app setting page via snackbar.

Using the library
=====
1. Identify the actions. An action is a sequential block of code that will be executed if permission is granted / not granted. Each action requires and is mapped to a permission. For actions requiring multiple permissions, see Restrictions / Implementation. For each action, we need one actionId declared. This actionId is how we distinguish each action and is also the requestcode we will be passing to the system.
2. Create an instance of PermissionHandler and implement interfaces in [PermissionHandle][1] that maps action and permissions. `doActionDirect()`, `doActionGranted()` and `doActionSetting()` are where you'd like to put your action logics. You would like to move your earlier implementations to these functions by each actionId. `doActionNoPermission` is the cleanup you'd like to do when the permission is not granted. `makeAskAgainSnackBar()` returns the snackbar that is used when user have denied or even decided to "Don't ask again" your permission requests. For simple usage we provide a Util function `PermissionHandler.makeAskAgainSnackBar()`. In `requestPermissions()` one should map the id to the permission request. For getDoNotAskAgainDialogString(), see Restrictions / Implementation.
3. Hook specific PermissionHandler methods in corresponding lifecycle callbacks in your Activity/Fragment. These methods are `onRequestPermissionsResult()`, `onRestoreInstanceState()`, `onSaveInstanceState()`, and `onActivityResult()`. They should be hooked to the corresponding activity counterparts with the same name. When using a Fragment, one can hook `onRestoreInstanceState()` to `onViewStateRestored()`
4. Now that all actual implementations are in the three `doAction()` functions, replace your original action implementations with `permissionHandler.tryAction()` this will automatically delegate to the correct `doAction()` function call.
5. See [here][2] for an concise but thorugh example of how one can migrate to use PermissionHandler.

Restrictions / Implementation
=====
* We do not support actions that requires multiple permissions (yet). But we'll be happy to know if there are needs for this feature.
* We block permission requests during the showing of snackBar.
* If the process is killed after user enters setting, we cannot resume the action the user was trying to do before even if the user have granted the permission.
* We uses the sharedpreference to store the permission usage.
* `getDoNotAskAgainDialogString()` is currently unused but we might re-introduce them so they're currently kept in the source code.


[1]: https://github.com/mozilla-tw/android-permission-handler/blob/master/permissionhandler/src/main/java/org/mozilla/permissionhandler/PermissionHandle.java
[2]: https://github.com/mozilla-tw/android-permission-handler/commit/884da082a07a04dd0ddee3e2eb1685e8d39981f3
