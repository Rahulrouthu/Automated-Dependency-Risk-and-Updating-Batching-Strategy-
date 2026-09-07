import React from 'react';
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, BarChart, Bar, XAxis, YAxis } from 'recharts';

const RISK_COLORS = {
  LOW: '#10b981',
  MEDIUM: '#3b82f6',
  HIGH: '#f59e0b',
  CRITICAL: '#ef4444',
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
    { name: 'Minor', count: diffCounts.MINOR, fill: '#0ea5e9' },
    { name: 'Patch', count: diffCounts.PATCH, fill: '#10b981' },
    { name: 'Current', count: diffCounts.UP_TO_DATE, fill: '#64748b' },
  ];

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-8">
      {/* Risk Distribution Donut */}
      <div className="glass-panel p-5 rounded-2xl border border-slate-800">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center justify-between">
          <span>Risk Level Distribution</span>
          <span className="text-xs font-normal text-slate-400 font-mono">{dependencies.length} Packages</span>
        </h3>
        <div className="h-56 w-full flex items-center justify-center">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={pieData}
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={80}
                paddingAngle={4}
                dataKey="value"
              >
                {pieData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={RISK_COLORS[entry.name] || '#64748b'} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: '#0f172a',
                  borderColor: '#334155',
                  borderRadius: '0.75rem',
                  fontSize: '0.75rem',
                  color: '#fff',
                }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="flex justify-center gap-4 text-xs font-medium mt-1">
          {Object.entries(riskCounts).map(([lvl, cnt]) => (
            <div key={lvl} className="flex items-center space-x-1.5">
              <span className="h-2 w-2 rounded-full" style={{ backgroundColor: RISK_COLORS[lvl] }} />
              <span className="text-slate-300">{lvl}:</span>
              <span className="text-white font-mono font-bold">{cnt}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Version Change Distribution */}
      <div className="glass-panel p-5 rounded-2xl border border-slate-800">
        <h3 className="text-sm font-bold text-white mb-2 flex items-center justify-between">
          <span>Version Update Types</span>
          <span className="text-xs font-normal text-slate-400 font-mono">SemVer Jumps</span>
        </h3>
        <div className="h-56 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={barData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <XAxis dataKey="name" stroke="#64748b" fontSize={11} tickLine={false} />
              <YAxis stroke="#64748b" fontSize={11} tickLine={false} allowDecimals={false} />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#0f172a',
                  borderColor: '#334155',
                  borderRadius: '0.75rem',
                  fontSize: '0.75rem',
                  color: '#fff',
                }}
              />
              <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                {barData.map((entry, index) => (
                  <Cell key={`bar-${index}`} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="flex justify-center gap-4 text-xs font-medium mt-1 text-slate-400">
          <span>Major: <b className="text-rose-400">{diffCounts.MAJOR}</b></span>
          <span>Minor: <b className="text-sky-400">{diffCounts.MINOR}</b></span>
          <span>Patch: <b className="text-emerald-400">{diffCounts.PATCH}</b></span>
          <span>Up-to-Date: <b className="text-slate-300">{diffCounts.UP_TO_DATE}</b></span>
        </div>
      </div>
    </div>
  );
}
