package com.farmmanagementpro.modals;

public class FarmTask {

        private String taskDate;
        private String taskStatus;
        private String taskName;
        private String description;

        public FarmTask() {
        }

    public FarmTask(String taskDate, String taskStatus, String taskName, String description) {
        this.taskDate = taskDate;
        this.taskStatus = taskStatus;
        this.taskName = taskName;
        this.description = description;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
