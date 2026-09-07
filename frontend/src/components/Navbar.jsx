import React from 'react';
import { ShieldCheck, Sparkles, BookOpen, Activity, Cpu } from 'lucide-react';

export default function Navbar({ onOpenPresets, onNewScan }) {
  return (
    <header className="sticky top-0 z-30 w-full border-b border-white/[0.08] bg-[#080c15]/80 backdrop-blur-xl transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand Logo & Title */}
        <div className="flex items-center space-x-3.5 cursor-pointer group" onClick={onNewScan}>
          <div className="relative">
            <div className="absolute -inset-1 bg-gradient-to-r from-teal-500 to-cyan-500 rounded-xl blur opacity-40 group-hover:opacity-80 transition duration-300"></div>
            <div className="relative h-10 w-10 rounded-xl bg-gradient-to-tr from-teal-500 via-emerald-400 to-cyan-400 flex items-center justify-center shadow-lg shadow-teal-500/25">
              <ShieldCheck className="h-5 w-5 text-slate-950 stroke-[2.5]" />
            </div>
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="font-extrabold text-lg tracking-tight text-white group-hover:text-teal-300 transition">
                DepRisk
              </span>
              <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-teal-500/10 text-teal-300 border border-teal-500/25 shadow-inner">
                Enterprise DevOps
              </span>
            </div>
            <p className="text-[11px] text-slate-400 font-medium tracking-wide">
              Automated Dependency Risk &amp; Batching Platform
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center space-x-3">
          {/* Live Engine Status Pill */}
          <div className="hidden md:flex items-center space-x-2 px-3 py-1.5 rounded-full bg-slate-900/80 border border-white/[0.06] text-xs font-medium text-slate-300">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-teal-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-teal-500"></span>
            </span>
            <span className="font-mono text-[11px] text-slate-400">OSV &bull; SemVer 3.0</span>
          </div>

          <button
            onClick={onOpenPresets}
            className="flex items-center space-x-1.5 text-xs font-semibold px-3.5 py-1.5 rounded-xl bg-slate-800/80 hover:bg-slate-700/80 text-white border border-white/[0.1] hover:border-teal-500/50 shadow-sm hover:shadow-glow-teal transition duration-200 cursor-pointer"
          >
            <Sparkles className="h-3.5 w-3.5 text-teal-400" />
            <span>Preset Repositories</span>
          </button>
        </div>
      </div>
    </header>
  );
}
