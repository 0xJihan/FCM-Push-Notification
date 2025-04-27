package com.jihan.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jihan.composeutils.CenterBox
import com.jihan.composeutils.text
import kotlinx.serialization.Serializable

@Composable
fun NavigationController() {
    val navController = rememberNavController()

    val navigateTo = remember { {route: Routes?->

        when(route){
            Routes.LoginRoute -> {
                navController.navigate(route){
                    popUpTo(Routes.MainRoute) {
                        inclusive = true
                    }
                }
            }
            Routes.MainRoute -> navController.navigate(route){
                popUpTo(Routes.LoginRoute) {
                    inclusive = true
                }
            }
            Routes.SignupRoute -> navController.navigate(route)
            null -> navController.navigateUp()
        }

    } }

    Surface {

        NavHost(navController, Routes.MainRoute) {


            composable<Routes.MainRoute> {
                MainScreen{navigateTo(it)}
            }

            composable<Routes.LoginRoute> {
                CenterBox {
                    "Login Screen".text.size(25).make()
                }
            }


        }


    }
}


sealed interface Routes {

    @Serializable
    data object MainRoute : Routes

    @Serializable
    data object LoginRoute : Routes

    @Serializable
    data object SignupRoute : Routes

}