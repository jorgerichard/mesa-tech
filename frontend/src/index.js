import React from "react";
import ReactDOM from "react-dom/client";
import { PublicClientApplication } from "@azure/msal-browser";
import { MsalProvider } from "@azure/msal-react";
import { msalConfig } from "./authConfig";
import App from "./App";
import "./styles.css";

const msal = new PublicClientApplication(msalConfig);
msal.initialize().then(() => {
  ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode><MsalProvider instance={msal}><App /></MsalProvider></React.StrictMode>
  );
});
