package sdk.chat.core.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.jetbrains.annotations.Contract;
import org.pmw.tinylog.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.plugins.RxJavaPlugins;
import sdk.chat.core.base.BaseNetworkAdapter;
import sdk.chat.core.dao.DaoCore;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.User;
import sdk.chat.core.handlers.AudioMessageHandler;
import sdk.chat.core.handlers.AuthenticationHandler;
import sdk.chat.core.handlers.BlockingHandler;
import sdk.chat.core.handlers.ContactHandler;
import sdk.chat.core.handlers.ContactMessageHandler;
import sdk.chat.core.handlers.CoreHandler;
import sdk.chat.core.handlers.EventHandler;
import sdk.chat.core.handlers.FileMessageHandler;
import sdk.chat.core.handlers.HookHandler;
import sdk.chat.core.handlers.IEncryptionHandler;
import sdk.chat.core.handlers.ImageMessageHandler;
import sdk.chat.core.handlers.LastOnlineHandler;
import sdk.chat.core.handlers.LocationMessageHandler;
import sdk.chat.core.handlers.ProfilePicturesHandler;
import sdk.chat.core.handlers.PublicThreadHandler;
import sdk.chat.core.handlers.PushHandler;
import sdk.chat.core.handlers.ReadReceiptHandler;
import sdk.chat.core.handlers.SearchHandler;
import sdk.chat.core.handlers.StickerMessageHandler;
import sdk.chat.core.handlers.ThreadHandler;
import sdk.chat.core.handlers.TypingIndicatorHandler;
import sdk.chat.core.handlers.UploadHandler;
import sdk.chat.core.handlers.VideoMessageHandler;
import sdk.chat.core.interfaces.IKeyStorage;
import sdk.chat.core.interfaces.InterfaceAdapter;
import sdk.chat.core.module.Module;
import sdk.chat.core.storage.FileManager;
import sdk.chat.core.utils.AppBackgroundMonitor;
import sdk.chat.core.utils.KeyStorage;
import sdk.chat.core.utils.StringChecker;


/**
 * Created by ben on 9/5/17.
 */

public class ChatSDK {

    @NonNull
    private static final ChatSDK instance = new ChatSDK();
    @NonNull
    public Config<ChatSDK> config = new Config<>(this);

    public static String Preferences = "chat_sdk_preferences";
    @Nullable
    protected WeakReference<Context> context;
    @Nullable
    protected InterfaceAdapter interfaceAdapter;
    @Nullable
    protected StorageManager storageManager;
    @Nullable
    protected BaseNetworkAdapter networkAdapter;
    @Nullable
    protected FileManager fileManager;
    @NonNull
    protected List<String> requiredPermissions = new ArrayList<>();
    @Nullable
    protected ConfigBuilder<ChatSDK> builder;
    @Nullable
    protected String licenseIdentifier;
    protected boolean isActive = false;
    @Nullable
    protected IKeyStorage keyStorage;
    @NonNull
    protected List<Runnable> onActivateListeners = new ArrayList<>();

    @NonNull
    public static ChatSDK shared() {
        return instance;
    }

    protected ChatSDK() {
    }

    /**
     * You can override the network adapter and interface adapter classes here. If these values are provided, they will be used instead of any that could
     * be provided by a module. These values can be null but by the end of setup, the network adapter and interface adapter must both be set. Either
     * here or by a module.
     */
    public static ConfigBuilder<ChatSDK> configure(
            @Nullable Class<? extends BaseNetworkAdapter> networkAdapterClass,
            @Nullable Class<? extends InterfaceAdapter> interfaceAdapterClass
    ) {
        ChatSDK instance = shared();
        ConfigBuilder<ChatSDK> builder = new ConfigBuilder<>(instance);

        if (networkAdapterClass != null)
            builder.setNetworkAdapter(networkAdapterClass);
        if (interfaceAdapterClass != null)
            builder.setInterfaceAdapter(interfaceAdapterClass);

        instance.builder = builder;

        return builder;
    }

