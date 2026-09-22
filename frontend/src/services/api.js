import { InteractionRequiredAuthError } from "@azure/msal-browser";
import { loginRequest } from "../authConfig";

const baseUrl = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080";

export async function accessToken(instance, account) {
  try {
    const result = await instance.acquireTokenSilent({ ...loginRequest, account });
    return result.accessToken;
  } catch (error) {
    if (error instanceof InteractionRequiredAuthError) {
      await instance.acquireTokenRedirect({ ...loginRequest, account });
    }
    throw error;
  }
}

export async function api(instance, account, path, options = {}) {
  const token = await accessToken(instance, account);
  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}`, ...(options.headers || {}) }
  });
  if (!response.ok) {
    let message = `Error ${response.status}`;
    try { const body = await response.json(); message = body.message || body.detail || message; } catch (_) {}
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}
