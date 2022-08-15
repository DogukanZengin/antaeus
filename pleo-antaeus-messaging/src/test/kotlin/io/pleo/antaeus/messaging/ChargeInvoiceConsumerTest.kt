package io.pleo.antaeus.messaging

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.ChargeFailedException
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.models.messaging.ChargeInvoiceMessage
import org.apache.commons.lang3.SerializationUtils
import org.junit.jupiter.api.Test

class ChargeInvoiceConsumerTest {

    private val channel = mockk<Channel>()
    private val billingService = mockk<BillingService>()

    private val chargeInvoiceConsumer = ChargeInvoiceConsumer(channel, billingService)


    @Test
    fun `consume message successfully`() {
        val message = ChargeInvoiceMessage(id = 1)
        val serializedMessage = SerializationUtils.serialize(message)
        every { billingService.charge(1) } returns Unit
        every { channel.basicAck(any(),any()) } returns Unit
        chargeInvoiceConsumer.handleDelivery("consumer",
                Envelope(1L,false,"ex","ex"), AMQP.BasicProperties(),serializedMessage)

        verify(exactly = 1) { channel.basicAck(any(),any()) }
    }

    @Test
    fun `send message to DLQ when exception occurs`() {
        val message = ChargeInvoiceMessage(id = 1)
        val serializedMessage = SerializationUtils.serialize(message)
        every { billingService.charge(1) } throws ChargeFailedException(1)
        every { channel.basicNack(any(),any(), any()) } returns Unit
        chargeInvoiceConsumer.handleDelivery("consumer",
                Envelope(1L,false,"ex","ex"), AMQP.BasicProperties(),serializedMessage)

        verify(exactly = 0) { channel.basicAck(any(),any()) }
        verify(exactly = 1) { channel.basicNack(any(),any(),any()) }
    }
}