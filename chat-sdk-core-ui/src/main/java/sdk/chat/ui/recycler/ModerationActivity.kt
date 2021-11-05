package sdk.chat.ui.recycler

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import org.pmw.tinylog.Logger
import sdk.chat.core.dao.Keys
import sdk.chat.core.dao.Thread
import sdk.chat.core.dao.User
import sdk.chat.core.events.NetworkEvent
import sdk.chat.core.session.ChatSDK
import sdk.chat.ui.R
import sdk.chat.ui.activities.BaseActivity
import sdk.chat.ui.utils.ToastHelper
import sdk.guru.common.RX
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener

open class ModerationActivity : BaseActivity() {

//    class SingleItemCheckedViewModel : ViewEventViewModel<ViewEvent, OnSingleItemCheckListener>(
//            OnSingleItemCheckListener(viewId = R.id.radioButton)
//    )

    open var user: User? = null
    open var thread: Thread? = null

    open lateinit var smartRecyclerAdapter: SmartRecyclerAdapter

    override fun getLayout(): Int {
        return R.layout.activity_smart_recycler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        val userEntityID = intent.getStringExtra(Keys.IntentKeyUserEntityID)
        if (userEntityID != null && userEntityID.isNotEmpty()) {
            user = ChatSDK.db()?.fetchUserWithEntityID(userEntityID)
        }

        val threadEntityID = intent.getStringExtra(Keys.IntentKeyThreadEntityID)
        if (threadEntityID != null && threadEntityID.isNotEmpty()) {
            thread = ChatSDK.db()?.fetchThreadWithEntityID(threadEntityID)
        }

        if (user == null || thread === null) {
            ToastHelper.show(this, R.string.user_entity_id_not_set)
            finish()
            return
        }

        val items = items()
        if (items.size <= 2) {
            finish()
            showProfile(user)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        smartRecyclerAdapter = SmartRecyclerAdapter
            .items(items)
            .map(SectionViewModel::class, SectionViewHolder::class)
            .map(NavigationViewModel::class, NavigationViewHolder::class)
            .map(RadioViewModel::class, RadioViewHolder::class)
            .map(ButtonViewModel::class, ButtonViewHolder::class)
            .map(DividerViewModel::class, DividerViewHolder::class)
            .map(ToggleViewModel::class, ToggleViewHolder::class)
            .add(OnClickEventListener {

                it.view.clearAnimation()

                val model = smartRecyclerAdapter.getItem(it.position)
                if (model is NavigationViewModel) {
                    model.click()
                }
                if (model is ButtonViewModel) {
                    model.click(this)
                }
                if (model is RadioViewModel) {
                    if (!model.starting.get()) {
                        for (i in 0 until smartRecyclerAdapter.itemCount) {
                            val item = smartRecyclerAdapter.getItem(i)
                            if (item is RadioViewModel) {
                                if (item.group == model.group) {
                                    item.checked = item == model
                                }
                            }
                            smartRecyclerAdapter.notifyItemChanged(i)
                        }
                        model.click()
                    }
                }
//                    if (model is ToggleViewModel) {
//                        model.click()
//                        smartRecyclerAdapter.notifyItemChanged(it.position)
//                    }
            })

            .into(recyclerView)

        update()
    }

    override fun onStart() {
        super.onStart()

        dm.add(
            ChatSDK.events()
                ?.sourceOnMain()
                ?.filter(NetworkEvent.filterRoleUpdated(thread, user))
                ?.subscribe({ update() }, this)
        )

    }

    override fun onStop() {
        super.onStop()
        dm.dispose()
    }

    fun update() {
        smartRecyclerAdapter.setItems(items())
    }

    protected fun items(): MutableList<Any> {
        val items = arrayListOf<Any>()

        items.add(SectionViewModel(getString(R.string.profile)))
        items.add(
            NavigationViewModel(
                getString(R.string.view_profile)
            ) { showProfile(user) }
        )

        // Edit roles
        val threads = ChatSDK.thread()
        if (threads?.canChangeRole(thread, user) == true) {

            val roles = threads.availableRoles(thread, user)

            val currentRole = threads.roleForUser(thread, user)

            if (roles.size > 0) {
                val group = getString(R.string.role)
                items.add(SectionViewModel(group))

                val roleRunnable = object : RadioRunnable {
                    override fun run(value: String) {
                        dm.add(
                            threads.setRole(value, thread, user)
                                .observeOn(RX.main())
                                .subscribe({ Logger.info("Done") }, this@ModerationActivity)
                        )
                    }
                }

                for (role in roles) {
                    val localized = threads.localizeRole(role)

                    val startingValue = object : StartingValue {
                        override fun get(): Boolean {
                            return role == currentRole
                        }
                    }

                    items.add(RadioViewModel(group, localized, role, startingValue, roleRunnable))
                }
            }
        }

        val canChangeModerator = threads?.canChangeModerator(thread, user) == true
        val canChangeVoice = threads?.canChangeVoice(thread, user) == true

        if (canChangeModerator || canChangeVoice) {
            items.add(SectionViewModel(getString(R.string.moderation)))

            if (canChangeModerator) {

                val startingValue = object : StartingValue {
                    override fun get(): Boolean {
                        return threads!!.isModerator(thread, user)
                    }
                }

                items.add(
                    ToggleViewModel(
                        getString(R.string.moderator),
                        startingValue,
                        object : ToggleRunnable {
                            override fun run(value: Boolean) {
                                if (value) {
                                    dm.add(
                                        threads!!.grantModerator(thread, user)
                                            .observeOn(RX.main())
                                            .subscribe(
                                                { Logger.info("Done") },
                                                this@ModerationActivity
                                            )
                                    )
                                } else {
                                    dm.add(
                                        threads!!.revokeModerator(thread, user)
                                            .observeOn(RX.main())
                                            .subscribe(
                                                { Logger.info("Done") },
                                                this@ModerationActivity
                                            )
                                    )
                                }
                            }
                        })
                )
            }

            if (canChangeVoice) {

                val startingValue = object : StartingValue {
                    override fun get(): Boolean {
                        return !threads!!.hasVoice(thread, user)
                    }
                }

                items.add(
                    ToggleViewModel(
                        getString(R.string.silence),
                        startingValue,
                        object : ToggleRunnable {
                            override fun run(value: Boolean) {
                                if (value) {
                                    dm.add(
                                        threads!!.revokeVoice(thread, user)
                                            .observeOn(RX.main())
                                            .subscribe(
                                                { Logger.info("Done") },
                                                this@ModerationActivity
                                            )
                                    )
                                } else {
                                    dm.add(
                                        threads!!.grantVoice(thread, user)
                                            .observeOn(RX.main())
                                            .subscribe(
                                                { Logger.info("Done") },
                                                this@ModerationActivity
                                            )
                                    )
                                }
                            }
                        })
                )
            }
        }

        // Remove a user from the group
        if (threads?.canRemoveUserFromThread(thread, user) == true) {
            items.add(DividerViewModel())
            items.add(
                ButtonViewModel(
                    "Remove from Group",
                    resources.getColor(R.color.red),
                    object : ButtonRunnable {
                        override fun run(value: Activity) {
                            dm.add(
                                threads.removeUsersFromThread(thread, listOf(user))
                                    .observeOn(RX.main())
                                    .subscribe({ finish() }, this@ModerationActivity)
                            )
                        }
                    })
            )
        }

        return items
    }

    protected open fun showProfile(user: User?) {
        ChatSDK.ui().startProfileActivity(this, user?.entityID)
    }
}