package com.example.oshu_android.feature.inquiry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.ui.theme.OshuPink
import com.example.oshu_android.ui.theme.OshuTextPrimary
import com.example.oshu_android.ui.theme.OshuTextSecondary
import com.example.oshu_android.ui.theme.OshuWhite

private val InquiryBackground = Color(0xFFFFFCFD)
private val InquiryHeader = Color(0xFFFFF0F1)
private val InquiryBorder = Color(0xFFF0DCE1)
private val InquiryBottomBar = Color(0xFFF6F6F6)

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
        InquiryCompleteDialog(onConfirm = onSuccessConfirm)
    }

    Scaffold(
        containerColor = InquiryBackground,
        topBar = {
            InquiryHeader(onBackClick = onBackClick)
        },
        bottomBar = {
            InquiryBottomActions(
                isSubmitting = uiState.isSubmitting,
                onCancelClick = onBackClick,
                onSubmitClick = onSubmit,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "문의사항",
                color = OshuTextPrimary,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 4.dp),
            )

            Text(
                text = "매장에 전달될 문의 내용을 작성해주세요.",
                color = OshuTextSecondary.copy(alpha = 0.68f),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            InquiryField(
                label = "문의제목",
                value = uiState.title,
                placeholder = "문의제목을 입력하세요",
                onValueChange = onTitleChanged,
            )

            InquiryField(
                label = "문의자 성함",
                value = uiState.name,
                placeholder = "문의자 성함을 입력하세요",
                onValueChange = onNameChanged,
            )

            InquiryField(
                label = "문의자 전화번호",
                value = uiState.number,
                placeholder = "전화번호를 입력하세요",
                onValueChange = onNumberChanged,
            )

            InquiryField(
                label = "문의 내용",
                value = uiState.content,
                placeholder = "문의 내용을 입력하세요",
                onValueChange = onContentChanged,
                minLines = 6,
            )

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFE25368),
                    fontSize = 12.sp,
                )
            }

            Text(
                text = "※ 정보통신망 이용촉진 및 정보보호 등에 관한 법률에 의거하여, 타인의 명예를 훼손하는 비방·욕설이나 공포심·불안감을 유발하는 악성 메시지를 반복적으로 접수하는 경우 상담이 제한될 수 있으며 법적 처벌의 대상이 될 수 있습니다. ※",
                color = OshuTextSecondary.copy(alpha = 0.58f),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun InquiryHeader(onBackClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InquiryHeader)
            .statusBarsPadding()
            .height(58.dp)
            .padding(horizontal = 22.dp),
    ) {
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Text(
                text = "←",
                color = OshuTextPrimary,
                fontSize = 28.sp,
            )
        }

        Text(
            text = "OSHU",
            modifier = Modifier.align(Alignment.Center),
            color = OshuPink,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun InquiryField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = OshuTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (minLines == 1) 54.dp else 180.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = OshuTextSecondary.copy(alpha = 0.55f),
                    fontSize = 15.sp,
                )
            },
            minLines = minLines,
            singleLine = minLines == 1,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OshuPink,
                unfocusedBorderColor = InquiryBorder,
                cursorColor = OshuPink,
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
            ),
        )
    }
}

@Composable
private fun InquiryBottomActions(
    isSubmitting: Boolean,
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InquiryBottomBar)
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onCancelClick,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OshuWhite,
                contentColor = OshuPink,
            ),
            border = BorderStroke(1.dp, OshuPink),
        ) {
            Text(
                text = "취소하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(
            onClick = onSubmitClick,
            enabled = !isSubmitting,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OshuPink,
                contentColor = OshuWhite,
            ),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = OshuWhite,
                    strokeWidth = 2.dp,
                    modifier = Modifier.height(20.dp),
                )
            } else {
                Text(
                    text = "문의하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun InquiryCompleteDialog(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onConfirm,
        containerColor = InquiryBackground,
        shape = RoundedCornerShape(20.dp),
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
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OshuPink),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("확인")
            }
        },
    )
}
