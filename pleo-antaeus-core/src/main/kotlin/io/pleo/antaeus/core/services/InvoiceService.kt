/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoicePaginationCriteria
import io.pleo.antaeus.models.PaginatedInvoiceList

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetchPaginated(criteria: InvoicePaginationCriteria): PaginatedInvoiceList {
        return dal.fetchInvoicesPaginated(criteria)
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun update(invoice:Invoice){
        dal.updateInvoice(invoice)
    }
}
