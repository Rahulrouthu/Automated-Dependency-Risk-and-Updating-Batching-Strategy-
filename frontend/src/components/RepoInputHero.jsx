import React, { useState } from 'react';
import { Search, GitBranch, Key, Sparkles, ArrowRight, ShieldCheck, Github, CheckCircle, Flame, Layers, Lock } from 'lucide-react';

export default function RepoInputHero({ onAnalyze, isLoading, onOpenPresets, presets }) {
  const [repoUrl, setRepoUrl] = useState('https://github.com/Rahulrouthu/Automated-Dependency-Risk-and-Updating-Batching-Strategy-');
  const [branch, setBranch] = useState('main');
  const [githubToken, setGithubToken] = useState('');
  const [showAdvanced, setShowAdvanced] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!repoUrl.trim()) return;
    onAnalyze(repoUrl.trim(), branch.trim(), githubToken.trim());
  };

  const getPresetIcon = (eco) => {
    if (eco === 'MAVEN') return '☕';
    if (eco === 'NPM') return '⬡';
    if (eco === 'PYTHON') return '🐍';
    return '🛡️';
  };

  return (
    <div className="relative py-12 md:py-20 text-center max-w-5xl mx-auto px-4">
      {/* Decorative Aura Backgrounds */}
      <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[34rem] h-[34rem] bg-gradient-to-tr from-teal-500/15 via-cyan-500/10 to-indigo-500/10 rounded-full blur-3xl pointer-events-none -z-10" />

      {/* Capstone Badge */}
      <div className="inline-flex items-center space-x-2 px-4 py-1.5 rounded-full bg-slate-900/90 border border-teal-500/30 text-xs font-semibold text-teal-300 mb-6 shadow-glow-teal animate-float backdrop-blur-md">
        <Sparkles className="h-3.5 w-3.5 text-teal-400" />
        <span>DevOps Capstone Engineering Platform</span>
      </div>

      {/* Hero Headline */}
      <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-[1.15]">
        Automated Dependency Update{' '}
        <span className="bg-gradient-to-r from-teal-400 via-cyan-300 to-emerald-400 bg-clip-text text-transparent drop-shadow-sm">
          Risk Assessment
        </span>{' '}
        &amp; Batching
      </h1>

      <p className="mt-5 text-sm sm:text-base text-slate-300 max-w-2xl mx-auto font-normal leading-relaxed">
        Deep multi-manifest dependency scanning, automated 5-factor risk scoring, CVE vulnerability correlation with OSV, and intelligent PR batching strategy.
      </p>

      {/* Input Form with Gradient Border */}
      <form onSubmit={handleSubmit} className="mt-9 max-w-3xl mx-auto">
        <div className="relative group">
          <div className="absolute -inset-0.5 bg-gradient-to-r from-teal-500 via-cyan-500 to-indigo-500 rounded-2xl blur-sm opacity-50 group-hover:opacity-100 transition duration-300"></div>
          <div className="relative bg-[#0b101b] p-2 sm:p-2.5 rounded-2xl border border-white/[0.1] shadow-2xl flex flex-col sm:flex-row items-center gap-2">
            <div className="relative flex-1 w-full flex items-center">
              <div className="absolute left-4 text-slate-400">
                <Github className="h-5 w-5 group-focus-within:text-teal-400 transition" />
              </div>
              <input
                type="text"
                value={repoUrl}
                onChange={(e) => setRepoUrl(e.target.value)}
                placeholder="https://github.com/owner/repository"
                required
                disabled={isLoading}
                className="w-full pl-12 pr-4 py-3.5 bg-transparent rounded-xl text-sm text-white placeholder-slate-400 focus:outline-none font-mono selection:bg-teal-500/40"
              />
            </div>

            <button
              type="submit"
              disabled={isLoading || !repoUrl.trim()}
              className="w-full sm:w-auto px-7 py-3.5 bg-gradient-to-r from-teal-400 via-teal-500 to-emerald-500 hover:from-teal-300 hover:to-emerald-400 text-slate-950 font-extrabold text-sm rounded-xl transition-all duration-200 shadow-lg shadow-teal-500/25 flex items-center justify-center space-x-2 shrink-0 disabled:opacity-50 cursor-pointer active:scale-95"
            >
              {isLoading ? (
                <span className="flex items-center space-x-2">
                  <span className="h-4 w-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin" />
                  <span>Analyzing...</span>
                </span>
              ) : (
                <>
                  <span>Analyze Repository</span>
                  <ArrowRight className="h-4 w-4 stroke-[2.5]" />
                </>
              )}
            </button>
          </div>
        </div>

        {/* Quick Presets Pills */}
        <div className="mt-5 flex flex-wrap items-center justify-center gap-2 text-xs">
          <span className="text-slate-400 font-medium">Quick Presets:</span>
          {presets.slice(0, 4).map((p, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => {
                setRepoUrl(p.url);
                onAnalyze(p.url, 'main', '');
              }}
              disabled={isLoading}
              className="px-3 py-1.5 rounded-xl bg-slate-900/90 hover:bg-slate-800 text-slate-200 border border-white/[0.08] hover:border-teal-500/50 transition duration-200 cursor-pointer flex items-center space-x-1.5 shadow-sm"
            >
              <span>{getPresetIcon(p.ecosystem)}</span>
              <span className="font-medium">{p.name}</span>
            </button>
          ))}
          <button
            type="button"
            onClick={onOpenPresets}
            className="text-teal-400 hover:text-teal-300 font-semibold ml-1 transition cursor-pointer"
          >
            View all presets &rarr;
          </button>
        </div>

        {/* Advanced Options Toggle */}
        <div className="mt-4">
          <button
            type="button"
            onClick={() => setShowAdvanced(!showAdvanced)}
            className="text-xs text-slate-400 hover:text-slate-200 transition font-medium flex items-center justify-center gap-1 mx-auto cursor-pointer"
          >
            <span>{showAdvanced ? '▴ Hide advanced options' : '▾ Advanced scan options (branch, token)'}</span>
          </button>
        </div>

        {showAdvanced && (
          <div className="mt-3 p-4 glass-panel rounded-2xl border border-white/[0.08] grid grid-cols-1 sm:grid-cols-2 gap-3.5 text-left animate-in fade-in duration-200">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5 flex items-center space-x-1.5">
                <GitBranch className="h-3.5 w-3.5 text-teal-400" />
                <span>Target Branch</span>
              </label>
              <input
                type="text"
                value={branch}
                onChange={(e) => setBranch(e.target.value)}
                placeholder="main or master"
                className="w-full px-3.5 py-2 bg-slate-900/90 rounded-xl border border-slate-700/80 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5 flex items-center space-x-1.5">
                <Key className="h-3.5 w-3.5 text-teal-400" />
                <span>GitHub Token (Optional for Private Repos)</span>
              </label>
              <input
                type="password"
                value={githubToken}
                onChange={(e) => setGithubToken(e.target.value)}
                placeholder="ghp_xxxxxxxxxxxx"
                className="w-full px-3.5 py-2 bg-slate-900/90 rounded-xl border border-slate-700/80 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono"
              />
            </div>
          </div>
        )}
      </form>
    </div>
  );
}
