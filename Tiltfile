# -*- mode: Python -*-

load('ext://configmap', 'configmap_create', 'configmap_from_dict')
load('ext://secret', 'secret_yaml_generic', 'secret_from_dict')
load('ext://helm_remote', 'helm_remote')
load('ext://helm_resource', 'helm_resource', 'helm_repo')

helm_repo('bitnami', 'https://charts.bitnami.com/bitnami')

# k8s_yaml(configmap_from_dict("postgres-env", inputs={
#     'POSTGRES_USER': "postgres",
#     'DB_USER': "dbuser",
#     'DB_PASS': "dbpass",
#     'DB_NAME': "database_name"
# }))

# configmap_create('postgres-init',
#   from_file=[
#     "00_init.sql=path/to/init.sql"
# ])

# helm_remote('postgresql',
#             repo_name='bitnami',
#             repo_url='https://charts.bitnami.com/bitnami',
#             values='tilt/postgresql/kubernetes-postgresql-values.yaml')

# helm_resource(
#    'postgresql',
#    'bitnami/postgresql',
#    flags=[
#        '-f', './tilt/postgresql/values.yaml',
#    ],
#    labels=['services']
# )
#
# k8s_resource(
#    'postgresql',
#    port_forwards=['5432:5432']
# )

# k8s_yaml('tilt/kafka/kubernetes-zookeeper.yaml')
# k8s_resource('zookeeper', port_forwards=['2181:2181'], labels=['services'])
#
# k8s_yaml('tilt/kafka/kubernetes-kafka.yaml')
# k8s_resource('kafka', port_forwards=['9092'], labels=['services'])
#
# k8s_yaml('tilt/kafka/kubernetes-kafka-manager.yaml')
# k8s_resource('kafka-manager', port_forwards=['9000', '9999'], labels=['services'])
#
# k8s_yaml('tilt/rabbitmq/kubernetes-rabbitmq.yaml')
# k8s_resource('rabbitmq', port_forwards='15672', labels=['services'])

custom_build(
    'demo',
    './gradlew --parallel modules:demo:bootBuildImage --imageName=$EXPECTED_REF',
    deps=[
        'modules/demo/src',
        'modules/demo/build.gradle.kts'
    ]
)

helm_resource(
    'demo',
    'charts/springboot',
    image_deps=['demo'],
    image_keys=[
        ('image.repository', 'image.tag')
    ],
    flags=[
        '-f', './tilt/demo/values.yaml',
    ],
    resource_deps=['postgresql']
)

k8s_resource(
    'demo',
    port_forwards=['8100:8080', '8101:8081', '6565:6565'],
    trigger_mode=TRIGGER_MODE_MANUAL,
    labels=['application']
)
