import React, { useState } from 'react';
import { Package, RefreshCw, ShieldAlert, GitPullRequest, AlertTriangle, ShieldCheck, Activity, Info, CheckCircle2, XCircle } from 'lucide-react';
import { RiskBadge, DataSourceBadge, ConfidenceBadge } from './Badges';

export default function OverviewMetrics({ summary }) {
  const [showConfidenceDetails, setShowConfidenceDetails] = useState(false);

  if (!summary) return null;

  return (
    <div className="space-y-4 mb-8">
      {/* Top Header Information Bar */}
      <div className="glass-panel p-4 rounded-2xl border border-slate-800 flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <h2 className="text-xl font-bold text-white tracking-tight">{summary.fullName || summary.url}</h2>
            <span className="text-xs font-mono px-2 py-0.5 rounded bg-slate-800 text-slate-300 border border-slate-700">
              branch: {summary.defaultBranch || 'main'}
            </span>
            <DataSourceBadge source={summary.dataSource} />
            <div 
              className="cursor-pointer"
              onClick={() => setShowConfidenceDetails(!showConfidenceDetails)}
              title="Click to view signal confidence breakdown"
            >
              <ConfidenceBadge level={summary.analysisConfidence} score={summary.analysisConfidenceScore} />
            </div>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Ecosystems: <span className="text-teal-300 font-medium">{summary.detectedEcosystems?.join(', ') || 'Multi-ecosystem'}</span> • Scanned at {new Date(summary.lastScannedAt).toLocaleTimeString()}
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <div className="text-right">
            <div className="text-xs text-slate-400">Composite Risk Level</div>
            <div className="mt-0.5">
              <RiskBadge level={summary.overallRiskLevel} score={summary.overallRiskScore} />
            </div>
          </div>
        </div>
      </div>

      {/* Expandable Confidence Signals Breakdown */}
      {showConfidenceDetails && (
        <div className="glass-panel p-4 rounded-2xl border border-teal-500/30 bg-slate-900/90 animate-fadeIn space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white flex items-center gap-1.5">
              <Info className="h-4 w-4 text-teal-400" />
              Analysis Confidence Engine Signals ({summary.analysisConfidenceScore || 100}% Confidence Score)
            </h3>
            <button 
              onClick={() => setShowConfidenceDetails(false)}
              className="text-xs text-slate-400 hover:text-slate-200"
            >
              Close
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 gap-3 text-xs">
            <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/50">
              <div className="text-slate-400 font-medium mb-1">GitHub Access</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-semibold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.githubStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1">20 / 20 pts</div>
            </div>

            <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/50">
              <div className="text-slate-400 font-medium mb-1">Manifest Parsed</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-semibold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.totalFiles || 1} file(s)</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1">25 / 25 pts</div>
            </div>

            <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/50">
              <div className="text-slate-400 font-medium mb-1">Lockfile Resolution</div>
              <div className="flex items-center gap-1.5 text-cyan-300 font-semibold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>PINNED / MERGED</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1">20 / 20 pts</div>
            </div>

            <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/50">
              <div className="text-slate-400 font-medium mb-1">Registry API</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-semibold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.registryStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1">20 / 20 pts</div>
            </div>

            <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/50">
              <div className="text-slate-400 font-medium mb-1">Security DB (OSV)</div>
              <div className="flex items-center gap-1.5 text-emerald-400 font-semibold">
                <CheckCircle2 className="h-3.5 w-3.5" />
                <span>{summary.securityStatus || 'CONNECTED'}</span>
              </div>
              <div className="text-[10px] text-slate-400 mt-1">15 / 15 pts</div>
            </div>
          </div>
        </div>
      )}

      {/* Metrics Cards Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        {/* Total Dependencies */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Total Dependencies</span>
            <Package className="h-4 w-4 text-teal-400" />
          </div>
          <div className="text-2xl font-black text-white">{summary.totalDependencies}</div>
          <div className="text-[11px] text-slate-400 font-mono">across {summary.totalFiles || 1} manifest(s)</div>
        </div>

        {/* Outdated Dependencies */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Updates Available</span>
            <RefreshCw className="h-4 w-4 text-sky-400" />
          </div>
          <div className="text-2xl font-black text-sky-400">{summary.outdatedCount}</div>
          <div className="text-[11px] text-slate-400 font-mono">
            {summary.totalDependencies > 0 ? Math.round((summary.outdatedCount / summary.totalDependencies) * 100) : 0}% outdated
          </div>
        </div>

        {/* Total Vulnerabilities */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Vulnerabilities</span>
            <ShieldAlert className="h-4 w-4 text-red-400" />
          </div>
          <div className={`text-2xl font-black ${summary.vulnerabilityCount > 0 ? 'text-red-400' : 'text-emerald-400'}`}>
            {summary.vulnerabilityCount}
          </div>
          <div className="text-[11px] text-slate-400 font-mono">
            {summary.vulnerabilityCount === 0 ? '✓ Zero CVEs' : 'active advisories'}
          </div>
        </div>

        {/* Critical & High */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Critical / High</span>
            <AlertTriangle className="h-4 w-4 text-amber-400" />
          </div>
          <div className="text-2xl font-black text-amber-400">
            {summary.criticalCount + summary.highCount}
          </div>
          <div className="text-[11px] text-slate-400 font-mono">
            {summary.criticalCount} crit • {summary.highCount} high
          </div>
        </div>

        {/* Recommended Batches */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Generated Batches</span>
            <GitPullRequest className="h-4 w-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-black text-emerald-400">{summary.recommendedBatches}</div>
          <div className="text-[11px] text-slate-400 font-mono">optimized PR groups</div>
        </div>

        {/* Security Health Index */}
        <div className="glass-panel p-4 rounded-2xl border border-slate-800 space-y-1">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-medium">Health Index</span>
            <Activity className="h-4 w-4 text-teal-400" />
          </div>
          <div className="text-2xl font-black text-teal-300">
            {Math.max(10, Math.round(100 - summary.overallRiskScore))}/100
          </div>
          <div className="text-[11px] text-slate-400 font-mono">Risk-Adjusted Score</div>
        </div>
      </div>
    </div>
  );
}
