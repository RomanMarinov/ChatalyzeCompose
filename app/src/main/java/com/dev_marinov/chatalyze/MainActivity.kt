package com.dev_marinov.chatalyze

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintEmail
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintLogin
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.ui.theme.ChatalyzeTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatalyzeTheme {

                HideStatusBar()

                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("auth_screen") {
            AuthScreen(navController = navController)
        }
        composable("sign_up_screen") {
            SignUpScreen(navController = navController)
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    // hideStatusBarOnlyCurrentScreen()
    startAnimationLogo(navController = navController)
}

@Composable
fun hideStatusBarOnlyCurrentScreen() {
    val systemUiController: SystemUiController = rememberSystemUiController()
    DisposableEffect(key1 = true) {
        systemUiController.isStatusBarVisible = false // Status bar
        onDispose {
            systemUiController.isStatusBarVisible = true // Status bar
        }
    }
}

@Composable
fun HideStatusBar() {
    val systemUiController: SystemUiController = rememberSystemUiController()
    systemUiController.isStatusBarVisible = false // Status bar
}

@Composable
fun startAnimationLogo(navController: NavController) {
    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { // степень смягчения
                    OvershootInterpolator(5f).getInterpolation(it)
                }
            )
        )
        delay(1000L)
        navController.navigate("auth_screen")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.main_yellow_splash_screen)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_screen),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(navController: NavHostController) {
    // SetStatusBarColor()
    GradientBackground()
//    SetGradientAnimation()

    var textLoginState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
        // .background(colorResource(id = R.color.main_violet))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintLogin(
                value = textLoginState,
                onValueChanged = { textLoginState = it },
                hintText = "login",
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                icon = Icons.Rounded.Person
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintPassword(
                value = textPasswordState,
                onValueChanged = { textPasswordState = it },
                hintText = "password",
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                icon = Icons.Rounded.Lock
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                modifier = Modifier
                    .width(300.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet)),
                onClick = {

                Log.d("4444", " textPasswordState=" + textPasswordState)

                },
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.auth_bt_sign_in),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            OutlinedButton(
                modifier = Modifier
                    .width(300.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                onClick = {
                    navController.navigate("sign_up_screen")
                }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.auth_bt_sign_up),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun SetStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Black,
            darkIcons = true
        )
    }
}

@Composable
fun GradientBackground() {
    val colorAnimation = rememberInfiniteTransition()
    val color1 by colorAnimation.animateColor(
        initialValue = colorResource(id = R.color.main_violet),
        targetValue = colorResource(id = R.color.main_violet),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by colorAnimation.animateColor(
        initialValue = colorResource(id = R.color.main_violet),
        targetValue = colorResource(id = R.color.main_yellow),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color3 by colorAnimation.animateColor(
        initialValue = colorResource(id = R.color.main_violet),
        targetValue = colorResource(id = R.color.main_yellow),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = mutableStateListOf(color1, color2, color3),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        // Добавьте свое содержимое, как текст, изображения и т. д.
    }
}

@Composable
fun SignUpScreen(navController: NavHostController) {

    GradientBackground()

    Image(
        painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
        contentDescription = "back",
        modifier = Modifier
            .padding(start = 8.dp, top = 16.dp)
            .size(30.dp)
            .clickable {
            navController.popBackStack("auth_screen", false)
        }
    )

    var textLoginState by remember { mutableStateOf("") }
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
        // .background(colorResource(id = R.color.main_violet))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintLogin(
                value = textLoginState,
                onValueChanged = { textLoginState = it },
                hintText = "login",
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                icon = Icons.Rounded.Person
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintEmail(
                value = textEmailState,
                onValueChanged = { textEmailState = it },
                hintText = "email",
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                icon = Icons.Rounded.Mail
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintPassword(
                value = textPasswordState,
                onValueChanged = { textPasswordState = it },
                hintText = "password",
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp),
                icon = Icons.Rounded.Lock
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            OutlinedButton(
                modifier = Modifier
                    .width(300.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(100.dp)
                    ),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                onClick = {

                }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.auth_bt_sign_up),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}



