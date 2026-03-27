package com.premiumminds.internship.taskscheduler;

import java.util.List;

public class Task {

    private final String id;
    private final int priority;
    private final List<String> dependencies;
    private TaskStatus status;

    public Task(String id, int priority, List<String> dependencies) {
        this.id = id;
        this.priority = priority;
        this.dependencies = dependencies;
        this.status = TaskStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
