
export default ['$http', AppSettings];

function AppSettings($http) {
  return {
    version: '0.1',
    jobs_api: 'http://localhost:8081/jobs'
  };
};