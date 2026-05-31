package org.activiti.engine.history;

/**
 * Allows to fetch the {@link ProcessInstanceHistoryLog} for a process instance.
 *
 * Note that every includeXXX() method below will lead to an additional query.
 *
 * This class is actually a convenience on top of the other specific queries such
 * as {@link HistoricTaskInstanceQuery}, {@link HistoricActivityInstanceQuery}, ...
 * It will execute separate queries for each included type, order the
 * data according to the date (ascending) and wrap the results in the {@link ProcessInstanceHistoryLog}.
 *
 * @author Joram Barrez
 */
public interface ProcessInstanceHistoryLogQuery {

	/** The {@link ProcessInstanceHistoryLog} will contain the {@link HistoricTaskInstance} instances. */
	ProcessInstanceHistoryLogQuery includeTasks();

	/** The {@link ProcessInstanceHistoryLog} will contain the {@link HistoricActivityInstance} instances. */
	ProcessInstanceHistoryLogQuery includeActivities();

	/** The {@link ProcessInstanceHistoryLog} will contain the {@link HistoricVariableInstance} instances. */
	ProcessInstanceHistoryLogQuery includeVariables();

	/** Executes the query. */
	ProcessInstanceHistoryLog singleResult();

}
