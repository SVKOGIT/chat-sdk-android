package co.chatsdk.ui.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.FileUploadResult;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.chat.MediaSelector;
import id.zelory.compressor.Compressor;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ImagePickerUploader {

    protected MediaSelector.CropType cropType;
    protected MediaSelector mediaSelector = new MediaSelector();

    public enum Status {
        Uploading,
        Complete
    }

    public class Result {


        public String url;
        public String uri;

        public Result(String url, String uri) {
            this.url = url;
            this.uri = uri;
        }

    }

    public ImagePickerUploader(MediaSelector.CropType cropType) {
        this.cropType = cropType;
    }

    public Single<Result> takePhoto (Activity activity) {
        return mediaSelector.startTakePhotoActivity(activity, cropType).map(this::compressFile).flatMap(this::uploadImageFile);
    }

    public Single<Result> choosePhoto (Activity activity) {
        return mediaSelector.startChooseImageActivity(activity, cropType).map(this::compressFile).flatMap(this::uploadImageFile);
    }

    public File compressFile (String path) throws Exception {
        File compress = new Compressor(ChatSDK.shared().context())
                .setMaxHeight(ChatSDK.config().imageMaxThumbnailDimension)
                .setMaxWidth(ChatSDK.config().imageMaxThumbnailDimension)
                .compressToFile(new File(path));

        Bitmap bitmap = BitmapFactory.decodeFile(compress.getPath());

        // Cache the file
        return ImageUtils.compressImageToFile(ChatSDK.shared().context(), bitmap, ChatSDK.currentUserID(), ".png");
    }

    public Single<Result> uploadImageFile (File file) {
        return ChatSDK.upload().uploadImage(BitmapFactory.decodeFile(file.getPath())).flatMapMaybe((Function<FileUploadResult, MaybeSource<Result>>) fileUploadResult -> {
            if (fileUploadResult.urlValid()) {
                return Maybe.just(new Result(fileUploadResult.url, file.getPath()));
            } else {
                return Maybe.empty();
            }
        }).firstElement().toSingle();
    }
}
