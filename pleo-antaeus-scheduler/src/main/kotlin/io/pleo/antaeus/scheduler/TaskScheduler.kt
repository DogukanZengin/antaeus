package io.pleo.antaeus.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.ExecutionContext
import com.github.kagkarlsson.scheduler.task.TaskInstance
import com.github.kagkarlsson.scheduler.task.VoidExecutionHandler
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.Schedules
import java.time.Instant
import javax.sql.DataSource


class TaskScheduler(dataSource: DataSource) {

    init {

        //Run every month on the First day
        val monthlyChargeInvoiceTask = Tasks.recurring("charge-invoices-task", Schedules.cron("0 0 0 1 * *"))
                .execute { instance: TaskInstance<Void>, ctx: ExecutionContext ->
                    val task = ChargeInvoiceTask()
                    task.execute()
                }



        //Testing a one time runner to see the results
        val myAdhocTask: OneTimeTask<Void> = Tasks.oneTime("my-typed-adhoc-task")
                .execute{ instance: TaskInstance<Void>, ctx: ExecutionContext ->
                    val task = ChargeInvoiceTask()
                    task.execute()
                }

        val scheduler = Scheduler
                .create(dataSource,myAdhocTask)
                .startTasks(monthlyChargeInvoiceTask)
                .registerShutdownHook()
                .jdbcCustomization(CustomJdbcCustomization())
                .enableImmediateExecution()
                .build();

        scheduler.start()
        scheduler.schedule(myAdhocTask.instance("1"), Instant.now())
    }
}