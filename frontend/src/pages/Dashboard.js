import { useEffect, useMemo, useState } from "react";
import { useMsal } from "@azure/msal-react";
import { api } from "../services/api";

const inicial = {
  titulo: "",
  descripcion: "",
  categoria: "",
  prioridad: ""
};

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = accounts[0];

  const [session, setSession] = useState(null);
  const [solicitudes, setSolicitudes] = useState([]);
  const [catalogo, setCatalogo] = useState([]);
  const [form, setForm] = useState(inicial);
  const [error, setError] = useState("");
  const [procesando, setProcesando] = useState(null);

  const roles =
    session?.roles ||
    account?.idTokenClaims?.roles ||
    [];

  const staff =
    roles.includes("OPERADOR") ||
    roles.includes("ADMINISTRADOR");

  const categorias = useMemo(
    () => catalogo.filter((item) => item.tipo === "CATEGORIA"),
    [catalogo]
  );

  const prioridades = useMemo(
    () => catalogo.filter((item) => item.tipo === "PRIORIDAD"),
    [catalogo]
  );

  async function cargar() {
    setError("");

    try {
      const [datosSesion, datosCatalogo, listaSolicitudes] =
        await Promise.all([
          api(instance, account, "/api/v1/session"),
          api(instance, account, "/api/v1/catalogo"),
          api(
            instance,
            account,
            staff
              ? "/api/v1/solicitudes"
              : "/api/v1/solicitudes/mias"
          )
        ]);

      setSession(datosSesion);
      setCatalogo(datosCatalogo);
      setSolicitudes(listaSolicitudes);
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (account) {
      cargar();
    }

  }, [account?.homeAccountId, staff]);

  async function crear(e) {
    e.preventDefault();
    setError("");
    setProcesando("crear");

    try {
      await api(instance, account, "/api/v1/solicitudes", {
        method: "POST",
        body: JSON.stringify(form)
      });

      setForm(inicial);
      await cargar();
    } catch (e) {
      setError(e.message);
    } finally {
      setProcesando(null);
    }
  }

  async function asignar(id) {
    setError("");
    setProcesando(id);

    try {
      await api(
        instance,
        account,
        `/api/v1/solicitudes/${id}/asignacion`,
        {
          method: "PATCH"
        }
      );

      await cargar();
    } catch (e) {
      setError(e.message);
    } finally {
      setProcesando(null);
    }
  }

  async function cambiarEstado(id, estado) {
    setError("");
    setProcesando(id);

    try {
      await api(
        instance,
        account,
        `/api/v1/solicitudes/${id}/estado`,
        {
          method: "PATCH",
          body: JSON.stringify({
            estado,
            comentario: `Cambio de estado a ${estado}`
          })
        }
      );

      await cargar();
    } catch (e) {
      setError(e.message);
    } finally {
      setProcesando(null);
    }
  }

  function obtenerAccion(solicitud) {
    if (!staff) {
      return null;
    }

    const deshabilitado = procesando === solicitud.id;

    switch (solicitud.estado) {
      case "CREADA":
        return (
          <button
            type="button"
            disabled={deshabilitado}
            onClick={() => asignar(solicitud.id)}
          >
            {deshabilitado ? "Procesando..." : "Asignar"}
          </button>
        );

      case "ASIGNADA":
        return (
          <button
            type="button"
            disabled={deshabilitado}
            onClick={() =>
              cambiarEstado(solicitud.id, "EN_PROCESO")
            }
          >
            {deshabilitado
              ? "Procesando..."
              : "Iniciar atención"}
          </button>
        );

      case "EN_PROCESO":
        return (
          <button
            type="button"
            disabled={deshabilitado}
            onClick={() =>
              cambiarEstado(solicitud.id, "RESUELTA")
            }
          >
            {deshabilitado ? "Procesando..." : "Resolver"}
          </button>
        );

      case "RESUELTA":
        return (
          <button
            type="button"
            disabled={deshabilitado}
            onClick={() =>
              cambiarEstado(solicitud.id, "CERRADA")
            }
          >
            {deshabilitado ? "Procesando..." : "Cerrar"}
          </button>
        );

      case "CERRADA":
      case "CANCELADA":
        return <span>Sin acciones</span>;

      default:
        return <span>Estado desconocido</span>;
    }
  }

  return (
    <div className="app-shell">
      <aside>
        <div className="logo">
          <span>MT</span>
          <strong>MesaTech</strong>
        </div>

        <nav>
          <a href="#resumen">Resumen</a>
          <a href="#nueva">Nueva solicitud</a>
          <a href="#solicitudes">Solicitudes</a>
        </nav>
      </aside>

      <div className="content">
        <header>
          <div>
            <small>
              Sesión protegida por Microsoft Entra ID
            </small>
            <h1>Panel de soporte</h1>
          </div>

          <div className="user">
            <div>
              <strong>
                {session?.name || account?.name}
              </strong>

              <small>
                {session?.username || account?.username}
              </small>
            </div>

            <button
              type="button"
              className="secondary"
              onClick={() => instance.logoutRedirect()}
            >
              Cerrar sesión
            </button>
          </div>
        </header>

        <main>
          {error && (
            <div className="alert">
              {error}
            </div>
          )}

          <section
            id="resumen"
            className="cards"
          >
            <article>
              <small>Total visible</small>
              <b>{solicitudes.length}</b>
            </article>

            <article>
              <small>En proceso</small>
              <b>
                {
                  solicitudes.filter(
                    (item) =>
                      item.estado === "EN_PROCESO"
                  ).length
                }
              </b>
            </article>

            <article>
              <small>Rol</small>
              <b className="role">
                {roles.join(", ") || "SIN ROL"}
              </b>
            </article>
          </section>

          <section
            id="nueva"
            className="panel"
          >
            <h2>Crear solicitud</h2>

            <form
              onSubmit={crear}
              className="form-grid"
            >
              <label>
                Título

                <input
                  required
                  value={form.titulo}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      titulo: e.target.value
                    })
                  }
                />
              </label>

              <label>
                Categoría

                <select
                  required
                  value={form.categoria}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      categoria: e.target.value
                    })
                  }
                >
                  <option value="">
                    Seleccione
                  </option>

                  {categorias.map((item) => (
                    <option
                      key={item.id}
                      value={item.codigo}
                    >
                      {item.nombre}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                Prioridad

                <select
                  required
                  value={form.prioridad}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      prioridad: e.target.value
                    })
                  }
                >
                  <option value="">
                    Seleccione
                  </option>

                  {prioridades.map((item) => (
                    <option
                      key={item.id}
                      value={item.codigo}
                    >
                      {item.nombre}
                    </option>
                  ))}
                </select>
              </label>

              <label className="wide">
                Descripción

                <textarea
                  required
                  value={form.descripcion}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      descripcion: e.target.value
                    })
                  }
                />
              </label>

              <button
                type="submit"
                disabled={procesando === "crear"}
              >
                {procesando === "crear"
                  ? "Registrando..."
                  : "Registrar solicitud"}
              </button>
            </form>
          </section>

          <section
            id="solicitudes"
            className="panel"
          >
            <div className="section-title">
              <h2>
                {staff
                  ? "Todas las solicitudes"
                  : "Mis solicitudes"}
              </h2>

              <button
                type="button"
                className="secondary"
                onClick={cargar}
              >
                Actualizar
              </button>
            </div>

            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Categoría</th>
                    <th>Prioridad</th>
                    <th>Estado</th>
                    <th>Creada</th>

                    {staff && <th>Acción</th>}
                  </tr>
                </thead>

                <tbody>
                  {solicitudes.map((solicitud) => (
                    <tr key={solicitud.id}>
                      <td>#{solicitud.id}</td>
                      <td>{solicitud.titulo}</td>
                      <td>{solicitud.categoria}</td>
                      <td>{solicitud.prioridad}</td>

                      <td>
                        <span
                          className={`status ${solicitud.estado?.toLowerCase()}`}
                        >
                          {solicitud.estado}
                        </span>
                      </td>

                      <td>
                        {new Date(
                          solicitud.fechaCreacion
                        ).toLocaleString("es-CL")}
                      </td>

                      {staff && (
                        <td className="actions">
                          {obtenerAccion(solicitud)}
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>

              {!solicitudes.length && (
                <p className="empty">
                  No existen solicitudes para mostrar.
                </p>
              )}
            </div>
          </section>
        </main>
      </div>
    </div>
  );
}
