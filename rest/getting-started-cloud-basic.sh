export BOOTSTRAP_SERVERS=< BOOTSTRAP SERVERS >
export SECURITY_PROTOCOL=SASL_SSL
export SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username='< CLUSTER API KEY >' password='< CLUSTER API SECRET >';"
export SASL_MECHANISM=PLAIN

