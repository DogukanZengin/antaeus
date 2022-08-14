/*
    Defines database tables and their schemas.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object InvoiceTable : Table() {
    val id = integer("id").autoIncrement()
    val currency = varchar("currency", 3)
    val value = decimal("value", 1000, 2)
    val customerId = reference("customer_id", CustomerTable.id)
    val status = text("status")
    override val primaryKey = PrimaryKey(id, name = "PK_InvoiceTable_Id")
}

object CustomerTable : Table() {
    val id = integer("id").autoIncrement()
    val currency = varchar("currency", 3)
    override val primaryKey = PrimaryKey(id, name = "PK_CustomerTable_Id")
}

object ScheduledTasksTable: Table("scheduled_tasks"){
    val taskName = varchar("task_name",40)
    val taskInstance = varchar("task_instance",40)
    val taskData = blob("task_data")
    val executionTime = timestamp("execution_time")
    val picked = bool("picked").isNotNull()
    val pickedBy = varchar("picked_by",50)
    val lastSuccess = timestamp("last_success").isNull()
    val lastFailure = timestamp("last_failure").isNull()
    val consecutiveFailures = integer("consecutive_failures")
    val lastHeartbeat = timestamp("last_heartbeat").isNull()
    val version = long("version").isNotNull()
    override val primaryKey = PrimaryKey(arrayOf(taskName, taskInstance))
}