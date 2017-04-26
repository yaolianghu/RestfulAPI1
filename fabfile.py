from fabric.api import local, run, env, cd, settings, sudo
import os, time

# remote ssh info
env.user = 'deployuser'
srvr_path = 'ROOT.war'

if env.api_env == 'prod':
    env.hosts = ['10.210.51.31','10.210.50.172']
elif env.api_env == 'vpc-prod':
    env.hosts = ['10.210.51.55','10.210.50.15']
elif env.api_env == 'prod1':
    env.hosts = ['10.210.51.55']
elif env.api_env == 'prod2':
    env.hosts = ['10.210.50.15']
elif env.api_env == 'stage':
    env.hosts = ['10.210.40.124']
elif env.api_env == 'qa':
    env.hosts = ['10.210.30.44']
elif env.api_env == 'dev':
    env.hosts = ['10.210.30.223']

local('echo Deploying site %s' % env.site)

local('echo Deploying to %s' % env.api_env)

def deploy():

    
    local('scp build/libs/{0} {1}@{2}:/tmp'.format(srvr_path, env.user, env.host_string))
        
    with cd('/opt/tomcat8/webapps/'):
        run('rm -f /opt/tomcat8/webapps/%s' % srvr_path, shell=False)
        run('rm -rf /opt/tomcat8/webapps/ROOT*')

        time.sleep(5)
        run('mv -f /tmp/%s /opt/tomcat8/webapps/' % srvr_path, shell=False)
        run('chown -R deployuser:deployuser /opt/tomcat8')
        time.sleep(2)
        run('/opt/tomcat8/bin/shutdown.sh -force')
        time.sleep(5)
        run('set -m; /opt/tomcat8/bin/startup.sh')