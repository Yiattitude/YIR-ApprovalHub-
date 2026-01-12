<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>YIR 审批系统</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="demo-accounts">
        <h4>测试账号：</h4>
        <p>管理员: admin / admin123</p>
        <p>审批人1: manager1 / manager123</p>
        <p>审批人2: hrmanager / hr123</p>
        <p>员工1: employee1 / emp123</p>
        <p>员工2: employee2 / emp123</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../services/api';

export default {
  name: 'Login',
  setup() {
    const router = useRouter();
    const loginFormRef = ref(null);
    const loading = ref(false);

    const loginForm = reactive({
      username: '',
      password: ''
    });

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    };

    const handleLogin = async () => {
      if (!loginFormRef.value) return;
      
      await loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const response = await api.login(loginForm);
            const { token, username, fullName, role } = response.data;
            
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify({ username, fullName, role }));
            
            ElMessage.success('登录成功');
            router.push('/my-applications');
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '登录失败');
          } finally {
            loading.value = false;
          }
        }
      });
    };

    return {
      loginForm,
      loginFormRef,
      rules,
      loading,
      handleLogin
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 450px;
}

.login-card h2 {
  text-align: center;
  color: #333;
  margin: 0;
}

.demo-accounts {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.demo-accounts h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.demo-accounts p {
  margin: 5px 0;
}
</style>
