package info.nightscout.implementation.queue.commands

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.data.PumpEnactResultObject
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.queue.commands.Command
import info.nightscout.implementation.R
import info.nightscout.interfaces.profile.Profile
import info.nightscout.interfaces.pump.PumpSync
import info.nightscout.interfaces.queue.Callback
import info.nightscout.rx.logging.LTag
import javax.inject.Inject

class CommandTempBasalPercent(
    injector: HasAndroidInjector,
    private val percent: Int,
    private val durationInMinutes: Int,
    private val enforceNew: Boolean,
    private val profile: Profile,
    private val tbrType: PumpSync.TemporaryBasalType,
    callback: Callback?
) : Command(injector, CommandType.TEMPBASAL, callback) {

    @Inject lateinit var activePlugin: ActivePlugin

    override fun execute() {
        val r =
            if (percent == 100)
                activePlugin.activePump.cancelTempBasal(enforceNew)
            else
                activePlugin.activePump.setTempBasalPercent(percent, durationInMinutes, profile, enforceNew, tbrType)
        aapsLogger.debug(LTag.PUMPQUEUE, "Result percent: $percent durationInMinutes: $durationInMinutes success: ${r.success} enacted: ${r.enacted}")
        callback?.result(r)?.run()
    }

    override fun status(): String = rh.gs(R.string.temp_basal_percent, percent, durationInMinutes)

    override fun log(): String = "TEMP BASAL $percent% $durationInMinutes min"
    override fun cancel() {
        aapsLogger.debug(LTag.PUMPQUEUE, "Result cancel")
        callback?.result(PumpEnactResultObject(injector).success(false).comment(info.nightscout.core.main.R.string.connectiontimedout))?.run()
    }
}