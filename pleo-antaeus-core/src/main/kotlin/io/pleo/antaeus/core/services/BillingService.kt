package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.ChargeFailedException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {

    fun charge(invoiceId: Int) {
        logger.info { "Trying charge on customer with Invoice ID: $invoiceId" }
        val invoice = invoiceService.fetch(invoiceId)

        val result = paymentProvider.charge(invoice)

        if(!result) throw ChargeFailedException(invoiceId)

        invoiceService.update(invoice.copy(status = InvoiceStatus.PAID))
    }
}
