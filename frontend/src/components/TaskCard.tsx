import type { Task } from '../types/task';
import './TaskCard.css';

interface Props {
  task: Task;
  onEdit: (task: Task) => void;
  onDelete: (id: string) => void;
}

const STATUS_CLASS: Record<string, string> = {
  PENDING: 'badge-pending',
  IN_PROGRESS: 'badge-progress',
  DONE: 'badge-done',
};

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function TaskCard({ task, onEdit, onDelete }: Props) {
  return (
    <div className="task-card">
      <div className="task-card-header">
        <span className={`badge ${STATUS_CLASS[task.status] ?? ''}`}>
          {task.statusDisplayName}
        </span>
        <span className="task-date">{formatDate(task.createdAt)}</span>
      </div>

      <h3 className="task-title">{task.title}</h3>

      {task.description && (
        <p className="task-description">{task.description}</p>
      )}

      <div className="task-actions">
        <button className="btn-edit" onClick={() => onEdit(task)}>
          Editar
        </button>
        <button className="btn-delete" onClick={() => onDelete(task.id)}>
          Excluir
        </button>
      </div>
    </div>
  );
}
