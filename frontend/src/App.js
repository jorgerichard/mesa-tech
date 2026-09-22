import { AuthenticatedTemplate, UnauthenticatedTemplate, useMsal } from "@azure/msal-react";
import { loginRequest } from "./authConfig";
import Dashboard from "./pages/Dashboard";

function Login() {
  const { instance } = useMsal();
  return <main className="login"><section className="login-card"><span className="brand">MT</span><h1>MesaTech Cloud</h1><p>Centraliza y gestiona tus solicitudes de soporte tecnológico.</p><button onClick={() => instance.loginRedirect(loginRequest)}>Iniciar sesión con Microsoft</button></section></main>;
}

export default function App() {
  return <><UnauthenticatedTemplate><Login /></UnauthenticatedTemplate><AuthenticatedTemplate><Dashboard /></AuthenticatedTemplate></>;
}
