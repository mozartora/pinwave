package com.pinwave.ui.legal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Reads the `page` nav argument for [LegalScreen]. */
@HiltViewModel
class LegalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val page: String = savedStateHandle.get<String>("page").orEmpty()
}
