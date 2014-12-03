#!/bin/bash

pushd `dirname $BASH_SOURCE`

mkdir -p packages

pushd packages

# PogtgreSQL
rm postgresql93*.rpm
rm libxslt*.rpm

curl -O http://yum.postgresql.org/9.3/redhat/rhel-5-x86_64/postgresql93-9.3.5-1PGDG.rhel5.x86_64.rpm
curl -O http://yum.postgresql.org/9.3/redhat/rhel-5-x86_64/postgresql93-libs-9.3.5-1PGDG.rhel5.x86_64.rpm
curl -O http://yum.postgresql.org/9.3/redhat/rhel-5-x86_64/postgresql93-server-9.3.5-1PGDG.rhel5.x86_64.rpm

curl -O http://yum.postgresql.org/9.3/redhat/rhel-6-x86_64/postgresql93-9.3.5-1PGDG.rhel6.x86_64.rpm
curl -O http://yum.postgresql.org/9.3/redhat/rhel-6-x86_64/postgresql93-libs-9.3.5-1PGDG.rhel6.x86_64.rpm
curl -O http://yum.postgresql.org/9.3/redhat/rhel-6-x86_64/postgresql93-server-9.3.5-1PGDG.rhel6.x86_64.rpm

curl -O http://centos.serverspace.co.uk/centos/5.11/os/x86_64/CentOS/libxslt-1.1.17-4.el5_8.3.x86_64.rpm
curl -O http://centos.serverspace.co.uk/centos/6/os/x86_64/Packages/libxslt-1.1.26-2.el6_3.1.x86_64.rpm

popd
popd