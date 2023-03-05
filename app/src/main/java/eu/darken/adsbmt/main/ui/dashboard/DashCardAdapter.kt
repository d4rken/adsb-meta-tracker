package eu.darken.adsbmt.main.ui.dashboard

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import eu.darken.adsbmt.common.lists.BindableVH
import eu.darken.adsbmt.common.lists.differ.AsyncDiffer
import eu.darken.adsbmt.common.lists.differ.DifferItem
import eu.darken.adsbmt.common.lists.differ.HasAsyncDiffer
import eu.darken.adsbmt.common.lists.differ.setupDiffer
import eu.darken.adsbmt.common.lists.modular.ModularAdapter
import eu.darken.adsbmt.common.lists.modular.mods.DataBinderMod
import eu.darken.adsbmt.common.lists.modular.mods.TypedVHCreatorMod
import eu.darken.adsbmt.networkstats.ui.dashboard.NetworkStatsCardVH
import javax.inject.Inject


class DashCardAdapter @Inject constructor() :
    ModularAdapter<DashCardAdapter.BaseVH<DashCardAdapter.Item, ViewBinding>>(),
    HasAsyncDiffer<DashCardAdapter.Item> {

    override val asyncDiffer: AsyncDiffer<*, Item> = setupDiffer()

    init {
        modules.add(DataBinderMod(data))
        modules.add(TypedVHCreatorMod({ data[it] is NetworkStatsCardVH.Item }) { NetworkStatsCardVH(it) })
    }

    override fun getItemCount(): Int = data.size

    abstract class BaseVH<D : Item, B : ViewBinding>(
        @LayoutRes layoutId: Int,
        parent: ViewGroup
    ) : ModularAdapter.VH(layoutId, parent), BindableVH<D, B>

    interface Item : DifferItem
}