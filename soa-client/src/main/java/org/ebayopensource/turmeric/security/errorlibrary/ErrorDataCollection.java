/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 * Once the errorlibrary is created, these files should be removed and the generated files
 * should be used instead.
 * 
 */
package org.ebayopensource.turmeric.security.errorlibrary;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

/**
 * @deprecated
 */
public class ErrorDataCollection {

    private final static String ORGANIZATION = "eBay";

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_trafficlimiter_init_failed = createCommonErrorData(10000L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_trafficlimiter_init_failed", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_trafficlimiter_call_exceeded_limit = createCommonErrorData(10001L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_trafficlimiter_call_exceeded_limit", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_rate_limiter_null_host_name = createCommonErrorData(10002L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_rate_limiter_null_host_name", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_object_id_cache_refresh_failed = createCommonErrorData(10003L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_object_id_cache_refresh_failed", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_policy_subject_group_error = createCommonErrorData(10004L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_policy_subject_group_error", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_group_membership_resolver_error = createCommonErrorData(10005L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_group_membership_resolver_error", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_ratelimiter_system_error = createCommonErrorData(10006L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_ratelimiter_system_error", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_ratelimiter_service_init_failed = createCommonErrorData(10000L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_ratelimiter_service_init_failed", "Security", "RateLimiter", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_unexpected_authn_error = createCommonErrorData(11000L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_unexpected_authn_error", "Security", "Authentication", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_unexpected_authz_error = createCommonErrorData(11001L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_unexpected_authz_error", "Security", "Authorization", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_authn_failed = createCommonErrorData(11002L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_authn_failed", "Security", "Authentication", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_authz_failed = createCommonErrorData(11003L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_authz_failed", "Security", "Authorization", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_set_authn_status_error = createCommonErrorData(11004L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_set_authn_status_error", "Security", "Authentication", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_set_authz_status_error = createCommonErrorData(11005L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_set_authz_status_error", "Security", "Authorization", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_assertion_creation_error = createCommonErrorData(11006L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_assertion_creation_error", "Security", "Miscellaneous", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_invalid_url_credentials = createCommonErrorData(11007L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_invalid_url_credentials", "Security", "Miscellaneous", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_external_call_not_allowed = createCommonErrorData(11008L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_external_call_not_allowed", "Security", "Miscellaneous", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_unexpected_blacklist_error = createCommonErrorData(11009L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_unexpected_blacklist_error", "Security", "BlackList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_unexpected_whitelist_error = createCommonErrorData(11010L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_unexpected_whitelist_error", "Security", "WhiteList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_blacklist_failed = createCommonErrorData(11011L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_blacklist_failed", "Security", "BlackList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_whitelist_failed = createCommonErrorData(11012L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_whitelist_failed", "Security", "WhiteList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_set_blacklist_status_error = createCommonErrorData(11013L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_set_blacklist_status_error", "Security", "BlackList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_set_whitelist_status_error = createCommonErrorData(11014L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_set_whitelist_status_error", "Security", "WhiteList", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_unexpected_policyenforcement_error = createCommonErrorData(11015L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_unexpected_policyenforcement_error", "Security", "PolicyEnforcement", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_policyenforcement_failed = createCommonErrorData(11016L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_policyenforcement_failed", "Security", "PolicyEnforcement", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_clienttokenhandler_init_failed = createCommonErrorData(11017L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_clienttokenhandler_init_failed", "Security", "Security", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_clienttokenhandler_error = createCommonErrorData(11018L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_clienttokenhandler_error", "Security", "Security", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_external_nonhttps_call_not_allowed = createCommonErrorData(11019L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_external_nonhttps_call_not_allowed", "Security", "Security", (null));

    /**
     * @deprecated
     */
    public final static CommonErrorData svc_security_invalid_provider_configuration = createCommonErrorData(11020L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), "svc_security_invalid_provider_configuration", "Security", "Miscellaneous", (null));

    private static CommonErrorData createCommonErrorData(long errorId, ErrorSeverity severity, ErrorCategory category, String errorName, String domain, String subDomain, String errorGroup) {
        CommonErrorData errorData = new CommonErrorData();
        errorData.setErrorId(errorId);
        errorData.setSeverity(severity);
        errorData.setCategory(category);
        errorData.setSubdomain(subDomain);
        errorData.setDomain(domain);
        errorData.setErrorGroups(errorGroup);
        errorData.setErrorName(errorName);
        errorData.setOrganization(ORGANIZATION);
        return errorData;
    }

}
