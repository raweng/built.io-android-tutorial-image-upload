package com.buitio.builtimageupload;

import android.app.Application;

import com.raweng.built.Built;

/**
 * This is built.io android tutorial.
 * 
 * Short introduction of some classes with some methods.
 * Contain classes: 1.BuiltFile 2.BuiltImageView 3.BuiltUIPickerController 
 * 
 * For quick start with built.io refer "http://docs.built.io/quickstart/index.html#android"
 * 
 * @author raw engineering, Inc
 *
 */
public class ImageUploadApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		/*
		 * Initialize the application for a project using built.io Application credentials "Application Api Key" 
		 * and "Application UID".
		 * 
		 */
		try {
			Built.initializeWithApiKey(ImageUploadApplication.this, "blta6f4ec821d77f303", "imagetutorial");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
