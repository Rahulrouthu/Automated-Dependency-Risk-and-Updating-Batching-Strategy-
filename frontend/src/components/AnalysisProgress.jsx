import React, { useEffect, useState } from 'react';
import { Loader2, CheckCircle2, ShieldAlert, Cpu, GitPullRequest, Search } from 'lucide-react';

const STEPS = [
  { label: 'Validating GitHub Repository & Owner', icon: Search },
  { label: 'Scanning Manifests (pom.xml, package.json, requirements.txt)', icon: Cpu },
  { label: 'Extracting Direct & Transitive Dependencies', icon: Cpu },
  { label: 'Querying Package Registries for Available Updates', icon: Search },
  { label: 'Live OSV Security & CVE Advisory Scanning', icon: ShieldAlert },
  { label: 'Calculating Explainable 5-Factor Risk Scores (0-100)', icon: ShieldAlert },
  { label: 'Generating Safe Dependency Batching Strategy', icon: GitPullRequest },
];

export default function AnalysisProgress({ isLoading }) {
  const [currentStep, setCurrentStep] = useState(0);

  useEffect(() => {
    if (!isLoading) {
      setCurrentStep(0);
      return;
    }

    const interval = setInterval(() => {
      setCurrentStep((prev) => (prev < STEPS.length - 1 ? prev + 1 : prev));
    }, 750);

    return () => clearInterval(interval);
  }, [isLoading]);

  if (!isLoading) return null;

  return (
    <div className="w-full max-w-4xl mx-auto my-8 p-6 glass-panel rounded-2xl border border-teal-500/30 shadow-2xl animate-in fade-in">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-3">
          <Loader2 className="h-6 w-6 text-teal-400 animate-spin" />
          <h3 className="text-base font-bold text-white">Analyzing Repository Dependencies...</h3>
        </div>
        <span className="text-xs font-mono text-teal-400 font-semibold">
          Step {currentStep + 1} of {STEPS.length}
        </span>
      </div>

      {/* Progress Bar */}
      <div className="w-full bg-slate-800 rounded-full h-2 mb-6 overflow-hidden">
        <div
          className="bg-gradient-to-r from-teal-500 to-emerald-400 h-2 rounded-full transition-all duration-500"
          style={{ width: `${((currentStep + 1) / STEPS.length) * 100}%` }}
        />
      </div>

      {/* Stepper list */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-xs">
        {STEPS.map((step, idx) => {
          const isDone = idx < currentStep;
          const isCurrent = idx === currentStep;

          return (
            <div
              key={idx}
              className={`flex items-center space-x-2.5 p-2 rounded-lg transition ${
                isCurrent
                  ? 'bg-teal-500/10 text-teal-300 border border-teal-500/30'
                  : isDone
                  ? 'text-emerald-400 opacity-80'
                  : 'text-slate-400'
              }`}
            >
              {isDone ? (
                <CheckCircle2 className="h-4 w-4 text-emerald-400 shrink-0" />
              ) : isCurrent ? (
                <Loader2 className="h-4 w-4 text-teal-400 animate-spin shrink-0" />
              ) : (
                <div className="h-4 w-4 rounded-full border border-slate-700 shrink-0" />
              )}
              <span className="truncate">{step.label}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
