import React from 'react';
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, BarChart, Bar, XAxis, YAxis } from 'recharts';
import { PieChart as PieIcon, BarChart3, ShieldAlert, GitCommit } from 'lucide-react';

const RISK_COLORS = {
  LOW: '#10b981',
  MEDIUM: '#38bdf8',
  HIGH: '#f59e0b',
  CRITICAL: '#f43f5e',
};

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    const data = payload[0];
    return (
      <div className="p-3 rounded-xl bg-[#090d16]/95 border border-white/[0.1] shadow-2xl backdrop-blur-xl text-xs">
        <div className="flex items-center space-x-2 mb-1">
          <span className="h-2 w-2 rounded-full" style={{ backgroundColor: data.payload.fill || data.color }} />
          <span className="font-bold text-white">{data.name || label}</span>
        </div>
        <div className="font-mono text-slate-300">
          Count: <span className="font-bold text-white">{data.value}</span>
        </div>
      </div>
    );
  }
  return null;
};

export default function RiskDistributionCharts({ dependencies, summary }) {
  if (!dependencies || dependencies.length === 0) return null;

  // 1. Risk Level Counts
  const riskCounts = { LOW: 0, MEDIUM: 0, HIGH: 0, CRITICAL: 0 };
  const diffCounts = { MAJOR: 0, MINOR: 0, PATCH: 0, UP_TO_DATE: 0 };

  dependencies.forEach((d) => {
    const level = d.riskAssessment?.riskLevel || 'LOW';
    if (riskCounts[level] !== undefined) riskCounts[level]++;

    const diff = d.versionDiffType || 'UNKNOWN';
    if (diffCounts[diff] !== undefined) diffCounts[diff]++;
  });

  const pieData = Object.entries(riskCounts)
    .filter(([_, count]) => count > 0)
    .map(([name, value]) => ({ name, value }));

  const barData = [
    { name: 'Major', count: diffCounts.MAJOR, fill: '#f43f5e' },
    { name: 'Minor', count: diffCounts.MINOR, fill: '#0284c7' },
    { name: 'Patch', count: diffCounts.PATCH, fill: '#10b981' },
    { name: 'Up-to-Date', count: diffCounts.UP_TO_DATE, fill: '#64748b' },
  ];

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 mb-8">
      {/* Risk Distribution Donut */}
      <div className="glass-panel p-6 rounded-3xl border border-white/[0.08] relative">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2">
            <div className="p-1.5 rounded-lg bg-teal-500/10 text-teal-400">
              <PieIcon className="h-4 w-4" />
            </div>
            <h3 className="text-sm font-extrabold text-white tracking-tight">Risk Level Distribution</h3>
          </div>
          <span className="text-xs font-semibold text-slate-400 font-mono px-2 py-0.5 rounded-lg bg-slate-900/90 border border-white/[0.06]">
            {dependencies.length} Analyzed
          </span>
        </div>

        <div className="h-60 w-full relative flex items-center justify-center">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={pieData}
                cx="50%"
                cy="50%"
                innerRadius={62}
                outerRadius={88}
                paddingAngle={5}
                dataKey="value"
                stroke="none"
              >
                {pieData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={RISK_COLORS[entry.name] || '#64748b'} />
                ))}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
            </PieChart>
          </ResponsiveContainer>

          {/* Centered Donut Summary */}
          <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
            <span className="text-2xl font-black text-white font-mono tracking-tight">{dependencies.length}</span>
            <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Packages</span>
          </div>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs font-medium mt-2 pt-3 border-t border-white/[0.05]">
          {Object.entries(riskCounts).map(([lvl, cnt]) => (
            <div key={lvl} className="p-2 rounded-xl bg-slate-900/60 border border-white/[0.04] flex items-center justify-between">
              <div className="flex items-center space-x-1.5">
                <span className="h-2 w-2 rounded-full" style={{ backgroundColor: RISK_COLORS[lvl] }} />
                <span className="text-slate-300 text-[11px] capitalize">{lvl.toLowerCase()}</span>
              </div>
              <span className="text-white font-mono font-bold text-xs">{cnt}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Version Change Distribution */}
      <div className="glass-panel p-6 rounded-3xl border border-white/[0.08] relative">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2">
            <div className="p-1.5 rounded-lg bg-sky-500/10 text-sky-400">
              <BarChart3 className="h-4 w-4" />
            </div>
            <h3 className="text-sm font-extrabold text-white tracking-tight">SemVer Version Jumps</h3>
          </div>
          <span className="text-xs font-semibold text-slate-400 font-mono px-2 py-0.5 rounded-lg bg-slate-900/90 border border-white/[0.06]">
            Update Impact
          </span>
        </div>

        <div className="h-60 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={barData} margin={{ top: 15, right: 10, left: -20, bottom: 0 }}>
              <XAxis dataKey="name" stroke="#64748b" fontSize={11} tickLine={false} axisLine={{ stroke: '#1e293b' }} />
              <YAxis stroke="#64748b" fontSize={11} tickLine={false} axisLine={{ stroke: '#1e293b' }} allowDecimals={false} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="count" radius={[8, 8, 0, 0]}>
                {barData.map((entry, index) => (
                  <Cell key={`bar-${index}`} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs font-medium mt-2 pt-3 border-t border-white/[0.05]">
          <div className="p-2 rounded-xl bg-slate-900/60 border border-white/[0.04] flex items-center justify-between">
            <span className="text-rose-400 text-[11px] font-medium">Major</span>
            <span className="text-rose-400 font-mono font-bold text-xs">{diffCounts.MAJOR}</span>
          </div>
          <div className="p-2 rounded-xl bg-slate-900/60 border border-white/[0.04] flex items-center justify-between">
            <span className="text-sky-400 text-[11px] font-medium">Minor</span>
            <span className="text-sky-400 font-mono font-bold text-xs">{diffCounts.MINOR}</span>
          </div>
          <div className="p-2 rounded-xl bg-slate-900/60 border border-white/[0.04] flex items-center justify-between">
            <span className="text-emerald-400 text-[11px] font-medium">Patch</span>
            <span className="text-emerald-400 font-mono font-bold text-xs">{diffCounts.PATCH}</span>
          </div>
          <div className="p-2 rounded-xl bg-slate-900/60 border border-white/[0.04] flex items-center justify-between">
            <span className="text-slate-400 text-[11px] font-medium">Up-to-Date</span>
            <span className="text-slate-300 font-mono font-bold text-xs">{diffCounts.UP_TO_DATE}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
