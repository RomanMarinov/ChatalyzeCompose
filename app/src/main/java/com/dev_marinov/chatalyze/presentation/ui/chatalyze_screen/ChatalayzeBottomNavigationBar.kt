package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.dev_marinov.chatalyze.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.model.ChatalyzeBottomNavItem

@Composable
fun ChatalyzeBottomNavigationBar(
    items: List<ChatalyzeBottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (ChatalyzeBottomNavItem) -> Unit,
) {

    val backStackEntry = navController.currentBackStackEntryAsState()
   // val isHideBottomBar by viewModel.isHideBottomBar.collectAsStateWithLifecycle(initialValue = false)

    //Log.d("4444", " ChatalyzeBottomNavigationBar isHideBottomBar=" + isHideBottomBar)

    BottomNavigation(
        backgroundColor = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(width = 1.dp,
                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
          //  .background(colorResource(id = R.color.main_violet_light)),
       // .height(height = if (isHideBottomBar == true) 0.dp else 70.dp),
        elevation = 60.dp
    ) {

        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route

            backStackEntry.value?.destination?.route?.let {
              //  viewModel.saveCurrentRoute(route = it)
            }

            BottomNavigationItem(
                modifier = Modifier
                    .background(colorResource(id = R.color.main_violet_light)),
//                modifier = Modifier.background(
//                    brush = Brush.verticalGradient(
//                      //  colors = gradientColors,
//                        startY = 0f,
//                        endY = Float.POSITIVE_INFINITY // наибольшее возможное значение
//                    )
//                ),
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = colorResource(id = R.color.main_yellow_new_chat_screen),
                unselectedContentColor = Color.White,
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (item.badgeCount > 0) {
                            BadgedBox(
                                modifier = Modifier.size(30.dp),
                                badge = {
                                    Text(text = item.badgeCount.toString())
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        }
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}