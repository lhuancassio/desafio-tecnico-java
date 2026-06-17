import type { Task } from '../types/task';
import { TaskCard } from './TaskCard';
import './TaskList.css';

interface Props {
  tasks: Task[];
  onEdit: (task: Task) => void;
  onDelete: (id: string) => void;
  loading: boolean;
}

export function TaskList({ tasks, onEdit, onDelete, loading }: Props) {
  if (loading) {
    return (
      <div className="task-list-empty">
        <p>Carregando tarefas...</p>
      </div>
    );
  }

  if (tasks.length === 0) {
    return (
      <div className="task-list-empty">
        <p>Nenhuma tarefa encontrada.</p>
        <p className="hint">Clique em "Nova Tarefa" para criar a primeira.</p>
      </div>
    );
  }

  return (
    <div className="task-list">
      {tasks.map((task) => (
        <TaskCard key={task.id} task={task} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </div>
  );
}
