/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

const endpoints = {
  resumen: "/demo/resumen",
  auditoria: "/demo/auditoria",
  reportes: "/demo/reportes",
  registros: "/demo/registros",
};

const statsNode = document.getElementById("stats");
const auditBody = document.getElementById("audit-body");
const reportBody = document.getElementById("report-body");
const recordsBody = document.getElementById("records-body");
const lastUpdate = document.getElementById("last-update");
const recordsTag = document.getElementById("records-tag");
const refreshButton = document.getElementById("refresh-button");
const autoRefresh = document.getElementById("auto-refresh");
const curpForm = document.getElementById("curp-form");
const curpInput = document.getElementById("curp-input");

let activeRecordsEndpoint = endpoints.registros;
let intervalId = null;

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;");
}

async function fetchJson(url) {
  const response = await fetch(url, { headers: { Accept: "application/json" } });
  if (!response.ok) {
    throw new Error(`Error ${response.status} al consultar ${url}`);
  }
  return response.json();
}

function renderStats(summary) {
  const cards = [
    ["Reportes", summary.totalReportes],
    ["Activos", summary.reportesActivos],
    ["Fase 1", summary.fase1Completada],
    ["Fase 2", summary.fase2Completada],
    ["Registros", summary.totalRegistrosInstitucionales],
    ["Eventos auditados", summary.ultimosEventos],
  ];

  statsNode.innerHTML = cards.map(([label, value]) => `
    <article class="stat">
      <span class="tag">${escapeHtml(label)}</span>
      <strong>${escapeHtml(value)}</strong>
    </article>
  `).join("");
}

function renderAudit(logs) {
  if (!logs.length) {
    auditBody.innerHTML = `<tr><td class="empty" colspan="6">Sin eventos registrados</td></tr>`;
    return;
  }

  auditBody.innerHTML = logs.map((log) => `
    <tr>
      <td>${escapeHtml(log.timestampUtc)}</td>
      <td>${escapeHtml(log.tipoOperacion)}</td>
      <td>${escapeHtml(log.reporteId)}</td>
      <td>${escapeHtml(log.faseBusqueda)}</td>
      <td>${escapeHtml(log.resultadoHttp)}</td>
      <td>${escapeHtml(log.detalle)}</td>
    </tr>
  `).join("");
}

function renderReports(reportes) {
  if (!reportes.length) {
    reportBody.innerHTML = `<tr><td class="empty" colspan="6">No hay reportes cargados</td></tr>`;
    return;
  }

  reportBody.innerHTML = reportes.map((reporte) => `
    <tr>
      <td>${escapeHtml(reporte.id)}</td>
      <td>${escapeHtml(reporte.curp)}</td>
      <td><span class="pill ${reporte.activo ? "" : "off"}">${reporte.activo ? "Activo" : "Inactivo"}</span></td>
      <td>${reporte.fase1Completada ? "Si" : "No"}</td>
      <td>${reporte.fase2Completada ? "Si" : "No"}</td>
      <td>${escapeHtml(reporte.ultimaVerificacion)}</td>
    </tr>
  `).join("");
}

function renderRecords(registros) {
  if (!registros.length) {
    recordsBody.innerHTML = `<tr><td class="empty" colspan="7">Sin registros para esa consulta</td></tr>`;
    return;
  }

  recordsBody.innerHTML = registros.map((registro) => `
    <tr>
      <td>${escapeHtml(registro.idRegistro)}</td>
      <td>${escapeHtml(registro.curp)}</td>
      <td>${escapeHtml(registro.nombreCompleto)}</td>
      <td>${escapeHtml(registro.tipoEvento)}</td>
      <td>${escapeHtml(registro.fechaEvento)}</td>
      <td>${escapeHtml(registro.descripcionLugar)}</td>
      <td>${escapeHtml(registro.telefono || registro.correo || "")}</td>
    </tr>
  `).join("");
}

async function refreshDashboard() {
  try {
    const [summary, auditoria, reportes, registros] = await Promise.all([
      fetchJson(endpoints.resumen),
      fetchJson(endpoints.auditoria),
      fetchJson(endpoints.reportes),
      fetchJson(activeRecordsEndpoint),
    ]);

    renderStats(summary);
    renderAudit(auditoria);
    renderReports(reportes);
    renderRecords(registros);
    lastUpdate.textContent = `Ultima actualizacion: ${new Date().toLocaleString()}`;
  } catch (error) {
    lastUpdate.textContent = error.message;
  }
}

function updateAutoRefresh() {
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }

  if (autoRefresh.checked) {
    intervalId = setInterval(refreshDashboard, 15000);
  }
}

curpForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const curp = curpInput.value.trim().toUpperCase();
  activeRecordsEndpoint = curp ? `${endpoints.registros}/${curp}` : endpoints.registros;
  recordsTag.textContent = activeRecordsEndpoint;
  refreshDashboard();
});

refreshButton.addEventListener("click", refreshDashboard);
autoRefresh.addEventListener("change", updateAutoRefresh);

updateAutoRefresh();
refreshDashboard();
