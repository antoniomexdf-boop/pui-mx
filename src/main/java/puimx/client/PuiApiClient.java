/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client;

import lombok.extern.slf4j.Slf4j;
import puimx.client.dto.BusquedaFinalizadaRequest;
import puimx.client.dto.NotificarCoincidenciaRequest;
import puimx.client.dto.PuiLoginRequest;
import puimx.client.dto.PuiLoginResponse;
import puimx.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Component
public class PuiApiClient {

    private final RestTemplate restTemplate;
    private final AuditoriaService auditoriaService;

    @Value("${pui.api.base-url}")
    private String baseUrl;

    @Value("${pui.api.institucion-id}")
    private String institucionId;

    @Value("${pui.api.clave}")
    private String clave;

    @Value("${pui.api.demo-mode:true}")
    private boolean demoMode;

    private volatile String tokenActivo;
    private volatile long tokenExpiracionMs = 0;

    public PuiApiClient(
            RestTemplateBuilder builder,
            AuditoriaService auditoriaService,
            @Value("${pui.api.connect-timeout-ms:5000}") long connectTimeoutMs,
            @Value("${pui.api.read-timeout-ms:5000}") long readTimeoutMs) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
        this.auditoriaService = auditoriaService;
    }

    public String obtenerToken() {
        if (demoMode) {
            return "demo-token";
        }

        long ahora = System.currentTimeMillis();
        if (tokenActivo == null || ahora >= tokenExpiracionMs) {
            renovarToken();
        }
        return tokenActivo;
    }

    private synchronized void renovarToken() {
        if (demoMode) {
            tokenActivo = "demo-token";
            tokenExpiracionMs = System.currentTimeMillis() + (48 * 60 * 1000L);
            return;
        }

        long ahora = System.currentTimeMillis();
        if (tokenActivo != null && ahora < tokenExpiracionMs) {
            return;
        }

        HttpHeaders headers = jsonHeaders();
        PuiLoginRequest body = new PuiLoginRequest(institucionId, clave);
        HttpEntity<PuiLoginRequest> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PuiLoginResponse> resp = restTemplate.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    entity,
                    PuiLoginResponse.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                tokenActivo = resp.getBody().getToken();
                tokenExpiracionMs = ahora + (48 * 60 * 1000L);
                return;
            }
            throw new PuiClientException("La PUI no devolvio token");
        } catch (Exception e) {
            throw new PuiClientException("No se pudo obtener token de la PUI", e);
        }
    }

    public boolean notificarCoincidencia(NotificarCoincidenciaRequest request) {
        if (demoMode) {
            auditoriaService.registrar(
                    "ENVIO_COINCIDENCIA",
                    request.getId(),
                    request.getCurp(),
                    request.getFase_busqueda(),
                    200,
                    "demo-mode",
                    "Coincidencia simulada enviada a PUI para evento " + request.getTipo_evento());
            return true;
        }

        return notificarCoincidencia(request, true);
    }

    private boolean notificarCoincidencia(NotificarCoincidenciaRequest request, boolean reintentoPermitido) {
        String token = obtenerToken();
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        HttpEntity<NotificarCoincidenciaRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    baseUrl + "/notificar-coincidencia",
                    HttpMethod.POST,
                    entity,
                    String.class);

            auditoriaService.registrar(
                    "ENVIO_COINCIDENCIA",
                    request.getId(),
                    request.getCurp(),
                    request.getFase_busqueda(),
                    resp.getStatusCode().value(),
                    "pui",
                    "Coincidencia enviada a PUI");
            return resp.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401 && reintentoPermitido) {
                tokenActivo = null;
                tokenExpiracionMs = 0;
                return notificarCoincidencia(request, false);
            }
            auditoriaService.registrar(
                    "ENVIO_COINCIDENCIA",
                    request.getId(),
                    request.getCurp(),
                    request.getFase_busqueda(),
                    e.getStatusCode().value(),
                    "pui",
                    truncarDetalle(e.getResponseBodyAsString()));
            return false;
        } catch (HttpServerErrorException e) {
            auditoriaService.registrar(
                    "ENVIO_COINCIDENCIA",
                    request.getId(),
                    request.getCurp(),
                    request.getFase_busqueda(),
                    e.getStatusCode().value(),
                    "pui",
                    truncarDetalle(e.getMessage()));
            return false;
        } catch (Exception e) {
            auditoriaService.registrar(
                    "ENVIO_COINCIDENCIA",
                    request.getId(),
                    request.getCurp(),
                    request.getFase_busqueda(),
                    500,
                    "pui",
                    truncarDetalle(e.getMessage()));
            return false;
        }
    }

    public boolean notificarBusquedaFinalizada(String id, String institucionId) {
        if (demoMode) {
            auditoriaService.registrar(
                    "BUSQUEDA_FINALIZADA",
                    id,
                    null,
                    "2",
                    200,
                    "demo-mode",
                    "Busqueda finalizada simulada");
            return true;
        }

        return notificarBusquedaFinalizada(id, institucionId, true);
    }

    private boolean notificarBusquedaFinalizada(String id, String institucionId, boolean reintentoPermitido) {
        String token = obtenerToken();
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        BusquedaFinalizadaRequest body = new BusquedaFinalizadaRequest(id, institucionId);
        HttpEntity<BusquedaFinalizadaRequest> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    baseUrl + "/busqueda-finalizada",
                    HttpMethod.POST,
                    entity,
                    String.class);
            auditoriaService.registrar(
                    "BUSQUEDA_FINALIZADA",
                    id,
                    null,
                    "2",
                    resp.getStatusCode().value(),
                    "pui",
                    "Notificacion de busqueda finalizada enviada");
            return resp.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401 && reintentoPermitido) {
                tokenActivo = null;
                tokenExpiracionMs = 0;
                return notificarBusquedaFinalizada(id, institucionId, false);
            }
            auditoriaService.registrar(
                    "BUSQUEDA_FINALIZADA",
                    id,
                    null,
                    "2",
                    e.getStatusCode().value(),
                    "pui",
                    truncarDetalle(e.getResponseBodyAsString()));
            return false;
        } catch (Exception e) {
            auditoriaService.registrar(
                    "BUSQUEDA_FINALIZADA",
                    id,
                    null,
                    "2",
                    500,
                    "pui",
                    truncarDetalle(e.getMessage()));
            return false;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        return h;
    }

    private String truncarDetalle(String detalle) {
        if (detalle == null) {
            return null;
        }
        return detalle.length() <= 1000 ? detalle : detalle.substring(0, 1000);
    }
}
