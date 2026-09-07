export const getRiskBadgeStyle = (level) => {
  switch (level?.toUpperCase()) {
    case 'CRITICAL':
      return {
        bg: 'bg-red-500/15',
        border: 'border-red-500/30',
        text: 'text-red-400',
        dot: 'bg-red-500',
        icon: '🚨',
      };
    case 'HIGH':
      return {
        bg: 'bg-amber-500/15',
        border: 'border-amber-500/30',
        text: 'text-amber-400',
        dot: 'bg-amber-500',
        icon: '⚠️',
      };
    case 'MEDIUM':
      return {
        bg: 'bg-blue-500/15',
        border: 'border-blue-500/30',
        text: 'text-blue-400',
        dot: 'bg-blue-500',
        icon: '⚡',
      };
    case 'LOW':
    default:
      return {
        bg: 'bg-emerald-500/15',
        border: 'border-emerald-500/30',
        text: 'text-emerald-400',
        dot: 'bg-emerald-500',
        icon: '✓',
      };
  }
};

export const getVersionDiffBadge = (type) => {
  switch (type) {
    case 'MAJOR':
      return { label: 'MAJOR', bg: 'bg-rose-950 text-rose-300 border-rose-800' };
    case 'MINOR':
      return { label: 'MINOR', bg: 'bg-sky-950 text-sky-300 border-sky-800' };
    case 'PATCH':
      return { label: 'PATCH', bg: 'bg-emerald-950 text-emerald-300 border-emerald-800' };
    case 'UP_TO_DATE':
      return { label: 'CURRENT', bg: 'bg-slate-800 text-slate-400 border-slate-700' };
    default:
      return { label: type || 'UNKNOWN', bg: 'bg-slate-800 text-slate-400 border-slate-700' };
  }
};

export const getEcosystemIcon = (eco) => {
  switch (eco?.toUpperCase()) {
    case 'MAVEN':
    case 'GRADLE':
      return '☕';
    case 'NPM':
      return '⬡';
    case 'PYTHON':
      return '🐍';
    default:
      return '📦';
  }
};
