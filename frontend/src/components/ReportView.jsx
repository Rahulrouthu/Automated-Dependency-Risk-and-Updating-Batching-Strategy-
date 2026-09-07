import React from 'react';
import { FileText, Download, Printer, ShieldCheck, CheckCircle2, AlertTriangle, ArrowRight, Award } from 'lucide-react';
import { downloadMarkdownReport, downloadJson } from '../utils/exportUtils';
import { RiskBadge, DataSourceBadge, ConfidenceBadge } from './Badges';

export default function ReportView({ report, rawResult }) {
  if (!report) return null;

  return (
    <div className="space-y-6">
      {/* Action Bar */}
      <div className="glass-panel p-5 rounded-3xl border border-white/[0.08] flex flex-wrap items-center justify-between gap-4">
        <div>
          <h3 className="text-base font-extrabold text-white tracking-tight">Executive Dependency Risk Audit Report</h3>
          <p className="text-xs text-slate-400 mt-0.5 font-medium">Generated on {new Date(report.generatedAt).toLocaleString()}</p>
        </div>

        <div className="flex items-center space-x-2.5">
          <button
            onClick={() => downloadMarkdownReport(report)}
            className="flex items-center space-x-2 text-xs px-4 py-2 rounded-xl bg-gradient-to-r from-teal-400 to-emerald-400 hover:from-teal-300 hover:to-emerald-300 text-slate-950 font-extrabold transition shadow-lg shadow-teal-500/25 cursor-pointer"
          >
            <Download className="h-3.5 w-3.5 stroke-[2.5]" />
            <span>Export Markdown</span>
          </button>

          <button
            onClick={() => downloadJson(rawResult, `${report.repositoryName || 'report'}-analysis.json`)}
            className="flex items-center space-x-2 text-xs px-4 py-2 rounded-xl bg-slate-900/90 hover:bg-slate-800 text-slate-200 border border-white/[0.1] hover:border-teal-500/40 font-semibold transition cursor-pointer"
          >
            <FileText className="h-3.5 w-3.5 text-teal-400" />
            <span>Export JSON</span>
          </button>
        </div>
      </div>

      {/* Report Document Box */}
      <div className="glass-panel p-8 sm:p-10 rounded-3xl border border-white/[0.08] space-y-8 print:p-0 print:border-none">
        {/* Header Section */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 pb-6 border-b border-white/[0.08]">
          <div>
            <div className="flex flex-wrap items-center gap-2 mb-2">
              <span className="text-xs uppercase font-mono font-bold tracking-wider text-teal-400">DevOps Audit Summary</span>
              <DataSourceBadge source={report.dataSource} />
              <ConfidenceBadge level={report.analysisConfidence} score={report.analysisConfidenceScore} />
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-white tracking-tight">{report.repositoryName || report.repositoryUrl}</h2>
            <p className="text-xs text-slate-400 font-mono mt-1">{report.repositoryUrl}</p>
          </div>

          <div className="flex items-center space-x-3.5">
            <div className="text-center p-4 rounded-3xl bg-slate-900/90 border border-teal-500/30 shadow-glow-teal min-w-[110px]">
              <div className="text-[11px] text-slate-400 font-bold uppercase tracking-wider">Health Grade</div>
              <div className="text-3xl sm:text-4xl font-black text-teal-300 mt-1">{report.healthGrade}</div>
              <div className="text-[10px] text-slate-400 font-mono font-bold mt-0.5">{report.healthGradeScore}/100</div>
            </div>

            <div className="text-center p-4 rounded-3xl bg-slate-900/90 border border-white/[0.08] min-w-[110px]">
              <div className="text-[11px] text-slate-400 font-bold uppercase tracking-wider">Risk Status</div>
              <div className="mt-2.5">
                <RiskBadge level={report.overallRisk} />
              </div>
            </div>
          </div>
        </div>

        {/* Executive Summary Bullets */}
        <div className="space-y-3.5">
          <h3 className="text-xs font-bold uppercase tracking-widest text-slate-400">Executive Summary</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
            {report.executiveSummaryBullets?.map((bullet, idx) => (
              <div key={idx} className="p-4 rounded-2xl bg-[#090d16]/90 border border-white/[0.06] flex items-start space-x-3 text-xs text-slate-200">
                <CheckCircle2 className="h-4 w-4 text-teal-400 shrink-0 mt-0.5" />
                <span className="leading-relaxed font-medium">{bullet}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Prioritized Remediation Roadmap */}
        <div className="space-y-3.5">
          <h3 className="text-xs font-bold uppercase tracking-widest text-slate-400">Prioritized Remediation Roadmap</h3>
          <div className="space-y-2.5">
            {report.prioritizedRemediationRoadmap?.map((item, idx) => (
              <div key={idx} className="p-3.5 rounded-2xl bg-[#090d16]/90 border border-white/[0.06] flex items-center space-x-3.5 text-xs text-slate-200 font-mono">
                <span className="h-7 w-7 rounded-xl bg-teal-500/15 text-teal-300 border border-teal-500/30 flex items-center justify-center font-black text-xs shrink-0 shadow-inner">
                  {idx + 1}
                </span>
                <span className="font-semibold">{item}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Proposed Batch Execution Strategy */}
        <div className="space-y-3.5">
          <h3 className="text-xs font-bold uppercase tracking-widest text-slate-400">Proposed Batches Summary</h3>
          <div className="space-y-3">
            {report.proposedBatches?.map((batch) => (
              <div key={batch.batchNumber} className="p-4.5 rounded-2xl bg-[#090d16]/90 border border-white/[0.06] flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                <div>
                  <div className="font-bold text-white font-mono text-xs">{batch.title}</div>
                  <p className="text-slate-400 text-[11px] mt-0.5">{batch.strategyDescription}</p>
                </div>
                <div className="flex items-center space-x-2.5 shrink-0">
                  <RiskBadge level={batch.batchRiskLevel} score={batch.batchRiskScore} />
                  <span className="text-[11px] font-mono text-slate-400 px-2 py-0.5 rounded-lg bg-slate-900 border border-white/[0.06]">
                    {batch.dependencies.length} packages
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
