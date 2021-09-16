/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:24 PM
 */

package sdk.chat.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;


/**
 * Created by braunster on 04/09/14.
 */
public class Cropper {

    public static final int RequestCode = 123;

    /**
     * @return Intent that will open the crop context with an adjustable bounds for the cropping square.
     * * *
     */
    public static void startActivity(Activity activity, Uri output) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent photoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    .putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
            Intent chooserIntent = Intent.createChooser(photoIntent, "");
            activity.startActivityForResult(chooserIntent, Cropper.RequestCode);
        }
        // start picker to get image for cropping and then use the image in cropping activity
        /*CropImage.activity(output)
                .setAllowFlipping(false)
                .setInitialCropWindowPaddingRatio(0)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);*/

    }

    /**
     * @return Intent that will open the crop context with an adjustable bounds for the cropping square.
     * * *
     */
    public static void startSquareActivity(Activity activity, Uri output) {
        startActivity(activity, output);
        /*CropImage.activity(output)
                .setAspectRatio(1,1)
                .setInitialCropWindowPaddingRatio(0)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);*/
    }

    public static void startCircleActivity(Activity activity, Uri output) {
        startActivity(activity, output);
        /*CropImage.activity(output)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setInitialCropWindowPaddingRatio(0)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);*/
    }

}