    /**
     * Configure and let modules provide the interface and network adapters. We will loop over the modules and see if they provide each adapter,
     * the first that does will be used and any subsequent provider will be ignored.
     */
    public static Config<ConfigBuilder<ChatSDK>> builder() {
        shared().builder = new ConfigBuilder<>(shared());
        return shared().builder.builder();
    }

    /**
     * Shortcut to return the interface adapter
     *
     * @return InterfaceAdapter
     */
    public static InterfaceAdapter ui() {
        return shared().interfaceAdapter;
    }

    public void activate(Context context) throws Exception {
        activate(context, null);
    }

    public void activateWithPatreon(Context context, @Nullable String patreonId) throws Exception {
        activate(context, "Patreon:" + patreonId);
    }

    public void activateWithEmail(Context context, @Nullable String email) throws Exception {
        activate(context, "Email:" + email);
    }

    public void activateWithGithubSponsors(Context context, @Nullable String githubSponsorsId) throws Exception {
        activate(context, "Github:" + githubSponsorsId);
    }

    @Nullable
    @Contract(pure = true)
    public static CoreHandler core() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.core;
    }

    @Nullable
    @Contract(pure = true)
    public static AuthenticationHandler auth() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.auth;
    }

    @Nullable
    public static Context ctx() {
        return shared().context();
    }

    @Nullable
    @Contract(pure = true)
    public static ThreadHandler thread() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.thread;
    }

    @NonNull
    public static String getString(@StringRes int stringId) {
        return getString(stringId, "");
    }

    @NonNull
    public static String getString(@StringRes int stringId, @NonNull String defaultValue) {
        Context context = ctx();
        return context == null ? defaultValue : context.getString(stringId);
    }

    @NonNull
    @Contract("_ -> new")
    public static Exception getException(@StringRes int stringId) {
        return new Exception(getString(stringId));
    }

    @NonNull
    public static Config<ChatSDK> config() {
        return shared().config;
    }

    @Nullable
    @Contract(pure = true)
    public static PublicThreadHandler publicThread() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.publicThread;
    }

    public FileManager fileManager() {
        return fileManager;
    }

    @Nullable
    @Contract(pure = true)
    public static PushHandler push() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.push;
    }

    @Nullable
    @Contract(pure = true)
    public static UploadHandler upload() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.upload;
    }

    @Nullable
    @Contract(pure = true)
    public static EventHandler events() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.events;
    }

    @Nullable
    public static User currentUser() {
        AuthenticationHandler handler = auth();
        if (handler == null) {
            return null;
        }
        return handler.currentUser();
    }

    @Nullable
    @Contract(pure = true)
    public static SearchHandler search() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.search;
    }

    @Nullable
    @Contract(pure = true)
    public static ContactHandler contact() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.contact;
    }

    @Nullable
    @Contract(pure = true)
    public static BlockingHandler blocking() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.blocking;
    }

    @Nullable
    @Contract(pure = true)
    public static IEncryptionHandler encryption() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.encryption;
    }

    @Nullable
    @Contract(pure = true)
    public static LastOnlineHandler lastOnline() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.lastOnline;
    }

    @Nullable
    @Contract(pure = true)
    public static AudioMessageHandler audioMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.audioMessage;
    }

    @Nullable
    public static String currentUserID() {
        AuthenticationHandler handler = auth();
        if (handler == null) {
            return null;
        }
        return handler.getCurrentUserEntityID();
    }

    @Nullable
    @Contract(pure = true)
    public static VideoMessageHandler videoMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.videoMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static HookHandler hook() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.hook;
    }

    @Nullable
    @Contract(pure = true)
    public static StickerMessageHandler stickerMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.stickerMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static ContactMessageHandler contactMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.contactMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static FileMessageHandler fileMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.fileMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static ImageMessageHandler imageMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.imageMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static LocationMessageHandler locationMessage() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.locationMessage;
    }

    @Nullable
    @Contract(pure = true)
    public static ReadReceiptHandler readReceipts() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.readReceipts;
    }

    @Nullable
    @Contract(pure = true)
    public static TypingIndicatorHandler typingIndicator() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.typingIndicator;
    }

    @Nullable
    @Contract(pure = true)
    public static ProfilePicturesHandler profilePictures() {
        BaseNetworkAdapter adapter = a();
        if (adapter == null) {
            return null;
        }
        return adapter.profilePictures;
    }

    @Nullable
    public static StorageManager db() {
        return shared().storageManager;
    }

    @NonNull
    public static String getMessageImageURL(@NonNull Message message) {
        @Nullable String imageURL = message.getImageURL();
        if (StringChecker.isNullOrEmpty(imageURL)) {
            ImageMessageHandler handler = imageMessage();
            imageURL = handler == null ? imageURL : handler.getImageURL(message);
        }
        if (StringChecker.isNullOrEmpty(imageURL)) {
            LocationMessageHandler handler = locationMessage();
            imageURL = handler == null ? imageURL : handler.getImageURL(message);
        }
        if (StringChecker.isNullOrEmpty(imageURL)) {
            ConfigBuilder<ChatSDK> builder = shared().builder;
            List<Module> modules = builder == null ? Collections.emptyList() : builder.modules;
            for (Module module : modules) {
                if (module.getMessageHandler() != null) {
                    imageURL = module.getMessageHandler().getImageURL(message);
                    if (imageURL != null) {
                        break;
                    }
                }
            }
        }
        return imageURL == null ? "" : imageURL;
    }

    @NonNull
    public static String getMessageText(@NonNull Message message) {
        String text = message.isReply() ? message.getReply() : message.getText();
        if (StringChecker.isNullOrEmpty(text)) {
            ImageMessageHandler handler = imageMessage();
            text = handler == null ? text : handler.toString(message);
        }
        if (StringChecker.isNullOrEmpty(text)) {
            LocationMessageHandler handler = locationMessage();
            text = handler == null ? text : handler.toString(message);
        }
        if (StringChecker.isNullOrEmpty(text)) {
            ConfigBuilder<ChatSDK> builder = shared().builder;
            List<Module> modules = builder == null ? Collections.emptyList() : builder.modules;
            for (Module module : modules) {
                if (module.getMessageHandler() != null) {
                    text = module.getMessageHandler().toString(message);
                    if (!StringChecker.isNullOrEmpty(text)) {
                        break;
                    }
                }
            }
        }
        return text == null ? "" : text;
    }

    @Nullable
    public static BaseNetworkAdapter a() {
        return shared().networkAdapter;
    }

    private void setContext(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Nullable
    public Context context() {
        WeakReference<Context> contextReference = context;
        if (contextReference == null) {
            return null;
        }

        return context.get();
    }

    public void stop() {
        context = null;
        config = new Config<>(this);
        if (networkAdapter != null) {
            networkAdapter = null;
        }
        if (interfaceAdapter != null) {
            interfaceAdapter = null;
        }
        requiredPermissions.clear();
        AppBackgroundMonitor.shared().stop();

        if (builder != null) {
            for (Module module : builder.modules) {
                module.stop();
            }
        }
        isActive = false;
    }

    public void activate(Context context, @Nullable String identifier) throws Exception {

        if (isActive) {
            throw new Exception("Chat SDK is already active. It is not recommended to call activate twice. If you must do this, make sure to call stop() first.");
        }

        setContext(context);
        keyStorage = new KeyStorage(context);

        ConfigBuilder<ChatSDK> builder = this.builder;
        if (builder != null)
            config = builder.config().build().build().config;

        Class<? extends BaseNetworkAdapter> networkAdapter = builder != null ? builder.networkAdapter : null;
        if (networkAdapter != null) {
            Logger.info("Network adapter provided by ChatSDK.configure call");
        }

        Class<? extends InterfaceAdapter> interfaceAdapter = builder != null ? builder.interfaceAdapter : null;
        if (networkAdapter != null) {
            Logger.info("Interface adapter provided by ChatSDK.configure call");
        }

        List<Module> modules = builder != null ? builder.modules : Collections.emptyList();
        for (Module module : modules) {
            if (networkAdapter == null) {
                if (module instanceof NetworkAdapterProvider) {
                    NetworkAdapterProvider provider = (NetworkAdapterProvider) module;
                    provider.getNetworkAdapter();
                    networkAdapter = provider.getNetworkAdapter();
                    Logger.info("Module: " + module.getName() + " provided network adapter");
                }
            }
            if (interfaceAdapter == null) {
                if (module instanceof InterfaceAdapterProvider) {
                    InterfaceAdapterProvider provider = (InterfaceAdapterProvider) module;
                    if (provider.getInterfaceAdapter() != null) {
                        interfaceAdapter = provider.getInterfaceAdapter();
                        Logger.info("Module: " + module.getName() + " provided interface adapter");
                    }
                }
            }
            for (String permission : module.requiredPermissions()) {
                if (!requiredPermissions.contains(permission)) {
                    requiredPermissions.add(permission);
                }
            }
            if (module.isPremium() && (identifier == null || identifier.isEmpty())) {
                System.out.println("<<");
                System.out.println(">>");
                System.out.println("<<");
                System.out.println(">>");
                System.out.println("To use premium modules you must include either your email, Patreon ID or Github Sponsors ID");
                System.out.println("ChatSDK.builder()....build().activateWith...");
                System.out.println("<<");
                System.out.println(">>");
                System.out.println("<<");
                System.out.println(">>");


                throw new Exception("To use premium modules you must include either your email, Patreon ID or Github Sponsors ID");
            }
        }

        if (networkAdapter != null) {
            setNetworkAdapter(networkAdapter.getConstructor().newInstance());
        } else {
            throw new Exception("The network adapter cannot be null. A network adapter must be defined using ChatSDK.configure(...) or by a module");
        }

        if (interfaceAdapter != null) {
            Constructor<? extends InterfaceAdapter> constructor = interfaceAdapter.getConstructor(Context.class);
            Object[] parameters = {context};

            setInterfaceAdapter(constructor.newInstance(parameters));
        } else {
            throw new Exception("The interface adapter cannot be null. An interface adapter must be defined using ChatSDK.configure(...) or by a module");
        }

        DaoCore.init(context);

        storageManager = new StorageManager();

        // Monitor the app so if it goes into the background we know
        AppBackgroundMonitor.shared().setEnabled(true);

        RxJavaPlugins.setErrorHandler(ChatSDK.events());

        fileManager = new FileManager(context);

        for (Module module : builder.modules) {
            module.activate(context);
            Logger.info("Module " + module.getName() + " activated successfully");
        }

        for (Runnable r : onActivateListeners) {
            r.run();
        }

        licenseIdentifier = identifier;
        isActive = true;

    }

    @Nullable
    public SharedPreferences getPreferences() {
        Context context = context();
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(Preferences, Context.MODE_PRIVATE);
    }

    public void setInterfaceAdapter(InterfaceAdapter interfaceAdapter) {
        shared().interfaceAdapter = interfaceAdapter;
    }

    public void setNetworkAdapter(BaseNetworkAdapter networkAdapter) {
        shared().networkAdapter = networkAdapter;
    }

    @NonNull
    public List<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isValid() {
        return isActive && (context != null && context.get() != null && networkAdapter != null && interfaceAdapter != null);
    }

    public void addOnActivateListener(Runnable runnable) {
        onActivateListeners.add(runnable);
    }

    @Nullable
    public String getLicenseIdentifier() {
        return licenseIdentifier;
    }

    @Nullable
    public IKeyStorage getKeyStorage() {
        return keyStorage;
    }

}

