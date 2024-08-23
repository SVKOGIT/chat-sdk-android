
# ChatSDK
-keep public class sdk.chat.**, sdk.guru.**, firestream.chat.**, app.xmpp.**, co.chatsdk.** {
    public protected *;
}

-keepparameternames
-keeppackagenames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclasseswithmembernames,includedescriptorclasses class sdk.chat.**, sdk.guru.**, firestream.chat.**, app.xmpp.**, co.chatsdk.** {
    native <methods>;
}

-keepclassmembers,allowoptimization enum sdk.chat.**, sdk.guru.**, firestream.chat.**, app.xmpp.**, co.chatsdk.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class sdk.chat.**, sdk.guru.**, firestream.chat.**, app.xmpp.**, co.chatsdk.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# End

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Image Cropper
-keep class androidx.appcompat.widget.** { *; }

# matisse
-dontwarn com.bumptech.glide.**

# Pretty time
-keep class org.ocpsoft.prettytime.i18n.**

# ChatKit

-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$OutcomingTextMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$IncomingTextMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$IncomingImageMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.stfalcon.chatkit.messages.MessageHolders$OutcomingImageMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }

 # shameImageView

# -dontwarn android.support.v7.**
# -keep class android.support.v7.** { ; }
# -keep interface android.support.v7.* { ; }
 -keepattributes *Annotation,Signature
 -dontwarn com.github.siyamed.**
 -keep class com.github.siyamed.shapeimageview.**{ *; }

 # Iconics
 -keep class .R
 -keep class **.R$* {
     <fields>;
 }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn smartadapter.SmartAdapterBuilder
-dontwarn smartadapter.SmartRecyclerAdapter$Companion
-dontwarn smartadapter.SmartRecyclerAdapter
-dontwarn smartadapter.extension.SmartExtensionIdentifier
-dontwarn smartadapter.viewevent.listener.OnClickEventListener
-dontwarn smartadapter.viewevent.listener.OnLongClickEventListener
-dontwarn smartadapter.viewevent.model.ViewEvent$OnClick
-dontwarn smartadapter.viewevent.model.ViewEvent$OnLongClick
-dontwarn smartadapter.viewholder.SmartViewHolder