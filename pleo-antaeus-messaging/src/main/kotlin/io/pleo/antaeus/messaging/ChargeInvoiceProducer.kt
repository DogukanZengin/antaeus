package io.pleo.antaeus.messaging

import com.rabbitmq.client.Channel
import io.pleo.antaeus.models.config.BrokerConfiguration
import io.pleo.antaeus.models.messaging.ChargeInvoiceMessage
import mu.KotlinLogging
import org.apache.commons.lang3.SerializationUtils

private val logger = KotlinLogging.logger {}

class ChargeInvoiceProducer(private val channel: Channel,
                            private val brokerConfiguration: BrokerConfiguration) {
    fun publish(message: ChargeInvoiceMessage) {
        logger.info { "Publishing message: $message" }
        channel.basicPublish(
                brokerConfiguration.exchange,
                brokerConfiguration.queue,
                null,
                SerializationUtils.serialize(message)
        )
    }
}