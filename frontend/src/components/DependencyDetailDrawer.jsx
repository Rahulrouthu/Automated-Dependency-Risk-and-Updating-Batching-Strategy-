import React from 'react';
import { X, ExternalLink, ShieldAlert, BookOpen, GitFork, AlertCircle, FileText, CheckCircle2, Lock, ShieldCheck, Sparkles, Cpu } from 'lucide-react';
import { RiskBadge, VersionBadge, EcosystemBadge, SecurityPriorityBadge, LockfileBadge, DataSourceBadge } from './Badges';

export default function DependencyDetailDrawer({ dependency, onClose }) {
  if (!dependency) return null;

  const { riskAssessment, links, vulnerabilities } = dependency;
  const contributions = riskAssessment?.factorContributions || [];

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="absolute inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-xl bg-[#090d16] border-l border-white/[0.1] shadow-2xl p-7 overflow-y-auto space-y-6">
          {/* Drawer Header */}
          <div className="flex items-start justify-between pb-5 border-b border-white/[0.08]">
            <div className="space-y-1.5">
              <div className="flex flex-wrap items-center gap-2">
                <EcosystemBadge ecosystem={dependency.ecosystem} />
                <span className="text-xs text-slate-400 font-mono px-2 py-0.5 rounded-md bg-slate-900 border border-white/[0.06]">
                  {dependency.manifestFile}
                </span>
                <DataSourceBadge source={dependency.dataSource} />
              </div>
              <h2 className="text-xl sm:text-2xl font-black text-white font-mono tracking-tight">{dependency.coordinates}</h2>
              <div className="flex flex-wrap items-center gap-2 pt-1 text-xs font-mono">
                <span className="text-slate-400">
                  Declared: <b className="text-slate-200">{dependency.declaredVersionRange || dependency.currentVersion}</b>
                </span>
                <span className="text-slate-400">
                  Resolved: <b className="text-cyan-300">{dependency.resolvedVersion || dependency.currentVersion}</b>
                </span>
                <LockfileBadge source={dependency.lockfileSource} />
                <span className="text-slate-400">&rarr;</span>
                <span className="text-slate-400">
                  Target: <b className="text-teal-300 font-bold">{dependency.recommendedTargetVersion || dependency.latestVersion}</b>
                </span>
                <VersionBadge type={dependency.versionDiffType} />
              </div>
            </div>

            <button
              onClick={onClose}
              className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition cursor-pointer"
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          {/* Risk Level & Score Hero Box */}
          <div className="glass-panel p-6 rounded-3xl border border-white/[0.08] space-y-4">
            <div className="grid grid-cols-2 gap-4 pb-3 border-b border-white/[0.06]">
              <div>
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">Composite Risk Score</span>
                <div className="flex items-baseline space-x-2 mt-1">
                  <span className="text-3xl font-black text-white font-mono">{riskAssessment?.totalScore || 0}</span>
                  <span className="text-xs text-slate-400 font-mono">/ 100</span>
                </div>
                <div className="mt-1.5">
                  <RiskBadge level={riskAssessment?.riskLevel} />
                </div>
              </div>

              <div>
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">Security Priority</span>
                <div className="mt-1.5">
                  <SecurityPriorityBadge priority={dependency.securityPriority} />
                </div>
                <div className="text-[11px] text-slate-400 font-mono mt-1.5">
                  Update Risk: <b className="text-slate-200">{riskAssessment?.updateRiskScore || 0}/100</b> ({riskAssessment?.updateRiskLevel})
                </div>
              </div>
            </div>

            {/* Recommendation Box */}
            <div className="p-4 rounded-2xl bg-[#060911] border border-white/[0.06] text-xs">
              <span className="font-extrabold text-teal-400 block mb-1">Recommended Strategy:</span>
              <p className="text-slate-300 leading-relaxed font-normal">{riskAssessment?.recommendation || dependency.recommendedAction}</p>
            </div>
          </div>

          {/* Factor Contribution Breakdown */}
          <div className="glass-panel p-6 rounded-3xl border border-white/[0.08] space-y-4">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">Mathematical Factor Contributions</h4>
            
            {contributions.length > 0 ? (
              <div className="space-y-3.5 text-xs">
                {contributions.map((c, i) => (
                  <div key={i} className="space-y-1.5">
                    <div className="flex justify-between text-slate-200 font-medium">
                      <span>{c.factorName}</span>
                      <span className="font-mono font-bold text-teal-300">
                        {c.score} pts ({c.contributionPercentage}%)
                      </span>
                    </div>
                    <div className="w-full bg-slate-900 h-2 rounded-full overflow-hidden border border-white/[0.04]">
                      <div 
                        className="bg-gradient-to-r from-teal-500 to-cyan-400 h-full rounded-full transition-all duration-500" 
                        style={{ width: `${Math.min(100, c.contributionPercentage)}%` }} 
                      />
                    </div>
                    <div className="text-[10px] text-slate-400 font-mono">{c.description}</div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="space-y-3 text-xs">
                <div>
                  <div className="flex justify-between text-slate-200 mb-1 font-medium">
                    <span>Version Change Risk</span>
                    <span className="font-mono font-bold text-teal-300">{riskAssessment?.versionRisk} pts</span>
                  </div>
                  <div className="w-full bg-slate-900 h-2 rounded-full overflow-hidden border border-white/[0.04]">
                    <div className="bg-teal-500 h-full rounded-full" style={{ width: `${((riskAssessment?.versionRisk || 0) / 25) * 100}%` }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-slate-200 mb-1 font-medium">
                    <span>Security Vulnerability Risk</span>
                    <span className="font-mono font-bold text-rose-400">{riskAssessment?.securityRisk} pts</span>
                  </div>
                  <div className="w-full bg-slate-900 h-2 rounded-full overflow-hidden border border-white/[0.04]">
                    <div className="bg-rose-500 h-full rounded-full" style={{ width: `${((riskAssessment?.securityRisk || 0) / 55) * 100}%` }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-slate-200 mb-1 font-medium">
                    <span>Compatibility Risk</span>
                    <span className="font-mono font-bold text-amber-400">{riskAssessment?.compatibilityRisk} pts</span>
                  </div>
                  <div className="w-full bg-slate-900 h-2 rounded-full overflow-hidden border border-white/[0.04]">
                    <div className="bg-amber-500 h-full rounded-full" style={{ width: `${((riskAssessment?.compatibilityRisk || 0) / 20) * 100}%` }} />
                  </div>
                </div>
              </div>
            )}

            {/* Additive Reasons Explanation */}
            <div className="pt-3 border-t border-white/[0.06] space-y-1.5">
              <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider block">Explainability Reasons:</span>
              <ul className="space-y-1.5 text-xs text-slate-300">
                {riskAssessment?.reasons?.map((reason, idx) => (
                  <li key={idx} className="flex items-start space-x-2 font-mono text-[11px]">
                    <span className="text-teal-400 font-bold">&bull;</span>
                    <span>{reason}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Security Advisories (If any) */}
          {vulnerabilities && vulnerabilities.length > 0 && (
            <div className="glass-panel p-6 rounded-3xl border border-rose-500/40 bg-rose-950/20 space-y-3.5 shadow-glow-rose">
              <h4 className="text-xs font-bold uppercase tracking-wider text-rose-300 flex items-center space-x-2">
                <ShieldAlert className="h-4 w-4 text-rose-400" />
                <span>Known Vulnerabilities ({vulnerabilities.length})</span>
              </h4>

              <div className="space-y-3">
                {vulnerabilities.map((vuln, idx) => (
                  <div key={idx} className="p-3.5 bg-slate-900/90 rounded-2xl border border-rose-900/40 text-xs space-y-1.5">
                    <div className="flex items-center justify-between">
                      <span className="font-mono font-bold text-rose-300">{vuln.vulnId}</span>
                      <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase bg-rose-500/20 text-rose-300 border border-rose-500/30">
                        {vuln.severity} (CVSS {vuln.cvssScore})
                      </span>
                    </div>
                    <p className="text-slate-300 leading-relaxed">{vuln.summary}</p>
                    <div className="flex items-center justify-between text-[11px] text-slate-400 pt-1 font-mono">
                      <span>Affected: {vuln.affectedRange || 'All previous'}</span>
                      <span className="text-emerald-400 font-bold">Fixed in: {vuln.fixedVersion || 'Latest'}</span>
                    </div>
                    {vuln.referenceUrl && (
                      <a
                        href={vuln.referenceUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center space-x-1 text-teal-400 hover:text-teal-300 font-semibold pt-1 text-[11px] transition"
                      >
                        <span>View Security Advisory on OSV</span>
                        <ExternalLink className="h-3 w-3" />
                      </a>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Authoritative Resource Links */}
          <div className="glass-panel p-6 rounded-3xl border border-white/[0.08] space-y-3.5">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">Authoritative Links &amp; References</h4>
            <div className="grid grid-cols-2 gap-2.5 text-xs">
              {links?.repositoryUrl && (
                <a
                  href={links.repositoryUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center space-x-2 p-3 rounded-2xl bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-white/[0.06] hover:border-teal-500/40 transition font-medium"
                >
                  <GitFork className="h-4 w-4 text-teal-400 shrink-0" />
                  <span className="truncate">GitHub Repo</span>
                </a>
              )}
              {links?.packageRegistryUrl && (
                <a
                  href={links.packageRegistryUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center space-x-2 p-3 rounded-2xl bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-white/[0.06] hover:border-teal-500/40 transition font-medium"
                >
                  <BookOpen className="h-4 w-4 text-teal-400 shrink-0" />
                  <span className="truncate">Package Registry</span>
                </a>
              )}
              {links?.documentationUrl && (
                <a
                  href={links.documentationUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center space-x-2 p-3 rounded-2xl bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-white/[0.06] hover:border-teal-500/40 transition font-medium"
                >
                  <FileText className="h-4 w-4 text-sky-400 shrink-0" />
                  <span className="truncate">Documentation</span>
                </a>
              )}
              {links?.changelogUrl && (
                <a
                  href={links.changelogUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center space-x-2 p-3 rounded-2xl bg-slate-900/80 hover:bg-slate-800 text-slate-200 border border-white/[0.06] hover:border-teal-500/40 transition font-medium"
                >
                  <AlertCircle className="h-4 w-4 text-amber-400 shrink-0" />
                  <span className="truncate">Changelog</span>
                </a>
              )}
              {links?.migrationGuideUrl && (
                <a
                  href={links.migrationGuideUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="col-span-2 flex items-center space-x-2 p-3 rounded-2xl bg-amber-950/30 hover:bg-amber-900/40 text-amber-300 border border-amber-500/30 transition font-medium"
                >
                  <BookOpen className="h-4 w-4 text-amber-400 shrink-0" />
                  <span className="truncate">Official Migration &amp; Upgrade Guide</span>
                  <ExternalLink className="h-3.5 w-3.5 ml-auto shrink-0" />
                </a>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
