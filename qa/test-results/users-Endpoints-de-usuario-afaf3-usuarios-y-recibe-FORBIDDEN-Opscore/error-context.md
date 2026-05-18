# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: users.spec.ts >> Endpoints de usuarios >> operador no puede crear usuarios y recibe FORBIDDEN
- Location: tests/users.spec.ts:69:7

# Error details

```
Error: expect(received).toBe(expected) // Object.is equality

Expected: 403
Received: 500
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | import { login, createRandomUser, randomUsername } from './test-utils';
  3   | 
  4   | test.describe('Endpoints de usuarios', () => {
  5   |   test('admin puede crear un usuario nuevo', async ({ request }) => {
  6   |     const token = await login(request);
  7   |     await createRandomUser(request, token, 'TECHNICIAN');
  8   |   });
  9   | 
  10  |   test('admin puede listar usuarios', async ({ request }) => {
  11  |     const token = await login(request);
  12  | 
  13  |     const response = await request.get('/users', {
  14  |       headers: {
  15  |         Authorization: `Bearer ${token}`,
  16  |       },
  17  |     });
  18  | 
  19  |     expect(response.status()).toBe(200);
  20  |     const users = await response.json();
  21  |     expect(Array.isArray(users)).toBe(true);
  22  |     expect(users.some((user: any) => user.username === 'admin')).toBe(true);
  23  |   });
  24  | 
  25  |   test('admin puede actualizar el rol de un usuario', async ({ request }) => {
  26  |     const token = await login(request);
  27  |     const createdUser = await createRandomUser(request, token, 'TECHNICIAN');
  28  | 
  29  |     const roleResponse = await request.patch(`/users/${createdUser.id}/role`, {
  30  |       headers: {
  31  |         Authorization: `Bearer ${token}`,
  32  |       },
  33  |       data: {
  34  |         role: 'SUPERVISOR',
  35  |       },
  36  |     });
  37  | 
  38  |     expect(roleResponse.status()).toBe(200);
  39  |     const updatedUser = await roleResponse.json();
  40  |     expect(updatedUser).toMatchObject({
  41  |       id: createdUser.id,
  42  |       username: createdUser.username,
  43  |       role: 'SUPERVISOR',
  44  |     });
  45  |   });
  46  | 
  47  |   test('admin puede actualizar el estado de un usuario', async ({ request }) => {
  48  |     const token = await login(request);
  49  |     const createdUser = await createRandomUser(request, token, 'TECHNICIAN');
  50  | 
  51  |     const statusResponse = await request.patch(`/users/${createdUser.id}/status`, {
  52  |       headers: {
  53  |         Authorization: `Bearer ${token}`,
  54  |       },
  55  |       data: {
  56  |         enabled: false,
  57  |       },
  58  |     });
  59  | 
  60  |     expect(statusResponse.status()).toBe(200);
  61  |     const updatedUser = await statusResponse.json();
  62  |     expect(updatedUser).toMatchObject({
  63  |       id: createdUser.id,
  64  |       username: createdUser.username,
  65  |     });
  66  |     expect(updatedUser.role).toBe(createdUser.role);
  67  |   });
  68  | 
  69  |   test('operador no puede crear usuarios y recibe FORBIDDEN', async ({ request }) => {
  70  |     const adminToken = await login(request);
  71  |     const operator = await createRandomUser(request, adminToken, 'TECHNICIAN');
  72  | 
  73  |     const operatorLoginResponse = await request.post('/auth/login', {
  74  |       headers: {
  75  |         'Content-Type': 'application/json',
  76  |       },
  77  |       data: {
  78  |         username: operator.username,
  79  |         password: operator.password,
  80  |       },
  81  |     });
  82  | 
  83  |     expect(operatorLoginResponse.status()).toBe(200);
  84  |     const operatorTokenBody = await operatorLoginResponse.json();
  85  |     const operatorToken = operatorTokenBody.token as string;
  86  | 
  87  |     const unauthorizedResponse = await request.post('/users', {
  88  |       headers: {
  89  |         Authorization: `Bearer ${operatorToken}`,
  90  |         'Content-Type': 'application/json',
  91  |       },
  92  |       data: {
  93  |         username: randomUsername('no-admin'),
  94  |         password: 'Test1234!',
  95  |         role: 'TECHNICIAN',
  96  |       },
  97  |     });
  98  | 
> 99  |     expect(unauthorizedResponse.status()).toBe(403);
      |                                           ^ Error: expect(received).toBe(expected) // Object.is equality
  100 |     const errorBody = await unauthorizedResponse.json();
  101 |     expect(errorBody).toMatchObject({
  102 |       status: 403,
  103 |       error: 'FORBIDDEN',
  104 |     });
  105 |     expect(errorBody.message).toMatch(/permission|autorizad/i);
  106 |     expect(errorBody.path).toBe('/users');
  107 |   });
  108 | 
  109 |   test('un usuario puede cambiar su contraseña', async ({ request }) => {
  110 |     const adminToken = await login(request);
  111 |     const createdUser = await createRandomUser(request, adminToken, 'TECHNICIAN');
  112 | 
  113 |     const userTokenResponse = await request.post('/auth/login', {
  114 |       headers: {
  115 |         'Content-Type': 'application/json',
  116 |       },
  117 |       data: {
  118 |         username: createdUser.username,
  119 |         password: createdUser.password,
  120 |       },
  121 |     });
  122 | 
  123 |     expect(userTokenResponse.status()).toBe(200);
  124 |     const userTokenBody = await userTokenResponse.json();
  125 |     const userToken = userTokenBody.token as string;
  126 | 
  127 |     const newPassword = 'NewPass123!';
  128 |     const changePasswordResponse = await request.patch('/users/change-password', {
  129 |       headers: {
  130 |         Authorization: `Bearer ${userToken}`,
  131 |       },
  132 |       data: {
  133 |         currentPassword: createdUser.password,
  134 |         newPassword,
  135 |       },
  136 |     });
  137 | 
  138 |     expect(changePasswordResponse.status()).toBe(204);
  139 |     expect(await changePasswordResponse.text()).toBe('');
  140 | 
  141 |     const reloginResponse = await request.post('/auth/login', {
  142 |       headers: {
  143 |         'Content-Type': 'application/json',
  144 |       },
  145 |       data: {
  146 |         username: createdUser.username,
  147 |         password: newPassword,
  148 |       },
  149 |     });
  150 | 
  151 |     expect(reloginResponse.status()).toBe(200);
  152 |     const reloginBody = await reloginResponse.json();
  153 |     expect(reloginBody).toHaveProperty('token');
  154 |   });
  155 | });
  156 | 
```