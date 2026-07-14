package com.example.oshu_android.data.inquiry

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface InquiryApi {

    @POST("inquiry/store/{storeId}")
    suspend fun createInquiry(
        @Path("storeId") storeId: Long,
        @Body request: InquiryRequest,
    ): Response<Unit>

    @GET("inquiry/store/{storeId}")
    suspend fun getStoreInquiries(
        @Path("storeId") storeId: Long,
    ): Response<List<InquiryResponse>>

    @GET("inquiry/{inquiryId}")
    suspend fun getInquiry(
        @Path("inquiryId") inquiryId: Long,
    ): Response<InquiryResponse>

    @PATCH("inquiry/{inquiryId}")
    suspend fun updateInquiry(
        @Path("inquiryId") inquiryId: Long,
        @Body request: InquiryRequest,
    ): Response<Unit>

    @DELETE("inquiry/{inquiryId}")
    suspend fun deleteInquiry(
        @Path("inquiryId") inquiryId: Long,
    ): Response<Unit>
}
