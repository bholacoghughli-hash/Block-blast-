package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Block
import com.example.model.BlockShape
import com.example.ui.components.renderBlockCentered

@Composable
fun HowToPlayScreen(
    onGotItClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .testTag("how_to_play_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Header Bar with Back Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onGotItClick,
                        modifier = Modifier.testTag("how_to_play_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOW TO PLAY",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Step 1: Pick a block
                TutorialStepCard(
                    stepNumber = "1",
                    title = "Pick a Block",
                    description = "Choose any of the 3 colorful geometric shapes available in the bottom tray.",
                    demoContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MiniBlockBox(block = Block(shape = BlockShape.H_LINE_3, colorIndex = 1))
                            MiniBlockBox(block = Block(shape = BlockShape.SQUARE_2X2, colorIndex = 2))
                            MiniBlockBox(block = Block(shape = BlockShape.L_NORMAL, colorIndex = 3))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 2: Drag onto the board
                TutorialStepCard(
                    stepNumber = "2",
                    title = "Drag Onto The Board",
                    description = "Press and drag the shape onto the 8×8 grid. The block is lifted above your finger for a clear view!",
                    demoContent = {
                        MiniGridDemo(showGhost = true)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 3: Complete a row or column
                TutorialStepCard(
                    stepNumber = "3",
                    title = "Complete Rows & Columns",
                    description = "Fill all 8 squares of any horizontal row or vertical column to trigger an explosive clear.",
                    demoContent = {
                        MiniLineClearDemo()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 4: Clear lines to earn points
                TutorialStepCard(
                    stepNumber = "4",
                    title = "Clear Lines & Build Combos",
                    description = "Clear multiple lines at once for huge bonus points. Clear lines on consecutive turns to stack massive COMBO multipliers!",
                    demoContent = {
                        Surface(
                            color = Color(0xFFFF2A6D).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF2A6D))
                        ) {
                            Text(
                                text = "COMBO x3! +150 BONUS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFFFF2A6D),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 5: Keep playing
                TutorialStepCard(
                    stepNumber = "5",
                    title = "Survive & Beat Your Best",
                    description = "The game continues until no available block can fit onto the board. Plan ahead and beat your record!",
                    demoContent = null
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom "GOT IT" Button
            Button(
                onClick = onGotItClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp)
                    .testTag("how_to_play_got_it_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GOT IT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TutorialStepCard(
    stepNumber: String,
    title: String,
    description: String,
    demoContent: (@Composable () -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                RoundedCornerShape(18.dp)
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            if (demoContent != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    demoContent()
                }
            }
        }
    }
}

@Composable
private fun MiniBlockBox(block: Block) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(10.dp)),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            renderBlockCentered(block, size)
        }
    }
}

@Composable
private fun MiniGridDemo(showGhost: Boolean) {
    Canvas(
        modifier = Modifier
            .size(120.dp, 80.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF141933))
            .padding(6.dp)
    ) {
        val cellW = size.width / 5f
        val cellH = size.height / 3f
        for (r in 0 until 3) {
            for (c in 0 until 5) {
                val left = c * cellW + 1.dp.toPx()
                val top = r * cellH + 1.dp.toPx()
                val w = cellW - 2.dp.toPx()
                val h = cellH - 2.dp.toPx()

                drawRoundRect(
                    color = Color(0xFF222B52),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        // Ghost block preview at row 1, cols 1..3
        if (showGhost) {
            for (c in 1..3) {
                val left = c * cellW + 1.dp.toPx()
                val top = 1 * cellH + 1.dp.toPx()
                val w = cellW - 2.dp.toPx()
                val h = cellH - 2.dp.toPx()

                drawRoundRect(
                    color = Color(0xFF00E5FF).copy(alpha = 0.7f),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun MiniLineClearDemo() {
    Canvas(
        modifier = Modifier
            .size(160.dp, 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF141933))
            .padding(4.dp)
    ) {
        val cellW = size.width / 6f
        val cellH = size.height
        for (c in 0 until 6) {
            val left = c * cellW + 1.5.dp.toPx()
            val top = 1.5.dp.toPx()
            val w = cellW - 3.dp.toPx()
            val h = cellH - 3.dp.toPx()

            // Glowing cleared line
            drawRoundRect(
                color = Color(0xFFFFD600),
                topLeft = Offset(left, top),
                size = Size(w, h),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}
