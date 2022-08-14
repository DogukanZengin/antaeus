package io.pleo.antaeus.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.ExecutionContext
import com.github.kagkarlsson.scheduler.task.TaskInstance
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.Schedules
import javax.sql.DataSource


class TaskScheduler(dataSource: DataSource) {

    init {
        val scheduler = Scheduler
                .create(dataSource)
                .registerShutdownHook()
                .build();

        //Run every month on the First day
        val monthlyChargeInvoiceTask = Tasks.recurring("charge-invoices-task", Schedules.cron("0 0 1 * *"))
                .execute { instance: TaskInstance<Void>, ctx: ExecutionContext ->
                    println("Executed!") }

        scheduler.start()
    }
}