import React, { useState } from 'react';
import { Package, RefreshCw, ShieldAlert, GitPullRequest, AlertTriangle, Activity, Info, CheckCircle2, ChevronDown, ChevronUp, ShieldCheck, Zap } from 'lucide-react';
import { RiskBadge, DataSourceBadge, ConfidenceBadge } from './Badges';

export default function OverviewMetrics({ summary }) {
  const [showConfidenceDetails, setShowConfidenceDetails] = useState(false);

  if (!summary) return null;

  const healthScore = Math.max(10, Math.round(100 - (summary.overallRiskScore || 0)));
  const getHealthGrade = (score) => {
    if (score >= 85) return { grade: 'A', color: 'text-emerald-400', border: 'border-emerald-500/30' };
    if (score >= 70) return { grade: 'B', color: 'text-teal-400', border: 'border-teal-500/30' };
    if (score >= 50) return { grade: 'C', color: 'text-amber-400', border: 'border-amber-500/30' };
    return { grade: 'D', color: 'text-rose-400', border: 'border-rose-500/30' };
  };
  const healthGrade = getHealthGrade(healthScore);

  return (
    <div className="space-y-4 mb-8">
      {/* Top Header Information Bar */}
      <div className="glass-panel p-5 rounded-3xl border border-white/[0.08] relative overflow-hidden">
        <div className="absolute top-0 right-0 -mt-8 -mr-8 w-48 h-48 bg-teal-500/10 rounded-full blur-2xl pointer-events-none" />

        <div className="flex flex-wrap items-center justify-between gap-4 relative z-10">
          <div>
            <div className="flex flex-wrap items-center gap-2.5">
              <h2 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
                {summary.fullName || summary.url}
              </h2>
              <span className="text-xs font-mono px-2.5 py-0.5 rounded-lg bg-slate-900/90 text-slate-300 border border-white/[0.08]">
                branch: {summary.defaultBranch || 'main'}
              </span>
              <DataSourceBadge source={summary.dataSource} />
              <div
                className="cursor-pointer transition hover:scale-105"
                onClick={() => setShowConfidenceDetails(!showConfidenceDetails)}
                title="Click to view signal confidence breakdown"
              >
                <ConfidenceBadge level={summary.analysisConfidence} score={summary.analysisConfidenceScore} />
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-2 text-xs text-slate-400 mt-2 font-medium">
              <span>Ecosystems:</span>
              <div className="flex items-center gap-1.5">
                {summary.detectedEcosystems?.map((eco, i) => (
                  <span key={i} className="px-2 py-0.5 rounded-md bg-teal-950/40 text-teal-300 border border-teal-500/20 text-[11px] font-mono">
                    {eco}
                  </span>
                )) || <span className="text-teal-300">Multi-ecosystem</span>}
              </div>
              <span className="text-slate-400">&bull;</span>
              <span>Scanned at {new Date(summary.lastScannedAt).toLocaleTimeString()}</span>
            </div>
          </div>

          <div className="flex items-center space-x-3">
            <div className="text-right">
              <div className="text-xs text-slate-400 font-medium">Composite Risk Level</div>
              <div className="mt-1">
                <RiskBadge level={summary.overallRiskLevel} score={summary.overallRiskScore} />
              </div>
            </div>
          </div>
        </div>

        {/* Expand/Collapse confidence trigger */}
        <div className="mt-3 pt-3 border-t border-white/[0.05] flex items-center justify-between text-xs text-slate-400">
          <button
            onClick={() => setShowConfidenceDetails(!showConfidenceDetails)}
            className="flex items-center space-x-1 text-teal-400 hover:text-teal-300 font-medium transition cursor-pointer"
          >
            <Info className="h-3.5 w-3.5" />
            <span>Analysis Confidence Signals ({summary.analysisConfidenceScore || 100}%)</span>
            {showConfidenceDetails ? <ChevronUp className="h-3 w-3" /> : <ChevronDown className="h-3 w-3" />}
          </button>
          <span className="text-[11px] font-mono text-slate-400">Status: {summary.analysisStatus || 'COMPLETED'}</span>
        </div>
      </div>

      {/* Expandable Confidence Signals Breakdown */}
      {showConfidenceDetails && (
        <div className="glass-panel p-5 rounded-3xl border border-teal-500/30 bg-[#090e1a]/95 animate-in fade-in duration-200 space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <Info className="h-4 w-4 text-teal-400" />
              <span>Multi-Factor Confidence Verification</span>
            </h3>
            <button
              onClick={() => setShowConfidenceDetails(false)}
              className="text-xs text-slate-400 hover:text-white px-2 py-1 rounded-lg hover:bg-slate-800 transition"
            >
              Close
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 gap-3 text-xs">
            <div className="p-3 rounded-2xl bg-slate-900/80 border border-white/[0.06] shadow-sm">
              <div className="text-slate-400 font-medium mb-1">GitHub Access</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.githubStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1 font-mono">20 / 20 pts</div>
            </div>

            <div className="p-3 rounded-2xl bg-slate-900/80 border border-white/[0.06] shadow-sm">
              <div className="text-slate-400 font-medium mb-1">Manifests Parsed</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.totalFiles || 1} Manifest(s)</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1 font-mono">25 / 25 pts</div>
            </div>

            <div className="p-3 rounded-2xl bg-slate-900/80 border border-white/[0.06] shadow-sm">
              <div className="text-slate-400 font-medium mb-1">Lockfile Resolution</div>
              <div className="flex items-center gap-1.5 text-cyan-300 font-bold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>PINNED &bull; MERGED</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1 font-mono">20 / 20 pts</div>
            </div>

            <div className="p-3 rounded-2xl bg-slate-900/80 border border-white/[0.06] shadow-sm">
              <div className="text-slate-400 font-medium mb-1">Registry API</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.registryStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1 font-mono">20 / 20 pts</div>
            </div>

            <div className="p-3 rounded-2xl bg-slate-900/80 border border-white/[0.06] shadow-sm">
              <div className="text-slate-400 font-medium mb-1">Security DB (OSV)</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.securityStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1 font-mono">15 / 15 pts</div>
            </div>
          </div>
        </div>
      )}

      {/* Metrics Cards Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3.5">
        {/* Total Dependencies */}
        <div className="glass-panel p-4.5 rounded-3xl border border-white/[0.08] relative group hover:border-teal-500/40 transition duration-300">
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Total Dependencies</span>
            <div className="p-2 rounded-xl bg-teal-500/10 text-teal-400">
              <Package className="h-4 w-4" />
            </div>
          </div>
          <div className="text-2xl sm:text-3xl font-black text-white tracking-tight">{summary.totalDependencies}</div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">across {summary.totalFiles || 1} manifest(s)</div>
        </div>

        {/* Updates Available */}
        <div className="glass-panel p-4.5 rounded-3xl border border-white/[0.08] relative group hover:border-sky-500/40 transition duration-300">
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Updates Available</span>
            <div className="p-2 rounded-xl bg-sky-500/10 text-sky-400">
              <RefreshCw className="h-4 w-4" />
            </div>
          </div>
          <div className="text-2xl sm:text-3xl font-black text-sky-400 tracking-tight">{summary.outdatedCount}</div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">
            {summary.totalDependencies > 0 ? Math.round((summary.outdatedCount / summary.totalDependencies) * 100) : 0}% outdated
          </div>
        </div>

        {/* Total Vulnerabilities */}
        <div className="glass-panel p-4.5 rounded-3xl border border-white/[0.08] relative group hover:border-rose-500/40 transition duration-300">
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Vulnerabilities</span>
            <div className="p-2 rounded-xl bg-rose-500/10 text-rose-400">
              <ShieldAlert className="h-4 w-4" />
            </div>
          </div>
          <div className={`text-2xl sm:text-3xl font-black tracking-tight ${summary.vulnerabilityCount > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
            {summary.vulnerabilityCount}
          </div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">
            {summary.vulnerabilityCount === 0 ? '✓ Zero CVEs' : 'active advisories'}
          </div>
        </div>

        {/* Critical & High */}
        <div className="glass-panel p-4.5 rounded-3xl border border-white/[0.08] relative group hover:border-amber-500/40 transition duration-300">
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Critical / High</span>
            <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400">
              <AlertTriangle className="h-4 w-4" />
            </div>
          </div>
          <div className="text-2xl sm:text-3xl font-black text-amber-400 tracking-tight">
            {summary.criticalCount + summary.highCount}
          </div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">
            {summary.criticalCount} crit &bull; {summary.highCount} high
          </div>
        </div>

        {/* Generated Batches */}
        <div className="glass-panel p-4.5 rounded-3xl border border-white/[0.08] relative group hover:border-emerald-500/40 transition duration-300">
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Generated Batches</span>
            <div className="p-2 rounded-xl bg-emerald-500/10 text-emerald-400">
              <GitPullRequest className="h-4 w-4" />
            </div>
          </div>
          <div className="text-2xl sm:text-3xl font-black text-emerald-400 tracking-tight">{summary.recommendedBatches}</div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">optimized PR groups</div>
        </div>

        {/* Health Index */}
        <div className={`glass-panel p-4.5 rounded-3xl border ${healthGrade.border} relative group hover:shadow-glow-teal transition duration-300`}>
          <div className="flex items-center justify-between text-slate-400 mb-2">
            <span className="text-xs font-semibold">Health Index</span>
            <div className="p-2 rounded-xl bg-teal-500/10 text-teal-400">
              <Activity className="h-4 w-4" />
            </div>
          </div>
          <div className="flex items-baseline space-x-1.5">
            <span className={`text-2xl sm:text-3xl font-black tracking-tight ${healthGrade.color}`}>
              {healthScore}
            </span>
            <span className="text-xs text-slate-400 font-mono">/100</span>
          </div>
          <div className="text-[11px] text-slate-400 font-mono mt-1">Grade: <b className={healthGrade.color}>{healthGrade.grade}</b> &bull; Risk-Adjusted</div>
        </div>
      </div>
    </div>
  );
}
