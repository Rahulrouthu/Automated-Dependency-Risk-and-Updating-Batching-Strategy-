import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 120000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const analyzeRepositoryApi = async (repositoryUrl, branch, githubToken) => {
  const response = await api.post('/repositories/analyze', {
    repositoryUrl,
    branch,
    githubToken,
  });
  return response.data;
};

export const getPresetsApi = async () => {
  const response = await api.get('/presets');
  return response.data;
};

export const getReportApi = async (repositoryId) => {
  const response = await api.get(`/reports/${repositoryId}`);
  return response.data;
};

export const getBatchesApi = async (repositoryId) => {
  const response = await api.get(`/batches/repository/${repositoryId}`);
  return response.data;
};

export default api;
