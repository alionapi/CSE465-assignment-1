package com.example.pa1.motion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs
import kotlin.math.sqrt
val ColorX = Color(0xFFE53935)
val ColorY = Color(0xFF43A047)
val ColorZ = Color(0xFF1E88E5)

class RollingBuffer(private val capacity: Int) {

    private var _state by mutableStateOf(Triple(FloatArray(0), FloatArray(0), FloatArray(0)))
    private val bufX = FloatArray(capacity)
    private val bufY = FloatArray(capacity)
    private val bufZ = FloatArray(capacity)
    private var head  = 0
    private var count = 0

    fun push(x: Float, y: Float, z: Float) {
        bufX[head] = x
        bufY[head] = y
        bufZ[head] = z
        head  = (head + 1) % capacity
        if (count < capacity) count++
        val n     = count
        val start = if (count < capacity) 0 else head
        val sx    = FloatArray(n)
        val sy    = FloatArray(n)
        val sz    = FloatArray(n)
        for (i in 0 until n) {
            val idx = (start + i) % capacity
            sx[i] = bufX[idx]
            sy[i] = bufY[idx]
            sz[i] = bufZ[idx]
        }
        _state = Triple(sx, sy, sz)
    }
    val state: Triple<FloatArray, FloatArray, FloatArray>
        @Composable get() = _state
}

