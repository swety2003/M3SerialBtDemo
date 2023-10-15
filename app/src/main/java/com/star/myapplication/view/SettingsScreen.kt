package com.star.myapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.star.myapplication.R

@Composable
fun SettingView(navController: NavHostController) {

    AboutCard()

}
@Composable
fun AboutCard() {

    Column {

        // Add padding around our message
        Card (modifier = Modifier
            .padding(16.dp)
            .padding(top = 32.dp)
            .clickable { }){

            Column(modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.head),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier
                        // Set image size to 40 dp
                        .size(64.dp)
                        // Clip image to be shaped as a circle
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                )


                // Add a horizontal space between the image and the column
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "StarWink", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "\"May you, the beauty of this world, always shine.\"")

            }
        }

        OutlinedCard (modifier = Modifier.padding(horizontal = 16.dp) ){


            Column(modifier = Modifier.padding(vertical = 8.dp)) {


                item1("不知道写什么了","但是感觉空着很不好看")
                item1("于是就放了2个列表","这样看起来就不那么空了")

            }



        }
    }
}
@Composable
fun item1(head:String="22",body:String="11"){
    // Add padding around our message
    Row(modifier = Modifier
        .padding(all = 0.dp)
        .fillMaxWidth()
        .clickable { }, verticalAlignment = Alignment.CenterVertically) {
        Image(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Contact profile picture",
            modifier = Modifier
                // Set image size to 40 dp
                .size(40.dp)
                .padding(start = 14.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier
            .padding(vertical = 12.dp)) {
            Text(text = head,color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium)
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = body,color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
        }

    }
}