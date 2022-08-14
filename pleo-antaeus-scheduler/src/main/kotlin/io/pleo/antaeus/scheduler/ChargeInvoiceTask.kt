package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.messaging.ChargeInvoiceProducer
import io.pleo.antaeus.models.InvoicePaginationCriteria
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.PaginatedInvoiceList
import io.pleo.antaeus.models.messaging.ChargeInvoiceMessage

class ChargeInvoiceTask(private val chargeInvoiceProducer: ChargeInvoiceProducer,
                        private val invoiceService: InvoiceService) {

    fun execute(){
        val criteria = InvoicePaginationCriteria(status = InvoiceStatus.PENDING, limit = 1000)
        var page = PaginatedInvoiceList(result = listOf(), hasMore = true)
        var offset = 0L
        while (page.hasMore) {
            offset += page.result.size
            page = invoiceService.fetchPaginated(criteria)
            page.result.forEach {
                chargeInvoiceProducer.publish(ChargeInvoiceMessage(id = it.id))
            }
        }
    }
}