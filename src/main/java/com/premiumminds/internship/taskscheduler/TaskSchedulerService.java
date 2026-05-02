package com.premiumminds.internship.taskscheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskSchedulerService implements ITaskSchedulerService {

    @Override
    public List<Task> getEligibleTasks(Collection<Task> tasks) {

        List<Task> eligibleTasks = new ArrayList<>();
        
        // Mapping tasks for their id
        Map<String, Task> taskMap = new HashMap<>();
        for (Task t : tasks) {
            taskMap.put(t.getId(), t);
        }

        for (Task task : tasks) {
            
            boolean completedDependencies = true;
            
            // Check status
            if (task.getStatus() == TaskStatus.PENDING) {
                
                // Check dependencies
                for (String depId : task.getDependencies()) {
                    
                    Task dependentTask = taskMap.get(depId);

                    // Dependencies are checked in getExecutionOrder() so it won't be null
                    // but hipothethical further implementations could have functions that don't check it
                    if (dependentTask == null || dependentTask.getStatus() != TaskStatus.COMPLETED) {
                        completedDependencies = false;
                        break;
                    }
                }  
                
                if (completedDependencies) {
                    eligibleTasks.add(task);
                } 
            }
        }
        eligibleTasks.sort(Comparator.comparingInt(Task::getPriority));

        return eligibleTasks;
    }

    // Nota sobre a implementação
    // Um sistema de agendamento de tarefas realista apenas ordena tarefas, não as executa, 
    // pelo que não deveria modificar o status das tasks originais, que hão de estar visíveis 
    // para os trabalhadores. Deveria fazer uma cópia das tasks recebidas e modificá-las.
    // No entanto, o enunciado diz "um sistema de agendamento de tarefas que as execute",
    // portanto optei por modificar as tasks originais para simular a execução.
    @Override
    public List<Task> getExecutionOrder(Collection<Task> tasks) {
        List<Task> fullOrder = new ArrayList<>();
        
        // Mapping tasks for their id, only to check dependencies with less complexity
        Map<String, Task> taskMap = new HashMap<>();
        for (Task t : tasks) {
            taskMap.put(t.getId(), t);
        }

        // Check for missing dependencies
        for (Task task : tasks) {
            for (String depId : task.getDependencies()) {
                if (!taskMap.containsKey(depId)) {
                    throw new IllegalArgumentException("Missing dependency: " + depId);
                }
            }
        }

        while (fullOrder.size() != tasks.size()) {
            
            // Get eligible tasks
            List<Task> tasksToExecute = getEligibleTasks(tasks);

            if (tasksToExecute.isEmpty()) {
                throw new IllegalArgumentException("Circular dependency detected");
            }

            // Check the eligible tasks
            for (Task task : tasksToExecute) {
                task.setStatus(TaskStatus.COMPLETED);
                fullOrder.add(task);
            }
        }

        return fullOrder;
    }
}
