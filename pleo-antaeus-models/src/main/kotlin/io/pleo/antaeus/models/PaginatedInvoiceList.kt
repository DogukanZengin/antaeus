package io.pleo.antaeus.models

data class PaginatedInvoiceList(
    val result: List<Invoice>,
    val hasMore: Boolean
)
