package io.pleo.antaeus.scheduler

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.messaging.ChargeInvoiceProducer
import io.pleo.antaeus.models.PaginatedInvoiceList
import org.junit.jupiter.api.Test

class ChargeInvoiceTaskTest {

    private val chargeInvoiceProducer = mockk<ChargeInvoiceProducer>()
    private val invoiceService = mockk<InvoiceService>()

    private val chargeInvoiceTask = ChargeInvoiceTask(chargeInvoiceProducer, invoiceService)

    @Test
    fun `no action when no invoices`() {
        val page = PaginatedInvoiceList(result = listOf(), hasMore = false)

        every { invoiceService.fetchPaginated(any()) } returns page

        chargeInvoiceTask.execute()

        verify(exactly = 0) { chargeInvoiceProducer.publish(any())}
    }
}