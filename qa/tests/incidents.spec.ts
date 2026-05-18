import { test, expect } from '@playwright/test';
import { login, createIncident } from './test-utils';

test.describe('Endpoints de incidentes', () => {
  test('admin puede crear un incidente', async ({ request }) => {
    const token = await login(request);
    await createIncident(request, token);
  });

  test('admin puede listar incidentes', async ({ request }) => {
    const token = await login(request);
    const incident = await createIncident(request, token);

    const listResponse = await request.get('/incidents', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(listResponse.status()).toBe(200);
    const incidents = await listResponse.json();
    expect(Array.isArray(incidents)).toBe(true);
    expect(incidents.some((item: any) => item.id === incident.id)).toBe(true);
  });

  test('admin puede obtener un incidente por id', async ({ request }) => {
    const token = await login(request);
    const incident = await createIncident(request, token);

    const response = await request.get(`/incidents/${incident.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(response.status()).toBe(200);
    const incidentById = await response.json();
    expect(incidentById).toMatchObject({
      id: incident.id,
      title: incident.title,
      description: incident.description,
      priority: incident.priority,
      category: incident.category,
    });
  });

  test('admin puede consultar el historial de assignments de un incidente', async ({ request }) => {
    const token = await login(request);
    const incident = await createIncident(request, token);

    const response = await request.get(`/incidents/${incident.id}/assignments`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(response.status()).toBe(200);
    const assignments = await response.json();
    expect(Array.isArray(assignments)).toBe(true);
  });

  test('admin puede resolver un incidente', async ({ request }) => {
    const token = await login(request);
    const incident = await createIncident(request, token);

    const resolveResponse = await request.patch(`/incidents/${incident.id}/resolve`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(resolveResponse.status()).toBe(204);

    const getResponse = await request.get(`/incidents/${incident.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(getResponse.status()).toBe(200);
    const resolvedIncident = await getResponse.json();
    expect(resolvedIncident.status).toBe('RESOLVED');
    expect(resolvedIncident.resolvedAt).toBeTruthy();
  });

  test('admin puede asignar un incidente a un técnico', async ({ request }) => {
    const token = await login(request);
    const incident = await createIncident(request, token);

    const assignResponse = await request.post(`/incidents/${incident.id}/assign`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: {
        assignedTo: 'Técnico Juan',
      },
    });

    expect(assignResponse.status()).toBe(200);

    const historyResponse = await request.get(`/incidents/${incident.id}/assignments`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(historyResponse.status()).toBe(200);
    const assignments = await historyResponse.json();
    expect(Array.isArray(assignments)).toBe(true);
    expect(assignments.some((item: any) => item.assignedTo === 'Técnico Juan')).toBe(true);
  });
});
