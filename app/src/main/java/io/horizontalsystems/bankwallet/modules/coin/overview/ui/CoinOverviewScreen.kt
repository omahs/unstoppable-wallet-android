package io.horizontalsystems.bankwallet.modules.coin.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.iconUrl
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.CoinLink
import io.horizontalsystems.bankwallet.modules.coin.ContractInfo
import io.horizontalsystems.bankwallet.modules.coin.adapters.CoinChartAdapter
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.*
import io.horizontalsystems.bankwallet.modules.coin.ui.CoinScreenTitle
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.CellFooter
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView
import io.horizontalsystems.chartview.ChartView

@Composable
fun CoinOverviewScreen(
    viewModel: CoinOverviewViewModel,
    onClickCopyContract: (ContractInfo) -> Unit,
    onClickExplorerContract: (ContractInfo) -> Unit,
    onCoinLinkClick: (CoinLink) -> Unit,
) {
    val refreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val overview by viewModel.overviewLiveData.observeAsState()
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val chartInfo by viewModel.chartInfoLiveData.observeAsState()
    val title by viewModel.titleLiveData.observeAsState()
    val fullCoin = viewModel.fullCoin
    val currency = viewModel.currency

    HSSwipeRefresh(
        state = rememberSwipeRefreshState(refreshing),
        onRefresh = { },
        content = {
            when (viewState) {
                ViewState.Success -> {
                    overview?.let { overview ->
                        var scrollingEnabled by remember { mutableStateOf(true) }

                        Column(modifier = Modifier.verticalScroll(rememberScrollState(), enabled = scrollingEnabled)) {
                            CoinScreenTitle(
                                fullCoin.coin.name,
                                fullCoin.coin.marketCapRank,
                                fullCoin.coin.iconUrl
                            )

                            Title(title, chartInfo?.data?.chartData?.diff())

                            chartInfo?.let {
                                ChartInfo(it, currency, CoinChartAdapter.ChartViewType.CoinChart, object : CoinChartAdapter.Listener {
                                    override fun onChartTouchDown() {
                                        scrollingEnabled = false
                                    }

                                    override fun onChartTouchUp() {
                                        scrollingEnabled = true
                                    }

                                    override fun onTabSelect(chartType: ChartView.ChartType) {
                                        viewModel.changeChartType(chartType)
                                    }
                                })
                            }

                            if (overview.marketData.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                MarketData(overview.marketData)
                            }

                            if (overview.roi.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Roi(overview.roi)
                            }

                            if (overview.categories.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Categories(overview.categories)
                            }

                            if (overview.contracts.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Contracts(
                                    contracts = overview.contracts,
                                    onClickCopy = onClickCopyContract,
                                    onClickExplorer = onClickExplorerContract,
                                )
                            }

                            if (overview.about.isNotBlank()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                About(overview.about)
                            }

                            if (overview.links.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Links(overview.links, onCoinLinkClick)
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            CellFooter(text = stringResource(id = R.string.Market_PoweredByApi))
                        }
                    }

                }
                ViewState.Error -> {
                    ListErrorView(stringResource(id = R.string.BalanceSyncError_Title))
                }
            }
        },
    )
}

@Preview
@Composable
fun LoadingPreview() {
    ComposeAppTheme {
        Loading()
    }
}

@Composable
fun Error(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = ComposeAppTheme.typography.subhead2,
            color = ComposeAppTheme.colors.grey,
        )
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = ComposeAppTheme.colors.grey,
            strokeWidth = 2.dp
        )
    }
}
