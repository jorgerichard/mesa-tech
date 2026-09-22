import { LogLevel } from "@azure/msal-browser";

export const msalConfig = {
  auth: {
    clientId: "663c1419-552a-41e0-bdf1-45665c12c90c",
    authority: "https://login.microsoftonline.com/055a632e-e72b-4c47-8721-0622709b654f",
    redirectUri: window.location.origin,
    postLogoutRedirectUri: window.location.origin
  },
  cache: { cacheLocation: "sessionStorage", storeAuthStateInCookie: false },
  system: { loggerOptions: { loggerCallback: () => {}, logLevel: LogLevel.Warning } }
};

export const loginRequest = {
  scopes: ["api://d76c42fd-0fa1-4c27-8e13-3bee84d0d3f3/access_as_user"]
};
