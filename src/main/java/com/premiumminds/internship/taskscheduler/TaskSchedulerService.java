package com.premiumminds.internship.taskscheduler;

import java.util.Collection;
import java.util.List;

public class TaskSchedulerService implements ITaskSchedulerService {

    @Override
    public List<Task> getEligibleTasks(Collection<Task> tasks) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Task> getExecutionOrder(Collection<Task> tasks) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
