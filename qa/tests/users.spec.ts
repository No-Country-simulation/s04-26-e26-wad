import { test, expect } from '@playwright/test';
import { login, createRandomUser, randomUsername } from './test-utils';

test.describe('Endpoints de usuarios', () => {
  test('admin puede crear un usuario nuevo', async ({ request }) => {
    const token = await login(request);
    await createRandomUser(request, token, 'TECHNICIAN');
  });

  test('admin puede listar usuarios', async ({ request }) => {
    const token = await login(request);

    const response = await request.get('/users', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(response.status()).toBe(200);
    const users = await response.json();
    expect(Array.isArray(users)).toBe(true);
    expect(users.some((user: any) => user.username === 'admin')).toBe(true);
  });

  test('admin puede actualizar el rol de un usuario', async ({ request }) => {
    const token = await login(request);
    const createdUser = await createRandomUser(request, token, 'TECHNICIAN');

    const roleResponse = await request.patch(`/users/${createdUser.id}/role`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: {
        role: 'SUPERVISOR',
      },
    });

    expect(roleResponse.status()).toBe(200);
    const updatedUser = await roleResponse.json();
    expect(updatedUser).toMatchObject({
      id: createdUser.id,
      username: createdUser.username,
      role: 'SUPERVISOR',
    });
  });

  test('admin puede actualizar el estado de un usuario', async ({ request }) => {
    const token = await login(request);
    const createdUser = await createRandomUser(request, token, 'TECHNICIAN');

    const statusResponse = await request.patch(`/users/${createdUser.id}/status`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: {
        enabled: false,
      },
    });

    expect(statusResponse.status()).toBe(200);
    const updatedUser = await statusResponse.json();
    expect(updatedUser).toMatchObject({
      id: createdUser.id,
      username: createdUser.username,
    });
    expect(updatedUser.role).toBe(createdUser.role);
  });

  test('operador no puede crear usuarios y recibe FORBIDDEN', async ({ request }) => {
    const adminToken = await login(request);
    const operator = await createRandomUser(request, adminToken, 'TECHNICIAN');

    const operatorLoginResponse = await request.post('/auth/login', {
      headers: {
        'Content-Type': 'application/json',
      },
      data: {
        username: operator.username,
        password: operator.password,
      },
    });

    expect(operatorLoginResponse.status()).toBe(200);
    const operatorTokenBody = await operatorLoginResponse.json();
    const operatorToken = operatorTokenBody.token as string;

    const unauthorizedResponse = await request.post('/users', {
      headers: {
        Authorization: `Bearer ${operatorToken}`,
        'Content-Type': 'application/json',
      },
      data: {
        username: randomUsername('no-admin'),
        password: 'Test1234!',
        role: 'TECHNICIAN',
      },
    });

    expect(unauthorizedResponse.status()).toBe(403);
    const errorBody = await unauthorizedResponse.json();
    expect(errorBody).toMatchObject({
      status: 403,
      error: 'FORBIDDEN',
    });
    expect(errorBody.message).toMatch(/permission|autorizad/i);
    expect(errorBody.path).toBe('/users');
  });

  test('un usuario puede cambiar su contraseña', async ({ request }) => {
    const adminToken = await login(request);
    const createdUser = await createRandomUser(request, adminToken, 'TECHNICIAN');

    const userTokenResponse = await request.post('/auth/login', {
      headers: {
        'Content-Type': 'application/json',
      },
      data: {
        username: createdUser.username,
        password: createdUser.password,
      },
    });

    expect(userTokenResponse.status()).toBe(200);
    const userTokenBody = await userTokenResponse.json();
    const userToken = userTokenBody.token as string;

    const newPassword = 'NewPass123!';
    const changePasswordResponse = await request.patch('/users/change-password', {
      headers: {
        Authorization: `Bearer ${userToken}`,
      },
      data: {
        currentPassword: createdUser.password,
        newPassword,
      },
    });

    expect(changePasswordResponse.status()).toBe(204);
    expect(await changePasswordResponse.text()).toBe('');

    const reloginResponse = await request.post('/auth/login', {
      headers: {
        'Content-Type': 'application/json',
      },
      data: {
        username: createdUser.username,
        password: newPassword,
      },
    });

    expect(reloginResponse.status()).toBe(200);
    const reloginBody = await reloginResponse.json();
    expect(reloginBody).toHaveProperty('token');
  });
});
