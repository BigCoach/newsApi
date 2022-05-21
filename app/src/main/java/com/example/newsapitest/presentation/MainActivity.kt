package com.example.newsapitest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.example.newsapitest.presentation.ui.ArticlesList
import com.example.newsapitest.presentation.ui.FavouritesList
import com.example.newsapitest.presentation.ui.state.ArticlesScreenState
import com.example.newsapitest.presentation.ui.theme.NewsApiTestTheme
import com.google.accompanist.flowlayout.FlowRow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsApiTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(mainViewModel)
                }
            }
        }
    }


    private fun navigateToRoute(navController: NavController, route: String) {
        navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Greeting(mainViewModel: MainViewModel) {
        val screenState = remember {
            mainViewModel.articlesScreenState
        }
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val bottomSheetPeekHeight =
            if (currentDestination?.hierarchy?.any { it.route == ROUTE_FAVOURITES } == true) 0.dp else 55.dp

        when (screenState.value) {
            is ArticlesScreenState.Content -> {
                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberBottomSheetState(
                        initialValue = BottomSheetValue.Collapsed
                    )
                )
                val coroutineScope = rememberCoroutineScope()

                BottomSheetScaffold(
                    scaffoldState = bottomSheetScaffoldState,
                    sheetContent = {
                        BottomAppBar(
                            cutoutShape = MaterialTheme.shapes.small.copy(
                                CornerSize(percent = 50)
                            )
                        ) {
                            Spacer(Modifier.weight(1f, true))
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                            }) {
                                Icon(
                                    Icons.Filled.FilterAlt,
                                    contentDescription = "Localized description"
                                )
                            }
                            IconButton(onClick = {
                                navigateToRoute(
                                    navController = navController,
                                    route = ROUTE_FAVOURITES
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "Localized description"
                                )
                            }
                        }

                        Column(){
                            Text(text = "Sort by:",
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                style = TextStyle(fontSize = 20.sp))
                            Row(Modifier.padding(vertical = 4.dp, horizontal = 8.dp)){
                                Text(text = "Date",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
//                            pair.second = !pair.second
                                        }
                                        .background(
                                            Color.Gray.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    style = TextStyle(fontSize = 12.sp)
                                )
                                Text(text = "Popularity",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {

                                        }
                                        .background(
                                            Color.Gray.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            }
                            Text(text = "Selected timeframe:",
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                style = TextStyle(fontSize = 20.sp))
                            TimeFrames(Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
                            Text(text = "Select sources:",
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                style = TextStyle(fontSize = 20.sp))
                            Sources(Modifier.padding(vertical = 4.dp, horizontal = 8.dp).fillMaxSize())
                        }

                    },
                    // Defaults to BottomSheetScaffoldDefaults.SheetPeekHeight
                    sheetPeekHeight = bottomSheetPeekHeight,
                    // Defaults to true
                    sheetGesturesEnabled = true,
                ) { paddingValues ->

                    NavHost(
                        navController,
                        startDestination = ROUTE_MAIN,
                    ) {
                        composable(ROUTE_MAIN) { Main(paddingValues) }
                        composable(ROUTE_FAVOURITES) { Favorites(navController, mainViewModel) }
                    }
                }
            }
            is ArticlesScreenState.Loading -> {

            }
            is ArticlesScreenState.Error -> {

            }
        }

    }

    @Composable
    fun TimeFrames(modifier: Modifier){
        val timeFrameList: List<Pair<String, Boolean>> = arrayListOf(Pair("Last 30 days", true), Pair("Last 14 days", false), Pair("Last 7 days", false), Pair("Today", false))
        FlowRow(modifier){
            timeFrameList.forEachIndexed { index, pair ->
                val bgColor =
                    if (pair.second) Color.Blue.copy(alpha = 0.5f) else Color.Gray.copy(
                        alpha = 0.5f
                    )
                Text(text = pair.first,
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
//                            pair.second = !pair.second
                        }
                        .background(
                            bgColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    }

    @Composable
    fun Sources(modifier: Modifier){
        val sourcesLiveDataState = mainViewModel.sourcesLiveData.observeAsState()
        val sourcesList = sourcesLiveDataState.value ?: arrayListOf()
        Timber.d("sourcesLiveDataState: ${sourcesLiveDataState.value}")
        val listState = rememberLazyListState()
        LazyColumn(modifier) {
            item {
                FlowRow() {
                    sourcesList.forEachIndexed { index, item ->
                        val bgColor =
                            if (item.isSelected) Color.Blue.copy(alpha = 0.5f) else Color.Gray.copy(
                                alpha = 0.5f
                            )
                        Text(text = item.name ?: "No name",
                            color = Color.White,
                            modifier = Modifier
                                    .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    mainViewModel.updateSource(item.id, !item.isSelected)
                                }
                                .background(
                                    bgColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    @Composable
    fun Main(paddingValues: PaddingValues) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
        ) {
            val (input, list, userInfoBlock) = createRefs()
            ArticlesList(
                Modifier
                    .constrainAs(list) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .background(Color.White)
                    .padding(top = 0.dp),
                mainViewModel
            )
        }
    }


    @Composable
    fun Favorites(navController: NavController, viewModel: MainViewModel) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navigateToRoute(
                        navController = navController,
                        route = ROUTE_MAIN
                    )
                }) {
                    Icon(
                        Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Localized description"
                    )
                }
                Text(
                    text = "Favourite News",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    style = TextStyle(Color.Blue, fontSize = 22.sp, fontWeight = FontWeight(600))
                )
            }
            FavouritesList(
                modifier = Modifier.background(Color.White),
                viewModel = viewModel
            )
        }
    }

    companion object {
        const val ROUTE_MAIN = "main"
        const val ROUTE_FAVOURITES = "fav"
    }

}
