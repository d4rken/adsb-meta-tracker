package eu.darken.adsbmt.adsbfi.ui.dash

import android.view.ViewGroup
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

    override val onBindData = binding(payload = true) { item ->
        val stats = item.stats
        feederBeastValue.text = stats.beastFeeders.toString()
        feederMlatValue.text = stats.mlatFeeders.toString()
        updatedAtValue.text = stats.updatedAt.toString()
    }

    data class Item(
        val stats: AdsbFiStats,
    ) : DashCardAdapter.Item {
        override val stableId: Long = Item::class.hashCode().toLong()
    }
}