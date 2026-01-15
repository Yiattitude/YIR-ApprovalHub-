import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle errors
apiClient.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default {
    // Auth API
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },
    
    getCurrentUser() {
        return apiClient.get('/auth/me');
    },

    // Application API
    createLeaveApplication(data) {
        return apiClient.post('/applications/leave', data);
    },

    createReimbursementApplication(data) {
        return apiClient.post('/applications/reimbursement', data);
    },

    submitApplication(id) {
        return apiClient.post(`/applications/${id}/submit`);
    },

    withdrawApplication(id) {
        return apiClient.post(`/applications/${id}/withdraw`);
    },

    getMyApplications() {
        return apiClient.get('/applications/my');
    },

    getApplicationById(id) {
        return apiClient.get(`/applications/${id}`);
    },

    getApplicationHistory(id) {
        return apiClient.get(`/applications/${id}/history`);
    },

    // Approval API
    getMyPendingTasks() {
        return apiClient.get('/approvals/pending');
    },

    getApplicationTasks(applicationId) {
        return apiClient.get(`/approvals/application/${applicationId}`);
    },

    processApprovalTask(taskId, decision) {
        return apiClient.post(`/approvals/${taskId}/process`, decision);
    },

    // User Management API
    getAllUsers() {
        return apiClient.get('/users');
    },

    getUserById(id) {
        return apiClient.get(`/users/${id}`);
    },

    createUser(user) {
        return apiClient.post('/users', user);
    },

    updateUser(id, user) {
        return apiClient.put(`/users/${id}`, user);
    },

    deleteUser(id) {
        return apiClient.delete(`/users/${id}`);
    },

    getUsersByDepartment(departmentId) {
        return apiClient.get(`/users/department/${departmentId}`);
    },

    getUsersByRole(role) {
        return apiClient.get(`/users/role/${role}`);
    },

    // Department Management API
    getAllDepartments() {
        return apiClient.get('/departments');
    },

    getDepartmentById(id) {
        return apiClient.get(`/departments/${id}`);
    },

    createDepartment(department) {
        return apiClient.post('/departments', department);
    },

    updateDepartment(id, department) {
        return apiClient.put(`/departments/${id}`, department);
    },

    deleteDepartment(id) {
        return apiClient.delete(`/departments/${id}`);
    },

    // Position Management API
    getAllPositions() {
        return apiClient.get('/positions');
    },

    getPositionById(id) {
        return apiClient.get(`/positions/${id}`);
    },

    createPosition(position) {
        return apiClient.post('/positions', position);
    },

    updatePosition(id, position) {
        return apiClient.put(`/positions/${id}`, position);
    },

    deletePosition(id) {
        return apiClient.delete(`/positions/${id}`);
    }
};
