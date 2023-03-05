package eu.darken.adsbmt.main.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbmt.R
import eu.darken.adsbmt.common.lists.differ.update
import eu.darken.adsbmt.common.lists.setupDefaults
import eu.darken.adsbmt.common.navigation.doNavigate
import eu.darken.adsbmt.common.uix.Fragment3
import eu.darken.adsbmt.common.viewbinding.viewBinding
import eu.darken.adsbmt.databinding.MainFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment3(R.layout.main_fragment) {

    override val vm: DashboardFragmentVM by viewModels()
    override val ui: MainFragmentBinding by viewBinding()

    @Inject lateinit var dashCardAdapter: DashCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_settings -> {
                        doNavigate(DashboardFragmentDirections.actionExampleFragmentToSettingsContainerFragment())
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }

        ui.list.setupDefaults(dashCardAdapter, dividers = false)

        ui.fab.setOnClickListener { vm.refresh() }

        vm.state.observe2(ui) {
            dashCardAdapter.update(it.items)
        }

        vm.newRelease.observe2(ui) { release ->
            Snackbar
                .make(
                    requireView(),
                    getString(R.string.updater_new_release_message),
                    Snackbar.LENGTH_LONG
                )
                .setAction(getString(R.string.general_show_action)) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(release.htmlUrl)
                    }
                    requireActivity().startActivity(intent)
                }
                .show()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
