package eu.darken.adsbmt.main.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbmt.R
import eu.darken.adsbmt.common.BuildConfigWrap
import eu.darken.adsbmt.common.lists.differ.update
import eu.darken.adsbmt.common.lists.setupDefaults
import eu.darken.adsbmt.common.navigation.doNavigate
import eu.darken.adsbmt.common.observe2
import eu.darken.adsbmt.common.uix.Fragment3
import eu.darken.adsbmt.common.viewbinding.viewBinding
import eu.darken.adsbmt.databinding.MainFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment3(R.layout.main_fragment) {

    override val vm: MainFragmentVM by viewModels()
    override val ui: MainFragmentBinding by viewBinding()

    @Inject lateinit var someAdapter: SomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_help -> {
                        Snackbar.make(requireView(), R.string.app_name, Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_settings -> {
                        doNavigate(MainFragmentDirections.actionExampleFragmentToSettingsContainerFragment())
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
            subtitle = "Buildtype: ${BuildConfigWrap.BUILD_TYPE}"
        }

        ui.list.setupDefaults(someAdapter)

        ui.fab.setOnClickListener { vm.refresh() }

//        vm.listItems.observe2(this@MainFragment, ui) {
//            someAdapter.update(it)
//        }

        super.onViewCreated(view, savedInstanceState)
    }
}
