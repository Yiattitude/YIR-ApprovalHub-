import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Dashboard from '../views/Dashboard.vue';
import MyApplications from '../views/MyApplications.vue';
import NewLeaveApplication from '../views/NewLeaveApplication.vue';
import NewReimbursementApplication from '../views/NewReimbursementApplication.vue';
import PendingApprovals from '../views/PendingApprovals.vue';
import ApplicationDetail from '../views/ApplicationDetail.vue';
import UserManagement from '../views/UserManagement.vue';
import DepartmentManagement from '../views/DepartmentManagement.vue';
import PositionManagement from '../views/PositionManagement.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'my-applications',
        name: 'MyApplications',
        component: MyApplications
      },
      {
        path: 'new-leave',
        name: 'NewLeaveApplication',
        component: NewLeaveApplication
      },
      {
        path: 'new-reimbursement',
        name: 'NewReimbursementApplication',
        component: NewReimbursementApplication
      },
      {
        path: 'pending-approvals',
        name: 'PendingApprovals',
        component: PendingApprovals
      },
      {
        path: 'application/:id',
        name: 'ApplicationDetail',
        component: ApplicationDetail
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: UserManagement
      },
      {
        path: 'department-management',
        name: 'DepartmentManagement',
        component: DepartmentManagement
      },
      {
        path: 'position-management',
        name: 'PositionManagement',
        component: PositionManagement
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.path === '/login' && token) {
    next('/');
  } else {
    next();
  }
});

export default router;
