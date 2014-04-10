package com.buitio.builtimageupload;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltFile;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.view.BuiltImageView;

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
public class ImageAdapter extends ArrayAdapter<BuiltFile> {

	Activity context;
	ArrayList<BuiltFile> builtFileList;
	int width;
	String bit;
	
	@SuppressLint("NewApi")
	public ImageAdapter(Context context, ArrayList<BuiltFile> builtFileList){
		super(context, 0, builtFileList);
		
		this.context = (Activity) context;
		this.builtFileList = builtFileList;
		
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		RelativeLayout relative = new RelativeLayout(context);
		relative.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		relative.setGravity(Gravity.CENTER);
		relative.setBackgroundColor(Color.WHITE);
		
		ProgressBar progressBar = new ProgressBar(context);
		progressBar.setLayoutParams(new RelativeLayout.LayoutParams(40, 40));
		
		/*
		 * BuiltImageView class takes built file as parameter and set image in it.
		 * 
		 * Dynamically declaration of BuiltImageView.
		 * and adding properties to it.
		 */
		final BuiltImageView imageView = new BuiltImageView(context);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new GridView.LayoutParams(width, 325));
		
		/*
		 * Setting target width for heavy image.
		 */
		imageView.setTargetedWidth(300);
		
		/*
		 * Setting progress bar object to show before download.
		 */
		imageView.showProgressOnLoading(progressBar);
		
		/*
		 * Setting image inside BuiltImageView using BuiltFile object.
		 */
		imageView.setBuiltFile(context, builtFileList.get(position), new BuiltImageDownloadCallback() {
			
			@Override
			public void onSuccess(Bitmap bitMap) {
				Log.i(context.getClass().getSimpleName(), bitMap.toString());
			}
			
			@Override
			public void onError(BuiltError error) {
				Toast.makeText(context, "error :"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onAlways() {
			}
		});
		relative.addView(progressBar);
		relative.addView(imageView);
		
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Dialog dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar);
				dialog.setContentView(R.layout.preview_image);
				dialog.setTitle("Preview Image");
				
				/*
				 * layout declaration of BuiltImageView
				 */
				BuiltImageView image = (BuiltImageView) dialog.findViewById(R.id.previewImage);
				image.setTargetedWidth(300);
				/*
				 * Setting download image from the cache.
				 */
				image.setImageBitmap(imageView.getCachedImage());
				
				dialog.show();
			}
		});
		
		return relative;
	}

}
