import React from 'react';
import { FileText, Download, Printer, ShieldCheck, CheckCircle2, AlertTriangle, ArrowRight } from 'lucide-react';
import { downloadMarkdownReport, downloadJson } from '../utils/exportUtils';
import { RiskBadge, DataSourceBadge, ConfidenceBadge } from './Badges';

export default function ReportView({ report, rawResult }) {
  if (!report) return null;

  return (
    <div className="space-y-6">
      {/* Action Bar */}
      <div className="glass-panel p-4 rounded-2xl border border-slate-800 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h3 className="text-base font-bold text-white">Repository Dependency Audit Report</h3>
          <p className="text-xs text-slate-400">Generated on {new Date(report.generatedAt).toLocaleString()}</p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={() => downloadMarkdownReport(report)}
            className="flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold transition shadow-md cursor-pointer"
          >
            <Download className="h-3.5 w-3.5" />
            <span>Export Markdown</span>
          </button>

          <button
            onClick={() => downloadJson(rawResult, `${report.repositoryName || 'report'}-analysis.json`)}
            className="flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition cursor-pointer"
          >
            <FileText className="h-3.5 w-3.5 text-teal-400" />
            <span>Export JSON</span>
          </button>
        </div>
      </div>

      {/* Report Document Box */}
      <div className="glass-panel p-8 rounded-2xl border border-slate-800 space-y-8 print:p-0 print:border-none">
        {/* Header Section */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-800">
          <div>
            <div className="flex flex-wrap items-center gap-2 mb-1.5">
              <span className="text-xs uppercase font-mono tracking-wider text-teal-400">DevOps Capstone Audit Report</span>
              <DataSourceBadge source={report.dataSource} />
              <ConfidenceBadge level={report.analysisConfidence} score={report.analysisConfidenceScore} />
            </div>
            <h2 className="text-2xl font-black text-white mt-1">{report.repositoryName || report.repositoryUrl}</h2>
            <p className="text-xs text-slate-400 font-mono mt-0.5">{report.repositoryUrl}</p>
          </div>

          <div className="flex items-center space-x-4">
            <div className="text-center p-3 rounded-2xl bg-slate-900 border border-slate-800 min-w-[100px]">
              <div className="text-xs text-slate-400 font-medium">Health Grade</div>
              <div className="text-3xl font-black text-teal-400 mt-0.5">{report.healthGrade}</div>
              <div className="text-[10px] text-slate-400 font-mono">{report.healthGradeScore}/100</div>
            </div>

            <div className="text-center p-3 rounded-2xl bg-slate-900 border border-slate-800 min-w-[100px]">
              <div className="text-xs text-slate-400 font-medium">Risk Status</div>
              <div className="mt-1.5">
                <RiskBadge level={report.overallRisk} />
              </div>
            </div>
          </div>
        </div>

        {/* Executive Summary Bullets */}
        <div className="space-y-3">
          <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400">Executive Summary</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {report.executiveSummaryBullets?.map((bullet, idx) => (
              <div key={idx} className="p-3.5 rounded-xl bg-slate-900/80 border border-slate-800 flex items-start space-x-2.5 text-xs text-slate-200">
                <CheckCircle2 className="h-4 w-4 text-teal-400 shrink-0 mt-0.5" />
                <span className="leading-relaxed">{bullet}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Prioritized Remediation Roadmap */}
        <div className="space-y-3">
          <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400">Prioritized Remediation Roadmap</h3>
          <div className="space-y-2">
            {report.prioritizedRemediationRoadmap?.map((item, idx) => (
              <div key={idx} className="p-3 rounded-xl bg-slate-900/80 border border-slate-800 flex items-center space-x-3 text-xs text-slate-200 font-mono">
                <span className="h-6 w-6 rounded-full bg-teal-500/10 text-teal-400 flex items-center justify-center font-bold text-xs shrink-0">
                  {idx + 1}
                </span>
                <span>{item}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Proposed Batch Execution Strategy */}
        <div className="space-y-3">
          <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400">Proposed Batches Summary</h3>
          <div className="space-y-3">
            {report.proposedBatches?.map((batch) => (
              <div key={batch.batchNumber} className="p-4 rounded-xl bg-slate-900/80 border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                <div>
                  <div className="font-bold text-white font-mono">{batch.title}</div>
                  <p className="text-slate-400 text-[11px] mt-0.5">{batch.strategyDescription}</p>
                </div>
                <div className="flex items-center space-x-2 shrink-0">
                  <RiskBadge level={batch.batchRiskLevel} score={batch.batchRiskScore} />
                  <span className="text-[11px] font-mono text-slate-400">{batch.dependencies.length} packages</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
