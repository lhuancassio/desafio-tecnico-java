import { useCallback, useEffect, useState } from 'react';
import { TaskForm } from './components/TaskForm';
import { TaskList } from './components/TaskList';
import { taskService } from './services/taskService';
import type { CreateTaskRequest, Task, UpdateTaskRequest } from './types/task';
import './App.css';

export default function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchTasks = useCallback(async () => {
    try {
      setLoading(true);
      const data = await taskService.listAll();
      setTasks(data);
      setError(null);
    } catch {
      setError('Erro ao carregar tarefas. Verifique se a API está rodando.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleOpenCreate = () => {
    setEditingTask(null);
    setShowForm(true);
  };

  const handleOpenEdit = (task: Task) => {
    setEditingTask(task);
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingTask(null);
  };

  const handleSubmit = async (data: CreateTaskRequest | UpdateTaskRequest) => {
    setSaving(true);
    try {
      if (editingTask) {
        await taskService.update(editingTask.id, data as UpdateTaskRequest);
      } else {
        await taskService.create(data as CreateTaskRequest);
      }
      setShowForm(false);
      setEditingTask(null);
      await fetchTasks();
    } catch {
      setError('Erro ao salvar a tarefa. Tente novamente.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Deseja excluir esta tarefa?')) return;
    try {
      await taskService.remove(id);
      await fetchTasks();
    } catch {
      setError('Erro ao excluir a tarefa.');
    }
  };

  const counts = {
    total: tasks.length,
    pending: tasks.filter((t) => t.status === 'PENDING').length,
    inProgress: tasks.filter((t) => t.status === 'IN_PROGRESS').length,
    done: tasks.filter((t) => t.status === 'DONE').length,
  };

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <div>
            <h1>Task Manager</h1>
            <p className="header-subtitle">Gerenciamento de tarefas</p>
          </div>
          <button className="btn-new-task" onClick={handleOpenCreate}>
            + Nova Tarefa
          </button>
        </div>
      </header>

      <main className="app-main">
        {error && (
          <div className="alert-error">
            {error}
            <button onClick={() => setError(null)}>✕</button>
          </div>
        )}

        <div className="stats-bar">
          <div className="stat">
            <span className="stat-value">{counts.total}</span>
            <span className="stat-label">Total</span>
          </div>
          <div className="stat">
            <span className="stat-value stat-pending">{counts.pending}</span>
            <span className="stat-label">Pendentes</span>
          </div>
          <div className="stat">
            <span className="stat-value stat-progress">{counts.inProgress}</span>
            <span className="stat-label">Em andamento</span>
          </div>
          <div className="stat">
            <span className="stat-value stat-done">{counts.done}</span>
            <span className="stat-label">Concluídas</span>
          </div>
        </div>

        <TaskList
          tasks={tasks}
          onEdit={handleOpenEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      </main>

      {showForm && (
        <TaskForm
          task={editingTask}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          loading={saving}
        />
      )}
    </div>
  );
}
