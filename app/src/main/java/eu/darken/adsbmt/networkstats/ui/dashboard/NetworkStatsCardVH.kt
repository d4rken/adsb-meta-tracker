package eu.darken.adsbmt.networkstats.ui.dashboard

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import eu.darken.adsbmt.R
import eu.darken.adsbmt.common.lists.binding
import eu.darken.adsbmt.databinding.DashboardNetworkStatsItemBinding
import eu.darken.adsbmt.main.ui.dashboard.DashCardAdapter
import eu.darken.adsbmt.networkstats.core.NetworkStats

class NetworkStatsCardVH(parent: ViewGroup) :
    DashCardAdapter.BaseVH<NetworkStatsCardVH.Item, DashboardNetworkStatsItemBinding>(
        R.layout.dashboard_network_stats_item,
        parent
    ) {

    override val viewBinding = lazy {
        DashboardNetworkStatsItemBinding.bind(itemView)
    }

    fun TextView.setTrendColor(trend: Int) {
        setTextColor(
            getColorForAttr(
                when {
                    trend > 0 -> R.attr.colorPrimary
                    trend < 0 -> R.attr.colorError
                    else -> R.attr.colorControlNormal
                }
            )
        )
    }

    fun TextView.setTrendText(trend: Int) {
        text = if (trend > 0) "+$trend" else if (trend < 0) "$trend" else ""
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
                    else -> R.attr.colorControlNormal
                }
            )
        )
    }

    override val onBindData = binding(payload = true) { item ->
        val stats = item.stats

        trackerIcon.setImageResource(stats.network.iconRes)
        trackerName.setText(stats.network.labelRes)

        val beastTrend = stats.feederActive - stats.feederActiveDiff
        feederBeastValue.text = stats.feederActive.toString()
        feederBeastValueTrendValue.apply {
            setTrendText(beastTrend)
            setTrendColor(beastTrend)
        }
        feederBeastValueTrendIcon.setTrendIcon(beastTrend)

        if (stats is NetworkStats.Mlat) {
            mlatRow.isVisible = true
            val mlatTrend = stats.mlatActive - stats.mlatActiveDiff
            feederMlatValue.text = stats.mlatActive.toString()
            feederMlatValueTrendValue.apply {
                setTrendText(mlatTrend)
                setTrendColor(mlatTrend)
            }
            feederMlatValueTrendIcon.setTrendIcon(mlatTrend)
        } else {
            mlatRow.isVisible = false
        }

        if (stats is NetworkStats.Aircraft) {
            aircraftRow.isVisible = true
            val aircraftTrend = stats.aircraftActive - stats.aircraftActiveDiff
            feederAircraftValue.text = stats.aircraftActive.takeIf { it != 0 }?.toString() ?: "?"
            feederAircraftValueTrendValue.apply {
                setTrendText(aircraftTrend)
                setTrendColor(aircraftTrend)
            }
            feederAircraftValueTrendIcon.setTrendIcon(aircraftTrend)
        } else {
            aircraftRow.isVisible = false

        }

        updatedAtValue.text = stats.updatedAt.toString()

        root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(item.stats.network.website)
            }
            context.startActivity(intent)
        }
    }

    data class Item(
        val stats: NetworkStats,
    ) : DashCardAdapter.Item {
        override val stableId: Long = stats.network.labelRes.toLong()
    }
}