package psbds.demo.services.exchange;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import psbds.demo.backends.dolarapi.DolarApiClientWrapper;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.repository.cache.EuroExchangeRateCache;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;

import java.util.Optional;

/**
 * Service for retrieving Euro exchange rates.
 * Implements cache-first strategy with fallback to external API.
 */
@ApplicationScoped
public class GetEuroExchangeRateService {

    private static final Logger LOG = Logger.getLogger(GetEuroExchangeRateService.class);

    @Inject
    DolarApiClientWrapper dolarApiClientWrapper;

    @Inject
    EuroExchangeRateMapper euroExchangeRateMapper;

    @Inject
    EuroExchangeRateCache euroExchangeRateCache;

    /**
     * Retrieves EUR/BRL exchange rate.
     * Uses cache-first strategy unless noCache is true.
     *
     * @param noCache if true, bypasses cache and fetches fresh data
     * @return Optional containing exchange rate response if successful, empty otherwise
     */
    public Optional<GetEuroExchangeRateResponse> getEuroExchangeRate(boolean noCache) {
        // Check cache first unless no-cache is requested
        if (!noCache) {
            LOG.debug("Checking cache for EUR exchange rate");
            Optional<GetEuroExchangeRateResponse> cachedResponse = euroExchangeRateCache.get();
            if (cachedResponse.isPresent()) {
                LOG.info("Returning EUR exchange rate from cache");
                return cachedResponse;
            }
            LOG.debug("Cache miss for EUR exchange rate");
        } else {
            LOG.debug("Cache bypass requested via no-cache header");
        }

        // Fetch from external API
        LOG.info("Fetching EUR exchange rate from external API");
        Optional<EuroExchangeRateApiResponse> apiResponse = dolarApiClientWrapper.getEuroExchangeRate();

        if (apiResponse.isEmpty()) {
            LOG.error("Failed to fetch EUR exchange rate from external API");
            return Optional.empty();
        }

        // Map to public response
        GetEuroExchangeRateResponse publicResponse = euroExchangeRateMapper.toPublicResponse(apiResponse.get());

        if (publicResponse == null) {
            LOG.error("Failed to map EUR exchange rate API response to public response");
            return Optional.empty();
        }

        // Update cache with fresh data
        LOG.debug("Updating cache with fresh EUR exchange rate data");
        try {
            euroExchangeRateCache.set(publicResponse);
        } catch (Exception e) {
            LOG.warn("Failed to update cache, but continuing with response: " + e.getMessage());
        }

        return Optional.of(publicResponse);
    }
}
