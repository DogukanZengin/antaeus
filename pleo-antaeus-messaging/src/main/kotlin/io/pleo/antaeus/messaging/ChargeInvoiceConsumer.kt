package io.pleo.antaeus.messaging

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.models.messaging.ChargeInvoiceMessage
import mu.KotlinLogging
import org.apache.commons.lang3.SerializationUtils
import java.lang.Exception

private val logger = KotlinLogging.logger {}

class ChargeInvoiceConsumer(channel: Channel,
                            private val billingService: BillingService): DefaultConsumer(channel)  {

    override fun handleDelivery(consumerTag: String,
                                envelope: Envelope,
                                properties: AMQP.BasicProperties,
                                body: ByteArray) {
        val message: ChargeInvoiceMessage = SerializationUtils.deserialize(body)
        logger.info { "[$consumerTag] Received message: '$message'." }
        try{
            billingService.charge(message.id)
            channel.basicAck(envelope.deliveryTag, false)
        }catch (e: Exception){
            channel.basicNack(envelope.deliveryTag, false, false)//send to dlq for further investigation
        }
    }
}