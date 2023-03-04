package eu.darken.adsbmt.main.ui.main

import android.view.ViewGroup
import eu.darken.adsbmt.R
import eu.darken.adsbmt.common.lists.BindableVH
import eu.darken.adsbmt.common.lists.differ.AsyncDiffer
import eu.darken.adsbmt.common.lists.differ.DifferItem
import eu.darken.adsbmt.common.lists.differ.HasAsyncDiffer
import eu.darken.adsbmt.common.lists.differ.setupDiffer
import eu.darken.adsbmt.common.lists.modular.ModularAdapter
import eu.darken.adsbmt.common.lists.modular.mods.DataBinderMod
import eu.darken.adsbmt.common.lists.modular.mods.SimpleVHCreatorMod
import eu.darken.adsbmt.databinding.SomeItemLineBinding
import javax.inject.Inject


class SomeAdapter @Inject constructor() : ModularAdapter<SomeAdapter.ItemVH>(),
    HasAsyncDiffer<SomeAdapter.Item> {

    override val asyncDiffer: AsyncDiffer<*, Item> = setupDiffer()

    override fun getItemCount(): Int = data.size

    init {
        modules.add(DataBinderMod(data))
        modules.add(SimpleVHCreatorMod { ItemVH(it) })
    }

    data class Item(
        val label: String,
        val number: Long,
        val onClickAction: (Long) -> Unit
    ) : DifferItem {
        override val stableId: Long = label.hashCode().toLong()
    }

    class ItemVH(parent: ViewGroup) : ModularAdapter.VH(R.layout.some_item_line, parent),
        BindableVH<Item, SomeItemLineBinding> {

        override val viewBinding: Lazy<SomeItemLineBinding> = lazy {
            SomeItemLineBinding.bind(itemView)
        }

        override val onBindData: SomeItemLineBinding.(
            item: Item,
            payloads: List<Any>
        ) -> Unit = { item, _ ->
            numberDisplay.text = "${item.label} #${item.number}"

            itemView.setOnClickListener { item.onClickAction(item.number) }
        }
    }
}