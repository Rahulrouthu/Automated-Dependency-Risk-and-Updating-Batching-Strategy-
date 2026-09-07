import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import RepoInputHero from './components/RepoInputHero';
import AnalysisProgress from './components/AnalysisProgress';
import OverviewMetrics from './components/OverviewMetrics';
import RiskDistributionCharts from './components/RiskDistributionCharts';
import DependencyTable from './components/DependencyTable';
import DependencyDetailDrawer from './components/DependencyDetailDrawer';
import BatchingView from './components/BatchingView';
import VulnerabilityView from './components/VulnerabilityView';
import ReportView from './components/ReportView';
import PresetReposModal from './components/PresetReposModal';
import { analyzeRepositoryApi, getPresetsApi, getReportApi } from './api/apiClient';
import { Layers, ShieldAlert, GitPullRequest, FileText, AlertCircle, RefreshCw, Sparkles, ArrowLeft } from 'lucide-react';

export default function App() {
  const [isLoading, setIsLoading] = useState(false);
  const [analysisResult, setAnalysisResult] = useState(null);
  const [reportData, setReportData] = useState(null);
  const [activeTab, setActiveTab] = useState('inventory');
  const [selectedDependency, setSelectedDependency] = useState(null);
  const [isPresetsOpen, setIsPresetsOpen] = useState(false);
  const [presets, setPresets] = useState([]);
  const [errorMessage, setErrorMessage] = useState(null);

  // Load presets on startup
  useEffect(() => {
    getPresetsApi()
      .then((data) => setPresets(data || []))
      .catch(() => {
        setPresets([
          { name: 'Spring PetClinic', url: 'https://github.com/spring-projects/spring-petclinic', description: 'Enterprise Java Spring Boot with Maven', ecosystem: 'MAVEN', tag: 'Enterprise Java' },
          { name: 'Express.js App', url: 'https://github.com/expressjs/express', description: 'Node.js Express backend with npm', ecosystem: 'NPM', tag: 'Node.js / Express' },
          { name: 'Python Requests', url: 'https://github.com/psf/requests', description: 'Python HTTP library with requirements.txt', ecosystem: 'PYTHON', tag: 'Python / PyPI' },
          { name: 'Vulnerable Microservice', url: 'https://github.com/devops-capstone/vulnerable-microservice-demo', description: 'Multi-ecosystem demo with Log4j2, Spring4Shell, and breaking versions', ecosystem: 'MAVEN', tag: 'Security Audit' }
        ]);
      });
  }, []);

  const handleAnalyze = async (repoUrl, branch, githubToken) => {
    setIsLoading(true);
    setErrorMessage(null);
    setSelectedDependency(null);

    try {
      const result = await analyzeRepositoryApi(repoUrl, branch, githubToken);
      setAnalysisResult(result);
      setActiveTab('inventory');

      // Fetch executive report
      if (result.summary?.id) {
        try {
          const report = await getReportApi(result.summary.id);
          setReportData(report);
        } catch (e) {
          console.warn('Report fetch notice:', e);
        }
      }
    } catch (err) {
      console.error('Analysis failed:', err);
      setErrorMessage(
        err.response?.data?.message ||
        'Could not analyze repository. Please verify the URL or check your network connection.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewScan = () => {
    setAnalysisResult(null);
    setReportData(null);
    setErrorMessage(null);
    setSelectedDependency(null);
  };

  return (
    <div className="min-h-screen bg-[#080c15] text-slate-100 flex flex-col bg-grid-pattern relative selection:bg-teal-500/30 selection:text-teal-200">
      {/* Top Navigation */}
      <Navbar onOpenPresets={() => setIsPresetsOpen(true)} onNewScan={handleNewScan} />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Error Alert */}
        {errorMessage && (
          <div className="mb-6 p-4 rounded-2xl bg-rose-950/60 border border-rose-500/40 text-rose-200 flex items-center justify-between text-xs animate-in fade-in shadow-glow-rose">
            <div className="flex items-center space-x-2.5">
              <AlertCircle className="h-4 w-4 text-rose-400 shrink-0" />
              <span className="font-medium">{errorMessage}</span>
            </div>
            <button
              onClick={() => setErrorMessage(null)}
              className="text-slate-400 hover:text-white ml-4 font-bold p-1"
            >
              ✕
            </button>
          </div>
        )}

        {/* Hero Input Section */}
        {!analysisResult ? (
          <>
            <RepoInputHero
              onAnalyze={handleAnalyze}
              isLoading={isLoading}
              onOpenPresets={() => setIsPresetsOpen(true)}
              presets={presets}
            />
            <AnalysisProgress isLoading={isLoading} />
          </>
        ) : (
          <div className="space-y-6 animate-in fade-in duration-300">
            {/* Quick Re-scan button bar */}
            <div className="flex items-center justify-between pb-1">
              <button
                onClick={handleNewScan}
                className="flex items-center space-x-2 text-xs font-bold text-teal-400 hover:text-teal-300 transition cursor-pointer group"
              >
                <ArrowLeft className="h-3.5 w-3.5 transition group-hover:-translate-x-1" />
                <span>Analyze Another Repository</span>
              </button>

              <button
                onClick={() => handleAnalyze(analysisResult.summary.url, analysisResult.summary.defaultBranch, '')}
                disabled={isLoading}
                className="flex items-center space-x-2 text-xs font-semibold px-3.5 py-1.5 rounded-xl bg-slate-900/90 hover:bg-slate-800 text-slate-200 border border-white/[0.08] hover:border-teal-500/40 transition cursor-pointer shadow-sm"
              >
                <RefreshCw className={`h-3.5 w-3.5 ${isLoading ? 'animate-spin text-teal-400' : ''}`} />
                <span>Re-scan Repository</span>
              </button>
            </div>

            {/* Overview Metric Cards */}
            <OverviewMetrics summary={analysisResult.summary} />

            {/* Luxury Segmented Control Tab Navigation */}
            <div className="glass-panel p-1.5 rounded-2xl border border-white/[0.08] flex items-center justify-start overflow-x-auto gap-1 text-xs font-bold">
              <button
                onClick={() => setActiveTab('inventory')}
                className={`py-2.5 px-4 rounded-xl transition duration-200 flex items-center space-x-2 shrink-0 cursor-pointer ${
                  activeTab === 'inventory'
                    ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-slate-950 shadow-glow-teal font-extrabold'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/60'
                }`}
              >
                <Layers className="h-4 w-4" />
                <span>Dependency Inventory ({analysisResult.dependencies?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('batches')}
                className={`py-2.5 px-4 rounded-xl transition duration-200 flex items-center space-x-2 shrink-0 cursor-pointer ${
                  activeTab === 'batches'
                    ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-slate-950 shadow-glow-teal font-extrabold'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/60'
                }`}
              >
                <GitPullRequest className="h-4 w-4" />
                <span>Safe Batches ({analysisResult.batches?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('vulnerabilities')}
                className={`py-2.5 px-4 rounded-xl transition duration-200 flex items-center space-x-2 shrink-0 cursor-pointer ${
                  activeTab === 'vulnerabilities'
                    ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-slate-950 shadow-glow-teal font-extrabold'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/60'
                }`}
              >
                <ShieldAlert className="h-4 w-4" />
                <span>Security Advisories ({analysisResult.vulnerabilities?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('report')}
                className={`py-2.5 px-4 rounded-xl transition duration-200 flex items-center space-x-2 shrink-0 cursor-pointer ${
                  activeTab === 'report'
                    ? 'bg-gradient-to-r from-teal-500 to-emerald-500 text-slate-950 shadow-glow-teal font-extrabold'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/60'
                }`}
              >
                <FileText className="h-4 w-4" />
                <span>Executive Report</span>
              </button>
            </div>

            {/* Tab Views */}
            {activeTab === 'inventory' && (
              <div className="space-y-6 pt-1">
                <RiskDistributionCharts
                  dependencies={analysisResult.dependencies}
                  summary={analysisResult.summary}
                />
                <DependencyTable
                  dependencies={analysisResult.dependencies}
                  onSelectDependency={(dep) => setSelectedDependency(dep)}
                />
              </div>
            )}

            {activeTab === 'batches' && (
              <div className="pt-1">
                <BatchingView
                  batches={analysisResult.batches}
                  onSelectDependency={(dep) => setSelectedDependency(dep)}
                />
              </div>
            )}

            {activeTab === 'vulnerabilities' && (
              <div className="pt-1">
                <VulnerabilityView
                  vulnerabilities={analysisResult.vulnerabilities}
                  dependencies={analysisResult.dependencies}
                  onSelectDependency={(dep) => setSelectedDependency(dep)}
                />
              </div>
            )}

            {activeTab === 'report' && (
              <div className="pt-1">
                <ReportView
                  report={reportData}
                  rawResult={analysisResult}
                />
              </div>
            )}
          </div>
        )}
      </main>

      {/* Dependency Detail Slide-Over Drawer */}
      <DependencyDetailDrawer
        dependency={selectedDependency}
        onClose={() => setSelectedDependency(null)}
      />

      {/* Preset Repositories Modal */}
      <PresetReposModal
        isOpen={isPresetsOpen}
        onClose={() => setIsPresetsOpen(false)}
        presets={presets}
        onSelectPreset={(url) => handleAnalyze(url, 'main', '')}
      />

      {/* Footer */}
      <footer className="mt-auto border-t border-white/[0.06] py-6 text-center text-xs text-slate-400 bg-[#060911]/80 backdrop-blur-md">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2.5">
          <span className="font-medium">DevOps Engineering Capstone &bull; Automated Dependency Risk Assessment &amp; Batching Strategy</span>
          <span className="font-mono text-[11px] text-slate-400">Java 21 &bull; Spring Boot 3 &bull; React 18 &bull; OSV Security &bull; MySQL</span>
        </div>
      </footer>
    </div>
  );
}
