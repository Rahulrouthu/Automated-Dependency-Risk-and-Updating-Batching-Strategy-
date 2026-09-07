import React from 'react';
import { ShieldCheck, GitBranch, Terminal, Sparkles, BookOpen } from 'lucide-react';

export default function Navbar({ onOpenPresets, onNewScan }) {
  return (
    <header className="sticky top-0 z-30 w-full border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-3 cursor-pointer" onClick={onNewScan}>
          <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-teal-500 to-emerald-400 flex items-center justify-center shadow-lg shadow-teal-500/20">
            <ShieldCheck className="h-6 w-6 text-slate-950 stroke-[2.5]" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="font-bold text-lg tracking-tight text-white">DepRisk</span>
              <span className="text-[10px] uppercase font-semibold px-2 py-0.5 rounded-full bg-teal-500/10 text-teal-400 border border-teal-500/20">
                Capstone DevOps
              </span>
            </div>
            <p className="text-xs text-slate-400">Automated Risk Assessment &amp; Batching Strategy</p>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <button
            onClick={onOpenPresets}
            className="flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-200 border border-slate-700 transition"
          >
            <Sparkles className="h-3.5 w-3.5 text-teal-400" />
            <span>Preset Repositories</span>
          </button>

          <a
            href="#architecture"
            onClick={(e) => {
              e.preventDefault();
              alert("System Architecture: Spring Boot 3 + React 18 + OSV Security + SemVer Engine + Graph Partitioning Optimizer");
            }}
            className="flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition"
          >
            <BookOpen className="h-3.5 w-3.5" />
            <span>Documentation</span>
          </a>
        </div>
      </div>
    </header>
  );
}
