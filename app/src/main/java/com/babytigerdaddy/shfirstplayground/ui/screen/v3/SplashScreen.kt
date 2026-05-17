package com.babytigerdaddy.shfirstplayground.ui.screen.v3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import com.babytigerdaddy.shfirstplayground.data.local.AppStateStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

sealed class SplashTarget {
    data object Onboarding : SplashTarget()
    data object Home : SplashTarget()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appStateStore: AppStateStore,
) : ViewModel() {

    private val _target = MutableStateFlow<SplashTarget?>(null)
    val target: StateFlow<SplashTarget?> = _target.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = appStateStore.onboardingCompleted.first()
            _target.value = if (completed) SplashTarget.Home else SplashTarget.Onboarding
        }
    }
}

@Composable
fun SplashScreen(
    onResolved: (SplashTarget) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val target by viewModel.target.collectAsStateWithLifecycle()
    LaunchedEffect(target) {
        target?.let(onResolved)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
