package pe.edu.upeu.PharmaBackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger LOG =
            LoggerFactory.getLogger(RequestIdFilter.class);

    private static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = request.getHeader(HEADER);

        if (requestId == null
                || !requestId.matches("[A-Za-z0-9._-]{1,64}")) {
            requestId = UUID.randomUUID().toString();
        }

        String previousRequestId = MDC.get("requestId");
        MDC.put("requestId", requestId);
        response.setHeader(HEADER, requestId);

        long inicio = System.nanoTime();
        boolean fallo = false;

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            fallo = true;
            throw ex;
        } finally {
            long duracionMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - inicio
            );

            LOG.info(
                    "requestId={} | metodo={} | ruta={} | status={} "
                            + "| duracionMs={} | excepcion={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duracionMs,
                    fallo
            );

            if (previousRequestId == null) {
                MDC.remove("requestId");
            } else {
                MDC.put("requestId", previousRequestId);
            }
        }
    }
}