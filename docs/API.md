# YIR Approval Hub API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All API endpoints (except `/auth/login`) require JWT authentication.

Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Authentication APIs

### 1. User Login
**POST** `/auth/login`

登录获取JWT令牌

**Request Body:**
```json
{
  "username": "employee1",
  "password": "emp123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "employee1",
  "fullName": "王员工",
  "role": "EMPLOYEE"
}
```

### 2. Get Current User
**GET** `/auth/me`

获取当前登录用户信息

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "employee1",
    "fullName": "王员工",
    "email": "emp1@yir.com",
    "role": "EMPLOYEE"
  }
}
```

---

## Application Management APIs

### 1. Create Leave Application
**POST** `/applications/leave`

创建请假申请

**Request Body:**
```json
{
  "title": "请假申请",
  "leaveType": "SICK_LEAVE",
  "startDate": "2024-01-15",
  "endDate": "2024-01-17",
  "days": 3,
  "reason": "身体不适需要休息"
}
```

**Leave Types:**
- `SICK_LEAVE` - 病假
- `ANNUAL_LEAVE` - 年假
- `PERSONAL_LEAVE` - 事假
- `MATERNITY_LEAVE` - 产假
- `PATERNITY_LEAVE` - 陪产假
- `OTHER` - 其他

**Response:**
```json
{
  "success": true,
  "message": "请假申请创建成功",
  "data": {
    "id": 1,
    "applicationNo": "LEAVE-1705123456789-ABC12345",
    "status": "DRAFT",
    ...
  }
}
```

### 2. Create Reimbursement Application
**POST** `/applications/reimbursement`

创建报销申请

**Request Body:**
```json
{
  "title": "差旅报销",
  "reimbursementType": "TRAVEL",
  "amount": 1500.50,
  "expenseDate": "2024-01-10",
  "purpose": "出差北京参加培训"
}
```

**Reimbursement Types:**
- `TRAVEL` - 差旅
- `ENTERTAINMENT` - 招待
- `OFFICE_SUPPLIES` - 办公用品
- `TRAINING` - 培训
- `COMMUNICATION` - 通讯
- `OTHER` - 其他

**Response:**
```json
{
  "success": true,
  "message": "报销申请创建成功",
  "data": {
    "id": 2,
    "applicationNo": "REIMB-1705123456789-DEF67890",
    "status": "DRAFT",
    ...
  }
}
```

### 3. Submit Application
**POST** `/applications/{id}/submit`

提交申请（从草稿状态提交到审批流程）

**Response:**
```json
{
  "success": true,
  "message": "申请已提交",
  "data": {
    "id": 1,
    "status": "PENDING",
    "submittedAt": "2024-01-12T10:30:00",
    ...
  }
}
```

### 4. Withdraw Application
**POST** `/applications/{id}/withdraw`

撤回申请（只能撤回待审批状态的申请）

**Response:**
```json
{
  "success": true,
  "message": "申请已撤回",
  "data": {
    "id": 1,
    "status": "WITHDRAWN",
    ...
  }
}
```

### 5. Get My Applications
**GET** `/applications/my`

获取当前用户的所有申请

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "applicationNo": "LEAVE-1705123456789-ABC12345",
      "applicationType": "LEAVE",
      "title": "请假申请",
      "status": "PENDING",
      "createdAt": "2024-01-12T09:00:00",
      ...
    }
  ]
}
```

**Application Status:**
- `DRAFT` - 草稿
- `PENDING` - 待审批
- `APPROVED` - 已通过
- `REJECTED` - 已拒绝
- `WITHDRAWN` - 已撤回

### 6. Get Application Detail
**GET** `/applications/{id}`

