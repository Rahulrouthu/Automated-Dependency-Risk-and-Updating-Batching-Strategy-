import React from 'react';
import { getRiskBadgeStyle, getVersionDiffBadge } from '../utils/formatters';

export function RiskBadge({ level, score }) {
  const style = getRiskBadgeStyle(level);
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${style.bg} ${style.border} ${style.text}`}>
      <span className={`h-1.5 w-1.5 rounded-full mr-1.5 ${style.dot}`} />
      {style.icon} {level} {score !== undefined && <span className="ml-1 opacity-80 font-mono">({score})</span>}
    </span>
  );
}

export function VersionBadge({ type }) {
  const badge = getVersionDiffBadge(type);
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold border ${badge.bg}`}>
      {badge.label}
    </span>
  );
}

export function EcosystemBadge({ ecosystem }) {
  let icon = '📦';
  let color = 'bg-slate-800 text-slate-300 border-slate-700';
  if (ecosystem === 'MAVEN' || ecosystem === 'GRADLE') {
    icon = '☕';
    color = 'bg-amber-950/40 text-amber-300 border-amber-800/40';
  } else if (ecosystem === 'NPM') {
    icon = '⬡';
    color = 'bg-emerald-950/40 text-emerald-300 border-emerald-800/40';
  } else if (ecosystem === 'PYTHON') {
    icon = '🐍';
    color = 'bg-blue-950/40 text-blue-300 border-blue-800/40';
  }

  return (
    <span className={`inline-flex items-center space-x-1 px-2 py-0.5 rounded text-xs border ${color}`}>
      <span>{icon}</span>
      <span>{ecosystem}</span>
    </span>
  );
}

export function DataSourceBadge({ source }) {
  const isLive = (source || 'LIVE').toUpperCase() === 'LIVE';
  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold border ${
      isLive 
        ? 'bg-emerald-950/60 text-emerald-300 border-emerald-500/40 shadow-sm shadow-emerald-950/50' 
        : 'bg-amber-950/60 text-amber-300 border-amber-500/40 shadow-sm shadow-amber-950/50'
    }`}>
      <span className={`h-2 w-2 rounded-full mr-1.5 animate-pulse ${isLive ? 'bg-emerald-400' : 'bg-amber-400'}`} />
      {isLive ? 'LIVE REPOSITORY SCAN' : 'DEMO BENCHMARK DATA'}
    </span>
  );
}

export function ConfidenceBadge({ level, score }) {
  const lvl = (level || 'HIGH').toUpperCase();
  let color = 'bg-emerald-950/50 text-emerald-300 border-emerald-700/50';
  let dot = 'bg-emerald-400';
  let icon = '🛡️';

  if (lvl === 'MEDIUM') {
    color = 'bg-blue-950/50 text-blue-300 border-blue-700/50';
    dot = 'bg-blue-400';
    icon = 'ℹ️';
  } else if (lvl === 'LOW') {
    color = 'bg-amber-950/50 text-amber-300 border-amber-700/50';
    dot = 'bg-amber-400';
    icon = '⚠️';
  }

  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium border ${color}`}>
      <span className={`h-1.5 w-1.5 rounded-full mr-1.5 ${dot}`} />
      <span className="mr-1">{icon}</span>
      <span>{lvl} Confidence</span>
      {score !== undefined && <span className="ml-1 opacity-80 font-mono">({score}%)</span>}
    </span>
  );
}

export function SecurityPriorityBadge({ priority }) {
  const p = (priority || 'NONE').toUpperCase();
  let style = 'bg-slate-800/60 text-slate-400 border-slate-700';

  if (p === 'CRITICAL') {
    style = 'bg-rose-950/70 text-rose-300 border-rose-600/60 shadow-sm shadow-rose-950/50';
  } else if (p === 'HIGH') {
    style = 'bg-orange-950/70 text-orange-300 border-orange-600/60';
  } else if (p === 'MEDIUM') {
    style = 'bg-amber-950/60 text-amber-300 border-amber-600/50';
  } else if (p === 'LOW') {
    style = 'bg-blue-950/60 text-blue-300 border-blue-700/50';
  } else if (p === 'UNAVAILABLE') {
    style = 'bg-slate-900 text-slate-400 border-slate-700/60';
  }

  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold border ${style}`}>
      {p === 'CRITICAL' && <span className="mr-1">🚨</span>}
      {p === 'HIGH' && <span className="mr-1">⚠️</span>}
      {p === 'UNAVAILABLE' ? 'SERVICE OFFLINE' : p}
    </span>
  );
}

export function LockfileBadge({ source }) {
  if (!source || source === 'NONE') {
    return (
      <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] bg-slate-800 text-slate-400 border border-slate-700">
        Estimated (No Lockfile)
      </span>
    );
  }
  return (
    <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] bg-cyan-950/50 text-cyan-300 border border-cyan-700/40">
      🔒 {source}
    </span>
  );
}
