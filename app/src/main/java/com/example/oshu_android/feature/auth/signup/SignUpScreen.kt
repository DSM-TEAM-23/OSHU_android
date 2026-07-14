package com.example.oshu_android.feature.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oshu_android.R

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpSuccess()
            viewModel.onSuccessHandled()
        }
    }

    SignUpScreen(
        uiState = uiState,
        onLoginIdChanged =
            viewModel::onLoginIdChanged,
        onPasswordChanged =
            viewModel::onPasswordChanged,
        onPasswordConfirmChanged =
            viewModel::onPasswordConfirmChanged,
        onCheckLoginId =
            viewModel::checkLoginId,
        onAllTermsChanged =
            viewModel::onAllTermsChanged,
        onServiceTermsChanged =
            viewModel::onServiceTermsChanged,
        onPrivacyTermsChanged =
            viewModel::onPrivacyTermsChanged,
        onMarketingTermsChanged =
            viewModel::onMarketingTermsChanged,
        onSignUpClick =
            viewModel::signUp,
        onBackClick = onBackClick,
    )
}

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onLoginIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmChanged: (String) -> Unit,
    onCheckLoginId: () -> Unit,
    onAllTermsChanged: (Boolean) -> Unit,
    onServiceTermsChanged: (Boolean) -> Unit,
    onPrivacyTermsChanged: (Boolean) -> Unit,
    onMarketingTermsChanged: (Boolean) -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var passwordConfirmVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 24.dp,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(38.dp)
                        .clickable(
                            enabled = !uiState.isLoading,
                            onClick = onBackClick,
                        ),
                    contentAlignment =
                        Alignment.CenterStart,
                ) {
                    Text(
                        text = "‹",
                        fontSize = 35.sp,
                        lineHeight = 35.sp,
                        color = colorScheme.onBackground,
                    )
                }

                Text(
                    text = "OSHU",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.primary,
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "회원가입",
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            InputLabel(
                text = "아이디"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalAlignment =
                    Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SignUpTextField(
                        value = uiState.loginId,
                        onValueChange =
                            onLoginIdChanged,
                        placeholder =
                            "영문, 숫자 조합 6~20자",
                        isError =
                            uiState.loginIdError != null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Ascii,
                                imeAction =
                                    ImeAction.Next,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(
                                        FocusDirection.Down
                                    )
                                }
                            ),
                    )

                    uiState.loginIdError?.let {
                        SignUpErrorText(
                            text = it
                        )
                    }
                }

                Button(
                    onClick = onCheckLoginId,
                    modifier = Modifier
                        .width(96.dp)
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 0.dp,
                    ),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                colorScheme.primary.copy(
                                    alpha = 0.18f
                                ),
                            contentColor =
                                colorScheme.primary,
                            disabledContainerColor =
                                colorScheme.primary.copy(
                                    alpha = 0.10f
                                ),
                            disabledContentColor =
                                colorScheme.primary.copy(
                                    alpha = 0.55f
                                ),
                        ),
                ) {
                    Text(
                        text =
                            if (
                                uiState.isLoginIdChecked
                            ) {
                                "확인완료"
                            } else {
                                "중복확인"
                            },
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.loginIdError == null
                    ) {
                        22.dp
                    } else {
                        7.dp
                    }
                )
            )

            InputLabel(
                text = "비밀번호"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            SignUpTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder =
                    "영문, 숫자, 특수문자 포함 8자 이상",
                isError =
                    uiState.passwordError != null,
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }
                    ),
                trailingIcon = {
                    PasswordVisibilityButton(
                        visible = passwordVisible,
                        onClick = {
                            passwordVisible =
                                !passwordVisible
                        },
                    )
                },
            )

            uiState.passwordError?.let {
                SignUpErrorText(
                    text = it
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.passwordError == null
                    ) {
                        22.dp
                    } else {
                        7.dp
                    }
                )
            )

            InputLabel(
                text = "비밀번호 확인"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            SignUpTextField(
                value = uiState.passwordConfirm,
                onValueChange =
                    onPasswordConfirmChanged,
                placeholder =
                    "비밀번호를 다시 입력하세요",
                isError =
                    uiState.passwordConfirmError != null,
                visualTransformation =
                    if (passwordConfirmVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                trailingIcon = {
                    PasswordVisibilityButton(
                        visible =
                            passwordConfirmVisible,
                        onClick = {
                            passwordConfirmVisible =
                                !passwordConfirmVisible
                        },
                    )
                },
            )

            uiState.passwordConfirmError?.let {
                SignUpErrorText(
                    text = it
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.passwordConfirmError ==
                        null
                    ) {
                        38.dp
                    } else {
                        23.dp
                    }
                )
            )

            AgreementSection(
                uiState = uiState,
                onAllTermsChanged =
                    onAllTermsChanged,
                onServiceTermsChanged =
                    onServiceTermsChanged,
                onPrivacyTermsChanged =
                    onPrivacyTermsChanged,
                onMarketingTermsChanged =
                    onMarketingTermsChanged,
            )

            uiState.generalError?.let {
                SignUpErrorText(
                    text = it,
                    modifier = Modifier.padding(
                        top = 2.dp
                    ),
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.generalError == null
                    ) {
                        44.dp
                    } else {
                        29.dp
                    }
                )
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSignUpClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = uiState.canSubmit,
                shape = RoundedCornerShape(10.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            colorScheme.primary,
                        contentColor =
                            colorScheme.onPrimary,
                        disabledContainerColor =
                            colorScheme.primary.copy(
                                alpha = 0.52f
                            ),
                        disabledContentColor =
                            colorScheme.onPrimary,
                    ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(22.dp),
                        color =
                            colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "회원 가입",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation:
    VisualTransformation =
        VisualTransformation.None,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default,
    keyboardActions: KeyboardActions =
        KeyboardActions.Default,
    trailingIcon:
    (@Composable () -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = colorScheme.onSurface,
        ),
        placeholder = {
            Text(
                text = placeholder,
                maxLines = 1,
                fontSize = 13.sp,
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        isError = isError,
        visualTransformation =
            visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(7.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor =
                    Color.White,
                unfocusedContainerColor =
                    Color.White,
                errorContainerColor =
                    Color.White,
                disabledContainerColor =
                    Color.White,
                focusedBorderColor =
                    colorScheme.primary,
                unfocusedBorderColor =
                    colorScheme.outline.copy(
                        alpha = 0.32f
                    ),
                errorBorderColor =
                    colorScheme.error,
                focusedPlaceholderColor =
                    colorScheme.onSurfaceVariant
                        .copy(alpha = 0.50f),
                unfocusedPlaceholderColor =
                    colorScheme.onSurfaceVariant
                        .copy(alpha = 0.50f),
            ),
    )
}

@Composable
private fun PasswordVisibilityButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
    ) {
        Icon(
            painter = painterResource(
                if (visible) {
                    R.drawable.ic_visibility_off
                } else {
                    R.drawable.ic_visibility
                }
            ),
            contentDescription =
                if (visible) {
                    "비밀번호 숨기기"
                } else {
                    "비밀번호 보기"
                },
            modifier = Modifier.size(21.dp),
            tint = colorScheme.primary,
        )
    }
}

