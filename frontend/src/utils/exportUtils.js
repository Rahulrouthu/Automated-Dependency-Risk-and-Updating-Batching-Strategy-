export const downloadJson = (data, filename = 'dependency-analysis.json') => {
  const jsonStr = JSON.stringify(data, null, 2);
  const blob = new Blob([jsonStr], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};

export const downloadMarkdownReport = (report) => {
  if (!report) return;
  let md = `# Dependency Risk & Batching Assessment Report\n\n`;
  md += `**Repository:** ${report.repositoryName || report.repositoryUrl}\n`;
  md += `**Generated:** ${new Date().toLocaleString()}\n`;
  md += `**Health Grade:** ${report.healthGrade} (${report.healthGradeScore}/100)\n`;
  md += `**Overall Risk Level:** ${report.overallRisk}\n\n`;
  
  md += `## Executive Summary\n`;
  report.executiveSummaryBullets?.forEach((b) => {
    md += `- ${b}\n`;
  });
  
  md += `\n## Prioritized Remediation Roadmap\n`;
  report.prioritizedRemediationRoadmap?.forEach((r) => {
    md += `${r}\n`;
  });

  md += `\n## Recommended Batches (${report.proposedBatches?.length || 0})\n\n`;
  report.proposedBatches?.forEach((batch) => {
    md += `### ${batch.title}\n`;
    md += `- **Risk Level:** ${batch.batchRiskLevel} (${batch.batchRiskScore}/100)\n`;
    md += `- **Description:** ${batch.strategyDescription}\n`;
    md += `\n\`\`\`bash\n${batch.executionCommands}\n\`\`\`\n\n`;
  });

  const blob = new Blob([md], { type: 'text/markdown' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `${report.repositoryName || 'repository'}-risk-report.md`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};
