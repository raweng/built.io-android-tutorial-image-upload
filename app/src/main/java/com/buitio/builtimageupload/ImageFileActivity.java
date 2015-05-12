package com.buitio.builtimageupload;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltQuery;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUpload;
import com.raweng.built.BuiltUploadCallback;
import com.raweng.built.QueryResult;
import com.raweng.built.QueryResultsCallBack;
import com.raweng.built.userInterface.BuiltUIPickerController;
import com.raweng.built.userInterface.BuiltUIPickerController.PickerResultCode;
import com.raweng.built.utilities.BuiltConstant;

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
public class ImageFileActivity extends Activity {

	GridView 			 gridView;
	ImageAdapter		 imageAdapter;

	/*
	 * Declaring BuiltUIPickerController object for fetching files from the device.
	 * 
	 * Note: 
	 * 1. Before use of this class UIAndroidExplorerScreen need to add in manifest
	 * <activity android:name="com.raweng.built.userInterface.UIAndroidExplorerScreen"></activity>
	 * 
	 * 2. Allows an application to write to external memory. "http://developer.android.com/reference/android/Manifest.permission.html#WRITE_EXTERNAL_STORAGE"
	 *
	 */
	BuiltUIPickerController pickerObject = null;
    BuiltApplication builtApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_file);

		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(imageAdapter);
        try {
            builtApplication = Built.application(ImageFileActivity.this, "blta6f4ec821d77f303");
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*
		 * Fetching all images to set in grid view.
		 */
		fetchAllImages_UsingBuiltQuery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.image_file, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh: {

			fetchAllImages_UsingBuiltQuery();
			break;
		}
		case R.id.action_new: {

			/*
			 * Initializing the BuiltUIPickerController object.
			 */
			pickerObject = new BuiltUIPickerController(this);

			/*
			 * Displays the file selection dialogue.
			 * File and file path can get in onActivityResult method.
			 */
			try {
				pickerObject.showPicker(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Fetching all images of application.
	 */
	private void fetchAllImages_UsingBuiltQuery() {

		BuiltQuery query = builtApplication.classWithUid("userphoto").query();
		query.descending("updated_at");
		query.setCachePolicy(Built.BuiltCachePolicy.CACHE_ELSE_NETWORK);
		query.execInBackground(new QueryResultsCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, QueryResult queryResult, BuiltError builtError) {

                if (builtError == null) {
                    /// query is executed successfully.

                    ArrayList<BuiltUpload> builtFileList = new ArrayList<BuiltUpload>();

                    if (!queryResult.getResultObjects().isEmpty()) {

                        for (int i = 0; i < queryResult.getResultObjects().size(); i++) {

                            try {
                                JSONObject jsonFileField = queryResult.getResultObjects().get(i).getJSONObject("image_file");

								BuiltUpload builtFile = builtApplication.upload();

                                builtFile.configure(jsonFileField);

                                builtFileList.add(builtFile);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (builtFileList != null) {
                            imageAdapter = new ImageAdapter(ImageFileActivity.this, builtFileList);
                            gridView.setAdapter(imageAdapter);
                        }
                    }

                } else {
                    Toast.makeText(ImageFileActivity.this, "Error :" + builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
	}

	/**
	 * Upload a file.
	 * 
	 * @param filePath
	 * 					file path received from BuiltUIPickerController in onActivityResult.
	 */
	public void uploadFileUsingBuiltFile(String filePath){

		/*
		 * Uploading a file using BuiltFile class.
		 */
		final BuiltUpload builtFileObject = builtApplication.upload();
		builtFileObject.setFile(filePath);
		builtFileObject.saveInBackground(new BuiltUploadCallback() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                if (builtError == null){
                    /// object is created successfully.
                    /// builtFileObject contains more details of file.

                    Toast.makeText(ImageFileActivity.this, builtFileObject.getUploadUid() + " File uploaded sussessfully...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ImageFileActivity.this, "setting uid with object to save", Toast.LENGTH_SHORT).show();

				/*
				 * creating object with file attachment.
				 */
                    createBuiltObject(builtFileObject.getUploadUid());

                }else {
                    /// builtErrorObject contains more details of error.
                    Toast.makeText(ImageFileActivity.this, "error :" + builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onProgress(int i) {

            }
        });
	}
	
	/**
	 * Creating object with file attachment.
	 */
	private void createBuiltObject(String uploadUid) {
		
		BuiltObject object = builtApplication.classWithUid("userphoto").object();
		object.set("image_file", uploadUid);
		object.saveInBackground(new BuiltResultCallBack() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                if (builtError == null){
                    Toast.makeText(ImageFileActivity.this, "BuiltObject created sussessfully...refresh it get new...", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ImageFileActivity.this, "Error :"+builtError.getErrors(), Toast.LENGTH_SHORT).show();
                }
            }
        });
		
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * Checking requestCode and data separately for different options.
		 * 1. file system 
		 * 2. gallery
		 * 3. capture image
		 * 4. capture video
		 * 
		 * This becomes easier using PickerResultCode which is static enum of BuiltUIPickerController class.
		 * 
		 */
		if(requestCode == PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				/*
				 * Receiving file path using a BuiltUIPickerController object to upload from file system.
				 */
				uploadFileUsingBuiltFile((String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));

			}else if(resultCode == RESULT_CANCELED){
				Log.i("ImageFileActivity", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				/*
				 * Receiving file path using a BuiltUIPickerController object to upload from gallery.
				 */
				uploadFileUsingBuiltFile((String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));

			}else if(resultCode == RESULT_CANCELED){
				Log.i("ImageFileActivity", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				/*
				 * Receiving file path using a BuiltUIPickerController object to upload from camera.
				 */
				uploadFileUsingBuiltFile((String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));

			}else if(resultCode == RESULT_CANCELED){
				Log.i("ImageFileActivity", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				/*
				 * Receiving file path using a BuiltUIPickerController object to upload from recording video.
				 */
				uploadFileUsingBuiltFile((String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath"));

			}else if(resultCode == RESULT_CANCELED){
				Log.i("ImageFileActivity", "Nothing selected");
			}
		}

	}


}
