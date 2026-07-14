package com.example.oshu_android.feature.inquiry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.ui.theme.OshuPink
import com.example.oshu_android.ui.theme.OshuTextPrimary
import com.example.oshu_android.ui.theme.OshuTextSecondary

@Composable
fun InquiryRoute(
    viewModel: InquiryViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    InquiryScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onTitleChanged = viewModel::onTitleChanged,
        onNameChanged = viewModel::onNameChanged,
        onNumberChanged = viewModel::onNumberChanged,
        onContentChanged = viewModel::onContentChanged,
        onSubmit = viewModel::submit,
        onSuccessConfirm = onBackClick,
    )
}

@Composable
fun InquiryScreen(
    uiState: InquiryUiState,
    onBackClick: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onNumberChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onSuccessConfirm: () -> Unit,
) {
    if (uiState.submitted) {
        AlertDialog(
            onDismissRequest = onSuccessConfirm,
            containerColor = Color(0xFFFFFAFB),
            title = {
                Text(
                    text = "문의가 접수되었습니다",
                    color = OshuTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "매장 확인 후 입력하신 연락처로 답변드릴 예정입니다. 연락을 기다려주세요.",
                    color = OshuTextSecondary,
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = onSuccessConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = OshuPink),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("확인")
                }
            },
        )
    }

    Scaffold(
        containerColor = Color(0xFFFFFAFB),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onBackClick) { Text("←", fontSize = 28.sp, color = OshuTextPrimary) }
                Text("OSHU", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = OshuPink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(" ", modifier = Modifier.padding(horizontal = 18.dp))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(onClick = onBackClick, modifier = Modifier.weight(1f)) { Text("취소하기", color = OshuPink) }
                Button(onClick = onSubmit, enabled = !uiState.isSubmitting, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = OshuPink), shape = RoundedCornerShape(12.dp)) {
                    if (uiState.isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(2.dp)) else Text("문의하기")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("문의사항", color = OshuTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp, bottom = 12.dp))
            InquiryField("문의 제목", uiState.title, "문의제목을 입력하세요", onTitleChanged)
            InquiryField("문의자 성함", uiState.name, "문의자 성함을 입력하세요", onNameChanged)
            InquiryField("문의자 전화번호", uiState.number, "전화번호를 입력하세요", onNumberChanged)
            InquiryField("문의 내용", uiState.content, "문의 내용을 입력하세요", onContentChanged, minLines = 6)
            uiState.errorMessage?.let { Text(it, color = Color(0xFFE04B5F), fontSize = 12.sp) }
            Text("※ 문의 내용은 매장 운영자에게 전달됩니다.", color = OshuTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun InquiryField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit, minLines: Int = 1) {
    Column {
        Text(label, color = OshuTextPrimary, fontSize = 13.sp)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), placeholder = { Text(placeholder, color = OshuTextSecondary.copy(alpha = 0.65f)) }, minLines = minLines, shape = RoundedCornerShape(8.dp), singleLine = minLines == 1)
    }
}
