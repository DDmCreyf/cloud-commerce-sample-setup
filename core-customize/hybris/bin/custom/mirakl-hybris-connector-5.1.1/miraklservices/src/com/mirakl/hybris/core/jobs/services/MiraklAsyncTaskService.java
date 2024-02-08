package com.mirakl.hybris.core.jobs.services;

import java.util.Date;
import java.util.List;

import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;

public interface MiraklAsyncTaskService {

  /**
   * Creates and saves a new MiraklAsyncTask in the database.
   * 
   * @param trackingId The Mirakl tracking ID of the task
   * @param type The type of the Task
   * @param requestDate date just before requesting the task creation in Mirakl
   * @return The model of the persisted task
   */
  MiraklAsyncTaskModel saveNewTask(String trackingId, MiraklAsyncTaskType type, Date requestDate);

  /**
   * Updates task status
   * 
   * @param task The task to update
   * @param status The new status to write
   * @return The updated task
   */
  MiraklAsyncTaskModel updateTaskStatus(MiraklAsyncTaskModel task, MiraklAsyncTaskStatus status);

  /**
   * Determines if some tasks are still running for the given type.
   * 
   * @param type The type of the Task
   * @return Whether tasks are still running
   */
  boolean hasRunningTasks(MiraklAsyncTaskType type);

  /**
   * Find Mirakl asynchronous tasks not imported in Hybris yet by Type.
   * By default, looks for PENDING tasks sorted by ascending creation date.
   *
   * @param type The type of the tasks to find
   * @return A list of eligible tasks
   */
  List<MiraklAsyncTaskModel> findTasksToProcess(MiraklAsyncTaskType type);

  /**
   * Delete the provided task.
   * 
   * @param task The task to delete
   */
  void delete(MiraklAsyncTaskModel task);
}
