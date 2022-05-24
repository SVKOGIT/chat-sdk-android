package sdk.chat.core.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import sdk.chat.core.session.ChatSDK;

public class ImageMessageUtil {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float pxToDp(float dp) {
        Resources r = ChatSDK.ctx().getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics());
    }

    public static Size getImageMessageSize(int width, int height) {
        return getImageMessageSize((float) width, (float) height);
    }

    public static Size getImageMessageSize(float width, float height) {
        // Work out the dimensions
        float w = Math.min(getScreenWidth() * 0.8f, pxToDp(ChatSDK.config().imageMessageMaxWidthDp));

        float ar = 1;
        if (width > 0 && height > 0) {
            ar = (float) width / (float) height;
            ar = Math.max(ar, ChatSDK.config().imageMessageMinAR);
            ar = Math.min(ar, ChatSDK.config().imageMessageMaxAR);
        }

        float h = w / ar;

        return new Size(w, h);
    }

}