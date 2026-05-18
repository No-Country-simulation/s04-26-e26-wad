import { APIRequestContext, expect } from '@playwright/test';

export const adminCredentials = {
  username: process.env.API_ADMIN_USER || 'admin',
  password: process.env.API_ADMIN_PASSWORD || '1234',
};

const adminFallbackPasswords = [
  process.env.API_ADMIN_PASSWORD,
  '1234',
  'admin',
].filter(Boolean) as string[];

export async function login(request: APIRequestContext, credentials = adminCredentials) {
  const attempts = [credentials];
  const isAdminLogin = credentials.username === adminCredentials.username;

  if (isAdminLogin) {
    const fallback = adminFallbackPasswords
      .filter((password) => password !== credentials.password)
      .map((password) => ({ username: credentials.username, password }));
    attempts.push(...fallback);
  }

  const errors: string[] = [];
  for (const attempt of attempts) {
    const response = await request.post('/auth/login', {
      headers: {
        'Content-Type': 'application/json',
      },
      data: attempt,
    });

    if (response.status() === 200) {
      const body = await response.json();
      expect(body).toHaveProperty('token');
      expect(typeof body.token).toBe('string');
      return body.token as string;
    }

    const errorBody = await response.text();
    errors.push(`(${attempt.username}:${attempt.password}) ${response.status()} ${errorBody}`);
  }

  throw new Error(
    `Login failed for ${credentials.username}. Attempts: ${errors.join(' | ')}`
  );
}

export function randomUsername(prefix = 'user') {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substring(2, 8)}`;
}

export async function createRandomUser(request: APIRequestContext, token: string, role = 'TECHNICIAN') {
  const username = randomUsername('user');
  const password = 'Test1234!';

  const createUserResponse = await request.post('/users', {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    data: {
      username,
      password,
      role,
    },
  });

  expect(createUserResponse.status()).toBe(201);
  const user = await createUserResponse.json();
  expect(user).toMatchObject({
    username,
    role,
  });
  expect(user.id).toBeTruthy();

  return {
    id: user.id as number,
    username,
    password,
    role,
  };
}

export async function createIncident(request: APIRequestContext, token: string) {
  const incidentPayload = {
    title: `Falla ${Date.now()}`,
    description: 'La máquina dejó de funcionar y necesita mantenimiento urgente.',
    priority: 'HIGH',
    category: 'MAINTENANCE',
  };

  const createResponse = await request.post('/incidents', {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    data: incidentPayload,
  });

  expect(createResponse.status()).toBe(201);
  const incident = await createResponse.json();
  expect(incident).toMatchObject({
    title: incidentPayload.title,
    description: incidentPayload.description,
    priority: incidentPayload.priority,
    category: incidentPayload.category,
  });
  expect(incident.id).toBeTruthy();

  return incident;
}
