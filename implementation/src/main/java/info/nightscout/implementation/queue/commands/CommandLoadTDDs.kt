package info.nightscout.implementation.queue.commands

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.data.PumpEnactResultObject
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.queue.commands.Command
import info.nightscout.implementation.R
import info.nightscout.interfaces.queue.Callback
import info.nightscout.rx.logging.LTag
import javax.inject.Inject

class CommandLoadTDDs(
    injector: HasAndroidInjector,
    callback: Callback?
) : Command(injector, CommandType.LOAD_TDD, callback) {

    @Inject lateinit var activePlugin: ActivePlugin

    override fun execute() {
        val pump = activePlugin.activePump
        val r = pump.loadTDDs()
        aapsLogger.debug(LTag.PUMPQUEUE, "Result success: " + r.success + " enacted: " + r.enacted)
        callback?.result(r)?.run()
    }

    override fun status(): String = rh.gs(R.string.load_tdds)

    override fun log(): String = "LOAD TDDs"
    override fun cancel() {
        aapsLogger.debug(LTag.PUMPQUEUE, "Result cancel")
        callback?.result(PumpEnactResultObject(injector).success(false).comment(info.nightscout.core.main.R.string.connectiontimedout))?.run()
    }
}