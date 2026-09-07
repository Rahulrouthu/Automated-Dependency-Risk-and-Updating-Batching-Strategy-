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
import { Layers, ShieldAlert, GitPullRequest, FileText, AlertCircle, RefreshCw } from 'lucide-react';

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
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      {/* Top Navigation */}
      <Navbar onOpenPresets={() => setIsPresetsOpen(true)} onNewScan={handleNewScan} />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* Error Alert */}
        {errorMessage && (
          <div className="mb-6 p-4 rounded-xl bg-red-950/60 border border-red-800 text-red-300 flex items-center justify-between text-xs animate-in fade-in">
            <div className="flex items-center space-x-2">
              <AlertCircle className="h-4 w-4 text-red-400 shrink-0" />
              <span>{errorMessage}</span>
            </div>
            <button
              onClick={() => setErrorMessage(null)}
              className="text-slate-400 hover:text-white ml-4 font-bold"
            >
              ✕
            </button>
          </div>
        )}

        {/* Hero Input Section (Always shown or collapsed when result exists) */}
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
          <div className="space-y-6 animate-in fade-in">
            {/* Quick Re-scan button bar */}
            <div className="flex items-center justify-between pb-2">
              <button
                onClick={handleNewScan}
                className="flex items-center space-x-1.5 text-xs font-semibold text-teal-400 hover:text-teal-300 transition cursor-pointer"
              >
                <span>&larr; Analyze Another Repository</span>
              </button>

              <button
                onClick={() => handleAnalyze(analysisResult.summary.url, analysisResult.summary.defaultBranch, '')}
                disabled={isLoading}
                className="flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 transition cursor-pointer"
              >
                <RefreshCw className={`h-3.5 w-3.5 ${isLoading ? 'animate-spin text-teal-400' : ''}`} />
                <span>Re-scan Repository</span>
              </button>
            </div>

            {/* Overview Metric Cards */}
            <OverviewMetrics summary={analysisResult.summary} />

            {/* Tab Navigation */}
            <div className="border-b border-slate-800 flex items-center space-x-2 sm:space-x-4 text-xs font-semibold">
              <button
                onClick={() => setActiveTab('inventory')}
                className={`pb-3 px-3 border-b-2 transition flex items-center space-x-2 cursor-pointer ${
                  activeTab === 'inventory'
                    ? 'border-teal-500 text-teal-400 font-bold'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <Layers className="h-4 w-4" />
                <span>Dependency Inventory ({analysisResult.dependencies?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('batches')}
                className={`pb-3 px-3 border-b-2 transition flex items-center space-x-2 cursor-pointer ${
                  activeTab === 'batches'
                    ? 'border-teal-500 text-teal-400 font-bold'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <GitPullRequest className="h-4 w-4" />
                <span>Safe Batches ({analysisResult.batches?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('vulnerabilities')}
                className={`pb-3 px-3 border-b-2 transition flex items-center space-x-2 cursor-pointer ${
                  activeTab === 'vulnerabilities'
                    ? 'border-teal-500 text-teal-400 font-bold'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <ShieldAlert className="h-4 w-4" />
                <span>Security Advisories ({analysisResult.vulnerabilities?.length || 0})</span>
              </button>

              <button
                onClick={() => setActiveTab('report')}
                className={`pb-3 px-3 border-b-2 transition flex items-center space-x-2 cursor-pointer ${
                  activeTab === 'report'
                    ? 'border-teal-500 text-teal-400 font-bold'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <FileText className="h-4 w-4" />
                <span>Executive Scan Report</span>
              </button>
            </div>

            {/* Tab Views */}
            {activeTab === 'inventory' && (
              <div className="space-y-6 pt-2">
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
              <div className="pt-2">
                <BatchingView
                  batches={analysisResult.batches}
                  onSelectDependency={(dep) => setSelectedDependency(dep)}
                />
              </div>
            )}

            {activeTab === 'vulnerabilities' && (
              <div className="pt-2">
                <VulnerabilityView
                  vulnerabilities={analysisResult.vulnerabilities}
                  dependencies={analysisResult.dependencies}
                  onSelectDependency={(dep) => setSelectedDependency(dep)}
                />
              </div>
            )}

            {activeTab === 'report' && (
              <div className="pt-2">
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
      <footer className="mt-auto border-t border-slate-800/80 py-6 text-center text-xs text-slate-400">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <span>DevOps Engineering Capstone &bull; Automated Dependency Risk Assessment &amp; Batching Strategy</span>
          <span>Java 21 &bull; Spring Boot 3 &bull; React 18 &bull; OSV Security &bull; MySQL</span>
        </div>
      </footer>
    </div>
  );
}
