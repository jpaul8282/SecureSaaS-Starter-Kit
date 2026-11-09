package com.example.service.backend

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Contract for the trusted application backend. All methods require a short-lived access token
 * obtained from the app's real authentication provider. Do not send Stripe secret keys here.
 * These declarations alone do not enable billing until a backend is deployed and authenticated.
 */
interface BillingBackendApi {
  @POST("v1/billing/subscriptions")
  suspend fun createSubscription(
    @Header("Authorization") bearerToken: String,
    @Body request: CreateSubscriptionRequest,
  ): Response<PaymentSheetConfiguration>

  @GET("v1/billing/invoices")
  suspend fun listInvoices(
    @Header("Authorization") bearerToken: String,
    @Query("limit") limit: Int = 12,
  ): Response<InvoiceListResponse>

  @POST("v1/privacy/exports")
  suspend fun requestDataExport(
    @Header("Authorization") bearerToken: String,
  ): Response<PrivacyRequestResponse>

  @POST("v1/privacy/deletion-requests")
  suspend fun requestAccountDeletion(
    @Header("Authorization") bearerToken: String,
    @Body request: DeletionRequest,
  ): Response<PrivacyRequestResponse>
}

/** Client sends plan identifiers only. The server chooses price IDs and validates eligibility. */
data class CreateSubscriptionRequest(
  @Json(name = "plan_id") val planId: String,
  @Json(name = "billing_cycle") val billingCycle: String,
  @Json(name = "idempotency_key") val idempotencyKey: String,
)

/** The backend returns provider SDK client secrets; never accept an amount from the client. */
data class PaymentSheetConfiguration(
  @Json(name = "subscription_id") val subscriptionId: String,
  @Json(name = "customer_id") val customerId: String,
  @Json(name = "ephemeral_key_secret") val ephemeralKeySecret: String,
  @Json(name = "payment_intent_client_secret") val paymentIntentClientSecret: String?,
  @Json(name = "setup_intent_client_secret") val setupIntentClientSecret: String?,
  @Json(name = "publishable_key") val publishableKey: String,
)

data class InvoiceListResponse(
  @Json(name = "invoices") val invoices: List<BackendInvoice>,
)

data class BackendInvoice(
  @Json(name = "id") val id: String,
  @Json(name = "number") val number: String?,
  @Json(name = "created_at") val createdAt: String,
  @Json(name = "amount_minor") val amountMinor: Long,
  @Json(name = "currency") val currency: String,
  @Json(name = "status") val status: String,
  @Json(name = "hosted_invoice_url") val hostedInvoiceUrl: String?,
  @Json(name = "pdf_url") val pdfUrl: String?,
)

data class PrivacyRequestResponse(
  @Json(name = "request_id") val requestId: String,
  @Json(name = "status") val status: String,
  @Json(name = "status_url") val statusUrl: String?,
)

data class DeletionRequest(
  @Json(name = "confirmation") val confirmation: String,
)
