package eu.darken.adsbmt.adsbfi.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.Bugs
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltWorker
class AdsbFiStatsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val dispatcherProvider: DispatcherProvider,
) : CoroutineWorker(context, params) {

    private val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var finishedWithError = false

    @Inject lateinit var statsRepo: AdsbFiStatsRepo

    init {
        log(TAG, VERBOSE) { "init(): workerId=$id" }
    }

    override suspend fun doWork(): Result = try {
        val start = System.currentTimeMillis()
        log(TAG, VERBOSE) { "Executing $inputData now (runAttemptCount=$runAttemptCount)" }

        doDoWork()

        val duration = System.currentTimeMillis() - start

        log(TAG, VERBOSE) { "Execution finished after ${duration}ms, $inputData" }

        Result.success(inputData)
    } catch (e: Exception) {
        if (e !is CancellationException) {
            Bugs.report(e)
            finishedWithError = true
            Result.failure(inputData)
        } else {
            Result.success()
        }
    } finally {
        this.workerScope.cancel("Worker finished (withError?=$finishedWithError).")
    }

    private suspend fun doDoWork() = withContext(dispatcherProvider.IO) {
        try {
            withTimeout(15 * 1000) {
                statsRepo.refresh()
            }
            log(TAG) { "Worker" }
        } catch (e: TimeoutCancellationException) {
            log(TAG) { "Worker ran into timeout" }
        }
    }

    companion object {
        val TAG = logTag("AdsbFi", "Stats", "Worker")
    }
}
