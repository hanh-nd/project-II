package me.hanhngo.qrcode.view.history

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.hanhngo.qrcode.domain.BarcodeItem
import me.hanhngo.qrcode.domain.mapper.fromBarcodeEntityListToBarcodeItemList
import me.hanhngo.qrcode.repository.BarcodeRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: BarcodeRepository
) : ViewModel() {
    private val _barcodeList = MutableLiveData<List<BarcodeItem>>()
    val barcodeList: LiveData<List<BarcodeItem>>
        get() = _barcodeList

    init {
        getBarcodeList()
    }

    private fun getBarcodeList() {
        viewModelScope.launch {
            repository.getAll().collect {
                _barcodeList.value = fromBarcodeEntityListToBarcodeItemList(it)
            }
        }
    }

    fun searchBarcodeList(query: String) {
        viewModelScope.launch {
            repository.getAll().collect {
                val barcodeItemList = fromBarcodeEntityListToBarcodeItemList(it)

                if (query.trim().isNotEmpty()) {
                    _barcodeList.value = barcodeItemList.filter { item ->
                        item.content.contains(query, ignoreCase = true)
                    }
                } else {
                    _barcodeList.value = fromBarcodeEntityListToBarcodeItemList(it)
                }
            }
        }
    }
}