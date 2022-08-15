package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.ChargeFailedException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {

    private val paymentProvider = mockk<PaymentProvider>()
    private val invoiceService = mockk<InvoiceService>()


    private val billingService = BillingService(paymentProvider, invoiceService)

    @Test
    fun `throw exception when charge fails`() {

        val invoice = Invoice(1,1, Money(BigDecimal.TEN, Currency.EUR),InvoiceStatus.PENDING)
        every { invoiceService.fetch(1) } returns invoice
        every { paymentProvider.charge(invoice) } returns false

        Assertions.assertThrows(ChargeFailedException::class.java) { billingService.charge(1) }
    }
}