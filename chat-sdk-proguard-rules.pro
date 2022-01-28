#            | Classes | Members |
# Shrink:    |         |         |
# Obfuscate: |         |         |
-keep public class org.bouncycastle.jcajce.provider.**, org.jivesoftware.**, app.xmpp.**, sdk.chat.**, firestream.chat.**, sdk.guru.**, co.chatsdk.** {
    public protected *;
}

-keep public class smartadapter.**, materialsearchview.**, org.ocpsoft.prettytime.** {
    public protected *;
}

-keep class **.R$* {
    <fields>;
}

#            | Classes | Members |
# Shrink:    |    x    |         |
# Obfuscate: |    x    |         |

#            | Classes | Members |
# Shrink:    |    x    |    x    |
# Obfuscate: |         |         |
-keepnames class org.kxml2.io.**, org.xmlpull.** {
    public protected *;
}

#            | Classes | Members |
# Shrink:    |    x    |    x    |
# Obfuscate: |    x    |         |
#-keepclassmembernames public class sdk.chat.core.**, firestream.chat.**, sdk.guru.**, co.chatsdk.** {
#    public protected *;
#}
