import React, { useState } from 'react';
import { Search, GitBranch, Key, Sparkles, ArrowRight, ShieldCheck, Github } from 'lucide-react';

export default function RepoInputHero({ onAnalyze, isLoading, onOpenPresets, presets }) {
  const [repoUrl, setRepoUrl] = useState('https://github.com/spring-projects/spring-petclinic');
  const [branch, setBranch] = useState('main');
  const [githubToken, setGithubToken] = useState('');
  const [showAdvanced, setShowAdvanced] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!repoUrl.trim()) return;
    onAnalyze(repoUrl.trim(), branch.trim(), githubToken.trim());
  };

  return (
    <div className="relative py-12 md:py-16 text-center max-w-5xl mx-auto px-4">
      {/* Decorative Glow */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl pointer-events-none" />

      {/* Capstone Badge */}
      <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-slate-900/90 border border-teal-500/30 text-xs font-semibold text-teal-400 mb-6 shadow-inner">
        <Sparkles className="h-3.5 w-3.5" />
        <span>DevOps Capstone Engineering Project</span>
      </div>

      <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white max-w-3xl mx-auto leading-tight">
        Automated Dependency Update <span className="bg-gradient-to-r from-teal-400 to-emerald-300 bg-clip-text text-transparent">Risk Assessment</span> &amp; Batching
      </h1>

      <p className="mt-4 text-sm sm:text-base text-slate-300 max-w-2xl mx-auto">
        Enter a public or private GitHub repository to evaluate dependency vulnerabilities, quantify 5-factor risk scores, and generate intelligent, non-breaking update batches.
      </p>

      {/* Input Form */}
      <form onSubmit={handleSubmit} className="mt-8 max-w-3xl mx-auto">
        <div className="glass-panel p-2 sm:p-2.5 rounded-2xl border border-slate-700/80 shadow-2xl flex flex-col sm:flex-row items-center gap-2">
          <div className="relative flex-1 w-full flex items-center">
            <div className="absolute left-3.5 text-slate-400">
              <Github className="h-5 w-5" />
            </div>
            <input
              type="text"
              value={repoUrl}
              onChange={(e) => setRepoUrl(e.target.value)}
              placeholder="https://github.com/owner/repository"
              required
              disabled={isLoading}
              className="w-full pl-11 pr-4 py-3 bg-slate-900/80 rounded-xl border border-slate-800 text-sm text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono transition"
            />
          </div>

          <button
            type="submit"
            disabled={isLoading || !repoUrl.trim()}
            className="w-full sm:w-auto px-6 py-3 bg-gradient-to-r from-teal-500 to-emerald-500 hover:from-teal-400 hover:to-emerald-400 text-slate-950 font-bold text-sm rounded-xl transition shadow-lg shadow-teal-500/25 flex items-center justify-center space-x-2 shrink-0 disabled:opacity-50 cursor-pointer"
          >
            {isLoading ? (
              <span>Analyzing...</span>
            ) : (
              <>
                <span>Analyze Repository</span>
                <ArrowRight className="h-4 w-4" />
              </>
            )}
          </button>
        </div>

        {/* Quick Presets Pills */}
        <div className="mt-4 flex flex-wrap items-center justify-center gap-2 text-xs">
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
              className="px-2.5 py-1 rounded-lg bg-slate-900/70 hover:bg-slate-800 text-slate-300 border border-slate-800 hover:border-teal-500/40 transition cursor-pointer flex items-center space-x-1"
            >
              <span>{p.name}</span>
            </button>
          ))}
          <button
            type="button"
            onClick={onOpenPresets}
            className="text-teal-400 hover:underline font-medium ml-1"
          >
            View all presets &rarr;
          </button>
        </div>

        {/* Advanced Options Toggle */}
        <div className="mt-3">
          <button
            type="button"
            onClick={() => setShowAdvanced(!showAdvanced)}
            className="text-xs text-slate-400 hover:text-slate-200 transition underline cursor-pointer"
          >
            {showAdvanced ? 'Hide advanced scan options' : 'Advanced scan options (branch, auth token)'}
          </button>
        </div>

        {showAdvanced && (
          <div className="mt-3 p-4 glass-panel rounded-xl border border-slate-800 grid grid-cols-1 sm:grid-cols-2 gap-3 text-left animate-in fade-in">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1 flex items-center space-x-1">
                <GitBranch className="h-3.5 w-3.5 text-teal-400" />
                <span>Target Branch</span>
              </label>
              <input
                type="text"
                value={branch}
                onChange={(e) => setBranch(e.target.value)}
                placeholder="main or master"
                className="w-full px-3 py-1.5 bg-slate-900 rounded-lg border border-slate-700 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1 flex items-center space-x-1">
                <Key className="h-3.5 w-3.5 text-teal-400" />
                <span>GitHub Personal Access Token (Optional)</span>
              </label>
              <input
                type="password"
                value={githubToken}
                onChange={(e) => setGithubToken(e.target.value)}
                placeholder="ghp_xxxxxxxxxxxx"
                className="w-full px-3 py-1.5 bg-slate-900 rounded-lg border border-slate-700 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono"
              />
            </div>
          </div>
        )}
      </form>
    </div>
  );
}
