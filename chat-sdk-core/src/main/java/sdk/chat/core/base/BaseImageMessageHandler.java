package sdk.chat.core.base;

import java.io.File;

import io.reactivex.Completable;
import sdk.chat.core.R;
import sdk.chat.core.dao.Keys;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.handlers.ImageMessageHandler;
import sdk.chat.core.rigs.FileUploadable;
import sdk.chat.core.rigs.MessageSendRig;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.MessageType;

/**
 * Created by ben on 10/24/17.
 */

public class BaseImageMessageHandler implements ImageMessageHandler {

    @Override
    public Completable sendMessageWithImage(final File imageFile, final Thread thread) {
        return new MessageSendRig(new MessageType(MessageType.Image), thread, message -> {
            ImageExtras extras = BaseImageMessageHandlerExtKt.getImageExtras(imageFile);

            message.setValueForKey(extras.getWidth(), Keys.MessageImageWidth);
            message.setValueForKey(extras.getHeight(), Keys.MessageImageHeight);
            message.setValueForKey("file://" + imageFile.getAbsolutePath(), Keys.MessageImageURL);

        }).setUploadable(new FileUploadable(imageFile, "image.jpg", "image/jpeg", uploadable -> {
            FileUploadable source = (FileUploadable) uploadable;
            File file = BaseImageMessageHandlerExtKt.compressImage(source.file);

            return new FileUploadable(file, uploadable.name, uploadable.mimeType, it -> it);
        }), (message, result) -> {
            // When the file has uploaded, set the image URL
            message.setValueForKey(result.url, Keys.MessageImageURL);

        }).run();
    }

    @Override
    public String textRepresentation(Message message) {
        return message.stringForKey(Keys.MessageImageURL);
    }

    @Override
    public String toString(Message message) {
        return ChatSDK.getString(R.string.image_message);
    }

    @Override
    public String getImageURL(Message message) {
        if (message.getMessageType().is(MessageType.Image) || message.getReplyType().is(MessageType.Image)) {
            return message.getImageURL();
        }
        return null;
    }

}