@Composable
fun CollectScreen(vm: MotionViewModel = viewModel()) {
    val liveData    by vm.liveData.collectAsState()
    val sampleCount by vm.sampleCount.collectAsState()
    var isRecording   by remember { mutableStateOf(false) }
    var statusMsg     by remember { mutableStateOf("Data collection is stopped now") }
    var selectedLabel by remember { mutableStateOf("still") }
    val accelBuffer = remember { RollingBuffer(50) }
    val gyroBuffer  = remember { RollingBuffer(50) }
    accelBuffer.push(liveData.ax, liveData.ay, liveData.az)
    gyroBuffer.push(liveData.gx, liveData.gy, liveData.gz)
    val labels = listOf("still", "walking", "running", "stairs_up", "stairs_down", "turning")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                "Data Collection",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A)
            )
        }

        Spacer(Modifier.height(16.dp))

        SensorGraphCard(
            title    = "Accelerometer",
            buffer   = accelBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        SensorGraphCard(
            title    = "Gyroscope",
            buffer   = gyroBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "Activity Label:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = Color(0xFF1A1A1A),
                    modifier   = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    labels.take(3).forEach { label ->
                        ActivityChip(
                            label    = label.replace("_", " ")
                                .replaceFirstChar { it.uppercase() },
                            selected = selectedLabel == label,
                            enabled  = !isRecording,
                            onClick  = { selectedLabel = label },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    labels.drop(3).forEach { label ->
                        ActivityChip(
                            label    = label.replace("_", " ")
                                .replaceFirstChar { it.uppercase() },
                            selected = selectedLabel == label,
                            enabled  = !isRecording,
                            onClick  = { selectedLabel = label },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            if (isRecording) "● $statusMsg" else statusMsg,
            fontSize = 14.sp,
            color    = if (isRecording) Color(0xFF34C759) else Color(0xFF8E8E93),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = {
                    vm.repo.startListening()
                    vm.repo.startRecording(selectedLabel)
                    isRecording = true
                    statusMsg   = "Recording: $selectedLabel"
                },
                enabled  = !isRecording,
                shape    = RoundedCornerShape(50),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color(0xFF007AFF),
                    disabledContainerColor = Color(0xFFD1D1D6)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Start", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Button(
                onClick = {
                    statusMsg   = vm.repo.stopRecording()
                    vm.repo.stopListening()
                    isRecording = false
                },
                enabled  = isRecording,
                shape    = RoundedCornerShape(50),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color(0xFFFF3B30),
                    disabledContainerColor = Color(0xFFD1D1D6)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Stop", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }


        if (isRecording) {
            Spacer(Modifier.height(10.dp))
            Text(
                "Last 1s  •  Samples collected: $sampleCount",
                fontSize = 13.sp,
                color    = Color(0xFF8E8E93),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun DetectScreen(vm: MotionViewModel = viewModel()) {
    val liveData by vm.liveData.collectAsState()
    val activity by vm.activity.collectAsState()
    val features by vm.features.collectAsState()

    var isRunning by remember { mutableStateOf(false) }

    val accelBuffer = remember { RollingBuffer(50) }
    val gyroBuffer  = remember { RollingBuffer(50) }

    if (isRunning) {
        accelBuffer.push(liveData.ax, liveData.ay, liveData.az)
        gyroBuffer.push(liveData.gx, liveData.gy, liveData.gz)
    }

    val activityColor by animateColorAsState(
        targetValue = when (activity) {
            "STILL"       -> Color(0xFF8E8E93)
            "WALKING"     -> Color(0xFF007AFF)
            "RUNNING"     -> Color(0xFFFF3B30)
            "STAIRS UP"   -> Color(0xFF34C759)
            "STAIRS DOWN" -> Color(0xFF5856D6)
            "TURNING"     -> Color(0xFFFF9500)
            else          -> Color(0xFF1A1A1A)
        },
        label = "activityColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                "Recognition",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A)
            )
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(
                containerColor = activityColor.copy(alpha = 0.10f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    activity,
                    fontSize   = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color      = activityColor
                )
                Text(
                    "Current Activity",
                    fontSize = 17.sp,
                    color    = Color(0xFF8E8E93)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        SensorGraphCard(
            title    = "Accelerometer",
            buffer   = accelBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        SensorGraphCard(
            title    = "Gyroscope",
            buffer   = gyroBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(14.dp))

        features?.let { f ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeaturePill("std ${"%.2f".format(f.accelStd)}")
                FeaturePill("max ${"%.1f".format(f.accelMax)}")
                FeaturePill("gyro ${"%.2f".format(f.gyroMean)}")
                FeaturePill("step ${"%.1f".format(f.stepRate)}")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning) Color(0xFF34C759) else Color(0xFFD1D1D6)
                        )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isRunning) activity else "Stopped",
                    fontSize = 14.sp,
                    color    = Color(0xFF8E8E93)
                )
            }

            Button(
                onClick = {
                    if (!isRunning) {
                        vm.repo.startListening()
                        isRunning = true
                    } else {
                        vm.repo.stopListening()
                        isRunning = false
                    }
                },
                shape  = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFFF3B30) else Color(0xFF007AFF)
                ),
                modifier = Modifier
                    .height(52.dp)
                    .width(120.dp)
            ) {
                Text(
                    if (isRunning) "Stop" else "Start",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun SensorGraphCard(
    title: String,
    buffer: RollingBuffer,
    modifier: Modifier = Modifier
) {
    // Reading buffer.state here — Compose tracks it and redraws Canvas automatically
    val (sx, sy, sz) = buffer.state

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Title row + legend
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 17.sp,
                    color      = Color(0xFF1A1A1A)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LegendDot("X", ColorX)
                    LegendDot("Y", ColorY)
                    LegendDot("Z", ColorZ)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Graph + magnitude label side by side
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                ) {
                    drawGraphBackground()
                    if (sx.size > 1) {
                        drawSignalLine(sx, ColorX, size.width, size.height)
                        drawSignalLine(sy, ColorY, size.width, size.height)
                        drawSignalLine(sz, ColorZ, size.width, size.height)
                    }
                }

                // Magnitude column on right, matching the example screenshot
                Column(
                    modifier            = Modifier
                        .width(52.dp)
                        .padding(start = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Magnitude", fontSize = 8.sp, color = Color(0xFFAAAAAA))
                    val mag = if (sx.isNotEmpty()) {
                        val i = sx.size - 1
                        sqrt(sx[i] * sx[i] + sy[i] * sy[i] + sz[i] * sz[i])
                    } else 0f
                    Text(
                        "%.2f".format(mag),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text("Last 1s", fontSize = 11.sp, color = Color(0xFFAAAAAA))
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(3.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF555555))
    }
}

@Composable
fun ActivityChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (selected) Color(0xFF007AFF) else Color(0xFFF2F2F7)
    val textColor   = if (selected) Color.White else Color(0xFF3C3C43)
    val borderColor = if (selected) Color.Transparent else Color(0xFFD1D1D6)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize   = 13.sp,
            color      = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FeaturePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE5E5EA))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            fontSize   = 11.sp,
            color      = Color(0xFF3C3C43),
            fontFamily = FontFamily.Monospace
        )
    }
}


private fun DrawScope.drawGraphBackground() {
    val grid = Color(0xFFF0F0F0)
    val mid  = size.height / 2f
    drawLine(grid, Offset(0f, mid),                   Offset(size.width, mid),                   strokeWidth = 1.2f)
    drawLine(grid, Offset(0f, size.height * 0.25f),   Offset(size.width, size.height * 0.25f),   strokeWidth = 0.6f)
    drawLine(grid, Offset(0f, size.height * 0.75f),   Offset(size.width, size.height * 0.75f),   strokeWidth = 0.6f)
}

private fun DrawScope.drawSignalLine(
    values: FloatArray,
    color: Color,
    canvasWidth: Float,
    canvasHeight: Float
) {
    if (values.size < 2) return
    val maxAbs = values.maxOf { abs(it) }.coerceAtLeast(0.5f) * 1.3f
    val path   = Path()
    values.forEachIndexed { i, v ->
        val x = (i.toFloat() / (values.size - 1)) * canvasWidth
        val y = canvasHeight / 2f - (v / maxAbs) * (canvasHeight / 2f)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(path, color, style = Stroke(width = 2.0f))
}