import React, { useState, useMemo } from 'react';
import { Search, Filter, ArrowUpDown, ExternalLink, ShieldAlert, ChevronRight, Lock, Sparkles, Layers } from 'lucide-react';
import { RiskBadge, VersionBadge, EcosystemBadge, SecurityPriorityBadge, LockfileBadge } from './Badges';

export default function DependencyTable({ dependencies, onSelectDependency }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [riskFilter, setRiskFilter] = useState('ALL');
  const [ecoFilter, setEcoFilter] = useState('ALL');
  const [diffFilter, setDiffFilter] = useState('ALL');
  const [securityFilter, setSecurityFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('RISK_DESC');

  const filteredDependencies = useMemo(() => {
    return dependencies.filter((dep) => {
      // Search
      const matchSearch =
        dep.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (dep.groupOrNamespace && dep.groupOrNamespace.toLowerCase().includes(searchTerm.toLowerCase())) ||
        dep.manifestFile.toLowerCase().includes(searchTerm.toLowerCase());

      if (!matchSearch) return false;

      // Risk filter
      if (riskFilter !== 'ALL' && dep.riskAssessment?.riskLevel !== riskFilter) {
        return false;
      }

      // Security filter
      if (securityFilter !== 'ALL' && dep.securityPriority !== securityFilter) {
        return false;
      }

      // Ecosystem filter
      if (ecoFilter !== 'ALL' && dep.ecosystem !== ecoFilter) {
        return false;
      }

      // Diff filter
      if (diffFilter !== 'ALL' && dep.versionDiffType !== diffFilter) {
        return false;
      }

      return true;
    }).sort((a, b) => {
      if (sortBy === 'RISK_DESC') {
        return (b.riskAssessment?.totalScore || 0) - (a.riskAssessment?.totalScore || 0);
      }
      if (sortBy === 'RISK_ASC') {
        return (a.riskAssessment?.totalScore || 0) - (b.riskAssessment?.totalScore || 0);
      }
      if (sortBy === 'UPDATE_RISK_DESC') {
        return (b.riskAssessment?.updateRiskScore || 0) - (a.riskAssessment?.updateRiskScore || 0);
      }
      if (sortBy === 'NAME_ASC') {
        return a.name.localeCompare(b.name);
      }
      return 0;
    });
  }, [dependencies, searchTerm, riskFilter, securityFilter, ecoFilter, diffFilter, sortBy]);

  return (
    <div className="glass-panel rounded-3xl border border-white/[0.08] p-6 sm:p-7 space-y-5">
      {/* Table Header & Search Controls */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 pb-2">
        <div>
          <div className="flex items-center space-x-2">
            <div className="p-1.5 rounded-lg bg-teal-500/10 text-teal-400">
              <Layers className="h-4 w-4" />
            </div>
            <h3 className="text-base sm:text-lg font-extrabold text-white tracking-tight">
              Dependency Inventory &amp; Risk Matrix
            </h3>
          </div>
          <p className="text-xs text-slate-400 mt-1 font-medium">
            Showing <span className="text-teal-400 font-bold">{filteredDependencies.length}</span> of {dependencies.length} analyzed packages &bull; Cross-correlated with live OSV security database
          </p>
        </div>

        {/* Search & Filter Controls */}
        <div className="flex flex-wrap items-center gap-2.5">
          {/* Search bar */}
          <div className="relative">
            <Search className="h-3.5 w-3.5 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search package name, group..."
              className="pl-9 pr-3.5 py-2 bg-slate-900/90 rounded-xl border border-white/[0.08] text-xs text-white placeholder-slate-400 focus:outline-none focus:border-teal-500 font-mono w-48 sm:w-60 transition"
            />
          </div>

          {/* Risk Filter */}
          <select
            value={riskFilter}
            onChange={(e) => setRiskFilter(e.target.value)}
            className="px-3 py-2 bg-slate-900/90 rounded-xl border border-white/[0.08] text-xs text-slate-200 focus:outline-none focus:border-teal-500 cursor-pointer font-medium"
          >
            <option value="ALL">All Risk Levels</option>
            <option value="CRITICAL">🚨 Critical Risk</option>
            <option value="HIGH">⚠️ High Risk</option>
            <option value="MEDIUM">⚡ Medium Risk</option>
            <option value="LOW">✓ Low Risk</option>
          </select>

          {/* Security Filter */}
          <select
            value={securityFilter}
            onChange={(e) => setSecurityFilter(e.target.value)}
            className="px-3 py-2 bg-slate-900/90 rounded-xl border border-white/[0.08] text-xs text-slate-200 focus:outline-none focus:border-teal-500 cursor-pointer font-medium"
          >
            <option value="ALL">All Security Priorities</option>
            <option value="CRITICAL">🚨 Critical Advisory</option>
            <option value="HIGH">⚠️ High Advisory</option>
            <option value="MEDIUM">⚡ Medium Notice</option>
            <option value="NONE">🛡️ Clean (No Vulns)</option>
          </select>

          {/* Ecosystem Filter */}
          <select
            value={ecoFilter}
            onChange={(e) => setEcoFilter(e.target.value)}
            className="px-3 py-2 bg-slate-900/90 rounded-xl border border-white/[0.08] text-xs text-slate-200 focus:outline-none focus:border-teal-500 cursor-pointer font-medium"
          >
            <option value="ALL">All Ecosystems</option>
            <option value="MAVEN">☕ Maven</option>
            <option value="NPM">⬡ npm</option>
            <option value="PYTHON">🐍 Python</option>
            <option value="GRADLE">☕ Gradle</option>
          </select>

          {/* Sort By */}
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2 bg-slate-900/90 rounded-xl border border-white/[0.08] text-xs text-slate-200 focus:outline-none focus:border-teal-500 cursor-pointer font-medium"
          >
            <option value="RISK_DESC">Composite Risk Score (High-Low)</option>
            <option value="UPDATE_RISK_DESC">Update Risk Only (High-Low)</option>
            <option value="RISK_ASC">Risk Score (Low-High)</option>
            <option value="NAME_ASC">Package Name (A-Z)</option>
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto rounded-2xl border border-white/[0.08]">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="bg-slate-900/95 text-slate-400 font-bold border-b border-white/[0.08] uppercase tracking-wider text-[10px]">
              <th className="py-3.5 px-4">Dependency</th>
              <th className="py-3.5 px-3">Declared / Resolved</th>
              <th className="py-3.5 px-3">Target</th>
              <th className="py-3.5 px-3">Type</th>
              <th className="py-3.5 px-3">Security Advisory</th>
              <th className="py-3.5 px-4">Composite Risk</th>
              <th className="py-3.5 px-4">Update Risk</th>
              <th className="py-3.5 px-4">Recommended Action</th>
              <th className="py-3.5 px-3 text-right">Details</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/[0.05] font-sans">
            {filteredDependencies.length === 0 ? (
              <tr>
                <td colSpan="9" className="py-12 text-center text-slate-400 font-medium">
                  No dependencies match your search or filter criteria.
                </td>
              </tr>
            ) : (
              filteredDependencies.map((dep) => {
                const hasVuln = dep.vulnerabilities && dep.vulnerabilities.length > 0;
                const isResolvedDifferent = dep.resolvedVersion && dep.resolvedVersion !== 'UNKNOWN' && dep.resolvedVersion !== dep.declaredVersionRange;

                return (
                  <tr
                    key={dep.id || dep.coordinates}
                    onClick={() => onSelectDependency(dep)}
                    className="hover:bg-teal-500/[0.04] transition duration-150 cursor-pointer group"
                  >
                    {/* Dependency info */}
                    <td className="py-3.5 px-4">
                      <div className="flex items-center space-x-2.5">
                        <EcosystemBadge ecosystem={dep.ecosystem} />
                        <div>
                          <div className="font-bold text-white font-mono group-hover:text-teal-300 transition text-xs">
                            {dep.name}
                          </div>
                          <div className="text-[11px] text-slate-400 font-mono flex items-center gap-1.5 mt-0.5">
                            {dep.groupOrNamespace ? `${dep.groupOrNamespace} &bull; ` : ''}
                            <span className="text-slate-400 font-sans">{dep.manifestFile}</span>
                            {dep.dev && <span className="px-1.5 py-0.2 rounded bg-slate-800 text-slate-400 text-[10px]">dev</span>}
                          </div>
                        </div>
                      </div>
                    </td>

                    {/* Declared vs Resolved Version */}
                    <td className="py-3.5 px-3">
                      <div className="space-y-0.5 font-mono">
                        <div className="text-slate-200 font-semibold text-xs">
                          {dep.declaredVersionRange || dep.currentVersion}
                        </div>
                        <div className="flex items-center gap-1">
                          <LockfileBadge source={dep.lockfileSource} />
                          {isResolvedDifferent && (
                            <span className="text-[11px] text-cyan-300 font-mono">
                              ({dep.resolvedVersion})
                            </span>
                          )}
                        </div>
                      </div>
                    </td>

                    {/* Target version */}
                    <td className="py-3.5 px-3 font-mono font-bold text-teal-300 text-xs">
                      {dep.recommendedTargetVersion || dep.latestVersion || 'N/A'}
                    </td>

                    {/* Change type */}
                    <td className="py-3.5 px-3">
                      <VersionBadge type={dep.versionDiffType} />
                    </td>

                    {/* Security Priority */}
                    <td className="py-3.5 px-3">
                      <SecurityPriorityBadge priority={dep.securityPriority} />
                      {hasVuln && (
                        <div className="text-[10px] text-rose-300 font-mono mt-1 font-semibold">
                          {dep.vulnerabilities.length} CVE ({dep.vulnerabilities[0].vulnId})
                        </div>
                      )}
                    </td>

                    {/* Composite Risk Badge */}
                    <td className="py-3.5 px-4">
                      <RiskBadge
                        level={dep.riskAssessment?.riskLevel}
                        score={dep.riskAssessment?.totalScore}
                      />
                    </td>

                    {/* Pure Update Risk */}
                    <td className="py-3.5 px-4">
                      <div className="font-mono text-xs text-slate-300">
                        <span className={`font-extrabold ${
                          (dep.riskAssessment?.updateRiskScore || 0) > 50 ? 'text-amber-400' : 'text-emerald-400'
                        }`}>
                          {dep.riskAssessment?.updateRiskScore !== undefined ? dep.riskAssessment.updateRiskScore : 'N/A'}
                        </span>
                        <span className="text-slate-400 text-[10px]"> /100</span>
                      </div>
                      <div className="text-[10px] text-slate-400 capitalize font-medium">
                        {dep.riskAssessment?.updateRiskLevel?.toLowerCase() || 'low'}
                      </div>
                    </td>

                    {/* Recommended Action */}
                    <td className="py-3.5 px-4 text-slate-300 font-medium text-[11px]">
                      {dep.recommendedAction}
                    </td>

                    {/* Action Icon */}
                    <td className="py-3.5 px-3 text-right">
                      <button className="p-1.5 rounded-xl bg-slate-900 border border-white/[0.06] text-slate-400 group-hover:text-teal-300 group-hover:border-teal-500/40 group-hover:bg-slate-800 transition">
                        <ChevronRight className="h-4 w-4" />
                      </button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
