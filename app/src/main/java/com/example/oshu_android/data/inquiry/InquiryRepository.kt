package com.example.oshu_android.data.inquiry

import com.example.oshu_android.data.network.ApiResult
import com.example.oshu_android.data.network.executeApiCall
import com.example.oshu_android.data.network.executeUnitApiCall

class InquiryRepository(
    private val inquiryApi: InquiryApi,
) {

    suspend fun createInquiry(
        storeId: Long,
        request: InquiryRequest,
    ): ApiResult<Unit> =
        executeUnitApiCall("문의 등록에 실패했습니다.") {
            inquiryApi.createInquiry(storeId, request)
        }

    suspend fun getStoreInquiries(
        storeId: Long,
    ): ApiResult<List<InquiryResponse>> =
        executeApiCall("문의 목록을 불러오지 못했습니다.") {
            inquiryApi.getStoreInquiries(storeId)
        }

    suspend fun getInquiry(
        inquiryId: Long,
    ): ApiResult<InquiryResponse> =
        executeApiCall("문의 내용을 불러오지 못했습니다.") {
            inquiryApi.getInquiry(inquiryId)
        }

    suspend fun updateInquiry(
        inquiryId: Long,
        request: InquiryRequest,
    ): ApiResult<Unit> =
        executeUnitApiCall("문의 수정에 실패했습니다.") {
            inquiryApi.updateInquiry(inquiryId, request)
        }

    suspend fun deleteInquiry(
        inquiryId: Long,
    ): ApiResult<Unit> =
        executeUnitApiCall("문의 삭제에 실패했습니다.") {
            inquiryApi.deleteInquiry(inquiryId)
        }
}
