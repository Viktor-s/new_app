UPME 
==========
	App Flavors : 
	1. App : release - ProGuard (on), slowly build/ debug - ProGuard (off), fast build
	2. Launcher : Lrelease - ProGuard (on), slowly build/ Ldebug - ProGuard (off), fast build
	
	Install Exception :
	1. INSTALL FAILED CONFLICTING PROVIDER - delete all app who can contain provider;
	2. Dex-Exeption. Create MainDexClassList from Gradle go to UPME\app\build\intermediates\multi-dex\maindexlist and copy to multidex.keep
	3. --incremental is not supported with --multi-dex .Set incremental false in gradle.options.




