/*
    Defines database tables and their schemas.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import org.jetbrains.exposed.sql.Table

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
    override val primaryKey = PrimaryKey(InvoiceTable.id, name = "PK_CustomerTable_Id")
}
