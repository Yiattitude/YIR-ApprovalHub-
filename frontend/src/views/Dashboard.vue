<template>
  <el-container class="dashboard-container">
    <el-header class="dashboard-header">
      <h2>YIR 审批系统</h2>
      <div class="user-info">
        <span>{{ user?.fullName }} ({{ roleText }})</span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="dashboard-aside">
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/my-applications">
            <span>我的申请</span>
          </el-menu-item>
          <el-menu-item index="/new-leave">
            <span>新建请假申请</span>
          </el-menu-item>
          <el-menu-item index="/new-reimbursement">
            <span>新建报销申请</span>
          </el-menu-item>
          <el-menu-item index="/pending-approvals" v-if="isApprover">
            <span>待审批任务</span>
          </el-menu-item>
          <el-menu-item index="/user-management" v-if="isAdmin">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/department-management" v-if="isAdmin">
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/position-management" v-if="isAdmin">
            <span>岗位管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="dashboard-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  name: 'Dashboard',
  setup() {
    const router = useRouter();
    const route = useRoute();
    const user = ref(null);

    const activeMenu = computed(() => route.path);

    const isApprover = computed(() => {
      return user.value?.role === 'APPROVER' || user.value?.role === 'ADMIN';
    });

    const isAdmin = computed(() => {
      return user.value?.role === 'ADMIN';
    });

    const roleText = computed(() => {
      const roleMap = {
        'ADMIN': '管理员',
        'APPROVER': '审批人',
        'EMPLOYEE': '员工'
      };
      return roleMap[user.value?.role] || '用户';
    });

    const handleLogout = () => {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      ElMessage.success('已退出');
      router.push('/login');
    };

    onMounted(() => {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        user.value = JSON.parse(userStr);
      }
      
      // Redirect to my-applications if at root
      if (route.path === '/') {
        router.push('/my-applications');
      }
    });

    return {
      user,
      activeMenu,
      isApprover,
      isAdmin,
      roleText,
      handleLogout
    };
  }
};
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409eff;
  color: white;
  padding: 0 20px;
}

.dashboard-header h2 {
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.dashboard-aside {
  background-color: #f5f5f5;
  border-right: 1px solid #e6e6e6;
}

.dashboard-main {
  background-color: white;
  padding: 20px;
}
</style>
