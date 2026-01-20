package psbds.demo.services.exchange;

import io.quarkus.logging.Log;
import psbds.demo.backends.dolarapi.DolarApiAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.repository.cache.EuroExchangeRateCache;
import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for retrieving EUR/BRL exchange rates with cache-first strategy
 */
@ApplicationScoped
public class GetEuroExchangeRateService {

    @Inject
    DolarApiAPIClientWrapper dolarApiClient;

    @Inject
    EuroExchangeRateCache cache;

    @Inject
    EuroExchangeRateMapper mapper;

    /**
     * Get EUR/BRL exchange rate with cache-first strategy
     * 
     * @param noCache When true, bypasses cache and fetches fresh data
     * @return EUR exchange rate information or null if unavailable
     */
    public GetEurExchangeRateResponse getEuroExchangeRate(Boolean noCache) {
        // Check cache first (unless no-cache is requested)
        if (!Boolean.TRUE.equals(noCache)) {
            try {
                GetEurExchangeRateResponse cachedResponse = cache.get();
                if (cachedResponse != null) {
                    Log.info("EUR exchange rate retrieved from cache");
                    return cachedResponse;
                }
            } catch (Exception e) {
                Log.warnf(e, "Failed to retrieve EUR exchange rate from cache, falling back to API");
            }
        }

        // Fetch from external API
        try {
            DolarApiAPIGetEurResponse apiResponse = dolarApiClient.getEur();
            
            if (apiResponse == null) {
                Log.error("DolarAPI returned null response for EUR exchange rate");
                return null;
            }

            // Map to public response
            GetEurExchangeRateResponse publicResponse = mapper.toPublicResponse(apiResponse);

            // Update cache
            try {
                cache.set(publicResponse);
                Log.info("EUR exchange rate cached successfully");
            } catch (Exception e) {
                Log.warnf(e, "Failed to cache EUR exchange rate, but returning data anyway");
            }

            return publicResponse;
        } catch (Exception e) {
            Log.errorf(e, "Failed to retrieve EUR exchange rate from DolarAPI");
            return null;
        }
    }
}
