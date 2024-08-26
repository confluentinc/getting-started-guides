export BOOTSTRAP_SERVERS=< BOOTSTRAP SERVERS >
export SECURITY_PROTOCOL=SASL_SSL
export SASL_MECHANISM=OAUTHBEARER
export SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL=< OAUTH2 TOKEN ENDPOINT URL >
export SASL_LOGIN_CALLBACK_HANDLER_CLASS=org.apache.kafka.common.security.oauthbearer.secured.OAuthBearerLoginCallbackHandler
export SASL_JAAS_CONFIG="org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required \
        clientId='< OAUTH2 CLIENT ID >' \
        clientSecret='< OAUTH2 CLIENT SECRET >' \
        scope='< OAUTH2 SCOPE >' \
        extension_logicalCluster='< LOGICAL CLUSTER ID >' \
        extension_identityPoolId='< IDENTITY POOL ID >';"