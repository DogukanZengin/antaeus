/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import com.sksamuel.hoplite.ConfigLoader
import getPaymentProvider
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.CustomerTable
import io.pleo.antaeus.data.InvoiceTable
import io.pleo.antaeus.data.ScheduledTasksTable
import io.pleo.antaeus.messaging.AntaeusMessageBrokerClient
import io.pleo.antaeus.messaging.ChargeInvoiceConsumer
import io.pleo.antaeus.messaging.ChargeInvoiceProducer
import io.pleo.antaeus.models.config.BrokerConfiguration
import io.pleo.antaeus.rest.AntaeusRest
import io.pleo.antaeus.scheduler.TaskScheduler
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteDataSource
import setupInitialData
import java.io.File
import java.sql.Connection

fun main() {
    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable, ScheduledTasksTable)

    val dbFile = File.createTempFile("antaeus-db", ".sqlite")
    // Connect to the database and create the needed tables. Drop any existing data.
    val dataSource = SQLiteDataSource()
    dataSource.url = "jdbc:sqlite:${dbFile.absolutePath}"

    val db = Database
        .connect(dataSource)
        .also {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(it) {
                addLogger(StdOutSqlLogger)
                // Drop all existing tables to ensure a clean slate on each run
                SchemaUtils.drop(*tables)
                // Create all tables
                SchemaUtils.create(*tables)
            }
        }

    // Set up data access layer.
    val dal = AntaeusDal(db = db)

    // Insert example data in the database.
    setupInitialData(dal = dal)

    // Get third parties
    val paymentProvider = getPaymentProvider()

    // Create core services
    val invoiceService = InvoiceService(dal = dal)
    val customerService = CustomerService(dal = dal)

    // This is _your_ billing service to be included where you see fit
    val billingService = BillingService(paymentProvider = paymentProvider,invoiceService)

    // Create REST web service
    AntaeusRest(
        invoiceService = invoiceService,
        customerService = customerService
    ).run()

    //Load External Configuration
    val brokerConfig = ConfigLoader().loadConfigOrThrow<BrokerConfiguration>("/application.yml")

    //Init broker
    val brokerClient = AntaeusMessageBrokerClient(brokerConfig)
    brokerClient.consumer = ChargeInvoiceConsumer(brokerClient.channel, billingService)
    brokerClient.producer = ChargeInvoiceProducer(brokerClient.channel, brokerConfig)

    //Init scheduler
    val taskScheduler = TaskScheduler(dataSource, invoiceService, brokerClient.producer)
}