@Composable
private fun InputLabel(
    text: String,
) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color =
            MaterialTheme.colorScheme.onBackground,
    )
}

private enum class AgreementType(
    val title: String,
    val content: String,
) {
    SERVICE(
        title = "서비스 이용약관",
        content = """
            제1조 목적

            본 약관은 OSHU가 제공하는 서비스의 이용 조건과 절차, 회원과 서비스 제공자의 권리·의무 및 책임 사항을 정하는 것을 목적으로 합니다.

            제2조 회원 가입 및 계정 관리

            회원은 정확한 정보를 입력하여 가입해야 하며, 자신의 계정 정보를 안전하게 관리해야 합니다. 다른 사람의 계정을 사용하거나 계정을 양도할 수 없습니다.

            제3조 서비스 이용

            회원은 관련 법령과 본 약관을 준수해야 합니다. 서비스 운영을 방해하거나 다른 이용자에게 피해를 주는 행위, 허위 정보 등록, 비정상적인 접근은 제한될 수 있습니다.

            제4조 서비스 변경 및 중단

            서비스 개선, 점검, 장애 또는 운영상 필요한 경우 서비스의 일부 또는 전부가 변경되거나 일시 중단될 수 있습니다.

            제5조 이용 제한

            약관 위반, 불법 행위, 타인의 권리 침해 또는 서비스 운영을 방해한 경우 사전 안내 후 이용이 제한될 수 있습니다. 긴급한 경우에는 사전 안내 없이 제한한 뒤 안내할 수 있습니다.

            제6조 책임

            서비스는 안정적인 운영을 위해 노력합니다. 다만 천재지변, 통신 장애 등 통제하기 어려운 사유로 발생한 손해에 대해서는 책임이 제한될 수 있습니다.

            시행일: 2026년 7월 14일
        """.trimIndent(),
    ),
    PRIVACY(
        title = "개인정보 수집·이용 동의",
        content = """
            OSHU는 회원 가입과 서비스 제공을 위해 아래와 같이 개인정보를 수집·이용합니다.

            1. 수집 항목

            · 필수 항목: 아이디, 비밀번호
            · 서비스 이용 중 생성되는 정보: 접속 기록, 기기 정보, 서비스 이용 기록

            2. 수집 및 이용 목적

            · 회원 식별 및 계정 관리
            · 회원 가입과 로그인 기능 제공
            · 서비스 운영, 오류 확인 및 보안 관리
            · 문의 및 분쟁 대응

            3. 보유 및 이용 기간

            회원 탈퇴 시까지 보관하며, 관련 법령에 따라 보관이 필요한 정보는 해당 기간 동안 별도로 보관합니다.

            4. 동의 거부 권리

            개인정보 수집·이용에 동의하지 않을 수 있습니다. 다만 필수 항목의 수집·이용에 동의하지 않으면 회원 가입과 서비스 이용이 제한됩니다.

            시행일: 2026년 7월 14일
        """.trimIndent(),
    ),
    MARKETING(
        title = "마케팅 정보 수신 동의",
        content = """
            OSHU는 새로운 기능, 이벤트, 혜택 및 서비스 관련 안내를 제공하기 위해 마케팅 정보를 전송할 수 있습니다.

            1. 이용 목적

            · 이벤트 및 혜택 안내
            · 신규 기능과 서비스 소식 제공
            · 이용자 맞춤형 정보 제공

            2. 전송 방법

            앱 알림, 문자, 이메일 등 회원이 제공하거나 허용한 수단을 통해 전송될 수 있습니다.

            3. 보유 및 이용 기간

            회원이 동의를 철회하거나 회원 탈퇴를 할 때까지 이용합니다.

            4. 동의 거부 및 철회

            본 동의는 선택 사항이며, 동의하지 않아도 회원 가입과 기본 서비스 이용에는 제한이 없습니다. 동의 후에도 설정에서 언제든지 철회할 수 있습니다.

            시행일: 2026년 7월 14일
        """.trimIndent(),
    ),
}

