import React, { useState } from 'react';
import { GitPullRequest, Copy, Check, ShieldAlert, Sparkles, Terminal, FileCode, ExternalLink, CheckCircle2, ShieldCheck, Cpu } from 'lucide-react';
import { RiskBadge, VersionBadge, EcosystemBadge } from './Badges';

export default function BatchingView({ batches, onSelectDependency }) {
  const [copiedBatchIndex, setCopiedBatchIndex] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [activePrModalBatch, setActivePrModalBatch] = useState(null);

  const filteredBatches = batches.filter((b) => {
    if (selectedCategory === 'ALL') return true;
    return b.category === selectedCategory;
  });

  const handleCopyCommands = (commands, index) => {
    navigator.clipboard.writeText(commands);
    setCopiedBatchIndex(index);
    setTimeout(() => setCopiedBatchIndex(null), 2000);
  };

  return (
    <div className="space-y-6">
      {/* Header & Strategy Info */}
      <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-3">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2">
              <Cpu className="h-5 w-5 text-teal-400" />
              <h3 className="text-lg font-bold text-white tracking-tight">Risk-Aware Constraint-Based Dependency Batching</h3>
            </div>
            <p className="text-xs text-slate-300 mt-1">
              Deterministic constraint-satisfaction clustering: enforces ecosystem homogeneity, quarantines critical CVEs into atomic hotfixes, chunks safe updates (&le; 10 pkgs), and isolates major breaking bumps.
            </p>
          </div>

          {/* Category Filter */}
          <div className="flex flex-wrap items-center gap-1.5 bg-slate-900 p-1 rounded-xl border border-slate-800 text-xs">
            <button
              onClick={() => setSelectedCategory('ALL')}
              className={`px-3 py-1.5 rounded-lg transition font-medium cursor-pointer ${
                selectedCategory === 'ALL' ? 'bg-teal-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              All Batches ({batches.length})
            </button>
            <button
              onClick={() => setSelectedCategory('URGENT_SECURITY')}
              className={`px-3 py-1.5 rounded-lg transition font-medium cursor-pointer ${
                selectedCategory === 'URGENT_SECURITY' ? 'bg-rose-500 text-white font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              🚨 Security Hotfixes
            </button>
            <button
              onClick={() => setSelectedCategory('SAFE_PATCHES')}
              className={`px-3 py-1.5 rounded-lg transition font-medium cursor-pointer ${
                selectedCategory === 'SAFE_PATCHES' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ✓ Safe Patches
            </button>
            <button
              onClick={() => setSelectedCategory('MINOR_UPDATES')}
              className={`px-3 py-1.5 rounded-lg transition font-medium cursor-pointer ${
                selectedCategory === 'MINOR_UPDATES' ? 'bg-sky-500 text-white font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ⚡ Minor Updates
            </button>
            <button
              onClick={() => setSelectedCategory('ISOLATED_MAJOR')}
              className={`px-3 py-1.5 rounded-lg transition font-medium cursor-pointer ${
                selectedCategory === 'ISOLATED_MAJOR' ? 'bg-amber-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ⚠️ Major Upgrades
            </button>
          </div>
        </div>
      </div>

      {/* Batches Grid */}
      <div className="space-y-4">
        {filteredBatches.length === 0 ? (
          <div className="glass-panel p-8 rounded-2xl border border-slate-800 text-center text-slate-400">
            No batches found for this category filter.
          </div>
        ) : (
          filteredBatches.map((batch, index) => {
            const isSecurity = batch.category === 'URGENT_SECURITY';
            const isMajor = batch.category === 'ISOLATED_MAJOR';

            return (
              <div
                key={batch.batchNumber || index}
                className={`glass-panel p-6 rounded-2xl border transition ${
                  isSecurity
                    ? 'border-rose-900/60 bg-rose-950/10'
                    : isMajor
                    ? 'border-amber-900/40 bg-amber-950/10'
                    : 'border-slate-800'
                }`}
              >
                {/* Batch Header */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-slate-800/80">
                  <div className="space-y-1">
                    <div className="flex items-center space-x-2">
                      <span className="text-xs font-mono font-bold px-2 py-0.5 rounded bg-slate-800 text-teal-300 border border-slate-700">
                        Batch #{batch.batchNumber}
                      </span>
                      <h4 className="text-base font-bold text-white tracking-tight">{batch.title}</h4>
                    </div>
                    <p className="text-xs text-slate-400">{batch.strategyDescription}</p>
                  </div>

                  <div className="flex items-center space-x-2">
                    <RiskBadge level={batch.batchRiskLevel} score={batch.batchRiskScore} />
                    <button
                      onClick={() => setActivePrModalBatch(batch)}
                      className="text-xs font-medium px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition flex items-center space-x-1"
                    >
                      <FileCode className="h-3.5 w-3.5 text-teal-400" />
                      <span>PR Template</span>
                    </button>
                  </div>
                </div>

                {/* Reason for Grouping & Constraints Bar */}
                <div className="mt-3 p-3 rounded-xl bg-slate-950/70 border border-slate-800/80 space-y-2 text-xs">
                  {batch.reasonForGrouping && (
                    <div className="flex items-start space-x-2">
                      <span className="font-semibold text-teal-400 shrink-0">Reason for Grouping:</span>
                      <span className="text-slate-300">{batch.reasonForGrouping}</span>
                    </div>
                  )}

                  {batch.constraints && batch.constraints.length > 0 && (
                    <div className="flex flex-wrap items-center gap-1.5 pt-1">
                      <span className="text-[11px] text-slate-400 font-medium mr-1">Constraints Satisfied:</span>
                      {batch.constraints.map((c, ci) => (
                        <span key={ci} className="inline-flex items-center space-x-1 px-2 py-0.5 rounded text-[10px] bg-slate-900 text-slate-300 border border-slate-700 font-mono">
                          <CheckCircle2 className="h-2.5 w-2.5 text-emerald-400" />
                          <span>{c}</span>
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                {/* Dependencies List in Batch */}
                <div className="py-4 space-y-2">
                  <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400 block">
                    Included Package Updates ({batch.dependencies.length})
                  </span>

                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
                    {batch.dependencies.map((dep) => (
                      <div
                        key={dep.coordinates}
                        onClick={() => onSelectDependency(dep)}
                        className="p-3 rounded-xl bg-slate-900/70 border border-slate-800 hover:border-teal-500/40 transition cursor-pointer flex items-center justify-between group"
                      >
                        <div className="space-y-0.5 truncate pr-2">
                          <div className="font-mono text-xs font-bold text-white group-hover:text-teal-400 transition truncate">
                            {dep.name}
                          </div>
                          <div className="text-[11px] font-mono text-slate-400 flex items-center space-x-1">
                            <span>{dep.currentVersion}</span>
                            <span className="text-slate-400">&rarr;</span>
                            <span className="text-teal-400 font-semibold">{dep.latestVersion}</span>
                          </div>
                        </div>

                        <VersionBadge type={dep.versionDiffType} />
                      </div>
                    ))}
                  </div>
                </div>

                {/* Execution Commands Box */}
                <div className="mt-2 p-3.5 rounded-xl bg-slate-950/90 border border-slate-800">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2 text-xs font-mono text-slate-400">
                      <Terminal className="h-3.5 w-3.5 text-teal-400" />
                      <span>Execution CLI Commands:</span>
                    </div>
                    <button
                      onClick={() => handleCopyCommands(batch.executionCommands, index)}
                      className="text-xs font-mono text-slate-400 hover:text-teal-300 flex items-center space-x-1 transition cursor-pointer"
                    >
                      {copiedBatchIndex === index ? (
                        <>
                          <Check className="h-3 w-3 text-emerald-400" />
                          <span className="text-emerald-400">Copied!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="h-3 w-3" />
                          <span>Copy Commands</span>
                        </>
                      )}
                    </button>
                  </div>
                  <pre className="text-xs font-mono text-teal-300 overflow-x-auto whitespace-pre-wrap">
                    {batch.executionCommands}
                  </pre>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* PR Template Modal */}
      {activePrModalBatch && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-in fade-in">
          <div className="glass-panel w-full max-w-2xl rounded-2xl p-6 border border-slate-700 shadow-2xl space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h3 className="text-base font-bold text-white flex items-center space-x-2">
                <GitPullRequest className="h-4 w-4 text-teal-400" />
                <span>GitHub Pull Request Description</span>
              </h3>
              <button
                onClick={() => setActivePrModalBatch(null)}
                className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
              >
                ✕
              </button>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-slate-300 block">PR Title:</label>
              <input
                type="text"
                readOnly
                value={activePrModalBatch.prTitle}
                className="w-full px-3 py-2 bg-slate-900 rounded-xl border border-slate-800 text-xs font-mono text-teal-300"
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-slate-300 block">PR Markdown Body:</label>
              <textarea
                readOnly
                rows={10}
                value={activePrModalBatch.prBody}
                className="w-full p-3 bg-slate-900 rounded-xl border border-slate-800 text-xs font-mono text-slate-300 resize-none"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => {
                  navigator.clipboard.writeText(activePrModalBatch.prBody);
                  alert("Pull Request Markdown copied to clipboard!");
                }}
                className="px-4 py-2 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition"
              >
                Copy PR Markdown
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
