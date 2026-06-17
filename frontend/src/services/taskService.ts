import axios from 'axios';
import type { CreateTaskRequest, Task, UpdateTaskRequest } from '../types/task';

const api = axios.create({ baseURL: '/api' });

export const taskService = {
  listAll: (): Promise<Task[]> =>
    api.get<Task[]>('/tasks').then((r) => r.data),

  create: (data: CreateTaskRequest): Promise<Task> =>
    api.post<Task>('/tasks', data).then((r) => r.data),

  update: (id: string, data: UpdateTaskRequest): Promise<Task> =>
    api.put<Task>(`/tasks/${id}`, data).then((r) => r.data),

  remove: (id: string): Promise<void> =>
    api.delete(`/tasks/${id}`).then(() => undefined),
};
