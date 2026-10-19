import axios from 'axios';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Task } from './task.model';

const apiUrl = `${environment.apiUrl}/tasks`;

async function createTask(task: Task) {
  const response = await axios.post(apiUrl, task)
  return response.data;
}

async function updateTask(id: string, task: Task) {
  try {
    console.log('Updating task with data:', task);
    const response = await axios.put(`${apiUrl}/${id}`, task);
    return response.data;
  } catch (error) {
    console.error(`Error updating task with id: ${id}`, error);
    throw error; // Re-throw the error to handle it in the component
  }
}

async function getTasksFromCreator(creatorId: number) {
  try {
    const response = await axios.get(`${apiUrl}/creator/${creatorId}/overview`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching tasks from creator ${creatorId}: `, error);
    return null;
  }
}

async function getAllTasks() {
  try {
    const response = await axios.get(`${apiUrl}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching all tasks:', error);
    return null;
  }
}

async function approveTask(taskId: number) {
  try {
    const response = await axios.put(`${apiUrl}/${taskId}/approve`, {});
    console.log('Task approved successfully:', taskId);
    return response.data;
  } catch (error) {
    console.error(`Error approving task with id: ${taskId}`, error);
    return null;
  }
}

async function declineTask(taskId: number) {
  try {
    const response = await axios.put(`${apiUrl}/${taskId}/decline`, {});
    console.log('Task declined successfully:', taskId);
    return response.data;
  } catch (error) {
    console.error(`Error declining task with id: ${taskId}`, error);
    return null;
  }
}

async function archiveTask(taskId: number) {
  try {
    const response = await axios.delete(`${apiUrl}/${taskId}`);
    console.log('Task archived successfully:', response);
    return response.data;
  } catch (error) {
    console.error(`Error archiving task with id: ${taskId}`, error);
    return null;
  }
}

const TaskService = {
  createTask,
  updateTask,
  getTasksFromCreator,
  approveTask,
  declineTask,
  getAllTasks,
  archiveTask,
};

export default TaskService;
