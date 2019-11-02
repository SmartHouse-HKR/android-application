package se.hkr.smarthouse.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.Response
import se.hkr.smarthouse.ui.ResponseType
import se.hkr.smarthouse.util.ApiEmptyResponse
import se.hkr.smarthouse.util.ApiErrorResponse
import se.hkr.smarthouse.util.ApiSuccessResponse
import se.hkr.smarthouse.util.Constants.Companion.NETWORK_TIMEOUT
import se.hkr.smarthouse.util.Constants.Companion.TESTING_NETWORK_DELAY
import se.hkr.smarthouse.util.ErrorHandling
import se.hkr.smarthouse.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import se.hkr.smarthouse.util.ErrorHandling.Companion.ERROR_UNKNOWN
import se.hkr.smarthouse.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import se.hkr.smarthouse.util.GenericApiResponse
import java.util.concurrent.CancellationException

/**
 * Every time there is a database or cache transaction, a new object of this class will be created
 * and will emit its result through the "result" variable.
 */
abstract class NetworkBoundResource<ResponseObject, ViewStateType>(
    isNetworkAvailable: Boolean
) {
    val TAG: String = "AppDebug"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        result.value = DataState.loading(isLoading = true, cachedData = null)
        if (isNetworkAvailable) {
            // This starts along with the coroutine below
            coroutineScope.launch {
                delay(TESTING_NETWORK_DELAY)
                withContext(Main) {
                    val apiResponse = createCall()
                    result.addSource(apiResponse) { response ->
                        result.removeSource(apiResponse)
                        coroutineScope.launch {
                            handleNetworkCall(response)
                        }
                    }
                }
            }
            // And this are started at the same time, checking if the time takes too long
            GlobalScope.launch(IO) {
                delay(NETWORK_TIMEOUT)
                if (!job.isCompleted) {
                    Log.e(TAG, "NetworkBoundResource: Job network timeout")
                    job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                }
            }
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned Nothing (HTTP 204)")
                onErrorReturn("HTTP 204. Returned Nothing", true, false)
            }
        }
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: Called...")
        job = Job()
        // Will be triggered if it was canceled or when it just finished normally
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e("TAG", "NetworkBoundResource: Job has been cancelled.")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed...")
                        //Do nothing. Should be handled already.
                    }
                }
            }
        )
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            result.value = dataState
        }
    }

    fun onErrorReturn(
        errorMessage: String?,
        shouldUseDialog: Boolean,
        shouldUseToast: Boolean
    ) {
        var message = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if (message == null) {
            message = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(message)) {
            message = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(
            DataState.error(
                response = Response(
                    message = message,
                    responseType = responseType
                )
            )
        )
    }

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)
}