@Composable
private fun AgreementSection(
    uiState: SignUpUiState,
    onAllTermsChanged: (Boolean) -> Unit,
    onServiceTermsChanged: (Boolean) -> Unit,
    onPrivacyTermsChanged: (Boolean) -> Unit,
    onMarketingTermsChanged: (Boolean) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    var selectedAgreement by remember {
        mutableStateOf<AgreementType?>(null)
    }

    selectedAgreement?.let { agreement ->
        AgreementDialog(
            agreement = agreement,
            onDismiss = {
                selectedAgreement = null
            },
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(
                    color = colorScheme.surface.copy(
                        alpha = 0.82f
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
                .clickable {
                    onAllTermsChanged(
                        !uiState.allTermsAgreed
                    )
                }
                .padding(
                    horizontal = 14.dp,
                ),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            SquareCheck(
                checked =
                    uiState.allTermsAgreed,
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "전체 동의",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        AgreementRow(
            checked =
                uiState.serviceTermsAgreed,
            text = "[필수] 서비스 이용약관 동의",
            onAgreementClick = {
                onServiceTermsChanged(
                    !uiState.serviceTermsAgreed
                )
            },
            onViewClick = {
                selectedAgreement =
                    AgreementType.SERVICE
            },
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AgreementRow(
            checked =
                uiState.privacyTermsAgreed,
            text = "[필수] 개인정보 수집·이용 동의",
            onAgreementClick = {
                onPrivacyTermsChanged(
                    !uiState.privacyTermsAgreed
                )
            },
            onViewClick = {
                selectedAgreement =
                    AgreementType.PRIVACY
            },
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AgreementRow(
            checked =
                uiState.marketingTermsAgreed,
            text = "[선택] 마케팅 정보 수신 동의",
            onAgreementClick = {
                onMarketingTermsChanged(
                    !uiState.marketingTermsAgreed
                )
            },
            onViewClick = {
                selectedAgreement =
                    AgreementType.MARKETING
            },
        )
    }
}

@Composable
private fun AgreementRow(
    checked: Boolean,
    text: String,
    onAgreementClick: () -> Unit,
    onViewClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .clickable(
                    onClick = onAgreementClick
                ),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            SquareCheck(
                checked = checked,
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = text,
                maxLines = 1,
                fontSize = 12.sp,
                color = colorScheme.onSurface,
            )
        }

        Text(
            text = "보기  ›",
            modifier = Modifier
                .clickable(onClick = onViewClick)
                .padding(
                    start = 12.dp,
                    top = 7.dp,
                    bottom = 7.dp,
                ),
            maxLines = 1,
            fontSize = 11.sp,
            color =
                colorScheme.onSurfaceVariant.copy(
                    alpha = 0.58f
                ),
        )
    }
}

@Composable
private fun AgreementDialog(
    agreement: AgreementType,
    onDismiss: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = agreement.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = agreement.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .verticalScroll(scrollState),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = "확인",
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = colorScheme.surface,
    )
}

@Composable
private fun SquareCheck(
    checked: Boolean,
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(18.dp)
            .background(
                color =
                    if (checked) {
                        colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                shape = RoundedCornerShape(1.dp),
            )
            .border(
                width = 1.dp,
                color =
                    if (checked) {
                        colorScheme.primary
                    } else {
                        colorScheme.outline.copy(
                            alpha = 0.78f
                        )
                    },
                shape = RoundedCornerShape(1.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(
                text = "✓",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun SignUpErrorText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(
            start = 3.dp,
            top = 2.dp,
        ),
        maxLines = 1,
        color = MaterialTheme.colorScheme.error,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )
}
