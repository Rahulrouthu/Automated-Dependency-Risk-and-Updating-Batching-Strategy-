import React from 'react';
import { X, Sparkles, ArrowRight, ExternalLink } from 'lucide-react';
import { EcosystemBadge } from './Badges';

export default function PresetReposModal({ isOpen, onClose, presets, onSelectPreset }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-in fade-in">
      <div className="glass-panel w-full max-w-2xl rounded-2xl p-6 border border-slate-700 shadow-2xl relative">
        <div className="flex items-center justify-between pb-4 border-b border-slate-800">
          <div className="flex items-center space-x-2">
            <Sparkles className="h-5 w-5 text-teal-400" />
            <h3 className="text-lg font-bold text-white">Preset Reference Repositories</h3>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800">
            <X className="h-5 w-5" />
          </button>
        </div>

        <p className="text-sm text-slate-300 mt-3 mb-4">
          Select any benchmark open-source repository or curated test suite to instantly run the analysis pipeline:
        </p>

        <div className="space-y-3 max-h-[60vh] overflow-y-auto pr-1">
          {presets.map((preset, index) => (
            <div
              key={index}
              onClick={() => {
                onSelectPreset(preset.url);
                onClose();
              }}
              className="glass-panel-hover p-4 rounded-xl border border-slate-800 bg-slate-900/60 cursor-pointer flex items-center justify-between group"
            >
              <div className="space-y-1.5 flex-1 pr-4">
                <div className="flex items-center space-x-2">
                  <span className="font-semibold text-white group-hover:text-teal-400 transition">{preset.name}</span>
                  <EcosystemBadge ecosystem={preset.ecosystem} />
                  <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-800 text-teal-300 border border-slate-700">
                    {preset.tag}
                  </span>
                </div>
                <p className="text-xs text-slate-400">{preset.description}</p>
                <div className="text-[11px] font-mono text-slate-400 flex items-center space-x-1">
                  <span>{preset.url}</span>
                </div>
              </div>

              <div className="h-9 w-9 rounded-lg bg-teal-500/10 text-teal-400 flex items-center justify-center group-hover:bg-teal-500 group-hover:text-slate-950 transition">
                <ArrowRight className="h-4 w-4" />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
