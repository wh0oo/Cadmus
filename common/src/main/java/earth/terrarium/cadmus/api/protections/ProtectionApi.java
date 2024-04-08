package earth.terrarium.cadmus.api.protections;

import earth.terrarium.cadmus.api.ApiHelper;

import java.util.Map;
import java.util.Set;

public interface ProtectionApi {

    ProtectionApi API = ApiHelper.load(ProtectionApi.class);

    /**
     * Registers a protection and setting.
     *
     * @param protection The protection.
     * @throws IllegalArgumentException if the protection is already registered.
     */
    void register(Protection protection);

    /**
     * Registers a claim setting.
     *
     * @param setting the claim setting.
     */
    void registerSetting(String setting);

    /**
     * Gets a protection.
     *
     * @param setting the setting.
     * @return the protection.
     */
    Protection getProtection(String setting);

    /**
     * Gets all protections.
     *
     * @return all protections.
     */
    Map<String, Protection> getProtections();

    /**
     * Gets all registered settings.
     *
     * @return all settings.
     */
    Set<String> getSettings();
}
