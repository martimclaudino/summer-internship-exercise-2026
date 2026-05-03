package com.premiumminds.internship.taskscheduler;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskSchedulerServiceTest {

    private ITaskSchedulerService service;

    @BeforeEach
    void setUp() {
        service = new TaskSchedulerService();
    }

    @Test
    void testSingleTaskNoDependencies() {

        Task a = new Task("A", 1, Set.of());
        List<Task> order = service.getExecutionOrder(Set.of(a));

        assertEquals(1, order.size());
        assertEquals("A", order.get(0).getId());
    }

    @Test
    void testLinearDependency() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of("A"));

        List<Task> order = service.getExecutionOrder(Set.of(b, a));

        assertEquals(2, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("B", order.get(1).getId());
    }

    @Test
    void testPriorityBreaksTie() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of("A"));
        Task c = new Task("C", 1, Set.of("A"));

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c));

        assertEquals(3, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("C", order.get(1).getId());
        assertEquals("B", order.get(2).getId());
    }

    @Test
    void testCircularDependencyThrows() {
        Task a = new Task("A", 1, Set.of("B"));
        Task b = new Task("B", 1, Set.of("A"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a, b)));
    }

    @Test
    void testMissingDependencyThrows() {
        Task a = new Task("A", 1, Set.of("X"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a)));
    }

    @Test
    void testTaskWithMultipleDependencies() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of("A"));
        Task c = new Task("C", 1, Set.of("A"));
        Task d = new Task("D", 1, Set.of("B","C"));

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c, d));

        assertEquals(4, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("C", order.get(1).getId());
        assertEquals("B", order.get(2).getId());
        assertEquals("D", order.get(3).getId());
    }

    @Test
    void testMultipleTasksWithNoDependencies() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of());
        Task c = new Task("C", 3, Set.of());
        Task d = new Task("D", 4, Set.of());

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c, d));

        assertEquals(4, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("B", order.get(1).getId());
        assertEquals("C", order.get(2).getId());
        assertEquals("D", order.get(3).getId());
    }

    @Test
    void testLinearDependencyMoreDepth() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of("A"));
        Task c = new Task("C", 3, Set.of("B"));
        Task d = new Task("D", 4, Set.of("C"));

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c, d));

        assertEquals(4, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("B", order.get(1).getId());
        assertEquals("C", order.get(2).getId());
        assertEquals("D", order.get(3).getId());
    }

    @Test
    void testTaskDependsOnItselfThrows() {
        Task a = new Task("A", 1, Set.of("A"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a)));
    }

    @Test
    void testIndirectCircularDependencyThrows() {
        Task a = new Task("A", 1, Set.of("B"));
        Task b = new Task("B", 1, Set.of("C"));
        Task c = new Task("C", 1, Set.of("A"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a, b, c)));
    }

    @Test
    void testEmptyTaskCollectionThrows() {
        List<Task> order = service.getExecutionOrder(Set.of());
        assertEquals(0, order.size());
    }

    @Test
    void testCompletedTaskThrows() {
        Task a = new Task("A", 1, Set.of());
        a.setStatus(TaskStatus.COMPLETED);

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a)));
    }

    @Test
    void testCompletedAndPendingTasksThrows() {
        Task a = new Task("A", 1, Set.of());
        a.setStatus(TaskStatus.COMPLETED);
        Task b = new Task("B", 1, Set.of());
        
        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a, b)));
    }

    @Test
    void testPendingTaskDependsOnCompletedTaskThrows() {
        Task a = new Task("A", 1, Set.of());
        a.setStatus(TaskStatus.COMPLETED);
        Task b = new Task("B", 1, Set.of("A"));
        
        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a, b)));
    }

    @Test
    void testEligibleTasksNoDependencies() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of());

        List<Task> eligible = service.getEligibleTasks(Set.of(a, b));

        assertEquals(2, eligible.size());
    }

    @Test
    void testEligibleTasksWithPendingDependency() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of("A"));

        List<Task> eligible = service.getEligibleTasks(Set.of(a, b));

        assertEquals(1, eligible.size());
        assertEquals("A", eligible.get(0).getId());
    }
}
