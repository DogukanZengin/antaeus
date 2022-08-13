package io.pleo.antaeus.messaging

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import io.pleo.antaeus.models.config.BrokerConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AntaeusMessageBrokerClient(private val brokerConfiguration: BrokerConfiguration) {

    init {
        val factory = ConnectionFactory()
        factory.host = brokerConfiguration.host
        val connection = factory.newConnection()
        val channel: Channel = connection.createChannel()

        channel.queueDeclare(brokerConfiguration.queue, false, false, false, null)
        channel.queueDeclare(brokerConfiguration.dlq, false, false, false, null)
        channel.exchangeDeclare(brokerConfiguration.exchange, BuiltinExchangeType.DIRECT)
        channel.queueBind(brokerConfiguration.queue, brokerConfiguration.exchange, brokerConfiguration.queue)
        channel.queueBind(brokerConfiguration.dlq, brokerConfiguration.exchange, brokerConfiguration.dlq)
    }
}

/**
 * - finish scheduler logic - with leader message production and consume
 * - Finish DLQ setup
 * - finish exception handling logic - network errors should end up in DLQ
 * - Tests
 */