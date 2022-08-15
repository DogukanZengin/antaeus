package io.pleo.antaeus.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.ExecutionContext
import com.github.kagkarlsson.scheduler.task.TaskInstance
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.Schedules
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.messaging.ChargeInvoiceProducer
import java.time.Instant
import javax.sql.DataSource

/**
 * Main component for initializing the scheduler and registering scheduled tasks
 */
class TaskScheduler(dataSource: DataSource, invoiceService: InvoiceService, chargeInvoiceProducer: ChargeInvoiceProducer) {

    init {

        //Run every month on the First day
        val monthlyChargeInvoiceTask = Tasks.recurring("charge-invoices-task", Schedules.cron("1 0 0 1 * *"))
                .execute { _: TaskInstance<Void>, _: ExecutionContext ->
                    val task = ChargeInvoiceTask(chargeInvoiceProducer, invoiceService)
                    task.execute()
                }

        //Run for once
        val oneTimeTask : OneTimeTask<Void> = Tasks.oneTime("one-timer-initial-task")
                .execute{ _: TaskInstance<Void>, _: ExecutionContext ->
                    val task = ChargeInvoiceTask(chargeInvoiceProducer, invoiceService)
                    task.execute()
                }

        val scheduler = Scheduler
                .create(dataSource,oneTimeTask)
                .startTasks(monthlyChargeInvoiceTask)
                .registerShutdownHook()
                .jdbcCustomization(CustomJdbcCustomization())
                .enableImmediateExecution()
                .build();

        scheduler.start()
        scheduler.schedule(oneTimeTask.instance("1"), Instant.now())
    }
}