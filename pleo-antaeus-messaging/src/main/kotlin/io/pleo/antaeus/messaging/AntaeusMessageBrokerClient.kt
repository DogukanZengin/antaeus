package io.pleo.antaeus.messaging

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import io.pleo.antaeus.models.config.BrokerConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AntaeusMessageBrokerClient(brokerConfiguration: BrokerConfiguration) {

    lateinit var producer: ChargeInvoiceProducer
    lateinit var consumer: ChargeInvoiceConsumer
    val channel: Channel

    init {
        logger.info { "Initializing broker channel.." }
        val factory = ConnectionFactory()
        factory.host = brokerConfiguration.host
        val connection = factory.newConnection()
        channel = connection.createChannel()
        val arguments = mapOf<String, Any>("x-dead-letter-exchange" to brokerConfiguration.dlq,
                                           "x-dead-letter-routing-key" to brokerConfiguration.dlq)

        channel.queueDeclare(brokerConfiguration.queue, false, false, false, arguments)

        channel.exchangeDeclare(brokerConfiguration.exchange, BuiltinExchangeType.DIRECT)
        channel.exchangeDeclare(brokerConfiguration.dlq, BuiltinExchangeType.DIRECT)

        channel.queueBind(brokerConfiguration.queue, brokerConfiguration.exchange, brokerConfiguration.queue)
    }
}

/**
 * - finish scheduler logic - with leader message production and consume
 * - finish exception handling logic - network errors should end up in DLQ
 * - Tests   "x-dead-letter-exchange": "dlx_exchange", "x-dead-letter-routing-key": "dlx_key"
 */