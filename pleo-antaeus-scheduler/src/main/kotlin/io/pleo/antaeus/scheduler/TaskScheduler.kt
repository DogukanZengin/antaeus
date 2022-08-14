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
 * Main component for initalizing the scheduler and registering scheduled tasks
 */
class TaskScheduler(dataSource: DataSource, invoiceService: InvoiceService, chargeInvoiceProducer: ChargeInvoiceProducer) {

    init {

        //Run every month on the First day
        val monthlyChargeInvoiceTask = Tasks.recurring("charge-invoices-task", Schedules.cron("0 0 0 1 * *"))
                .execute { instance: TaskInstance<Void>, ctx: ExecutionContext ->
                    val task = ChargeInvoiceTask(chargeInvoiceProducer, invoiceService)
                    task.execute()
                }



        //Testing a one time runner to see the results
        val oneTimerTestTask : OneTimeTask<Void> = Tasks.oneTime("one-timer-test-task")
                .execute{ instance: TaskInstance<Void>, ctx: ExecutionContext ->
                    val task = ChargeInvoiceTask(chargeInvoiceProducer, invoiceService)
                    task.execute()
                }

        val scheduler = Scheduler
                .create(dataSource,oneTimerTestTask)
                .startTasks(monthlyChargeInvoiceTask)
                .registerShutdownHook()
                .jdbcCustomization(CustomJdbcCustomization())
                .enableImmediateExecution()
                .build();

        scheduler.start()
        scheduler.schedule(oneTimerTestTask.instance("1"), Instant.now())
    }
}