获取申请详情

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "applicationNo": "LEAVE-1705123456789-ABC12345",
    "applicant": {
      "id": 1,
      "username": "employee1",
      "fullName": "王员工"
    },
    "applicationType": "LEAVE",
    "title": "请假申请",
    "status": "PENDING",
    "leaveType": "SICK_LEAVE",
    "startDate": "2024-01-15",
    "endDate": "2024-01-17",
    "days": 3,
    "reason": "身体不适需要休息",
    "createdAt": "2024-01-12T09:00:00",
    "submittedAt": "2024-01-12T10:30:00"
  }
}
```

### 7. Get Application History
**GET** `/applications/{id}/history`

获取申请的审批历史

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "fullName": "王员工"
      },
      "action": "CREATED",
      "comment": "创建申请",
      "createdAt": "2024-01-12T09:00:00"
    },
    {
      "id": 2,
      "user": {
        "id": 1,
        "fullName": "王员工"
      },
      "action": "SUBMITTED",
      "comment": "提交申请",
      "createdAt": "2024-01-12T10:30:00"
    },
    {
      "id": 3,
      "user": {
        "id": 2,
        "fullName": "张经理"
      },
      "action": "APPROVED",
      "stepName": "部门经理审批",
      "comment": "同意",
      "createdAt": "2024-01-12T14:00:00"
    }
  ]
}
```

**History Actions:**
- `CREATED` - 创建
- `SUBMITTED` - 提交
- `APPROVED` - 同意
- `REJECTED` - 拒绝
- `WITHDRAWN` - 撤回
- `MODIFIED` - 修改

---

## Approval Management APIs

### 1. Get Pending Approval Tasks
**GET** `/approvals/pending`

获取当前用户的待审批任务

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "application": {
        "id": 1,
        "applicationNo": "LEAVE-1705123456789-ABC12345",
        "applicationType": "LEAVE",
        "title": "请假申请",
        "applicant": {
          "id": 1,
          "fullName": "王员工"
        }
      },
      "stepOrder": 1,
      "stepName": "部门经理审批",
      "status": "PENDING",
      "createdAt": "2024-01-12T10:30:00"
    }
  ]
}
```

### 2. Get Application Approval Tasks
**GET** `/approvals/application/{applicationId}`

获取指定申请的所有审批任务

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "approver": {
        "id": 2,
        "fullName": "张经理"
      },
      "stepOrder": 1,
      "stepName": "部门经理审批",
      "status": "APPROVED",
      "action": "APPROVE",
      "comment": "同意",
      "processedAt": "2024-01-12T14:00:00"
    },
    {
      "id": 2,
      "approver": {
        "id": 3,
        "fullName": "李HR"
      },
      "stepOrder": 2,
      "stepName": "HR经理审批",
      "status": "PENDING",
      "createdAt": "2024-01-12T10:30:00"
    }
  ]
}
```

**Task Status:**
- `PENDING` - 待处理
- `APPROVED` - 已同意
- `REJECTED` - 已拒绝
- `SKIPPED` - 已跳过

### 3. Process Approval Task
**POST** `/approvals/{taskId}/process`

处理审批任务（同意或拒绝）

**Request Body:**
```json
{
  "action": "APPROVE",
  "comment": "同意该申请"
}
```

**Actions:**
- `APPROVE` - 同意
- `REJECT` - 拒绝

**Response:**
```json
{
  "success": true,
  "message": "审批处理成功",
  "data": {
    "id": 1,
    "status": "APPROVED",
    "action": "APPROVE",
    "comment": "同意该申请",
    "processedAt": "2024-01-12T14:00:00"
  }
}
```

---

## Error Responses

所有API在发生错误时会返回如下格式：

```json
{
  "success": false,
  "message": "错误描述信息"
}
```

**Common Error Codes:**
- `400 Bad Request` - 请求参数错误
- `401 Unauthorized` - 未授权或令牌无效
- `403 Forbidden` - 无权限访问
- `404 Not Found` - 资源不存在
- `500 Internal Server Error` - 服务器内部错误

---

## Testing with cURL

### Login Example
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"employee1","password":"emp123"}'
```

### Create Leave Application Example
```bash
curl -X POST http://localhost:8080/api/applications/leave \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "请假申请",
    "leaveType": "SICK_LEAVE",
    "startDate": "2024-01-15",
    "endDate": "2024-01-17",
    "days": 3,
    "reason": "身体不适需要休息"
  }'
```

### Get Pending Tasks Example
```bash
curl -X GET http://localhost:8080/api/approvals/pending \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
