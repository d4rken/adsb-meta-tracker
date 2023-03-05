package eu.darken.adsbmt.adsbfi.ui.dash

import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import eu.darken.adsbmt.R
import eu.darken.adsbmt.adsbfi.core.AdsbFiStats
import eu.darken.adsbmt.common.lists.binding
import eu.darken.adsbmt.databinding.DashboardAdsbfiStatsItemBinding
import eu.darken.adsbmt.main.ui.dashboard.DashCardAdapter

class AdsbFiStatsCardVH(parent: ViewGroup) :
    DashCardAdapter.BaseVH<AdsbFiStatsCardVH.Item, DashboardAdsbfiStatsItemBinding>(
        R.layout.dashboard_adsbfi_stats_item,
        parent
    ) {

    override val viewBinding = lazy {
        DashboardAdsbfiStatsItemBinding.bind(itemView)
    }

    fun TextView.setTrendColor(trend: Int) {
        setTextColor(
            when {
                trend > 0 -> getColorForAttr(R.attr.colorPrimary)
                trend < 0 -> getColorForAttr(R.attr.errorTextColor)
                else -> getColorForAttr(android.R.attr.textColor)
            }
        )
    }

    fun TextView.setTrendText(trend: Int) {
        text = if (trend > 0) "+$trend" else if (trend < 0) "-$trend" else ""
    }

    fun ImageView.setTrendIcon(trend: Int) {
        setImageResource(
            when {
                trend > 0 -> R.drawable.ic_trending_up_24
                trend < 0 -> R.drawable.ic_trending_down_24
                else -> R.drawable.ic_trending_flat_24
            }
        )
        imageTintList = ColorStateList.valueOf(
            getColorForAttr(
                when {
                    trend > 0 -> R.attr.colorPrimary
                    trend < 0 -> R.attr.colorError
                    else -> android.R.attr.textColor
                }
            )
        )
    }

    override val onBindData = binding(payload = true) { item ->
        val stats = item.stats

        val beastTrend = stats.beastFeeders - stats.beastFeedersPrevious
        feederBeastValue.text = stats.beastFeeders.toString()
        feederBeastValueTrendValue.apply {
            setTrendText(beastTrend)
            setTrendColor(beastTrend)
        }
        feederBeastValueTrendIcon.setTrendIcon(beastTrend)

        val mlatTrend = stats.mlatFeeders - stats.mlatFeedersPrevious
        feederMlatValue.text = stats.mlatFeeders.toString()
        feederMlatValueTrendValue.apply {
            setTrendText(mlatTrend)
            setTrendColor(mlatTrend)
        }
        feederMlatValueTrendIcon.setTrendIcon(mlatTrend)

        val aircraftTrend = stats.totalAircraft - stats.totalAircraftPrevious
        feederAircraftValue.text = stats.totalAircraft.toString()
        feederAircraftValueTrendValue.apply {
            setTrendText(aircraftTrend)
            setTrendColor(aircraftTrend)
        }
        feederAircraftValueTrendIcon.setTrendIcon(aircraftTrend)

        updatedAtValue.text = stats.updatedAt.toString()
    }

    data class Item(
        val stats: AdsbFiStats,
    ) : DashCardAdapter.Item {
        override val stableId: Long = Item::class.hashCode().toLong()
    }
}