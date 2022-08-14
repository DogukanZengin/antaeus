package io.pleo.antaeus.models

data class InvoicePaginationCriteria(val status: InvoiceStatus, val limit: Int = 1000, val offset: Long = 0)
