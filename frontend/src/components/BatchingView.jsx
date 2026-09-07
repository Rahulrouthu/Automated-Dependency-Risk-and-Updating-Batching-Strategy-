import React, { useState } from 'react';
import { GitPullRequest, Copy, Check, ShieldAlert, Sparkles, Terminal, FileCode, ExternalLink, CheckCircle2, ShieldCheck, Cpu, ArrowRight } from 'lucide-react';
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
      <div className="glass-panel p-6 sm:p-7 rounded-3xl border border-white/[0.08] space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2.5">
              <div className="p-2 rounded-xl bg-teal-500/10 text-teal-400">
                <Cpu className="h-5 w-5" />
              </div>
              <h3 className="text-base sm:text-lg font-extrabold text-white tracking-tight">
                Constraint-Based Greedy Batching Strategy
              </h3>
            </div>
            <p className="text-xs text-slate-300 mt-1.5 leading-relaxed max-w-3xl">
              Deterministic clustering algorithm: enforces ecosystem boundaries, isolates critical CVEs into standalone hotfixes, caps safe patch groups (&le; 10 pkgs), and quarantines high-risk breaking major version jumps.
            </p>
          </div>

          {/* Category Filter Pills */}
          <div className="flex flex-wrap items-center gap-1.5 bg-slate-900/90 p-1.5 rounded-2xl border border-white/[0.08] text-xs">
            <button
              onClick={() => setSelectedCategory('ALL')}
              className={`px-3 py-1.5 rounded-xl transition duration-200 font-semibold cursor-pointer ${
                selectedCategory === 'ALL' ? 'bg-teal-500 text-slate-950 shadow-glow-teal font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              All ({batches.length})
            </button>
            <button
              onClick={() => setSelectedCategory('URGENT_SECURITY')}
              className={`px-3 py-1.5 rounded-xl transition duration-200 font-semibold cursor-pointer ${
                selectedCategory === 'URGENT_SECURITY' ? 'bg-rose-500 text-white shadow-glow-rose font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              🚨 Hotfixes
            </button>
            <button
              onClick={() => setSelectedCategory('SAFE_PATCHES')}
              className={`px-3 py-1.5 rounded-xl transition duration-200 font-semibold cursor-pointer ${
                selectedCategory === 'SAFE_PATCHES' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ✓ Patches
            </button>
            <button
              onClick={() => setSelectedCategory('MINOR_UPDATES')}
              className={`px-3 py-1.5 rounded-xl transition duration-200 font-semibold cursor-pointer ${
                selectedCategory === 'MINOR_UPDATES' ? 'bg-sky-500 text-white font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ⚡ Minor
            </button>
            <button
              onClick={() => setSelectedCategory('ISOLATED_MAJOR')}
              className={`px-3 py-1.5 rounded-xl transition duration-200 font-semibold cursor-pointer ${
                selectedCategory === 'ISOLATED_MAJOR' ? 'bg-amber-500 text-slate-950 shadow-glow-amber font-bold' : 'text-slate-400 hover:text-white'
              }`}
            >
              ⚠️ Major
            </button>
          </div>
        </div>
      </div>

      {/* Batches Grid */}
      <div className="space-y-4">
        {filteredBatches.length === 0 ? (
          <div className="glass-panel p-12 rounded-3xl border border-white/[0.08] text-center text-slate-400 font-medium">
            No batches found for this category filter.
          </div>
        ) : (
          filteredBatches.map((batch, index) => {
            const isSecurity = batch.category === 'URGENT_SECURITY';
            const isMajor = batch.category === 'ISOLATED_MAJOR';

            return (
              <div
                key={batch.batchNumber || index}
                className={`glass-panel p-6 sm:p-7 rounded-3xl border transition duration-300 ${
                  isSecurity
                    ? 'border-rose-500/40 bg-rose-950/15 shadow-sm shadow-rose-950/40 hover:border-rose-400/60'
                    : isMajor
                    ? 'border-amber-500/30 bg-amber-950/10 hover:border-amber-400/50'
                    : 'border-white/[0.08] hover:border-teal-500/40'
                }`}
              >
                {/* Batch Header */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-white/[0.06]">
                  <div className="space-y-1">
                    <div className="flex items-center space-x-2.5">
                      <span className="text-xs font-mono font-bold px-2.5 py-0.5 rounded-lg bg-slate-900 text-teal-300 border border-teal-500/30 shadow-inner">
                        Batch #{batch.batchNumber}
                      </span>
                      <h4 className="text-base font-extrabold text-white tracking-tight">{batch.title}</h4>
                    </div>
                    <p className="text-xs text-slate-400 font-medium">{batch.strategyDescription}</p>
                  </div>

                  <div className="flex items-center space-x-2.5">
                    <RiskBadge level={batch.batchRiskLevel} score={batch.batchRiskScore} />
                    <button
                      onClick={() => setActivePrModalBatch(batch)}
                      className="text-xs font-semibold px-3 py-1.5 rounded-xl bg-slate-900/90 hover:bg-slate-800 text-slate-200 border border-white/[0.1] hover:border-teal-500/40 transition flex items-center space-x-1.5 cursor-pointer shadow-sm"
                    >
                      <FileCode className="h-3.5 w-3.5 text-teal-400" />
                      <span>PR Template</span>
                    </button>
                  </div>
                </div>

                {/* Reason for Grouping & Constraints */}
                <div className="mt-4 p-4 rounded-2xl bg-[#090d16]/80 border border-white/[0.06] space-y-2.5 text-xs">
                  {batch.reasonForGrouping && (
                    <div className="flex items-start space-x-2">
                      <span className="font-bold text-teal-400 shrink-0">Reason for Grouping:</span>
                      <span className="text-slate-300 font-medium">{batch.reasonForGrouping}</span>
                    </div>
                  )}

                  {batch.constraints && batch.constraints.length > 0 && (
                    <div className="flex flex-wrap items-center gap-1.5 pt-1">
                      <span className="text-[11px] text-slate-400 font-medium mr-1">Constraints Satisfied:</span>
                      {batch.constraints.map((c, ci) => (
                        <span key={ci} className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-lg text-[10px] bg-slate-900 text-slate-300 border border-white/[0.08] font-mono font-medium">
                          <CheckCircle2 className="h-2.5 w-2.5 text-emerald-400" />
                          <span>{c}</span>
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                {/* Dependencies List in Batch */}
                <div className="py-4 space-y-2.5">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">
                    Included Package Updates ({batch.dependencies.length})
                  </span>

                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2.5">
                    {batch.dependencies.map((dep) => (
                      <div
                        key={dep.coordinates}
                        onClick={() => onSelectDependency(dep)}
                        className="p-3.5 rounded-2xl bg-slate-900/70 border border-white/[0.06] hover:border-teal-500/40 transition duration-150 cursor-pointer flex items-center justify-between group"
                      >
                        <div className="space-y-0.5 truncate pr-2">
                          <div className="font-mono text-xs font-bold text-white group-hover:text-teal-300 transition truncate">
                            {dep.name}
                          </div>
                          <div className="text-[11px] font-mono text-slate-400 flex items-center space-x-1.5">
                            <span>{dep.currentVersion}</span>
                            <span className="text-slate-400">&rarr;</span>
                            <span className="text-teal-300 font-bold">{dep.latestVersion}</span>
                          </div>
                        </div>

                        <VersionBadge type={dep.versionDiffType} />
                      </div>
                    ))}
                  </div>
                </div>

                {/* Execution Commands Box with Terminal Header */}
                <div className="mt-2 p-4 rounded-2xl bg-[#060911] border border-white/[0.08] shadow-inner">
                  <div className="flex items-center justify-between mb-2.5">
                    <div className="flex items-center space-x-2 text-xs font-mono text-slate-400">
                      <Terminal className="h-3.5 w-3.5 text-teal-400" />
                      <span className="font-semibold text-slate-300">Execution CLI Commands:</span>
                    </div>
                    <button
                      onClick={() => handleCopyCommands(batch.executionCommands, index)}
                      className="text-xs font-mono px-2.5 py-1 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-300 hover:text-teal-300 border border-white/[0.08] flex items-center space-x-1.5 transition cursor-pointer"
                    >
                      {copiedBatchIndex === index ? (
                        <>
                          <Check className="h-3 w-3 text-emerald-400" />
                          <span className="text-emerald-400 font-bold">Copied!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="h-3 w-3" />
                          <span>Copy Commands</span>
                        </>
                      )}
                    </button>
                  </div>
                  <pre className="text-xs font-mono text-teal-300 bg-slate-950/60 p-3 rounded-xl border border-white/[0.04] overflow-x-auto whitespace-pre-wrap leading-relaxed">
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
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/85 backdrop-blur-md animate-in fade-in duration-200">
          <div className="glass-panel w-full max-w-2xl rounded-3xl p-6 sm:p-7 border border-white/[0.12] shadow-2xl space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-white/[0.08]">
              <h3 className="text-base font-extrabold text-white flex items-center space-x-2">
                <GitPullRequest className="h-4 w-4 text-teal-400" />
                <span>GitHub Pull Request Description</span>
              </h3>
              <button
                onClick={() => setActivePrModalBatch(null)}
                className="text-slate-400 hover:text-white p-1 rounded-xl hover:bg-slate-800 transition"
              >
                ✕
              </button>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-300 block">PR Title:</label>
              <input
                type="text"
                readOnly
                value={activePrModalBatch.prTitle}
                className="w-full px-3.5 py-2.5 bg-slate-900 rounded-xl border border-white/[0.08] text-xs font-mono text-teal-300 font-semibold"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-300 block">PR Markdown Body:</label>
              <textarea
                readOnly
                rows={11}
                value={activePrModalBatch.prBody}
                className="w-full p-3.5 bg-slate-900 rounded-xl border border-white/[0.08] text-xs font-mono text-slate-200 resize-none leading-relaxed"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => {
                  navigator.clipboard.writeText(activePrModalBatch.prBody);
                  alert("Pull Request Markdown copied to clipboard!");
                }}
                className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-teal-400 to-emerald-400 hover:from-teal-300 hover:to-emerald-300 text-slate-950 font-extrabold text-xs transition shadow-lg shadow-teal-500/25 cursor-pointer"
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
