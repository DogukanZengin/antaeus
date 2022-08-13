package io.pleo.antaeus.models.config

data class BrokerConfiguration(val queue: String,
                               val dlq:String,
                               val exchange: String,
                               val host: String)
