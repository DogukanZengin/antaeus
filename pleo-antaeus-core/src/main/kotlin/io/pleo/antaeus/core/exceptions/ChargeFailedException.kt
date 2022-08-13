package io.pleo.antaeus.core.exceptions

class ChargeFailedException(invoiceId: Int) : Exception("Charge failed for Invoice $invoiceId")
