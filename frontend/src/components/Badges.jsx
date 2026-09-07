import React from 'react';
import { getRiskBadgeStyle, getVersionDiffBadge } from '../utils/formatters';

export function RiskBadge({ level, score }) {
  const lvl = (level || 'LOW').toUpperCase();
  let bg = 'bg-emerald-950/40 text-emerald-300 border-emerald-500/30';
  let dot = 'bg-emerald-400 shadow-[0_0_8px_#34d399]';
  let icon = '🛡️';

  if (lvl === 'MEDIUM') {
    bg = 'bg-sky-950/40 text-sky-300 border-sky-500/30';
    dot = 'bg-sky-400 shadow-[0_0_8px_#38bdf8]';
    icon = '⚡';
  } else if (lvl === 'HIGH') {
    bg = 'bg-amber-950/40 text-amber-300 border-amber-500/40';
    dot = 'bg-amber-400 shadow-[0_0_8px_#fbbf24]';
    icon = '⚠️';
  } else if (lvl === 'CRITICAL') {
    bg = 'bg-rose-950/50 text-rose-200 border-rose-500/50 shadow-sm shadow-rose-950/60';
    dot = 'bg-rose-400 shadow-[0_0_10px_#f43f5e] animate-pulse';
    icon = '🚨';
  }

  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold border backdrop-blur-md ${bg}`}>
      <span className={`h-2 w-2 rounded-full mr-1.5 ${dot}`} />
      <span>{icon} {lvl}</span>
      {score !== undefined && <span className="ml-1.5 opacity-90 font-mono text-[11px]">({score})</span>}
    </span>
  );
}

export function VersionBadge({ type }) {
  const badge = getVersionDiffBadge(type);
  const t = (type || '').toUpperCase();
  let color = 'bg-slate-900/80 text-slate-300 border-white/[0.08]';
  
  if (t === 'MAJOR') {
    color = 'bg-rose-950/40 text-rose-300 border-rose-500/30 shadow-sm shadow-rose-950/40';
  } else if (t === 'MINOR') {
    color = 'bg-sky-950/40 text-sky-300 border-sky-500/30';
  } else if (t === 'PATCH') {
    color = 'bg-emerald-950/40 text-emerald-300 border-emerald-500/30';
  }

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-lg text-[11px] font-bold font-mono border ${color}`}>
      {badge.label}
    </span>
  );
}

export function EcosystemBadge({ ecosystem }) {
  let icon = '📦';
  let color = 'bg-slate-900 text-slate-300 border-white/[0.08]';
  if (ecosystem === 'MAVEN' || ecosystem === 'GRADLE') {
    icon = '☕';
    color = 'bg-amber-950/30 text-amber-300 border-amber-500/25';
  } else if (ecosystem === 'NPM') {
    icon = '⬡';
    color = 'bg-emerald-950/30 text-emerald-300 border-emerald-500/25';
  } else if (ecosystem === 'PYTHON') {
    icon = '🐍';
    color = 'bg-sky-950/30 text-sky-300 border-sky-500/25';
  }

  return (
    <span className={`inline-flex items-center space-x-1.5 px-2.5 py-1 rounded-lg text-xs font-semibold border ${color}`}>
      <span>{icon}</span>
      <span className="font-mono text-[11px]">{ecosystem}</span>
    </span>
  );
}

export function DataSourceBadge({ source }) {
  const isLive = (source || 'LIVE').toUpperCase() === 'LIVE';
  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-lg text-xs font-bold border ${
      isLive 
        ? 'bg-emerald-950/50 text-emerald-300 border-emerald-500/40 shadow-sm shadow-emerald-950/50' 
        : 'bg-amber-950/50 text-amber-300 border-amber-500/40 shadow-sm shadow-amber-950/50'
    }`}>
      <span className={`h-2 w-2 rounded-full mr-2 animate-pulse ${isLive ? 'bg-emerald-400 shadow-[0_0_8px_#34d399]' : 'bg-amber-400 shadow-[0_0_8px_#fbbf24]'}`} />
      {isLive ? 'LIVE REPOSITORY SCAN' : 'DEMO BENCHMARK DATA'}
    </span>
  );
}

export function ConfidenceBadge({ level, score }) {
  const lvl = (level || 'HIGH').toUpperCase();
  let color = 'bg-emerald-950/40 text-emerald-300 border-emerald-500/30';
  let dot = 'bg-emerald-400';
  let icon = '🛡️';

  if (lvl === 'MEDIUM') {
    color = 'bg-sky-950/40 text-sky-300 border-sky-500/30';
    dot = 'bg-sky-400';
    icon = 'ℹ️';
  } else if (lvl === 'LOW') {
    color = 'bg-amber-950/40 text-amber-300 border-amber-500/30';
    dot = 'bg-amber-400';
    icon = '⚠️';
  }

  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-lg text-xs font-semibold border ${color} shadow-sm`}>
      <span className={`h-1.5 w-1.5 rounded-full mr-1.5 ${dot}`} />
      <span className="mr-1">{icon}</span>
      <span>{lvl} Confidence</span>
      {score !== undefined && <span className="ml-1 opacity-85 font-mono">({score}%)</span>}
    </span>
  );
}

export function SecurityPriorityBadge({ priority }) {
  const p = (priority || 'NONE').toUpperCase();
  let style = 'bg-slate-900/80 text-slate-400 border-white/[0.08]';

  if (p === 'CRITICAL') {
    style = 'bg-rose-950/50 text-rose-200 border-rose-500/50 shadow-sm shadow-rose-950/50';
  } else if (p === 'HIGH') {
    style = 'bg-orange-950/50 text-orange-200 border-orange-500/50';
  } else if (p === 'MEDIUM') {
    style = 'bg-amber-950/50 text-amber-300 border-amber-500/40';
  } else if (p === 'LOW') {
    style = 'bg-sky-950/50 text-sky-300 border-sky-500/40';
  } else if (p === 'UNAVAILABLE') {
    style = 'bg-slate-900 text-slate-400 border-white/[0.08]';
  }

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-lg text-[11px] font-bold font-mono border ${style}`}>
      {p === 'CRITICAL' && <span className="mr-1">🚨</span>}
      {p === 'HIGH' && <span className="mr-1">⚠️</span>}
      {p === 'UNAVAILABLE' ? 'SERVICE OFFLINE' : p}
    </span>
  );
}

export function LockfileBadge({ source }) {
  if (!source || source === 'NONE') {
    return (
      <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] bg-slate-900/90 text-slate-400 border border-white/[0.06] font-mono">
        Estimated (No Lockfile)
      </span>
    );
  }
  return (
    <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] bg-cyan-950/50 text-cyan-300 border border-cyan-500/30 font-mono shadow-sm">
      🔒 {source}
    </span>
  );
}
